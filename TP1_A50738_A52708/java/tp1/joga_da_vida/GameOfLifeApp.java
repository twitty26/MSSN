package tp1.joga_da_vida;

import processing.core.PApplet;
import setup.IProcessingApp;

/*
 * Jogo da Vida com cores (critério do nlife-color) e música generativa (ideia do PentatonicGameOfLife).
 * Um cursor percorre as colunas da esquerda para a direita e cada célula viva da coluna toca uma nota.
 * Quando o cursor chega ao fim, o autómato avança uma geração.
 *
 * Rato: clicar liga/desliga uma célula.
 * Teclas: espaço pausa, r aleatório, c limpar, m som, + e - velocidade, 1 regra 23/3, 2 regra 23/36.
 */
public class GameOfLifeApp implements IProcessingApp {

    private static final int NROWS = 24;
    private static final int NCOLS = 32;
    private static final float DENSITY = 0.25f;

    private CellularAutomata ca;
    private LifeMusic music;
    private int cursor;
    private int generation;
    private float stepTime = 0.12f;
    private float timer;
    private boolean paused;

    @Override
    public void setup(PApplet p) {
        int[] palette = {
                p.color(231, 76, 60),
                p.color(46, 204, 113),
                p.color(52, 152, 219),
                p.color(241, 196, 15)
        };
        ca = new CellularAutomata(p, NROWS, NCOLS, "23/3", palette);
        ca.randomize(p, DENSITY);
        music = new LifeMusic(palette.length);
    }

    @Override
    public void draw(PApplet p, float dt) {
        if (!paused) {
            timer += Math.min(dt, 0.25f);
            while (timer >= stepTime) {
                timer -= stepTime;
                tick(p);
            }
        }
        ca.display(p);
        drawCursor(p);
        drawInfo(p);
    }

    private void tick(PApplet p) {
        music.stopAll();
        for (int row = 0; row < NROWS; row++) {
            Cell c = ca.getCell(row, cursor);
            if (c.isAlive()) {
                music.play(row, NROWS, c.getColorIndex());
            }
        }
        cursor++;
        if (cursor == NCOLS) {
            cursor = 0;
            ca.step(p);
            generation++;
        }
    }

    private void drawCursor(PApplet p) {
        p.noStroke();
        p.fill(255, 50);
        p.rect(cursor * ca.getCellWidth(), 0, ca.getCellWidth(), p.height);
    }

    private void drawInfo(PApplet p) {
        p.fill(255);
        p.text("Geração " + generation + "   regra " + ca.getRule()
                + (paused ? "   [pausa]" : "") + (music.isMuted() ? "   [sem som]" : ""), 8, 16);
    }

    @Override
    public void mousePressed(PApplet p) {
        ca.toggle(p, p.mouseX, p.mouseY);
    }

    @Override
    public void keyPressed(PApplet p) {
        switch (p.key) {
            case ' ' -> {
                paused = !paused;
                music.stopAll();
            }
            case 'r' -> ca.randomize(p, DENSITY);
            case 'c' -> ca.clear();
            case 'm' -> music.toggleMute();
            case '+' -> stepTime = Math.max(0.02f, stepTime * 0.8f);
            case '-' -> stepTime = Math.min(1f, stepTime * 1.25f);
            case '1' -> ca.setRule("23/3");
            case '2' -> ca.setRule("23/36");
            default -> {
            }
        }
    }
}