package tp1.celulas;

import processing.core.PApplet;

/*
 * Grelha de células quadradas que ocupa a janela toda.
 * Vizinhança de Moore (8 vizinhas: horizontal, vertical e diagonal).
 * O jogo passa-se num arranjo infinito, que o computador não consegue guardar. A aproximação habitual
 * é ligar as bordas (toro): o que sai por um lado entra pelo lado oposto, por isso nenhuma célula
 * está "na borda" e todas têm sempre 8 vizinhas.
 */
public class CellularAutomata {

    private final int nrows;
    private final int ncols;
    private final int cellSize;
    private final int[] palette;
    private final Cell[][] cells;

    public CellularAutomata(PApplet p, int cellSize, int[] palette) {
        this.cellSize = cellSize;
        this.palette = palette;
        nrows = p.height / cellSize;
        ncols = p.width / cellSize;
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

    private boolean inside(int row, int col) {
        return row >= 0 && row < nrows && col >= 0 && col < ncols;
    }

    public void randomize(PApplet p, float density) {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                c.setState(p.random(1) < density, (int) p.random(palette.length));
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
     * Pincel do rato: à volta de (x, y) dá vida a cerca de metade das células,
     * todas com a mesma cor.
     */
    public void paint(PApplet p, float x, float y, int radius) {
        int row = (int) (y / cellSize);
        int col = (int) (x / cellSize);
        int colorIndex = (int) p.random(palette.length);
        for (int i = row - radius; i <= row + radius; i++) {
            for (int j = col - radius; j <= col + radius; j++) {
                if (inside(i, j) && p.random(1) < 0.5f) {
                    cells[i][j].setState(true, colorIndex);
                }
            }
        }
    }

    /*
     * Regras do Jogo da Vida (23/3), aplicadas a todas as células ao mesmo tempo:
     * 1. célula morta com exatamente 3 vizinhas vivas torna-se viva (nascimento);
     * 2. célula viva com menos de 2 vizinhas vivas morre (isolamento);
     * 3. célula viva com mais de 3 vizinhas vivas morre (superpopulação);
     * 4. célula viva com 2 ou 3 vizinhas vivas continua viva.
     * Para serem simultâneas, primeiro calcula-se o próximo estado de todas as
     * células e só depois
     * se atualizam; senão as primeiras células a mudar estragavam a contagem das
     * seguintes.
     */
    public void step(PApplet p) {
        for (Cell[] row : cells) {
            for (Cell c : row) {
                int n = c.countAliveNeighbors();
                if (!c.isAlive() && n == 3) {
                    c.setNext(true, c.dominantNeighborColor(p, palette.length));
                } else if (c.isAlive() && (n < 2 || n > 3)) {
                    c.setNext(false, c.getColorIndex());
                } else {
                    c.setNext(c.isAlive(), c.getColorIndex());
                }
            }
        }
        for (Cell[] row : cells) {
            for (Cell c : row) {
                c.update();
            }
        }
    }

    public void display(PApplet p) {
        p.noStroke();
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                Cell c = cells[i][j];
                if (c.isAlive()) {
                    p.fill(palette[c.getColorIndex()]);
                    p.rect(j * cellSize, i * cellSize, cellSize, cellSize);
                }
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
}