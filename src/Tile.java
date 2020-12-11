
public enum Tile {

    EMPTY(0), RED(1), YELLOW(2);
    private final int i;

    public int getI() {
        return i;
    }

    Tile(int i) {
        this.i = i;
    }
}
