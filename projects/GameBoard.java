import java.util.*;
import java.io.*;

class GameBoard {
    private int size;
    private Cell[][] grid;
    private ArrayList<Creature> creatures;
    private int totalCreatureCells;
    private int foundCreatureCells;

    public GameBoard(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        this.creatures = new ArrayList<>();
        this.totalCreatureCells = 0;
        this.foundCreatureCells = 0;
        
        // Initialize empty grid
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    public int getSize() { 
        return size; 
    }

    public void placeCreatures() {
        Random rand = new Random();
        // Creature definitions based on your instruction list
        String[] creatureNames = {"Nebula Beast", "Star Serpent", "Void Walker", "Cosmic Moth"};
        int[] creatureSizes = {3, 3, 4, 2};
        int[] creaturePoints = {10, 10, 15, 5};

        // Attempt to place a variety of creatures
        for (int i = 0; i < creatureNames.length && i < size - 1; i++) {
            boolean placed = false;
            int attempts = 0;
            
            while (!placed && attempts < 100) {
                int row = rand.nextInt(size);
                int col = rand.nextInt(size);
                boolean horizontal = rand.nextBoolean();
                
                if (canPlaceCreature(row, col, creatureSizes[i], horizontal)) {
                    Creature creature = new Creature(creatureNames[i], creatureSizes[i], creaturePoints[i]);
                    placeCreature(creature, row, col, horizontal);
                    creatures.add(creature);
                    totalCreatureCells += creatureSizes[i];
                    placed = true;
                }
                attempts++;
            }
        }
    }

    private boolean canPlaceCreature(int row, int col, int size, boolean horizontal) {
        if (horizontal) {
            if (col + size > this.size) return false;
            for (int i = 0; i < size; i++) {
                if (grid[row][col + i].hasCreature()) return false;
            }
        } else {
            if (row + size > this.size) return false;
            for (int i = 0; i < size; i++) {
                if (grid[row + i][col].hasCreature()) return false;
            }
        }
        return true;
    }

    private void placeCreature(Creature creature, int row, int col, boolean horizontal) {
        for (int i = 0; i < creature.getSize(); i++) {
            if (horizontal) {
                grid[row][col + i].setCreature(creature);
                creature.addPosition(row, col + i);
            } else {
                grid[row + i][col].setCreature(creature);
                creature.addPosition(row + i, col);
            }
        }
    }

    public void displayBoard(Player player) {
        // ... (Board drawing logic)
        System.out.print("\n     ");
        for (int i = 0; i < size; i++) {
            System.out.print((i + 1) + "   ");
        }
        System.out.println();
        
        System.out.print("   ┌");
        for (int i = 0; i < size; i++) {
            System.out.print("───" + (i < size - 1 ? "┬" : ""));
        }
        System.out.println("┐");
        
        for (int i = 0; i < size; i++) {
            System.out.printf(" %d │", (i + 1));
            for (int j = 0; j < size; j++) {
                if (player.hasRevealed(i, j)) {
                    if (grid[i][j].hasCreature()) {
                        System.out.print(" C ");
                    } else {
                        System.out.print(" · ");
                    }
                } else {
                    System.out.print(" ? ");
                }
                if (j < size - 1) System.out.print("│");
            }
            System.out.println("│");
            
            if (i < size - 1) {
                System.out.print("   ├");
                for (int j = 0; j < size; j++) {
                    System.out.print("───" + (j < size - 1 ? "┼" : ""));
                }
                System.out.println("┤");
            }
        }
        
        System.out.print("   └");
        for (int i = 0; i < size; i++) {
            System.out.print("───" + (i < size - 1 ? "┴" : ""));
        }
        System.out.println("┘");
        
        // Show creatures status
        System.out.println("\n🛸 Creatures Status:");
        for (Creature c : creatures) {
            int found = c.getFoundCount();
            int total = c.getSize();
            String bar = "█".repeat(found) + "░".repeat(total - found);
            String status = c.isCompletelyFound() ? "✓ COMPLETE" : found + "/" + total;
            System.out.printf("  %s: [%s] %s%n", c.getName(), bar, status);
        }
    }

    public void revealAllCreatures() {
        System.out.println("\n🗺️  FINAL CREATURE MAP:");
        System.out.println();
        
        for (Creature c : creatures) {
            System.out.println("  " + c.getName() + " locations: " + c.getPositionsString());
        }
    }

    public int revealCell(int row, int col, Player player) {
        if (player.hasRevealed(row, col)) {
            return -1;
        }
        
        player.revealCell(row, col);
        
        if (grid[row][col].hasCreature() && !grid[row][col].isRevealed()) {
            grid[row][col].reveal();
            Creature creature = grid[row][col].getCreature();
            creature.incrementFound();
            foundCreatureCells++;
            
            if (creature.isCompletelyFound()) {
                System.out.println("\n🌟🌟🌟 COMPLETE CREATURE FOUND! 🌟🌟🌟");
                System.out.println("     " + creature.getName() + " fully discovered!");
            }
            
            // Return points for the specific creature type
            return creature.getPoints(); 
        }
        
        return 0;
    }

    public boolean allCreaturesFound() {
        return foundCreatureCells >= totalCreatureCells;
    }

    // ============================================
    // Manual Save/Load Methods for GameBoard
    // ============================================

    public void saveToFile(PrintWriter writer) {
        // Save total and found creature cells
        writer.println(totalCreatureCells);
        writer.println(foundCreatureCells);
        
        // Save creatures count
        writer.println(creatures.size());
        
        // Save each creature
        for (Creature c : creatures) {
            writer.println(c.getName());
            writer.println(c.getSize());
            writer.println(c.getPoints());
            writer.println(c.getFoundCount());
            
            // Save creature positions
            ArrayList<int[]> positions = c.getPositions();
            for (int[] pos : positions) {
                writer.println(pos[0] + "," + pos[1]);
            }
        }
        
        // Save grid revealed state
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // If the cell was revealed by *anyone*
                writer.print(grid[i][j].isRevealed() ? "1" : "0"); 
                if (j < size - 1) writer.print(",");
            }
            writer.println();
        }
    }

    public void loadFromFile(BufferedReader reader) throws IOException {
        // Load creature cell counts
        totalCreatureCells = Integer.parseInt(reader.readLine());
        foundCreatureCells = Integer.parseInt(reader.readLine());
        
        // Load creatures
        int numCreatures = Integer.parseInt(reader.readLine());
        
        for (int i = 0; i < numCreatures; i++) {
            String name = reader.readLine();
            int size = Integer.parseInt(reader.readLine());
            int points = Integer.parseInt(reader.readLine());
            int foundCount = Integer.parseInt(reader.readLine());
            
            Creature c = new Creature(name, size, points);
            c.setFoundCount(foundCount);
            
            // Load creature positions and place on board
            for (int j = 0; j < size; j++) {
                String[] coords = reader.readLine().split(",");
                int row = Integer.parseInt(coords[0]);
                int col = Integer.parseInt(coords[1]);
                grid[row][col].setCreature(c);
                c.addPosition(row, col);
            }
            
            creatures.add(c);
        }
        
        // Load grid revealed state
        for (int i = 0; i < this.size; i++) {
            String[] revealedStates = reader.readLine().split(",");
            for (int j = 0; j < this.size; j++) {
                if (revealedStates[j].equals("1")) {
                    grid[i][j].reveal();
                }
            }
        }
    }
}