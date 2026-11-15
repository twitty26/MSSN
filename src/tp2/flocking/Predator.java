package tp2.flocking;

import processing.core.PVector;
import tp2.agentes.Boid;
import tp2.agentes.Wander;

/*
 * Predador do ecossistema (E.5): um Boid com energia.
 * Persegue a presa mais próxima que consegue ver; se não houver nenhuma, vagueia (Wander de Reynolds).
 * A energia gasta-se com o tempo e ganha-se ao comer; com energia suficiente divide-se em dois.
 */
public final class Predator extends Boid {

    private static final float MAX_SPEED = 125;
    private static final float MAX_FORCE = 300;
    private static final float RADIUS = 9;

    private final Pursuit pursuit = new Pursuit(0.5f, 1);
    private final Wander wander = new Wander(80, 40, 0.3f, 1);
    private float energy;

    public Predator(PVector pos, float energy, int color) {
        super(pos, MAX_SPEED, MAX_FORCE, RADIUS, color);
        this.energy = energy;
        addBehavior(pursuit);
        addBehavior(wander);
    }

    /*
     * Escolhe como alvo a presa mais próxima a menos de huntRadius. Comutação de comportamento:
     * com alvo persegue (wander com peso 0), sem alvo vagueia (o Pursuit devolve null e é ignorado).
     */
    public void hunt(Flock preys, float huntRadius) {
        Boid prey = preys.nearest(pos, huntRadius);
        pursuit.setTarget(prey);
        wander.setWeight(prey == null ? 1 : 0);
    }

    public Boid getTarget() {
        return pursuit.getTarget();
    }

    public void spendEnergy(float amount) {
        energy -= amount;
    }

    public void eat(float amount) {
        energy += amount;
    }

    public float getEnergy() {
        return energy;
    }

    /*
     * Divisão: a energia é repartida em partes iguais entre este predador e o filho, que nasce no mesmo sítio.
     */
    public Predator split() {
        energy /= 2;
        return new Predator(pos, energy, color);
    }
}
