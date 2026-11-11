import java.util.ArrayList;


public class Hand
{   
    // attributes
    private final ArrayList<Card> currentHand;

    // constructor
    public Hand()
    {
        this.currentHand = new ArrayList<>();
    }

    // methods
    public int getSize() {
        return this.currentHand.size();
    }

    public Card getCard(int index) {
        return this.currentHand.get(index);
    }

    public void addCard(Card card)
    {
        this.currentHand.add(card);
    }

    public int calculateScore()
    {
        int totalScore = 0;
        int aceCount = 0;
        for (int i = 0; i < currentHand.size(); i++)
        {
            // index through currentHand, adding up value of each card and counting up number of aces
            totalScore += currentHand.get(i).getValue();
            if (currentHand.get(i).getRank().equals("Ace"))
            {
                aceCount++;
            }
        }
        while (totalScore > 21 && aceCount > 0)
        {
            totalScore -= 10;
            aceCount--;
        }
        return totalScore;
    }
}