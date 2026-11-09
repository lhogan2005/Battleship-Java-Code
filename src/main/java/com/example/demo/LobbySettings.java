package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LobbySettings {
    private int gridSize;
    private List<ShipType> shipTypes;
    private List<AbilityType> abilityTypes;

    public LobbySettings(int gridSize, List<ShipType> shipTypes, List<AbilityType> abilityTypes) {
        this.gridSize = gridSize;
        this.shipTypes = shipTypes;
        this.abilityTypes = abilityTypes;
    }

    public void updateSettings(String setting, String value) {
        if (setting == null || value == null) {
            throw new IllegalArgumentException("Setting and value are required");
        }

        switch (setting.toLowerCase(Locale.ROOT).replace("_", "").replace("-", "")) {
            case "gridsize" -> updateGridSize(value);
            case "shiptype", "shiptypes" -> updateShipTypes(value);
            case "abilities", "abilitytype", "abilitytypes" -> updateAbilities(value);
            default -> throw new IllegalArgumentException("Unknown lobby setting: " + setting);
        }
    }

    private void updateGridSize(String value) {
        try {
            int updatedSize = Integer.parseInt(value.trim());
            if (updatedSize < 5 || updatedSize > 50) {
                throw new IllegalArgumentException("Grid size must be between 5 and 50");
            }
            gridSize = updatedSize;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Grid size must be a number", exception);
        }
    }

    private void updateShipTypes(String value) {
        List<ShipType> updatedTypes = Arrays.stream(value.split(","))
                .map(String::trim)
                .map(type -> parseEnum(type, ShipType.class))
                .toList();
        if (updatedTypes.isEmpty() || updatedTypes.stream().distinct().count() != updatedTypes.size()) {
            throw new IllegalArgumentException("Ship types must be non-empty and unique");
        }
        shipTypes = updatedTypes;
    }

    private void updateAbilities(String value) {
        List<AbilityType> updatedTypes = Arrays.stream(value.split(","))
                .map(String::trim)
                .map(type -> parseEnum(type, AbilityType.class))
                .toList();
        if (updatedTypes.isEmpty()) {
            throw new IllegalArgumentException("At least one ability type is required");
        }
        abilityTypes = updatedTypes;
    }

    private <T extends Enum<T>> T parseEnum(String value, Class<T> enumType) {
        try {
            return Enum.valueOf(enumType, value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid " + enumType.getSimpleName() + ": " + value, exception);
        }
    }

    public int getGridSize() {
        return gridSize;
    }

    public List<ShipType> getShipTypes() {
        return shipTypes;
    }

    public List<AbilityType> getAbilityTypes() {
        return abilityTypes;
    }
}
