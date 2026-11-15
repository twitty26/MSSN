package tp2.fisica;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * Sistema de partículas: um emissor que lança partículas a partir de uma origem.
 * Pode emitir continuamente (rate partículas por segundo) ou de uma só vez (burst, para explosões).
 * As partículas mortas são removidas com um Iterator: é a forma correta de apagar elementos de uma lista
 * enquanto se percorre (um for-each normal dava ConcurrentModificationException).
 */
public class ParticleSystem {

    private final List<Particle> particles = new ArrayList<>();
    private final PVector origin;
    private final PVector direction;
    private final float spread;
    private final float rate;
    private final float lifetime;
    private final float radius;
    private final int color;
    private float emitAccumulator;
    private boolean emitting;

    /*
     * direction: velocidade média das partículas; spread: velocidade aleatória extra em qualquer direção;
     * rate: partículas por segundo (0 para não emitir continuamente).
     */
    public ParticleSystem(PVector origin, PVector direction, float spread, float rate, float lifetime,
            float radius, int color) {
        this.origin = origin.copy();
        this.direction = direction.copy();
        this.spread = spread;
        this.rate = rate;
        this.lifetime = lifetime;
        this.radius = radius;
        this.color = color;
        emitting = rate > 0;
    }

    public void burst(PApplet p, int n) {
        for (int i = 0; i < n; i++) {
            emit(p);
        }
    }

    private void emit(PApplet p) {
        PVector v = PVector.add(direction, PVector.random2D().mult(p.random(spread)));
        particles.add(new Particle(origin, v, radius, color, lifetime * p.random(0.5f, 1)));
    }

    public void move(PApplet p, float dt) {
        if (emitting) {
            emitAccumulator += rate * dt;
            while (emitAccumulator >= 1) {
                emitAccumulator -= 1;
                emit(p);
            }
        }
        Iterator<Particle> it = particles.iterator();
        while (it.hasNext()) {
            Particle particle = it.next();
            particle.move(dt);
            if (particle.isDead()) {
                it.remove();
            }
        }
    }

    public void display(PApplet p) {
        for (Particle particle : particles) {
            particle.display(p);
        }
    }

    public void setOrigin(PVector origin) {
        this.origin.set(origin);
    }

    public void setDirection(PVector direction) {
        this.direction.set(direction);
    }

    public void stop() {
        emitting = false;
    }

    public boolean isFinished() {
        return !emitting && particles.isEmpty();
    }
}