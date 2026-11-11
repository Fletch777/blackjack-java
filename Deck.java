import java.util.ArrayList;
import java.util.Collections;

public class Deck
{
    
    // attributes
    private ArrayList<Card> cards;

    // constructor
    public Deck()
    {
        this.cards = new ArrayList<>();

        for (int i = 0; i < 4; i++) // 4 outer loops for each suit
        {
            String currentSuit = "";
            switch(i)
            {
                case 0 -> //hearts
                    currentSuit = "Hearts";

                case 1 -> //diamonds
                    currentSuit = "Diamonds";

                case 2 -> //spades
                    currentSuit = "Spades";

                case 3 -> //clubs
                    currentSuit = "Clubs";
            }
            // after suit is determined make its 13 cards
            for (int j = 1; j <= 13; j++)
                {
                    String rank;
                    int value;

                    switch (j)
                    {
                        case 1 -> {
                            // ace
                            rank = "Ace";
                            value = 11;
                    }
                        case 11 -> {
                            // jack
                            rank = "Jack";
                            value = 10;
                    }
                        case 12 -> {
                            // queen
                            rank = "Queen";
                            value = 10;
                    }
                        case 13 -> {
                            // king
                            rank = "King";
                            value = 10;
                    }
                        default -> {
                            // 2-10
                            rank = String.valueOf(j);
                            value = j;
                    }
                    }
                    // construct card
                    this.cards.add(new Card(currentSuit, rank, value));
                }
        }
    
    }
    // methods
    public void shuffle() // shuffles the deck
    {
        Collections.shuffle(this.cards);
    }
    public Card draw() // pops a card off the deck
    {
        // pop from the back end of the list to avoid moving everything over
        return this.cards.remove(this.cards.size() - 1); 
    }
}
