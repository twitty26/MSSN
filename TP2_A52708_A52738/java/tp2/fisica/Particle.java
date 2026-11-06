package tp2.fisica;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * Partícula: um corpo com tempo de vida. Vai ficando transparente até desaparecer.
 */
public class Particle extends Body {

    private final float lifetime;
    private float age;

    public Particle(PVector pos, PVector vel, float radius, int color, float lifetime) {
        super(pos, vel, 1, radius, color);
        this.lifetime = lifetime;
    }

    @Override
    public void move(float dt) {
        super.move(dt);
        age += dt;
    }

    public boolean isDead() {
        return age >= lifetime;
    }

    @Override
    public void display(PApplet p) {
        p.noStroke();
        p.fill(color, 255 * (1 - age / lifetime));
        p.circle(pos.x, pos.y, 2 * radius);
    }
}