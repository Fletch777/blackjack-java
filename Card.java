public class Card
{
    String suit;
    String rank;
    int value;

    public Card(String suit, String rank, int value)
    {
        this.suit = suit; // spade, club, heart, diamond
        this.rank = rank; // ace, two, three, queen, etc.
        this.value = value; // integer from [1, 11]
    }

    public int getValue()
    {
        return value;
    }

    public String toString()
    {
        return rank + " of " + suit;
    }
}