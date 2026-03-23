import java.util.*;

class Player {
    private String name;
    private int score;
    private int moveCount;
    // Uses a String of "row,col" to track revealed cells
    private HashSet<String> revealedCells; 

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.moveCount = 0;
        this.revealedCells = new HashSet<>();
    }

    // Getters
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getMoveCount() { return moveCount; }
    public HashSet<String> getRevealedCells() { return revealedCells; }

    // Setters (used by loadGame)
    public void setScore(int score) { this.score = score; }
    public void setMoveCount(int count) { this.moveCount = count; }

    // Logic
    public void addScore(int points) { score += points; }
    public void incrementMoves() { moveCount++; }
    
    public void revealCell(int row, int col) {
        revealedCells.add(row + "," + col);
    }
    
    public boolean hasRevealed(int row, int col) {
        return revealedCells.contains(row + "," + col);
    }
}