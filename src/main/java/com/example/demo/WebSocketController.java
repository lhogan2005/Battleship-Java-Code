package com.example.demo;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Controller
public class WebSocketController {
    private static final String REPLY_DESTINATION = "/queue/reply";

    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public WebSocketController(
            LobbyService lobbyService,
            SimpMessagingTemplate messagingTemplate,
            ObjectMapper objectMapper) {
        this.lobbyService = lobbyService;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/message")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse handleClientMessage(
            ClientMessage request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        requireRequest(request.type());
        requireRequest(request.payload());

        return switch (request.type()) {
            case "CREATE_LOBBY" -> {
                CreateLobbyPayload data = readPayload(request.payload(), CreateLobbyPayload.class);
                requireRequest(data.settings());
                yield createLobby(new CreateLobbyRequest(
                        playerName(data.playerId()),
                        data.settings().gridSize(),
                        data.settings().shipTypes(),
                        data.settings().abilityTypes()), sessionId);
            }
            case "JOIN_LOBBY" -> {
                JoinLobbyPayload data = readPayload(request.payload(), JoinLobbyPayload.class);
                yield joinLobby(new JoinLobbyRequest(data.code(), playerName(data.playerId())), sessionId);
            }
            case "UPDATE_SETTINGS" -> {
                UpdateSettingsPayload data = readPayload(request.payload(), UpdateSettingsPayload.class);
                String code = lobbyService.lobbyCodeById(data.lobbyId());
                Lobby lobby = lobbyService.updateSettings(sessionId, code, data.setting(), data.value());
                yield publish("settings-updated", "Lobby settings updated", lobby);
            }
            case "START_GAME" -> {
                LobbyPayload data = readPayload(request.payload(), LobbyPayload.class);
                yield startGame(new LobbyRequest(lobbyService.lobbyCodeById(data.lobbyId())), sessionId);
            }
            case "PLACE_SHIP" -> {
                PlaceShipPayload data = readPayload(request.payload(), PlaceShipPayload.class);
                yield placeShip(new PlaceShipRequest(
                        lobbyService.lobbyCodeById(data.lobbyId()),
                        data.shipType(), data.row(), data.column(), data.orientation()), sessionId);
            }
            case "CONFIRM_PLACEMENT" -> {
                LobbyPayload data = readPayload(request.payload(), LobbyPayload.class);
                yield confirmPlacement(
                        new LobbyRequest(lobbyService.lobbyCodeById(data.lobbyId())), sessionId);
            }
            default -> throw new IllegalArgumentException("Unsupported message type: " + request.type());
        };
    }

    @MessageMapping("/lobby/create")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse createLobby(
            CreateLobbyRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        Lobby lobby = lobbyService.createLobby(
                sessionId, request.username(), request.gridSize(), request.shipTypes(), request.abilityTypes());
        return publish("lobby-created", "Lobby created", lobby);
    }

    @MessageMapping("/lobby/join")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse joinLobby(
            JoinLobbyRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        Lobby lobby = lobbyService.joinLobby(sessionId, request.code(), request.username());
        return publish("lobby-joined", "Joined lobby", lobby);
    }

    @MessageMapping("/lobby/start")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse startGame(
            LobbyRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        return publish("game-started", "Game started", lobbyService.startGame(sessionId, request.code()));
    }

    @MessageMapping("/game/place-ship")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse placeShip(
            PlaceShipRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        Lobby lobby = lobbyService.placeShip(sessionId, request.code(), request.type(),
                request.row(), request.column(), request.vertical());
        return publish("ship-placed", "Ship placed", lobby,
                new GameAction(lobbyService.playerName(sessionId, request.code()),
                        request.type(), null, request.row(), request.column(), request.vertical()));
    }

    @MessageMapping("/game/ready")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse confirmPlacement(
            LobbyRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        Lobby lobby = lobbyService.confirmPlacement(sessionId, request.code());
        return publish("player-ready", "Placement confirmed", lobby);
    }

    @MessageMapping("/game/use-ability")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse useAbility(
            UseAbilityRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        Lobby lobby = lobbyService.useAbility(sessionId, request.code(), request.type(),
                request.row(), request.column());
        return publish("ability-used", "Ability used", lobby,
                new GameAction(lobbyService.playerName(sessionId, request.code()),
                        null, request.type(), request.row(), request.column(), null));
    }

    @MessageMapping("/lobby/leave")
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse leaveLobby(
            LobbyRequest request,
            @Header(SimpMessageHeaderAccessor.SESSION_ID_HEADER) String sessionId) {
        requireRequest(request);
        String code = lobbyService.leaveLobby(sessionId, request.code());
        WebSocketResponse response = new WebSocketResponse("lobby-closed", "Lobby closed", null, null);
        messagingTemplate.convertAndSend("/topic/lobbies/" + code, response);
        return response;
    }

    @MessageExceptionHandler(IllegalArgumentException.class)
    @SendToUser(value = REPLY_DESTINATION, broadcast = false)
    public WebSocketResponse handleRequestError(IllegalArgumentException exception) {
        return new WebSocketResponse("error", exception.getMessage(), null, null);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        String code = lobbyService.disconnect(event.getSessionId());
        if (code != null) {
            messagingTemplate.convertAndSend(
                    "/topic/lobbies/" + code,
                    new WebSocketResponse("lobby-closed", "A player disconnected", null, null));
        }
    }

    private WebSocketResponse publish(String type, String message, Lobby lobby) {
        return publish(type, message, lobby, null);
    }

    private WebSocketResponse publish(String type, String message, Lobby lobby, GameAction action) {
        WebSocketResponse response = new WebSocketResponse(
                type, message, lobbyService.snapshot(lobby), action);
        messagingTemplate.convertAndSend("/topic/lobbies/" + lobby.getCode(), response);
        return response;
    }

    private <T> void requireRequest(T request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
    }

    private <T> T readPayload(JsonNode payload, Class<T> payloadType) {
        if (!payload.isObject()) {
            throw new IllegalArgumentException("Message payload must be an object");
        }
        try {
            T value = objectMapper.treeToValue(payload, payloadType);
            requireRequest(value);
            return value;
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Message payload is invalid", exception);
        }
    }

    private String playerName(int playerId) {
        if (playerId < 1) {
            throw new IllegalArgumentException("Player ID must be positive");
        }
        return "Player " + playerId;
    }

    public record CreateLobbyRequest(
            String username,
            int gridSize,
            List<ShipType> shipTypes,
            List<AbilityType> abilityTypes) {}

    public record ClientMessage(String type, JsonNode payload) {}

    public record CreateLobbySettings(
            int gridSize, List<ShipType> shipTypes, List<AbilityType> abilityTypes) {}

    public record CreateLobbyPayload(int playerId, CreateLobbySettings settings) {}

    public record JoinLobbyPayload(String code, int playerId) {}

    public record UpdateSettingsPayload(int lobbyId, String setting, String value) {}

    public record LobbyPayload(int lobbyId) {}

    public record PlaceShipPayload(
            int lobbyId, ShipType shipType, int row, int column, boolean orientation) {}

    public record JoinLobbyRequest(String code, String username) {}

    public record LobbyRequest(String code) {}

    public record PlaceShipRequest(
            String code, ShipType type, int row, int column, boolean vertical) {}

    public record UseAbilityRequest(
            String code, AbilityType type, int row, int column) {}

    public record WebSocketResponse(
            String type, String message, LobbySnapshot lobby, GameAction action) {}

    public record GameAction(
            String username,
            ShipType shipType,
            AbilityType abilityType,
            Integer row,
            Integer column,
            Boolean vertical) {}

}
