import java.util.*;
import java.io.*;

public class ProjectNova {
    private static final Scanner scanner = new Scanner(System.in); 
    private static GameBoard board;
    private static ArrayList<Player> players;
    private static int currentPlayerIndex;
    private static final String SAVE_DIR = "saves/";

    public static void main(String[] args) {
        // Create saves directory if it doesn't exist
        new File(SAVE_DIR).mkdirs();
        
        showWelcome();
        mainMenu();
    }

    // ============================================
    // Core Game Flow Methods
    // ============================================

    private static void mainMenu() {
        while (true) {
            clearScreen();
            System.out.println("\n╔════════════════════════════╗");
            System.out.println("║       MAIN MENU            ║");
            System.out.println("╚════════════════════════════╝\n");
            System.out.println("  1.  Start New Game");
            System.out.println("  2.  Load Game");
            System.out.println("  3.  Help/Instructions");
            System.out.println("  4.  Exit");
            System.out.print("\n➤ Select option: ");

            int choice = getIntInput(1, 4);
            
            switch (choice) {
                case 1: startNewGame(); break;
                case 2: loadGame(); break;
                case 3: showInstructions(); break;
                case 4: 
                    clearScreen();
                    System.out.println("\n* Thank you for playing Project Nova! *");
                    System.out.println("   Safe travels through the cosmos!\n");
                    System.exit(0);
            }
        }
    }

    private static void startNewGame() {
        clearScreen();
        System.out.println("\n╔════════════════════════════╗");
        System.out.println("║    STARTING NEW GAME       ║");
        System.out.println("╚════════════════════════════╝\n");
        
        // Initialize players
        System.out.print("Enter number of players (1-4): ");
        int numPlayers = getIntInput(1, 4);
        
        players = new ArrayList<>();
        System.out.println();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print(" Enter name for Player " + (i + 1) + ": ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = "Player " + (i + 1);
            players.add(new Player(name));
        }

        // Initialize board
        System.out.print("\n Enter board size (5-10): ");
        int size = getIntInput(5, 10);
        
        System.out.println("\n Initializing space sector...");
        pause(800);
        
        board = new GameBoard(size);
        board.placeCreatures();
        
        System.out.println(" Creatures placed in the cosmos...");
        pause(800);

        // Randomly determine first player
        currentPlayerIndex = new Random().nextInt(numPlayers);
        System.out.println("\n " + players.get(currentPlayerIndex).getName() + " goes first!");
        
        System.out.print("\nPress Enter to begin...");
        scanner.nextLine();

        gameLoop();
    }

    private static void gameLoop() {
        boolean gameRunning = true;

        while (gameRunning) {
            Player currentPlayer = players.get(currentPlayerIndex);
            
            clearScreen();
            System.out.println("\n" + "═".repeat(60));
            System.out.println("   " + currentPlayer.getName() + "'s Turn  |  " +
                             "⭐ Score: " + currentPlayer.getScore() + "  |  " +
                             " Moves: " + currentPlayer.getMoveCount());
            System.out.println("═".repeat(60));
            
            board.displayBoard(currentPlayer);
            
            System.out.println("\n┌─────────────────────────┐");
            System.out.println("│  1.  Select a cell    │");
            System.out.println("│  2.  Save game        │");
            System.out.println("│  3.  Quit to menu     │");
            System.out.println("└─────────────────────────┘");
            System.out.print("\n➤ Choose: ");
            
            int choice = getIntInput(1, 3);
            
            switch (choice) {
                case 1:
                    makeMove(currentPlayer);
                    if (board.allCreaturesFound()) {
                        endGame();
                        gameRunning = false;
                    } else {
                        nextPlayer();
                    }
                    break;
                case 2:
                    saveGame();
                    System.out.print("\nPress Enter to continue...");
                    scanner.nextLine();
                    break;
                case 3:
                    System.out.print("\nAre you sure you want to quit? (y/n): ");
                    String confirm = scanner.nextLine().toLowerCase();
                    if (confirm.equals("y") || confirm.equals("yes")) {
                        gameRunning = false;
                    }
                    break;
            }
        }
    }

