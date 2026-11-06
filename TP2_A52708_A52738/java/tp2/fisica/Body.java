package tp2.fisica;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * Corpo com massa que se move sob a ação de forças (2.ª lei de Newton: a = F / m).
 * Integração pelo método de Euler semi-implícito: primeiro atualiza-se a velocidade e depois a posição
 * com a velocidade já nova. É tão simples como o Euler normal, mas muito mais estável nas órbitas.
 */
public class Body {

    protected final PVector pos;
    protected final PVector vel;
    protected final PVector acc = new PVector();
    protected float mass;
    protected float radius;
    protected int color;

    public Body(PVector pos, PVector vel, float mass, float radius, int color) {
        this.pos = pos.copy();
        this.vel = vel.copy();
        this.mass = mass;
        this.radius = radius;
        this.color = color;
    }

    public void applyForce(PVector force) {
        acc.add(PVector.div(force, mass));
    }

    public void move(float dt) {
        vel.add(PVector.mult(acc, dt));
        pos.add(PVector.mult(vel, dt));
        acc.set(0, 0);
    }

    /*
     * Força com que o corpo other atrai este corpo (lei da atração universal de Newton):
     * F = G * m1 * m2 / d^2, na direção de other.
     * A distância nunca é menor do que a soma dos raios, para a força não explodir quando os corpos se tocam.
     */
    public PVector attraction(Body other, float g) {
        PVector dir = PVector.sub(other.pos, pos);
        float d = Math.max(dir.mag(), radius + other.radius);
        return dir.setMag(g * mass * other.mass / (d * d));
    }

    public void display(PApplet p) {
        p.noStroke();
        p.fill(color);
        p.circle(pos.x, pos.y, 2 * radius);
    }

    public PVector getPos() {
        return pos;
    }

    public PVector getVel() {
        return vel;
    }

    public float getMass() {
        return mass;
    }

    public float getRadius() {
        return radius;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}