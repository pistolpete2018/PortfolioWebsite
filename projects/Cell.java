class Cell {
    private Creature creature;
    private boolean revealed;

    public Cell() {
        this.creature = null;
        this.revealed = false;
    }

    public boolean hasCreature() { return creature != null; }
    public Creature getCreature() { return creature; }
    public boolean isRevealed() { return revealed; }
    
    public void setCreature(Creature creature) { this.creature = creature; }
    public void reveal() { revealed = true; }
  
}  