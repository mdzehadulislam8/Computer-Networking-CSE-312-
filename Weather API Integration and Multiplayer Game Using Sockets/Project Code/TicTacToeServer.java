/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solve.mavenproject1;


import java.io.*;
import java.net.*;
import java.util.*;

public class TicTacToeServer {
    private static final int PORT = 12345;
    private static ServerSocket serverSocket;
    private static ArrayList<PlayerHandler> players = new ArrayList<>();
    private static char[] board = new char[9];
    private static int currentPlayer = 0;

    public static void main(String[] args) { 
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            Arrays.fill(board, ' ');  // Initialize the board

            while (true) {
                if (players.size() < 2) {
                    Socket socket = serverSocket.accept();
                    PlayerHandler player = new PlayerHandler(socket, players.size() + 1);
                    players.add(player);
                    new Thread(player).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized boolean makeMove(int position, char symbol) {
        if (board[position] == ' ') {
            board[position] = symbol;
            return true;
        }
        return false;
    }

    private static synchronized boolean checkWin() {
        // Winning combinations
        int[][] winCombos = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combo : winCombos) {
            if (board[combo[0]] != ' ' && board[combo[0]] == board[combo[1]] && board[combo[1]] == board[combo[2]]) {
                return true;
            }
        }
        return false;
    }

    private static class PlayerHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private int playerNumber;
        private char symbol;

        public PlayerHandler(Socket socket, int playerNumber) {
            this.socket = socket;
            this.playerNumber = playerNumber;
            this.symbol = playerNumber == 1 ? 'X' : 'O';
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out.println("WELCOME " + symbol);
                if (playerNumber == 2) {
                    out.println("START");
                    players.get(0).out.println("START");
                }

                while (true) {
                    String input = in.readLine();
                    if (input != null) {
                        int move = Integer.parseInt(input);
                        if (currentPlayer == playerNumber - 1 && makeMove(move, symbol)) {
                            broadcast("MOVE " + move + " " + symbol);
                            if (checkWin()) {
                                broadcast("WIN " + symbol);
                                break;
                            }
                            currentPlayer = (currentPlayer + 1) % 2;
                        } else {
                            out.println("INVALID");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void broadcast(String message) {
            for (PlayerHandler player : players) {
                player.out.println(message);
            }
        }
    }
}

