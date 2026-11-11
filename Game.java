import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

public class Game {
    // Game Logic Attributes
    private Deck gameDeck;
    private Hand playerHand;
    private Hand dealerHand;
    private boolean isDealerTurn = false;
    private String gameMessage = "Welcome to Blackjack!";

    // Betting & Bankroll Attributes
    private int chips = 100;
    private int currentBet = 0;
    private int highScore = 0;

    // GUI Attributes
    private final JFrame window;
    private final TablePanel tablePanel;
    private final JButton hitButton;
    private final JButton standButton;
    private final JButton playSameButton;
    private final JButton changeBetButton;
    private final JButton quitButton;

    public Game() {
        loadHighScore();

        // 1. Build the Window
        this.window = new JFrame("Blackjack Table");
        this.window.setSize(600, 500);
        this.window.setLocationRelativeTo(null); // <--- Add this line right here
        this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.window.setLayout(new BorderLayout());

        // 2. Build the Custom Graphics Table
        this.tablePanel = new TablePanel();
        this.window.add(this.tablePanel, BorderLayout.CENTER);

        // 3. Build the Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.DARK_GRAY);
        
        this.hitButton = new JButton("Hit");
        this.standButton = new JButton("Stand");
        this.playSameButton = new JButton("Play Again (Same Bet)");
        this.changeBetButton = new JButton("Change Bet");
        this.quitButton = new JButton("Quit & Save");
        
        buttonPanel.add(this.hitButton);
        buttonPanel.add(this.standButton);
        buttonPanel.add(this.playSameButton);
        buttonPanel.add(this.changeBetButton);
        buttonPanel.add(this.quitButton);
        this.window.add(buttonPanel, BorderLayout.SOUTH);

        // ---- ACTION LISTENERS ----

        this.hitButton.addActionListener(e -> {
            this.playerHand.addCard(this.gameDeck.draw());
            
            if (this.playerHand.calculateScore() > 21) {
                this.chips -= this.currentBet;
                this.gameMessage = "BUST! You went over 21. Dealer wins.";
                this.isDealerTurn = true; // Flip dealer card on bust
                endRoundUI();
            }
            this.tablePanel.repaint(); // Redraws the screen with the new card
        });

        this.standButton.addActionListener(e -> {
            this.isDealerTurn = true; // Reveal the hole card
            this.gameMessage = "Dealer's turn...";
            this.tablePanel.repaint();

            // Run dealer logic
            while (this.dealerHand.calculateScore() < 17) {
                this.dealerHand.addCard(this.gameDeck.draw());
            }

            int finalPlayerScore = this.playerHand.calculateScore();
            int finalDealerScore = this.dealerHand.calculateScore();

            if (finalDealerScore > 21) {
                this.chips += this.currentBet;
                this.gameMessage = "DEALER BUSTS! You win!";
            } else if (finalPlayerScore > finalDealerScore) {
                this.chips += this.currentBet;
                this.gameMessage = "YOU WIN!";
            } else if (finalDealerScore > finalPlayerScore) {
                this.chips -= this.currentBet;
                this.gameMessage = "DEALER WINS!";
            } else {
                this.gameMessage = "PUSH! It's a tie.";
            }
            
            endRoundUI();
            this.tablePanel.repaint();
        });

        this.playSameButton.addActionListener(e -> {
            if (this.chips >= this.currentBet) {
                startRound();
            } else {
                JOptionPane.showMessageDialog(this.window, "Not enough chips!");
                askForBet();
            }
        });

        this.changeBetButton.addActionListener(e -> askForBet());

        this.quitButton.addActionListener(e -> {
            saveHighScore();
            System.exit(0);
        });

        this.window.setVisible(true);
        askForBet();
    }

    // ---- ROUND LOGIC ----

    private void askForBet() {
        boolean validBet = false;
        while (!validBet) {
            String input = JOptionPane.showInputDialog(this.window, 
                "Current Chips: " + this.chips + "\nEnter your bet:");
            
            if (input == null) {
                saveHighScore();
                System.exit(0);
            }

            try {
                int bet = Integer.parseInt(input);
                if (bet > 0 && bet <= this.chips) {
                    this.currentBet = bet;
                    validBet = true;
                } else {
                    JOptionPane.showMessageDialog(this.window, "Bet must be between 1 and " + this.chips);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this.window, "Please enter a valid number.");
            }
        }
        startRound();
    }

    private void startRound() {
        this.gameDeck = new Deck();
        this.gameDeck.shuffle();
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.isDealerTurn = false;

        // Deal initial cards
        this.playerHand.addCard(this.gameDeck.draw());
        this.playerHand.addCard(this.gameDeck.draw());
        this.dealerHand.addCard(this.gameDeck.draw());
        this.dealerHand.addCard(this.gameDeck.draw());

        int initialPlayerScore = this.playerHand.calculateScore();
        int initialDealerScore = this.dealerHand.calculateScore();

        // ---- BLACKJACK CHECK ----
        boolean playerHasBlackjack = (initialPlayerScore == 21);
        boolean dealerHasBlackjack = (initialDealerScore == 21);

        if (playerHasBlackjack || dealerHasBlackjack) {
            this.isDealerTurn = true; // Flip the dealer's hidden card

            if (playerHasBlackjack && dealerHasBlackjack) {
                this.gameMessage = "Double Blackjack! PUSH.";
            } else if (playerHasBlackjack) {
                // 3:2 Payout (we cast to int in case the bet was an odd number)
                int payout = (int)(this.currentBet * 1.5);
                this.chips += payout; 
                this.gameMessage = "BLACKJACK! You win 3:2 payout!";
            } else {
                this.chips -= this.currentBet;
                this.gameMessage = "Dealer has Blackjack! You lose.";
            }
            
            endRoundUI(); // Locks out Hit/Stand, enables next round buttons
            this.tablePanel.repaint();
            return; // Exits the method immediately so the normal round doesn't start
        }

        // ---- NORMAL ROUND START ----
        this.gameMessage = "Round started. Good luck!";
        
        this.hitButton.setEnabled(true);
        this.standButton.setEnabled(true);
        this.playSameButton.setEnabled(false);
        this.changeBetButton.setEnabled(false);
        this.quitButton.setEnabled(false);
        
        this.tablePanel.repaint(); 
    }

