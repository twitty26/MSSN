package tp2.flocking;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import tp2.agentes.Boid;
import tp2.agentes.Eye;

/*
 * Flock de Reynolds: cada Boid tem um Eye (vê os vizinhos dentro do campo de visão) e três comportamentos
 * de grupo: separação, alinhamento e coesão. O Eye de cada Boid aponta para a lista do Flock, por isso
 * Boids acrescentados ou removidos passam logo a ser (ou deixam de ser) vistos pelos outros.
 */
public final class Flock {

    private static final float MAX_SPEED = 120;
    private static final float MAX_FORCE = 200;
    private static final float RADIUS = 6;
    private static final float VISION_RADIUS = 70;
    private static final float VISION_ANGLE = 3 * PApplet.PI / 4;
    private static final float SEPARATION_DISTANCE = 25;

    private final List<Boid> boids = new ArrayList<>();
    private final int color;

    public Flock(PApplet p, int n, int color) {
        this.color = color;
        for (int i = 0; i < n; i++) {
            addBoid(new PVector(p.random(p.width), p.random(p.height)));
        }
    }

    public Boid addBoid(PVector pos) {
        Boid b = new Boid(pos, MAX_SPEED, MAX_FORCE, RADIUS, color);
        b.setEye(new Eye(b, boids, VISION_RADIUS, VISION_ANGLE));
        b.addBehavior(new Separate(SEPARATION_DISTANCE, 1.5f));
        b.addBehavior(new Align(1));
        b.addBehavior(new Cohesion(1));
        boids.add(b);
        return b;
    }

    public void remove(Boid b) {
        boids.remove(b);
    }

    /*
     * Boid mais próximo de pos, a menos de maxDistance (null se não houver nenhum).
     */
    public Boid nearest(PVector pos, float maxDistance) {
        Boid nearest = null;
        float best = maxDistance;
        for (Boid b : boids) {
            float d = PVector.dist(pos, b.getPos());
            if (d < best) {
                best = d;
                nearest = b;
            }
        }
        return nearest;
    }

    /*
     * Primeiro todos olham e só depois todos se mexem: assim todos decidem com base no mesmo instante.
     */
    public void update(PApplet p, float dt) {
        for (Boid b : boids) {
            b.getEye().look();
        }
        for (Boid b : boids) {
            b.applyBehaviors();
            b.move(dt);
            b.wrap(p.width, p.height);
        }
    }

    public void display(PApplet p) {
        for (Boid b : boids) {
            b.display(p);
        }
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public int getColor() {
        return color;
    }
}