    private static void makeMove(Player player) {
        System.out.print("\n Enter row (1-" + board.getSize() + "): ");
        int row = getIntInput(1, board.getSize()) - 1;
        
        System.out.print("\n Enter column (1-" + board.getSize() + "): ");
        int col = getIntInput(1, board.getSize()) - 1;

        System.out.println("\n Scanning coordinates...");
        pause(600);

        int points = board.revealCell(row, col, player);
        
        if (points == -1) {
            System.out.println("\n Already scanned this location! No points.");
        } else if (points > 0) {
            System.out.println("\n CREATURE DETECTED! ");
            System.out.println("    +" + points + " points awarded!");
            player.addScore(points);
        } else {
            System.out.println("\n Empty space. The search continues...");
        }
        
        player.incrementMoves();
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private static void endGame() {
        clearScreen();
        System.out.println("\n" + "═".repeat(60));
        System.out.println("        MISSION COMPLETE! ALL CREATURES FOUND! ");
        System.out.println("═".repeat(60));
        
        // Sort players by score
        players.sort((p1, p2) -> {
            if (p2.getScore() != p1.getScore()) {
                return p2.getScore() - p1.getScore();
            }
            return p1.getMoveCount() - p2.getMoveCount(); // Tie-breaker: fewer moves wins
        });
        
        System.out.println("\n┌────────────────────────────────────────────────┐");
        System.out.println("│              FINAL RANKINGS                    │");
        System.out.println("└────────────────────────────────────────────────┘\n");
        
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            String medal;
            switch (i) {
                case 0: medal = "1st"; break;
                case 1: medal = "2nd"; break;
                case 2: medal = "3rd"; break;
                default: medal = "  ";
            }
            System.out.printf("%s %s: %d points (%d moves)%n", 
                            medal, p.getName(), p.getScore(), p.getMoveCount());
        }
        
        System.out.println("\n Champion: " + players.get(0).getName() + "! ");
        
        board.revealAllCreatures();
        
        System.out.print("\nPress Enter to return to menu...");
        scanner.nextLine();
    }

    // ============================================
    // Save/Load Methods (Manual I/O)
    // ============================================

