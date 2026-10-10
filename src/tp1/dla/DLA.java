package tp1.dla;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;

/*
 * DLA (Diffusion-Limited Aggregation): partículas em passeio aleatório agarram-se a um agregado
 * que começa nas sementes. O número de partículas em movimento é sempre NUM_WALKERS: sempre que
 * uma pára, cria-se logo uma nova.
 *
 * Inicializações (sementes): 1 ponto, 2 linha horizontal a meio, 3 círculo, 4 quadrado; clicar acrescenta sementes
 * (formas arbitrárias desenhadas com o rato).
 * Teclas: 1-4 forma, r recomeçar, + e - stickiness.
 */
public class DLA implements IProcessingApp {

    private enum Shape {
        POINT, LINE, CIRCLE, SQUARE
    }

    private static final int NUM_WALKERS = 200;
    private static final float STEP_TIME = 0.001f;

    private List<Walker> walkers;
    private Shape shape = Shape.POINT;
    private float stickiness = 1f;
    private float timer;

    @Override
    public void setup(PApplet p) {
        p.colorMode(PApplet.HSB, 360, 100, 100);
        restart(p);
    }

    private void restart(PApplet p) {
        walkers = new ArrayList<>();
        Walker.num_wanders = 0;
        Walker.num_stopped = 0;
        createSeeds(p);
        for (int i = 0; i < NUM_WALKERS; i++) {
            walkers.add(new Walker(p));
        }
    }

    /*
     * As formas são feitas de sementes encostadas umas às outras (distância de um diâmetro).
     */
    private void createSeeds(PApplet p) {
        float cx = p.width / 2f;
        float cy = p.height / 2f;
        int d = Walker.getDiameter();
        switch (shape) {
            case POINT -> addSeed(p, cx, cy);
            case LINE -> {
                for (int x = 0; x <= p.width; x += d) {
                    addSeed(p, x, cy);
                }
            }
            case CIRCLE -> {
                float r = 200;
                int n = (int) (PApplet.TWO_PI * r / d);
                for (int i = 0; i < n; i++) {
                    float a = PApplet.TWO_PI * i / n;
                    addSeed(p, cx + r * PApplet.cos(a), cy + r * PApplet.sin(a));
                }
            }
            case SQUARE -> {
                float half = 150;
                for (float t = -half; t <= half; t += d) {
                    addSeed(p, cx + t, cy - half);
                    addSeed(p, cx + t, cy + half);
                    addSeed(p, cx - half, cy + t);
                    addSeed(p, cx + half, cy + t);
                }
            }
        }
    }

    private void addSeed(PApplet p, float x, float y) {
        walkers.add(new Walker(p, new PVector(x, y)));
    }

    @Override
    public void draw(PApplet p, float dt) {
        timer += Math.min(dt, 0.25f);
        while (timer >= STEP_TIME) {
            timer -= STEP_TIME;
            step(p);
        }
        p.background(0);
        p.noStroke();
        for (Walker w : walkers) {
            w.display(p);
        }
        p.fill(0, 0, 100);
        p.text("Forma: " + shape + "   stickiness: " + PApplet.nf(stickiness, 1, 2)
                + "   paradas: " + Walker.num_stopped + "   em movimento: " + Walker.num_wanders, 8, 16);
    }

    /*
     * Usa-se um for com índice (e não for-each) porque se acrescentam walkers à lista durante o ciclo.
     */
    private void step(PApplet p) {
        for (int i = 0; i < walkers.size(); i++) {
            Walker w = walkers.get(i);
            if (w.getState() == Walker.State.WANDER) {
                w.wander(p);
                w.updateState(p, walkers, stickiness);
                if (w.getState() == Walker.State.STOPPED) {
                    walkers.add(new Walker(p));
                }
            }
        }
    }

    @Override
    public void mousePressed(PApplet p) {
        addSeed(p, p.mouseX, p.mouseY);
    }

    @Override
    public void keyPressed(PApplet p) {
        switch (p.key) {
            case '1' -> setShape(p, Shape.POINT);
            case '2' -> setShape(p, Shape.LINE);
            case '3' -> setShape(p, Shape.CIRCLE);
            case '4' -> setShape(p, Shape.SQUARE);
            case 'r' -> restart(p);
            case '+' -> stickiness = Math.min(1f, stickiness * 2);
            case '-' -> stickiness = Math.max(0.01f, stickiness / 2);
            default -> {
            }
        }
    }

    private void setShape(PApplet p, Shape shape) {
        this.shape = shape;
        restart(p);
    }
}
