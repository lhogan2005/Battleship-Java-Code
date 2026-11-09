
package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private Lobby lobby;
    private PlayerUser player1, player2;

    @MessageMapping("/game")
    @SendTo("/topic/game")
    public synchronized Object handle(Request req) {

        switch (req.action()) {

            case "CREATE_LOBBY":
                player1 = new PlayerUser(1, "Player1", true);
                player2 = null;
                LobbySettings settings = new LobbySettings(
                    5,
                    new ArrayList<>(List.of(ShipType.PATROLBOAT)),
                    new ArrayList<>(List.of(AbilityType.SHOT))
                );
                lobby = new Lobby(1, player1, settings, "DEMO");
                break;

            case "JOIN_LOBBY":
                player2 = new PlayerUser(2, "Player2", true);
                lobby.addOpponent(player2);
                break;

            case "UPDATE_SETTINGS":
                lobby.updateSettings(req.setting(), req.value());
                break;

            case "START_GAME":
                lobby.startGame();
                break;

            case "PLACE_SHIP":
                lobby.getGame().placeShip(
                    getPlayer(req.playerId()),
                    req.shipType(),
                    req.row(),
                    req.column(),
                    req.orientation()
                );
                break;

            case "READY":
                lobby.getGame().confirmPlacement(
                    getPlayer(req.playerId())
                );
                break;

            case "ATTACK":
                lobby.getGame().useAbility(
                    getPlayer(req.playerId()),
                    req.abilityType(),
                    req.row(),
                    req.column()
                );
                break;

            case "CLOSE_LOBBY":
                lobby.closeLobby(player1);
                lobby = null;
                break;
        }

        return getState();
    }

    private PlayerData getPlayer(int id) {
        Game game = lobby.getGame();
        return id == 1 ? game.getPlayer1() : game.getPlayer2();
    }

    private Object getState() {
        if (lobby == null)
            return "Lobby closed";

        Game game = lobby.getGame();

        if (game == null)
            return "Lobby: " + lobby.getCode();

        return new GameResponse(
            game.getState(),
            game.getPlayerForCurrentTurn().getId(),
            game.getPlayer1().getShipCount(),
            game.getPlayer2().getShipCount(),
            game.winner() == null ? null : game.winner().getId()
        );
    }

    public record Request(
        String action,
        Integer playerId,
        ShipType shipType,
        AbilityType abilityType,
        Integer row,
        Integer column,
        Boolean orientation,
        String setting,
        String value
    ) {}

    public record GameResponse(
        GameState state,
        int currentPlayer,
        int player1Ships,   
        int player2Ships,
        Integer winner
    ) {}
}
