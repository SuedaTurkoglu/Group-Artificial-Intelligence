import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.*;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.Integer.sum;

public class Referee extends Agent {
    private String playerAMove = null;
    private String playerBMove = null;
    private int playerAScore = 0;
    private int playerBScore = 0;
    private int drawScore = 0;

    protected void setup() {
        addBehaviour(new ManageGame());
    }

    private class ManageGame extends CyclicBehaviour {
        public void action() {
            // Receive PlayerA's move
            ACLMessage msgA = receive(MessageTemplate.MatchSender(new jade.core.AID("PlayerA", jade.core.AID.ISLOCALNAME)));

            if (msgA != null) {
                playerAMove = msgA.getContent();
            }

            // Receive PlayerB's move
            ACLMessage msgB = receive(MessageTemplate.MatchSender(new jade.core.AID("PlayerB", jade.core.AID.ISLOCALNAME)));
            if (msgB != null) {
                playerBMove = msgB.getContent();
            }

            if (playerAMove != null && playerBMove != null) {
                // Determine the winner
                String winner = determineWinner(playerAMove, playerBMove);

                // Update scores
                if (winner.contains("PlayerA")) {
                    playerAScore++;
                } else if (winner.contains("PlayerB")) {
                    playerBScore++;
                }
                else{
                    drawScore++;
                }

                // Send PlayerB's move and the winner to PlayerA
                ACLMessage toPlayerA = new ACLMessage(ACLMessage.INFORM);
                toPlayerA.addReceiver(new jade.core.AID("PlayerA", jade.core.AID.ISLOCALNAME));
                toPlayerA.setContent(playerBMove);
                send(toPlayerA);

                // Send PlayerA's move and the winner to PlayerB
                ACLMessage toPlayerB = new ACLMessage(ACLMessage.INFORM);
                toPlayerB.addReceiver(new jade.core.AID("PlayerB", jade.core.AID.ISLOCALNAME));
                toPlayerB.setContent(playerAMove );
                send(toPlayerB);

                // Print out the moves, the winner, and the scores for verification
                /*
                System.out.println("PlayerA played: " + playerAMove);
                System.out.println("PlayerB played: " + playerBMove);
                System.out.println("Winner: " + winner);
                System.out.println("PlayerA Score: " + playerAScore);
                System.out.println("PlayerB Score: " + playerBScore);
                */
                displayResponse("\nROUND: "+String.valueOf(playerAScore+playerBScore+drawScore)+ "\n\nPlayerA's move: " + playerAMove
                        + "\n\nPlayerB's move: " + playerBMove+ "\n\n" + winner + "\n\n\n" + "PlayerA:" + (playerAScore)+ "               " +
                        "PlayerB:" + (playerBScore));
                // Reset the moves
                playerAMove = null;
                playerBMove = null;
            } else {
                block(); // Wait for moves from players
            }
        }
    }

    private String determineWinner(String playerChoice, String opponentChoice) {
        if (playerChoice.equals(opponentChoice)) {
            return ")):!!There is a Draw!!:((";
        } else if ((playerChoice.equals("Rock") && opponentChoice.equals("Scissors")) ||
                (playerChoice.equals("Scissors") && opponentChoice.equals("Paper")) ||
                (playerChoice.equals("Paper") && opponentChoice.equals("Rock"))) {
            return "!!!!Winner: PlayerA!!!!"; // Assuming PlayerA is the sender of playerChoice
        } else {
            return "!!!!!Winner: PlayerB!!!!"; // Assuming PlayerB is the sender of opponentChoice
        }
    }
    public static void displayResponse(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE);
        optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);

        UIManager.put("OptionPane.okButtonText", "Play another round");

        final JOptionPane pane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE);
        final javax.swing.JDialog dialog = pane.createDialog(null, "Paper Rock Scissors GAME");

        dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Game ended.");
                System.exit(0); // Terminate the program
            }
        });
        dialog.setVisible(true);
    }
}