    private static void saveGame() {
        System.out.print("\n Enter filename to save (without extension): ");
        String filename = scanner.nextLine().trim();
        
        if (filename.isEmpty()) {
            filename = "savegame_" + System.currentTimeMillis();
        }
        
        // Note: Using the manual text-based PrintWriter approach from your original code
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_DIR + filename + ".txt"))) {
            // Save game metadata
            writer.println("PROJECT_NOVA_SAVE_V1");
            writer.println(System.currentTimeMillis()); // Timestamp
            
            // Save board size
            writer.println(board.getSize());
            
            // Save current player index
            writer.println(currentPlayerIndex);
            
            // Save number of players
            writer.println(players.size());
            
            // Save each player
            for (Player p : players) {
                writer.println(p.getName());
                writer.println(p.getScore());
                writer.println(p.getMoveCount());
                // Save revealed cells
                HashSet<String> revealed = p.getRevealedCells();
                writer.println(revealed.size());
                for (String cell : revealed) {
                    writer.println(cell);
                }
            }
            
            // Save board state
            board.saveToFile(writer);
            
            System.out.println("\n Game saved successfully as: " + filename + ".txt");
        } catch (IOException e) {
            System.out.println("\n Error saving game: " + e.getMessage());
        }
    }

    private static void loadGame() {
        clearScreen();
        System.out.println("\n╔════════════════════════════╗");
        System.out.println("║        LOAD GAME           ║");
        System.out.println("╚════════════════════════════╝\n");
        
        // List available save files
        File saveDir = new File(SAVE_DIR);
        File[] saveFiles = saveDir.listFiles((dir, name) -> name.endsWith(".txt"));
        
        if (saveFiles == null || saveFiles.length == 0) {
            System.out.println(" No saved games found.");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println(" Available save files:\n");
        for (int i = 0; i < saveFiles.length; i++) {
            System.out.println("  " + (i + 1) + ". " + saveFiles[i].getName());
        }
        
        System.out.print("\n➤ Select file number (0 to cancel): ");
        int fileChoice = getIntInput(0, saveFiles.length);
        
        if (fileChoice == 0) return;
        
        String filename = saveFiles[fileChoice - 1].getName();
        
        // Note: Using the manual text-based BufferedReader approach from your original code
        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_DIR + filename))) {
            // Verify save format
            String format = reader.readLine();
            if (!format.equals("PROJECT_NOVA_SAVE_V1")) {
                System.out.println("\n Invalid save file format!");
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            reader.readLine(); // Skip timestamp
            
            // Load board size
            int size = Integer.parseInt(reader.readLine());
            
            // Load current player
            currentPlayerIndex = Integer.parseInt(reader.readLine());
            
            // Load players
            int numPlayers = Integer.parseInt(reader.readLine());
            players = new ArrayList<>();
            
            for (int i = 0; i < numPlayers; i++) {
                String name = reader.readLine();
                int score = Integer.parseInt(reader.readLine());
                int moveCount = Integer.parseInt(reader.readLine());
                
                Player p = new Player(name);
                p.setScore(score);
                p.setMoveCount(moveCount);
                
                // Load revealed cells
                int revealedCount = Integer.parseInt(reader.readLine());
                for (int j = 0; j < revealedCount; j++) {
                    String cell = reader.readLine();
                    String[] coords = cell.split(",");
                    p.revealCell(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                }
                
                players.add(p);
            }
            
            // Load board
            board = new GameBoard(size);
            board.loadFromFile(reader);
            
            System.out.println("\n Game loaded successfully!");
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
            
            gameLoop();
            
        } catch (IOException | NumberFormatException e) {
            // This catches file reading errors and errors from parseInt (corrupted file)
            System.out.println("\n Error loading game (File may be corrupted): " + e.getMessage());
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // ============================================
    // Utility Methods
    // ============================================
    
    // (showWelcome, showInstructions, getIntInput, clearScreen, pause methods)
    // The implementation of these methods from your original file remains unchanged here
    
    private static void showWelcome() {
        clearScreen();
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║                                               ║");
        System.out.println("║         PROJECT NOVA: SPACE HUNT              ║");
        System.out.println("║                                               ║");
        System.out.println("║      Ancient Strategy Meets Future Tech       ║");
        System.out.println("║                                               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        System.out.println();
        pause(1000);
    }

    private static void showInstructions() {
        clearScreen();
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║              HOW TO PLAY                      ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        System.out.println("\n OBJECTIVE:");
        System.out.println("   Find all hidden space creatures on the board!");
        
        System.out.println("\n RULES:");
        System.out.println("   Players take turns selecting grid coordinates");
        System.out.println("   Find creature cells to earn points");
        System.out.println("   Different creatures are worth different points:");
        System.out.println("     - Cosmic Moth: 2 cells = 5 points");
        System.out.println("     - Nebula Beast: 3 cells = 10 points");
        System.out.println("     - Star Serpent: 3 cells = 10 points");
        System.out.println("     - Void Walker: 4 cells = 15 points");
        System.out.println("   Game ends when all creatures are found");
        System.out.println("   Highest score wins!");
        System.out.println("   Ties broken by fewest moves");
        
        System.out.println("\n BOARD SYMBOLS:");
        System.out.println("   ? = Unexplored space");
        System.out.println("   C = Creature found!");
        System.out.println("   · = Empty space");
        
        System.out.println("\n FEATURES:");
        System.out.println("   • Save your game anytime during play");
        System.out.println("   • Load and continue previous games");
        System.out.println("   • Play with 1-4 players");
        System.out.println("   • Customizable board size (5x5 to 10x10)");
        
        System.out.print("\n Press Enter to return to menu...");
        scanner.nextLine();
    }

    private static int getIntInput(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.print("  Please enter a number between " + min + " and " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("  Invalid input. Please enter a number: ");
            }
        }
    }

    private static void clearScreen() {
        // Simulated clear - prints newlines
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private static void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