    private void endRoundUI() {
        this.hitButton.setEnabled(false);
        this.standButton.setEnabled(false);
        
        // ---- NEW PEAK TRACKER ----
        // If your current chips just surpassed the high score, update it immediately in memory
        if (this.chips > this.highScore) {
            this.highScore = this.chips;
        }
        
        if (this.chips > 0) {
            this.playSameButton.setEnabled(true);
            this.changeBetButton.setEnabled(true);
        } else {
            this.gameMessage = "You went broke! Game Over.";
        }
        this.quitButton.setEnabled(true);
    }

    // ---- FILE I/O ----
    private void loadHighScore() {
        try {
            File saveFile = new File("highscore.txt");
            if (saveFile.exists()) {
                try (Scanner fileReader = new Scanner(saveFile)) {
                    if (fileReader.hasNextInt()) {
                        this.highScore = fileReader.nextInt();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No high score found.");
        }
    }

    private void saveHighScore() {
        try {
            FileWriter writer = new FileWriter("highscore.txt");
            // Save the tracked peak instead of this.chips
            writer.write(String.valueOf(this.highScore)); 
            writer.close();
            // Just a clean console log instead of a pop-up, since the high score 
            // is now constantly updated during gameplay
            System.out.println("Peak High Score Saved: " + this.highScore);
        } catch (IOException e) {
            System.out.println("Could not save high score.");
        }
    }

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        Game game = new Game();
    }

    // ==========================================
    // INNER CLASS: THE CUSTOM GRAPHICS TABLE
    // ==========================================
    private class TablePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // 1. Draw Casino Green Felt
            g.setColor(new Color(35, 112, 54)); 
            g.fillRect(0, 0, getWidth(), getHeight());

            // 2. Draw Table Text (Scores, Chips, Messages)
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Chips: " + chips + "   |   Bet: " + currentBet, 20, 30);
            
            // Central Game Message
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString(gameMessage, 200, 220);

            // 3. Draw Dealer Hand
            if (dealerHand != null && dealerHand.getSize() > 0) {
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("Dealer's Hand:", 20, 70);
                
                for (int i = 0; i < dealerHand.getSize(); i++) {
                    // Hide the second card (index 1) if it's not the dealer's turn
                    if (i == 1 && !isDealerTurn) {
                        drawCard(g, null, 20 + (i * 80), 80, true);
                    } else {
                        drawCard(g, dealerHand.getCard(i), 20 + (i * 80), 80, false);
                    }
                }
                // Show score only when revealed
                if (isDealerTurn) {
                    g.setColor(Color.WHITE);
                    g.drawString("Score: " + dealerHand.calculateScore(), 150, 70);
                }
            }

            // 4. Draw Player Hand
            if (playerHand != null && playerHand.getSize() > 0) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("Player's Hand:   Score: " + playerHand.calculateScore(), 20, 270);
                
                for (int i = 0; i < playerHand.getSize(); i++) {
                    drawCard(g, playerHand.getCard(i), 20 + (i * 80), 280, false);
                }
            }
        }

        // Helper method to draw a single card
        private void drawCard(Graphics g, Card card, int x, int y, boolean isHidden) {
            if (isHidden) {
                try {
                    // Points to your new folder for the card back
                    Image backImage = new ImageIcon("card_images/card_back.png").getImage();
                    g.drawImage(backImage, x, y, 70, 100, null);
                } catch (Exception e) {
                    // Fallback if card_back.png is missing
                    g.setColor(new Color(0, 51, 153));
                    g.fillRoundRect(x, y, 70, 100, 10, 10);
                    g.setColor(Color.WHITE);
                    g.drawRoundRect(x, y, 70, 100, 10, 10);
                    g.drawString("BACK", x + 18, y + 55);
                }
            } else {
                // Formats the string with spaces exactly as you named them
                // e.g., "card_images/Ace of Spades.png"
                // Note: If your files are strictly lowercase, change this to:
                // String fileName = "card_images/" + (card.getRank() + " of " + card.getSuit()).toLowerCase() + ".png";
                String fileName = "card_images/" + card.getRank() + " of " + card.getSuit() + ".png";
                
                try {
                    Image faceImage = new ImageIcon(fileName).getImage();
                    
                    // Fail-safe check to make sure the Image actually loaded
                    if (faceImage.getWidth(null) == -1) {
                        throw new Exception("Image not found: " + fileName);
                    }
                    
                    g.drawImage(faceImage, x, y, 70, 100, null);
                } catch (Exception e) {
                    // Fallback to text drawing if a specific file name has a typo or fails to load
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(x, y, 70, 100, 10, 10);
                    g.setColor(Color.BLACK);
                    g.drawRoundRect(x, y, 70, 100, 10, 10);
                    
                    String rank = card.getRank();
                    String suit = card.getSuit();
                    if (suit.equals("Hearts") || suit.equals("Diamonds")) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.setFont(new Font("Arial", Font.BOLD, 12));
                    g.drawString(rank, x + 5, y + 25);
                    g.drawString(suit, x + 5, y + 55);
                }
            }
        }
    }
}