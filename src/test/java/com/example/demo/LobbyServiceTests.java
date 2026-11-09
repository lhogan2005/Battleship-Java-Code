package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class LobbyServiceTests {
    @Test
    void createsLobbyAndRunsGameForItsTwoSessions() {
        LobbyService service = new LobbyService();
        Lobby lobby = service.createLobby(
                "host-session", "Host", 5, List.of(ShipType.PATROLBOAT), List.of(AbilityType.SHOT));
        service.joinLobby("opponent-session", lobby.getCode(), "Opponent");

        assertEquals(GameState.PLACE, service.startGame("host-session", lobby.getCode())
                .getGame().getState());
        service.placeShip("host-session", lobby.getCode(), ShipType.PATROLBOAT, 0, 0, false);
        service.placeShip("opponent-session", lobby.getCode(), ShipType.PATROLBOAT, 0, 0, false);
        service.confirmPlacement("host-session", lobby.getCode());
        assertEquals(GameState.PLACE, lobby.getGame().getState());
        service.confirmPlacement("opponent-session", lobby.getCode());

        assertEquals(GameState.BATTLE, lobby.getGame().getState());
        assertTrue(service.useAbility(
                "host-session", lobby.getCode(), AbilityType.SHOT, 0, 0).getGame() != null);
        assertEquals(2, lobby.getGame().getPlayerForCurrentTurn().getId());
        assertEquals(CellStatus.HIT,
                lobby.getGame().getPlayer2().getSelfGrid().getCell(0, 0).getStatus());
    }

    @Test
    void rejectsActionsFromSessionsOutsideTheLobby() {
        LobbyService service = new LobbyService();
        Lobby lobby = service.createLobby(
                "host-session", "Host", 5, List.of(ShipType.PATROLBOAT), List.of(AbilityType.SHOT));

        assertThrows(IllegalArgumentException.class,
                () -> service.startGame("other-session", lobby.getCode()));
    }

    @Test
    void updatesSettingsByLobbyCodeAndIncludesLobbyIdInSnapshot() {
        LobbyService service = new LobbyService();
        Lobby lobby = service.createLobby(
                "host-session", "Host", 10, List.of(ShipType.PATROLBOAT), List.of(AbilityType.SHOT));

        service.updateSettings("host-session", lobby.getCode(), "gridsize", "12");

        assertEquals(12, service.snapshot(lobby).gridSize());
        assertEquals(lobby.getId(), service.snapshot(lobby).lobbyId());
        assertEquals(lobby.getCode(), service.lobbyCodeById(lobby.getId()));
    }

    @Test
    void onlyHostCanUpdateLobbySettings() {
        LobbyService service = new LobbyService();
        Lobby lobby = service.createLobby(
                "host-session", "Host", 10, List.of(ShipType.PATROLBOAT), List.of(AbilityType.SHOT));
        service.joinLobby("opponent-session", lobby.getCode(), "Opponent");

        assertThrows(IllegalArgumentException.class,
                () -> service.updateSettings("opponent-session", lobby.getCode(), "gridsize", "12"));
    }
}
