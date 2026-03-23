import java.util.*;

class Creature {
    private String name;
    private int size;
    private int points;
    private int foundCount;
    // Stores the (row, col) coordinates occupied by this creature
    private ArrayList<int[]> positions; 

    public Creature(String name, int size, int points) {
        this.name = name;
        this.size = size;
        this.points = points;
        this.foundCount = 0;
        this.positions = new ArrayList<>();
    }

    // Getters
    public String getName() { return name; }
    public int getSize() { return size; }
    public int getPoints() { return points; }
    public int getFoundCount() { return foundCount; }
    public ArrayList<int[]> getPositions() { return positions; }

    // Setters (used by loadGame)
    public void setFoundCount(int count) { foundCount = count; }

    // Logic
    public void addPosition(int row, int col) {
        positions.add(new int[]{row, col});
    }
    
    public String getPositionsString() {
        // Helper method to display coordinates 
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < positions.size(); i++) {
            int[] pos = positions.get(i);
            // Convert 0-indexed to 1-indexed for display
            sb.append("(").append(pos[0] + 1).append(",").append(pos[1] + 1).append(")"); 
            if (i < positions.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
    
    public void incrementFound() { foundCount++; }
    
    public boolean isCompletelyFound() {
        return foundCount >= size;
    }
}
