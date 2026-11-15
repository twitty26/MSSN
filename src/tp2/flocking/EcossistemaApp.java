package tp2.flocking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tp2.agentes.Boid;

/*
 * E.5 Ecossistema predador-presa que se regula sozinho.
 * Presas (azuis): flock que foge do predador mais próximo e se reproduz (cada presa tem uma probabilidade
 * PREY_BIRTH_RATE por segundo de ter uma cria), até MAX_PREYS.
 * Predadores (vermelhos): perseguem a presa mais próxima que veem; gastam 1 de energia por segundo, ganham
 * ENERGY_PER_PREY ao comer, dividem-se em dois com SPLIT_ENERGY e morrem com energia 0.
 * Muitas presas -> os predadores comem e multiplicam-se -> as presas diminuem -> os predadores passam fome e
 * morrem -> as presas voltam a crescer. O gráfico em baixo mostra as duas populações ao longo do tempo.
 * Rato: clicar lança um predador novo. Teclas: r recomeça.
 */
public class EcossistemaApp implements IProcessingApp {

    private static final int N_PREYS = 80;
    private static final int N_PREDATORS = 3;
    private static final int MAX_PREYS = 250;
    private static final float PREY_BIRTH_RATE = 0.15f;
    private static final float PANIC_RADIUS = 100;
    private static final float HUNT_RADIUS = 160;
    private static final float START_ENERGY = 8;
    private static final float ENERGY_PER_PREY = 2;
    private static final float SPLIT_ENERGY = 20;
    private static final float HISTORY_STEP = 0.5f;
    private static final int HISTORY_SIZE = 300;
    private static final int PREDATOR_SCALE = 10;

    private Flock preys;
    private final List<Predator> predators = new ArrayList<>();
    private final List<int[]> history = new ArrayList<>();
    private float historyTimer;
    private float time;
    private int preyColor;
    private int predatorColor;

    @Override
    public void setup(PApplet p) {
        preyColor = p.color(80, 200, 255);
        predatorColor = p.color(255, 60, 60);
        reset(p);
    }

    private void reset(PApplet p) {
        predators.clear();
        history.clear();
        historyTimer = 0;
        time = 0;
        preys = new Flock(p, 0, preyColor);
        for (int i = 0; i < N_PREYS; i++) {
            addPrey(new PVector(p.random(p.width), p.random(p.height)));
        }
        for (int i = 0; i < N_PREDATORS; i++) {
            predators.add(new Predator(new PVector(p.random(p.width), p.random(p.height)), START_ENERGY,
                    predatorColor));
        }
    }

    private void addPrey(PVector pos) {
        Boid b = preys.addBoid(pos);
        b.addBehavior(new Evade(predators, PANIC_RADIUS, 0.5f, 3));
    }

    @Override
    public void draw(PApplet p, float dt) {
        dt = Math.min(dt, 0.1f);
        time += dt;
        updatePreys(p, dt);
        updatePredators(p, dt);
        recordHistory(dt);

        p.background(20);
        preys.display(p);
        for (Predator predator : predators) {
            predator.display(p);
        }
        drawGraph(p);
        p.fill(255);
        String text = String.format("Presas: %d   Predadores: %d   Tempo: %.0f s   clique: novo predador   r recomeça",
                preys.getBoids().size(), predators.size(), time);
        if (predators.isEmpty()) {
            text += "   [predadores extintos]";
        }
        p.text(text, 8, 16);
    }

    /*
     * Reprodução das presas: percorre-se uma cópia da lista porque as crias são acrescentadas à lista original.
     */
    private void updatePreys(PApplet p, float dt) {
        for (Boid parent : new ArrayList<>(preys.getBoids())) {
            if (preys.getBoids().size() >= MAX_PREYS) {
                break;
            }
            if (p.random(1) < PREY_BIRTH_RATE * dt) {
                addPrey(PVector.add(parent.getPos(), PVector.random2D().mult(10)));
            }
        }
        preys.update(p, dt);
    }

    /*
     * Os predadores que morrem são removidos com o Iterator (forma correta de apagar enquanto se percorre);
     * os que nascem ficam numa lista à parte e só entram no fim.
     */
    private void updatePredators(PApplet p, float dt) {
        List<Predator> born = new ArrayList<>();
        Iterator<Predator> it = predators.iterator();
        while (it.hasNext()) {
            Predator predator = it.next();
            predator.hunt(preys, HUNT_RADIUS);
            predator.applyBehaviors();
            predator.move(dt);
            predator.wrap(p.width, p.height);
            predator.spendEnergy(dt);

            Boid prey = predator.getTarget();
            if (prey != null
                    && PVector.dist(predator.getPos(), prey.getPos()) < predator.getRadius() + prey.getRadius()) {
                preys.remove(prey);
                predator.eat(ENERGY_PER_PREY);
            }

            if (predator.getEnergy() <= 0) {
                it.remove();
            } else if (predator.getEnergy() >= SPLIT_ENERGY) {
                born.add(predator.split());
            }
        }
        predators.addAll(born);
    }

    private void recordHistory(float dt) {
        historyTimer += dt;
        while (historyTimer >= HISTORY_STEP) {
            historyTimer -= HISTORY_STEP;
            history.add(new int[] { preys.getBoids().size(), predators.size() });
            if (history.size() > HISTORY_SIZE) {
                history.remove(0);
            }
        }
    }

    /*
     * Gráfico das populações (canto inferior esquerdo). Os predadores são multiplicados por PREDATOR_SCALE
     * para se verem na mesma escala das presas.
     */
    private void drawGraph(PApplet p) {
        float w = HISTORY_SIZE;
        float h = 100;
        float x0 = 8;
        float y0 = p.height - 8;
        p.noStroke();
        p.fill(0, 160);
        p.rect(x0, y0 - h, w, h);
        for (int i = 1; i < history.size(); i++) {
            int[] a = history.get(i - 1);
            int[] b = history.get(i);
            p.stroke(preyColor);
            p.line(x0 + i - 1, graphY(a[0], y0, h), x0 + i, graphY(b[0], y0, h));
            p.stroke(predatorColor);
            p.line(x0 + i - 1, graphY(a[1] * PREDATOR_SCALE, y0, h), x0 + i, graphY(b[1] * PREDATOR_SCALE, y0, h));
        }
        p.noStroke();
        p.fill(255);
        p.text("presas (azul)   predadores x" + PREDATOR_SCALE + " (vermelho)", x0, y0 - h - 6);
    }

    private float graphY(int value, float y0, float h) {
        return y0 - h * Math.min(1f, (float) value / MAX_PREYS);
    }

    @Override
    public void mousePressed(PApplet p) {
        predators.add(new Predator(new PVector(p.mouseX, p.mouseY), START_ENERGY, predatorColor));
    }

    @Override
    public void keyPressed(PApplet p) {
        if (p.key == 'r') {
            reset(p);
        }
    }
}
