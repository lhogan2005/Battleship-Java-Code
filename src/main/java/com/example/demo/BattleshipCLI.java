package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Plain CLI for testing the existing battleship classes (no networking).
 * It only calls your existing methods and prints what happened.
 * It adds no rules: no automatic points, no extra validation.
 *
 * Run: javac *.java && java BattleshipCLI
 */
public class BattleshipCLI {

    private static final int GRID_SIZE = 10; // change here if needed
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        // Default settings: every ship, every ability.
        List<ShipType> ships = new ArrayList<>();
        for (ShipType t : ShipType.values()) ships.add(t);
        List<AbilityType> abilities = new ArrayList<>();
        for (AbilityType a : AbilityType.values()) abilities.add(a);

        LobbySettings settings = new LobbySettings(GRID_SIZE, ships, abilities);
        PlayerUser host = new PlayerUser(1, "Player1", true);
        PlayerUser guest = new PlayerUser(2, "Player2", true);
        Lobby lobby = new Lobby(1, host, settings, "TEST");
        lobby.addOpponent(guest);
        Game game = new Game(lobby, host, guest);

        // Game has no player getters, so read them through the turn counter.
        PlayerData p1 = game.getPlayerForCurrentTurn();
        game.nextTurn();
        PlayerData p2 = game.getPlayerForCurrentTurn();
        game.nextTurn();

        placement(game, p1, ships);
        placement(game, p2, ships);
        battle(game, p1, p2);
    }

    // ---------------- placement ----------------

    private static void placement(Game game, PlayerData player, List<ShipType> ships) {
        blank();
        System.out.println("Player " + player.getId() + " placing ships.");
        for (ShipType type : ships) {
            while (true) {
                print(player.getSelfGrid(), true);
                System.out.print("Place " + type + " (size " + type.getSize()
                        + ") as '<coord> <H|V>' e.g. B3 H: ");
                String[] p = line().trim().split("\\s+");
                int[] pos = p.length == 2 ? coord(p[0]) : null;
                if (pos == null || !(p[1].equalsIgnoreCase("H") || p[1].equalsIgnoreCase("V"))) {
                    System.out.println("Bad input.");
                    continue;
                }
                int before = player.getShipCount();
                try {
                    game.placeShip(player, type, pos[0], pos[1], p[1].equalsIgnoreCase("V"));
                } catch (RuntimeException e) {
                    System.out.println("Exception: " + e);
                }
                if (player.getShipCount() > before) break;
                System.out.println("Ship was not placed.");
            }
        }
        print(player.getSelfGrid(), true);
        game.confirmPlacement(player);
        System.out.print("Confirmed. Press Enter to hide screen...");
        line();
        blank();
    }

    // ---------------- battle ----------------

    private static void battle(Game game, PlayerData p1, PlayerData p2) {
        while (true) {
            PlayerData attacker = game.getPlayerForCurrentTurn();
            PlayerData defender = (attacker == p1) ? p2 : p1;

            System.out.print("Player " + attacker.getId() + "'s turn. Press Enter...");
            line();

            boolean turnDone = false;
            while (!turnDone) {
                System.out.println("\nPlayer " + attacker.getId() + " | points: " + attacker.getPoints());
                System.out.println("Enemy grid:");
                print(defender.getSelfGrid(), false);
                System.out.println("Your grid:");
                print(attacker.getSelfGrid(), true);

                System.out.println("1) Shot  2) Bomb  3) Radar  4) Missile  "
                        + "g <n>) gainPoints(n)  q) quit");
                System.out.print("> ");
                String[] cmd = line().trim().split("\\s+");

                if (cmd[0].equalsIgnoreCase("q")) System.exit(0);

                // Test hook: calls your existing PlayerData.gainPoints.
                if (cmd[0].equalsIgnoreCase("g")) {
                    try {
                        attacker.gainPoints(Integer.parseInt(cmd[1]));
                    } catch (RuntimeException e) {
                        System.out.println("Usage: g <number>");
                    }
                    continue;
                }

                Ability ability;
                switch (cmd[0]) {
                    case "1": ability = new Shot(); break;
                    case "2": ability = new Bomb(); break;
                    case "3": ability = new Radar(); break;
                    case "4": ability = new Missile(); break;
                    default:
                        System.out.println("Bad choice.");
                        continue;
                }

                System.out.print("Target coord (e.g. B3): ");
                int[] pos = coord(line().trim());
                if (pos == null) {
                    System.out.println("Bad coordinate.");
                    continue;
                }

                Grid target = defender.getSelfGrid();
                int pointsBefore = attacker.getPoints();
                int hitsBefore = count(target, CellStatus.HIT);
                int missBefore = count(target, CellStatus.MISS);
                int shipsBefore = defender.getShipCount();

                try {
                    attacker.useAbility(ability, target.getCell(pos[0], pos[1]));
                } catch (RuntimeException e) {
                    // e.g. Bomb/Radar on an edge cell. Shown, not prevented.
                    System.out.println("Exception thrown by your code: " + e);
                }

                if (attacker.getPoints() == pointsBefore && ability.getCost() > 0) {
                    System.out.println("Ability not used (not enough points or it failed).");
                    continue;
                }

                System.out.println("Result: " + (count(target, CellStatus.HIT) - hitsBefore) + " hit(s), "
                        + (count(target, CellStatus.MISS) - missBefore) + " miss(es), "
                        + (shipsBefore - defender.getShipCount()) + " ship(s) newly sunk");
                turnDone = true;
            }

            if (defender.getShipCount() == 0) {
                game.endGame(defender);
                print(defender.getSelfGrid(), true);
                System.out.println("Player " + attacker.getId() + " wins!");
                return;
            }

            System.out.print("Press Enter to end turn and hide screen...");
            line();
            blank();
            game.nextTurn();
        }
    }

    // ---------------- helpers ----------------

    private static int count(Grid g, CellStatus s) {
        int n = 0;
        for (int r = 0; r < g.getSize(); r++)
            for (int c = 0; c < g.getSize(); c++)
                if (g.getCell(r, c).getStatus() == s) n++;
        return n;
    }

    /** "B3" -> {1, 2}; null if malformed or off the grid. */
    private static int[] coord(String s) {
        if (s.length() < 2) return null;
        int row = Character.toUpperCase(s.charAt(0)) - 'A';
        int col;
        try {
            col = Integer.parseInt(s.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }
        if (row < 0 || row >= GRID_SIZE || col < 0 || col >= GRID_SIZE) return null;
        return new int[] { row, col };
    }

    /** S = ship (only if reveal), X = hit, o = miss, . = untouched. */
    private static void print(Grid g, boolean reveal) {
        StringBuilder sb = new StringBuilder("    ");
        for (int c = 1; c <= g.getSize(); c++) sb.append(String.format("%-3d", c));
        sb.append("\n");
        for (int r = 0; r < g.getSize(); r++) {
            sb.append(String.format(" %c  ", (char) ('A' + r)));
            for (int c = 0; c < g.getSize(); c++) {
                Cell cell = g.getCell(r, c);
                char ch = '.';
                if (cell.getStatus() == CellStatus.HIT) ch = 'X';
                else if (cell.getStatus() == CellStatus.MISS) ch = 'o';
                else if (reveal && cell.hasShip()) ch = 'S';
                sb.append(ch).append("  ");
            }
            sb.append("\n");
        }
        System.out.print(sb);
    }

    private static String line() {
        if (!in.hasNextLine()) System.exit(0);
        return in.nextLine();
    }

    private static void blank() {
        for (int i = 0; i < 40; i++) System.out.println();
    }
}
