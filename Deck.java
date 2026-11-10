import java.util.ArrayList;
import java.util.Collections;

public class Deck
{
    
    // attributes
    private ArrayList<Card> cards;

    // constructor
    public Deck()
    {
        this.cards = new ArrayList<Card>();

        for (int i = 0; i < 4; i++) // 4 outer loops for each suit
        {
            String currentSuit = "";
            switch(i)
            {
                case 0: //hearts
                    currentSuit = "Hearts";
                    break;

                case 1: //diamonds
                    currentSuit = "Diamonds";
                    break;

                case 2: //spades
                    currentSuit = "Spades";
                    break;

                case 3: //clubs
                    currentSuit = "Clubs";
                    break;
            }
            // after suit is determined make its 13 cards
            for (int j = 1; j <= 13; j++)
                {
                    String rank;
                    int value;

                    if (j == 1) // index 1 is ace
                    {
                        rank = "Ace";
                        value = 11;
                    }
                    else if (j == 11) // 11 is jack
                    {
                        rank = "Jack";
                        value = 10;
                    }
                    else if (j == 12) // 12 is queen
                    {
                        rank = "Queen";
                        value = 10;
                    }
                    else if (j == 13) // 13 is king
                    {
                        rank = "King";
                        value = 10;
                    }
                    else // 2-10 handled normally
                    {
                        rank = String.valueOf(j);
                        value = j;
                    }
                    // construct heart card
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
