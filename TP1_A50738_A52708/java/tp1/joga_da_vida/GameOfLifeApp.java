package tp1.joga_da_vida;

import processing.core.PApplet;
import setup.IProcessingApp;

/*
 * Jogo da Vida (23/3) com cores (critério do nlife-color) e música generativa (ideia do PentatonicGameOfLife).
 *
 * Visual: fundo preto e células pequenas. No início cada célula viva recebe uma cor ao acaso; quando uma
 * célula nasce herda a cor mais comum das vizinhas. Assim formam-se zonas de cor que lutam entre si.
 *
 * Música: um cursor percorre a janela em BAND_STEPS faixas verticais, ao seu próprio ritmo
 * (independente da velocidade da simulação), e toca as células vivas de cada faixa.
 *
 * Rato: clicar pinta uma mancha de células vivas com uma cor ao acaso.
 * Teclas: espaço pausa, r recomeçar aleatório, c limpar, m som, + e - velocidade da simulação.
 */
public class GameOfLifeApp implements IProcessingApp {

    private static final int CELL_SIZE = 4;
    private static final float DENSITY = 0.25f;
    private static final int BRUSH_RADIUS = 4;

    private static final int BAND_STEPS = 16;
    private static final int BEAT = 4; // de 4 em 4 faixas há um tempo forte
    private static final float MUSIC_STEP_TIME = 0.15f;

    private CellularAutomata ca;
    private LifeMusic music;
    private int generation;
    private float generationTime = 0.05f;
    private float simTimer;
    private float musicTimer;
    private int band;
    private int bar;
    private boolean paused;

    @Override
    public void setup(PApplet p) {
        int[] palette = {
                p.color(30, 60, 255),
                p.color(0, 230, 0),
                p.color(255, 230, 0),
                p.color(255, 30, 30),
                p.color(230, 120, 230)
        };
        ca = new CellularAutomata(p, CELL_SIZE, palette);
        music = new LifeMusic(palette.length);
        restart(p);
    }

    private void restart(PApplet p) {
        ca.randomize(p, DENSITY);
        generation = 0;
    }

    @Override
    public void draw(PApplet p, float dt) {
        dt = Math.min(dt, 0.25f);
        if (!paused) {
            simTimer += dt;
            while (simTimer >= generationTime) {
                simTimer -= generationTime;
                ca.step(p);
                generation++;
            }
            musicTimer += dt;
            while (musicTimer >= MUSIC_STEP_TIME) {
                musicTimer -= MUSIC_STEP_TIME;
                playNextBand();
            }
        }
        p.background(0);
        ca.display(p);
        drawCursor(p);
        drawInfo(p);
    }

    private void playNextBand() {
        if (band == 0) {
            music.playChord(bar);
        }
        int ncols = ca.getNcols();
        music.playBand(ca, band * ncols / BAND_STEPS, (band + 1) * ncols / BAND_STEPS, band % BEAT == 0);
        band++;
        if (band == BAND_STEPS) {
            band = 0;
            bar++;
        }
    }

    private void drawCursor(PApplet p) {
        float width = (float) p.width / BAND_STEPS;
        p.noStroke();
        p.fill(255, 20);
        p.rect(band * width, 0, width, p.height);
    }

    private void drawInfo(PApplet p) {
        p.fill(255);
        p.text("Geração " + generation + (paused ? "   [pausa]" : "")
                + (music.isMuted() ? "   [sem som]" : ""), 8, 16);
    }

    @Override
    public void mousePressed(PApplet p) {
        ca.paint(p, p.mouseX, p.mouseY, BRUSH_RADIUS);
    }

    @Override
    public void keyPressed(PApplet p) {
        switch (p.key) {
            case ' ' -> {
                paused = !paused;
                music.stopAll();
            }
            case 'r' -> restart(p);
            case 'c' -> ca.clear();
            case 'm' -> music.toggleMute();
            case '+' -> generationTime = Math.max(0.01f, generationTime * 0.8f);
            case '-' -> generationTime = Math.min(1f, generationTime * 1.25f);
            default -> {
            }
        }
    }
}