package com.example.demo;

import java.util.List;

public record LobbySnapshot(
        int lobbyId,
        String code,
        String host,
        String opponent,
        int gridSize,
        List<ShipType> shipTypes,
        List<AbilityType> abilityTypes,
        GameState state,
        boolean hostReady,
        boolean opponentReady) {}
