import nl.saxion.app.CsvReader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Application{

    static JFrame frame = new JFrame("Game example");
    static JPanel panel = new JPanel();

    static volatile boolean clicked = false;
    static volatile int numberOfPlayers = 0;
    static volatile int throwed = 0;

    static ArrayList<String> pionnen = new ArrayList<>();

    static ArrayList<Player> players = new ArrayList<>();

    static ArrayList<Integer> standardTurnCicle = new ArrayList<>();
    static ArrayList<Integer> turn = new ArrayList<>();

    static ArrayList<Event> events = new ArrayList<>();

    static int timesBeforeEvent = 0;

    public static void main(String[] args) {
        createMainPanel();
        createMainFrame();
        while (numberOfPlayers == 0) {
            Thread.onSpinWait();
        }
        createPlayers(numberOfPlayers);
        pionChoice();
        throwPosition();
        turn.addAll(standardTurnCicle);
        turn.addAll(standardTurnCicle);
        Random random = new Random();
        timesBeforeEvent = random.nextInt(numberOfPlayers*3) + numberOfPlayers*3;
        while (playing());
    }

    public static void createMainPanel() {
        panel.setLayout(null);
        for (int i = 2; i <= 6; i++) panel.add(createPlayerNumberButton(i));
        createExitButton();
        JLabel label = new JLabel("Met hoeveel spelers wil je het spel spelen?");
        label.setForeground(Color.WHITE);
        label.setBounds(1920/2-125,400,250,25);
        panel.add(label);
        panel.setBackground(Color.DARK_GRAY);
        frame.add(panel);
    }

    public static void createMainFrame() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
    }

    public static void createExitButton() {
        JButton button = new JButton("Click here to exit");
        button.setBounds(1920/2-100, 1080-40, 200, 40);
        button.addActionListener(e -> System.exit(1));
        panel.add(button);
    }

    public static JButton createPlayerNumberButton(int i) {
        JButton button = new JButton("" + i);
        button.setBounds(1920 / 2 - 425 + 100 * i, 500, 50, 50);
        button.addActionListener(e -> numberOfPlayers = i);
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        return button;
    }

    public static void pionChoice() {
        pionnen = createPionnen();
        for (int i = 1; i <= numberOfPlayers; i++) {
            panel.removeAll();
            panel.revalidate();
            clicked = false;
            for (int j = 0; j < pionnen.size(); j++) createPionButton(i,j);
            JLabel label = new JLabel("Welke figuur wil speler " + i + " hebben?");
            label.setBounds(1920/2-100,400,200,25);
            label.setForeground(Color.WHITE);
            panel.add(label);
            createExitButton();
            panel.repaint();
            while (!clicked) Thread.onSpinWait();
        }
    }

    public static void createPionButton(int i, int j) {
        JButton button = new JButton("" + pionnen.get(j));
        button.setBounds(285 + (200 * j) + i * 100,500, 150, 50);
        button.addActionListener(e -> {
            players.get(i-1).pion = pionnen.get(j);
            pionnen.remove(j);
            clicked = true;
        });
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        panel.add(button);
    }

    public static void createPlayers(int numberOfPlayers) {
        for (int i = 1; i <= numberOfPlayers; i++) {
            Player player = new Player();
            player.number = i;
            players.add(player);
        }
    }

    public static ArrayList<String> createPionnen () {
        ArrayList<String> pionnen = new ArrayList<>();
        pionnen.add("Steen");
        pionnen.add("Boomstronk");
        pionnen.add("Sneeuwpop");
        pionnen.add("Auto");
        pionnen.add("Vlam");
        pionnen.add("Boot");
        return pionnen;
    }

    public static void throwPosition() {
        panel.removeAll();
        panel.revalidate();
        int[] player = new int[numberOfPlayers];
        int[] turn = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) createPlayerNames(i);
        for (int i = 0; i < numberOfPlayers; i++) {
            createThrowButton((1000 - 100 * numberOfPlayers) + (200 * i), 100);
            createExitButton();
            panel.repaint();
            throwed = 0;
            while (throwed == 0) {
                Thread.onSpinWait();
            }
            createThrowLabel(i);
            for (int j = 0; j <= i; j++) {
                if (throwed > turn[j]) {
                    for (int k = 0; k < i-j; k++) {
                        turn[i-k] = turn[i-k-1];
                        player[i-k] = player[i-k-1];
                    }
                    turn[j] = throwed;
                    player[j] = i;
                    break;
                }
            }
        }
        createPlayerLabel(0, "Dit is de volgorde van het spel:");
        for (int i = 0; i < numberOfPlayers; i++) {
            standardTurnCicle.add(player[i]);
            createPlayerLabel(i+1, players.get(standardTurnCicle.get(i)).pion);
        }
        createNextButton(1000 - 100 * numberOfPlayers, 300 + (50 * (numberOfPlayers + 1)));
        panel.repaint();
        while (!clicked) Thread.onSpinWait();

    }

    public static void createPlayerNames(int i) {
        JLabel label = new JLabel(players.get(i).pion);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setBounds((1000 - 100 * numberOfPlayers) + (200 * i),50,100,40);
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    public static void createThrowButton(int x, int y) {
        JButton button = new JButton("Throw");
        button.setBounds(x, y,100,50);
        Random random = new Random();
        button.addActionListener(e -> {
            throwed = (random.nextInt(6) + 1);
            button.setVisible(false);
        });
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        panel.add(button);
    }

    public static void createThrowLabel(int i) {
        JLabel label = new JLabel("" + throwed + " is throwed");
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setBounds((1000 - 100 * numberOfPlayers) + (200 * i),100,100,40);
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    public static void createPlayerLabel(int i, String pion) {
        JLabel label = new JLabel("" + pion);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setBounds(1000 - 100 * numberOfPlayers,300 + (50 * i),300,40);
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    public static void createNextButton(int x, int y) {
        clicked = false;
        JButton button = new JButton("Next");
        button.setBounds(x,y, 150, 50);
        button.addActionListener(e -> clicked = true);
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        panel.add(button);
    }

    public static void createEvents() {
        CsvReader csvReader = new CsvReader("src/Events.csv");
        csvReader.skipRow();
        csvReader.setSeparator(';');
        while (csvReader.loadRow()) {
            Event event = new Event();
            event.gebied = csvReader.getString(0);
            event.description = csvReader.getString(1);
            event.minimumPlaces = csvReader.getInt(2);
            event.maximumPlaces = csvReader.getInt(3);
            events.add(event);
        }
    }

    public static boolean playing() {
        turn.addAll(standardTurnCicle);
        int firstPerson = standardTurnCicle.get(0);
        panel.removeAll();
        panel.revalidate();
        do {
            throwed = 0;
            createText( 500,50, players.get(turn.get(0)).pion + " is aan zet");
            createThrowButton(750, 50);
            createEventButton();
            createExitButton();
            panel.repaint();
            while (throwed == 0) {
                Thread.onSpinWait();
            }
            createText(500, 100, players.get(turn.get(0)).pion + " heeft " + throwed + " gegooid");
            throwed = 0;
            createNextButton(750, 100);
            createSkip1Turn(750, 200, turn.get(0));
            createSkip2Turns(750, 300, turn.get(0));
            if (timesBeforeEvent == 0) generateEvent();
            panel.repaint();
            while (!clicked) {
                Thread.onSpinWait();
            }
            panel.removeAll();
            panel.revalidate();
            turn.remove(0);
            timesBeforeEvent--;
            System.out.println(timesBeforeEvent);
        } while (turn.get(turn.get(0)).equals(turn.get(firstPerson)));
        return true;
    }

    public static void createText(int x, int y, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setBounds(x,y,300,40);
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    public static void createSkip1Turn(int x, int y, int person) {
        clicked = false;
        JButton button = new JButton("Skip 1 turn");
        button.setBounds(x,y, 150, 50);
        button.addActionListener(e -> {
            for (int i = 1; i <= numberOfPlayers; i++) {
                if (person == turn.get(i)) {
                    turn.remove(i);
                    break;
                }
            }
        });
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        panel.add(button);
    }

    public static void createSkip2Turns(int x, int y, int person) {
        clicked = false;
        JButton button = new JButton("Skip 2 turns");
        button.setBounds(x,y, 150, 50);
        button.addActionListener(e -> {
            for (int i = 1; i <= numberOfPlayers*2; i++) {
                if (person == turn.get(i)) {
                    turn.remove(i);
                    break;
                }
            }
        });
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        panel.add(button);
    }

    public static void generateEvent() {
        Random random = new Random();
        timesBeforeEvent = random.nextInt(numberOfPlayers*3) + numberOfPlayers*3;
        JLabel label = new JLabel(events.remove(random.nextInt(events.size())).description);
        label.setFont(new Font("Serif", Font.PLAIN, 20));
        label.setBounds(500, 400,1500,50);
        int randomEvent = random.nextInt(events.size());
        label.setText(events.remove(randomEvent).description);
        label.setVisible(true);
        label.setForeground(Color.WHITE);
        panel.add(label);
    }

    public static void createEventButton() {
        Random random = new Random();
        if (events.size() == 0) createEvents();
        JButton button = new JButton("Generate event");
        button.setBounds(1400, 200,100,50);
        JLabel label = new JLabel("");
        label.setBounds(1400, 250,500,50);
        int randomEvent = random.nextInt(events.size());
        label.setText(events.get(randomEvent).description);
        label.setVisible(false);
        label.setForeground(Color.WHITE);
        button.addActionListener(e -> {
            events.remove(randomEvent);
            timesBeforeEvent = random.nextInt(numberOfPlayers*3) + numberOfPlayers*3;
            label.setVisible(true);
            button.setVisible(false);
        });
        button.setBackground(Color.BLACK);
        button.setBorderPainted(false);
        button.setForeground(Color.WHITE);
        button.setFocusable(false);
        button.setOpaque(true);
        panel.add(button);
        panel.add(label);
    }
}