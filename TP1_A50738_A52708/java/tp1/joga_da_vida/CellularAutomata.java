package tp1.joga_da_vida;

import processing.core.PApplet;

/*
 * Grelha de células que ocupa a janela toda.
 * Vizinhança de Moore (8 vizinhas) e bordas ligadas: o que sai de um lado entra pelo outro.
 * Regra clássica do Jogo da Vida (23/3): uma célula viva sobrevive com 2 ou 3 vizinhas vivas,
 * uma célula morta nasce com exatamente 3 vizinhas vivas.
 */
public class CellularAutomata {

    private final int nrows;
    private final int ncols;
    private final float cellWidth;
    private final float cellHeight;
    private final int[] palette;
    private final Cell[][] cells;

    public CellularAutomata(PApplet p, int nrows, int ncols, int[] palette) {
        this.nrows = nrows;
        this.ncols = ncols;
        this.palette = palette;
        cellWidth = (float) p.width / ncols;
        cellHeight = (float) p.height / nrows;
        cells = new Cell[nrows][ncols];
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                cells[i][j] = new Cell();
            }
        }
        setMooreNeighbors();
    }

    private void setMooreNeighbors() {
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell[] neighbors = new Cell[8];
                int k = 0;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        if (di == 0 && dj == 0) {
                            continue;
                        }
                        int row = (i + di + nrows) % nrows;
                        int col = (j + dj + ncols) % ncols;
                        neighbors[k++] = cells[row][col];
                    }
                }
                cells[i][j].setNeighbors(neighbors);
            }
        }
    }

    public void randomize(PApplet p, float density) {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                c.setState(p.random(1) < density, randomColor(p));
            }
        }
    }

    public void clear() {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                c.setState(false, 0);
            }
        }
    }

    /*
     * Primeiro calcula-se o próximo estado de todas as células e só depois se atualizam,
     * senão as primeiras células a mudar estragavam a contagem das seguintes.
     */
    public void step(PApplet p) {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                int n = c.countAliveNeighbors();
                if (c.isAlive()) {
                    c.setNext(n == 2 || n == 3, c.getColorIndex());
                } else if (n == 3) {
                    c.setNext(true, c.dominantNeighborColor(p, palette.length));
                } else {
                    c.setNext(false, c.getColorIndex());
                }
            }
        }
        for (Cell[] row : cells) {
            for (Cell c : row) {
                c.update();
            }
        }
    }

    public void toggle(PApplet p, float x, float y) {
        int row = (int) (y / cellHeight);
        int col = (int) (x / cellWidth);
        if (row < 0 || row >= nrows || col < 0 || col >= ncols) {
            return;
        }
        Cell c = cells[row][col];
        c.setState(!c.isAlive(), randomColor(p));
    }

    private int randomColor(PApplet p) {
        return (int) p.random(palette.length);
    }

    public void display(PApplet p) {
        p.stroke(20);
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell c = cells[i][j];
                if (c.isAlive()) {
                    p.fill(palette[c.getColorIndex()]);
                } else {
                    p.fill(40);
                }
                p.rect(j * cellWidth, i * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public int getNrows() {
        return nrows;
    }

    public int getNcols() {
        return ncols;
    }

    public float getCellWidth() {
        return cellWidth;
    }
}