/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package solve.mavenproject1;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class TicTacToeClient {
    private JFrame frame = new JFrame("Tic Tac Toe");
    private JButton[] buttons = new JButton[9];
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private char mySymbol;
    private boolean myTurn;

    public TicTacToeClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 12345);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton("");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 60));
            buttons[i].addActionListener(new ButtonClickListener(i));
            frame.add(buttons[i]);
        }

        frame.setLayout(new GridLayout(3, 3));
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Thread(new Listener()).start();
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (myTurn && buttons[index].getText().equals("")) {
                out.println(index);
            }
        }
    }

    private class Listener implements Runnable {
        public void run() {
            try {
                while (true) {
                    String response = in.readLine();
                    if (response.startsWith("WELCOME")) {
                        mySymbol = response.charAt(8);
                    } else if (response.startsWith("START")) {
                        myTurn = mySymbol == 'X';
                    } else if (response.startsWith("MOVE")) {
                        int index = Integer.parseInt(response.split(" ")[1]);
                        char symbol = response.split(" ")[2].charAt(0);
                        buttons[index].setText(String.valueOf(symbol));
                        myTurn = (symbol != mySymbol);
                    } else if (response.startsWith("WIN")) {
                        JOptionPane.showMessageDialog(frame, "Player " + response.charAt(4) + " wins!");
                        System.exit(0);
                    } else if (response.startsWith("INVALID")) {
                        JOptionPane.showMessageDialog(frame, "Invalid move. Try again.");
                     }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String serverAddress = JOptionPane.showInputDialog("Enter Server Address:");
        new TicTacToeClient(serverAddress);
    }
}


