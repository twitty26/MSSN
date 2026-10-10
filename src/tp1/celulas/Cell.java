package tp1.celulas;

import processing.core.PApplet;

/*
 * Uma célula do autómato: sabe o seu estado, a sua cor e quem são as vizinhas.
 * O próximo estado é guardado à parte para que todas as células mudem ao mesmo tempo.
 */
public class Cell {

    private boolean alive;
    private int colorIndex;
    private boolean nextAlive;
    private int nextColorIndex;
    private Cell[] neighbors;

    public void setNeighbors(Cell[] neighbors) {
        this.neighbors = neighbors;
    }

    public int countAliveNeighbors() {
        int count = 0;
        for (Cell n : neighbors) {
            if (n.alive) {
                count++;
            }
        }
        return count;
    }

    /*
     * Regra do nlife-color: a célula que nasce herda a cor mais comum entre as
     * vizinhas vivas.
     * Em caso de empate escolhe-se uma das cores empatadas ao acaso.
     */
    public int dominantNeighborColor(PApplet p, int ncolors) {
        int[] counts = new int[ncolors];
        for (Cell n : neighbors) {
            if (n.alive) {
                counts[n.colorIndex]++;
            }
        }
        int best = 0;
        int ties = 0;
        for (int i = 0; i < ncolors; i++) {
            if (counts[i] > counts[best]) {
                best = i;
                ties = 1;
            } else if (counts[i] == counts[best]) {
                ties++;
                if (p.random(ties) < 1) {
                    best = i;
                }
            }
        }
        return best;
    }

    public void setNext(boolean alive, int colorIndex) {
        nextAlive = alive;
        nextColorIndex = colorIndex;
    }

    public void update() {
        alive = nextAlive;
        colorIndex = nextColorIndex;
    }

    public void setState(boolean alive, int colorIndex) {
        this.alive = alive;
        this.colorIndex = colorIndex;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getColorIndex() {
        return colorIndex;
    }
}