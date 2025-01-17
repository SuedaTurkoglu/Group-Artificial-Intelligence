import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Player extends Agent {
    private double[] probabilities = {0.35, 0.35, 0.30};
    private List<String> opponentPlays = new ArrayList<>();

    protected void setup() {
        addBehaviour(new PlayGame());
    }

    private class PlayGame extends CyclicBehaviour {
        public void action() {
            updateStrategy(opponentPlays);
            String playerChoice = makeChoice();
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(playerChoice);
            msg.addReceiver(new jade.core.AID("Referee", jade.core.AID.ISLOCALNAME));
            send(msg);
            ACLMessage reply = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));

            if (reply != null) {
                String opponentChoice = reply.getContent();
                opponentPlays.add(opponentChoice);
                /*
                for(double probability: probabilities){
                    System.out.print(probability+"\t\t");
                }*/
            } else {
                block();
            }

            // Add a delay between games
            /*
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            */

        }
    }

    private String makeChoice() {
        Random rand = new Random();
        double choice = rand.nextDouble();
        if (choice < probabilities[0]) return "Paper";
        else if (choice < probabilities[0] + probabilities[1]) return "Rock";
        else return "Scissors";
    }

    private void updateStrategy(List<String> opponentPlays) {
        if (opponentPlays.size() == 0) return;
        int countPaper = 0;
        int countRock = 0;
        int countScissors = 0;

        for (String lastPlay : opponentPlays) {
            if (lastPlay.equals("Paper")) countPaper++;
            else if (lastPlay.equals("Rock")) countRock++;
            else countScissors++;
        }
        probabilities[0] += 0.02 * countRock;
        probabilities[1] += 0.02 * countScissors;
        probabilities[2] += 0.02 * countPaper;
        normalize(probabilities);
    }

    private void normalize(double[] probabilities) {
        double sum = Arrays.stream(probabilities).sum();
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= sum;
        }
    }
}
