package com.example.demo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class LobbyService {
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private final Map<String, PlayerUser> playersBySession = new ConcurrentHashMap<>();
    private final AtomicInteger playerIds = new AtomicInteger();

    public synchronized Lobby createLobby(
            String sessionId,
            String username,
            int gridSize,
            List<ShipType> shipTypes,
            List<AbilityType> abilityTypes) {
        requireNewSession(sessionId);
        validateUsername(username);
        if (gridSize < 5 || gridSize > 50) {
            throw new IllegalArgumentException("Grid size must be between 5 and 50");
        }
        if (shipTypes == null || shipTypes.isEmpty()) {
            throw new IllegalArgumentException("At least one ship type is required");
        }
        if (abilityTypes == null || abilityTypes.isEmpty()) {
            throw new IllegalArgumentException("At least one ability type is required");
        }
        if (shipTypes.stream().anyMatch(type -> type == null)
                || shipTypes.stream().distinct().count() != shipTypes.size()) {
            throw new IllegalArgumentException("Ship types must be valid and unique");
        }
        if (abilityTypes.stream().anyMatch(type -> type == null)) {
            throw new IllegalArgumentException("Ability types must be valid");
        }

        PlayerUser host = newPlayer(username);
        String code;
        do {
            code = createCode();
        } while (lobbies.containsKey(code));
        Lobby lobby = new Lobby(playerIds.incrementAndGet(), host,
                new LobbySettings(gridSize, List.copyOf(shipTypes), List.copyOf(abilityTypes)), code);
        lobbies.put(code, lobby);

        playersBySession.put(sessionId, host);
        return lobby;
    }

    public synchronized Lobby updateSettings(
            String sessionId, String code, String setting, String value) {
        Lobby lobby = requirePlayerLobby(sessionId, code);
        if (lobby.getHostPlayer() != playersBySession.get(sessionId)) {
            throw new IllegalArgumentException("Only the lobby host can update settings");
        }
        if (lobby.getGame() != null) {
            throw new IllegalArgumentException("Lobby settings cannot change after the game starts");
        }
        lobby.updateSettings(setting, value);
        return lobby;
    }

    public synchronized Lobby joinLobby(String sessionId, String code, String username) {
        requireNewSession(sessionId);
        validateUsername(username);
        Lobby lobby = requireLobby(code);
        if (lobby.getOpponentPlayer() != null || lobby.getGame() != null) {
            throw new IllegalArgumentException("Lobby is full or the game has already started");
        }

        PlayerUser opponent = newPlayer(username);
        lobby.addOpponent(opponent);
        playersBySession.put(sessionId, opponent);
        return lobby;
    }

    public synchronized Lobby startGame(String sessionId, String code) {
        Lobby lobby = requirePlayerLobby(sessionId, code);
        if (lobby.getHostPlayer() != playersBySession.get(sessionId)) {
            throw new IllegalArgumentException("Only the lobby host can start the game");
        }
        if (lobby.getOpponentPlayer() == null) {
            throw new IllegalArgumentException("A second player must join before starting");
        }
        if (lobby.getGame() != null) {
            throw new IllegalArgumentException("The game has already started");
        }

        lobby.startGame();
        return lobby;
    }

    public synchronized Lobby placeShip(
            String sessionId, String code, ShipType type, int row, int column, boolean vertical) {
        Lobby lobby = requirePlayerLobby(sessionId, code);
        Game game = requireGame(lobby);
        PlayerData player = getGamePlayer(lobby, sessionId);
        if (!game.placeShip(player, type, row, column, vertical)) {
            throw new IllegalArgumentException("Ship cannot be placed at that position");
        }
        return lobby;
    }

    public synchronized Lobby confirmPlacement(String sessionId, String code) {
        Lobby lobby = requirePlayerLobby(sessionId, code);
        Game game = requireGame(lobby);
        PlayerData player = getGamePlayer(lobby, sessionId);
        if (!game.confirmPlacement(player)) {
            throw new IllegalArgumentException("Place every configured ship before readying up");
        }
        return lobby;
    }

    public synchronized Lobby useAbility(
            String sessionId, String code, AbilityType type, int row, int column) {
        Lobby lobby = requirePlayerLobby(sessionId, code);
        Game game = requireGame(lobby);
        PlayerData player = getGamePlayer(lobby, sessionId);
        if (type == null || !player.getSelfGrid().inBounds(row, column)) {
            throw new IllegalArgumentException("Ability type or target position is invalid");
        }
        if (!game.useAbility(player, type, row, column)) {
            throw new IllegalArgumentException("Ability cannot be used now");
        }
        return lobby;
    }

    public synchronized String leaveLobby(String sessionId, String code) {
        Lobby lobby = requirePlayerLobby(sessionId, code);
        closeLobby(lobby);
        return lobby.getCode();
    }

    public synchronized String disconnect(String sessionId) {
        PlayerUser player = playersBySession.remove(sessionId);
        if (player == null || player.getConnectedLobby() == null) {
            return null;
        }
        Lobby lobby = player.getConnectedLobby();
        closeLobby(lobby);
        return lobby.getCode();
    }

    public synchronized LobbySnapshot snapshot(Lobby lobby) {
        Game game = lobby.getGame();
        PlayerData host = game == null ? null : game.getPlayer1();
        PlayerData opponent = game == null ? null : game.getPlayer2();
        return new LobbySnapshot(
                lobby.getId(),
                lobby.getCode(),
                lobby.getHostPlayer().getUsername(),
                lobby.getOpponentPlayer() == null ? null : lobby.getOpponentPlayer().getUsername(),
                lobby.getSettings().getGridSize(),
                lobby.getSettings().getShipTypes(),
                lobby.getSettings().getAbilityTypes(),
                game == null ? null : game.getState(),
                host != null && host.isReady(),
                opponent != null && opponent.isReady());
    }

    public synchronized String playerName(String sessionId, String code) {
        requirePlayerLobby(sessionId, code);
        return playersBySession.get(sessionId).getUsername();
    }

    public synchronized String lobbyCodeById(int lobbyId) {
        return lobbies.values().stream()
                .filter(lobby -> lobby.getId() == lobbyId)
                .map(Lobby::getCode)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lobby not found"));
    }

    private PlayerData getGamePlayer(Lobby lobby, String sessionId) {
        PlayerUser player = playersBySession.get(sessionId);
        if (player == lobby.getHostPlayer()) {
            return requireGame(lobby).getPlayer1();
        }
        if (player == lobby.getOpponentPlayer()) {
            return requireGame(lobby).getPlayer2();
        }
        throw new IllegalArgumentException("Player is not part of this lobby");
    }

    private Lobby requirePlayerLobby(String sessionId, String code) {
        Lobby lobby = requireLobby(code);
        PlayerUser player = playersBySession.get(sessionId);
        if (player == null || player.getConnectedLobby() != lobby) {
            throw new IllegalArgumentException("Player is not part of this lobby");
        }
        return lobby;
    }

    private Lobby requireLobby(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Lobby code is required");
        }
        Lobby lobby = lobbies.get(code.toUpperCase());
        if (lobby == null) {
            throw new IllegalArgumentException("Lobby not found");
        }
        return lobby;
    }

    private Game requireGame(Lobby lobby) {
        Game game = lobby.getGame();
        if (game == null) {
            throw new IllegalArgumentException("The game has not started");
        }
        return game;
    }

    private void requireNewSession(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("WebSocket session is required");
        }
        if (playersBySession.containsKey(sessionId)) {
            throw new IllegalArgumentException("This WebSocket session already belongs to a player");
        }
    }

    private PlayerUser newPlayer(String username) {
        return new PlayerUser(playerIds.incrementAndGet(), username.trim(), true);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank() || username.length() > 40) {
            throw new IllegalArgumentException("Username must contain 1 to 40 characters");
        }
    }

    private String createCode() {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }

    private void closeLobby(Lobby lobby) {
        playersBySession.entrySet().removeIf(entry -> entry.getValue().getConnectedLobby() == lobby);
        lobbies.remove(lobby.getCode());
        lobby.close();
    }
}
