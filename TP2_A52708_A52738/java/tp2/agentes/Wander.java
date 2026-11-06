package tp2.agentes;

import processing.core.PVector;

/*
 * Wander de Reynolds: projeta-se um círculo à frente do Boid (a distância distance, com raio wanderRadius)
 * e o alvo é um ponto desse círculo. A cada passo o ângulo desse ponto muda um pouco ao acaso,
 * por isso o Boid vagueia de forma suave, sem mudanças bruscas de direção.
 */
public class Wander extends Behavior {

    private final float distance;
    private final float wanderRadius;
    private final float jitter;
    private float angle;
    private final PVector center = new PVector();
    private final PVector target = new PVector();

    public Wander(float distance, float wanderRadius, float jitter, float weight) {
        super(weight);
        this.distance = distance;
        this.wanderRadius = wanderRadius;
        this.jitter = jitter;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        angle += (float) (Math.random() * 2 - 1) * jitter;
        center.set(PVector.fromAngle(me.getHeading()).mult(distance).add(me.getPos()));
        target.set(PVector.fromAngle(me.getHeading() + angle).mult(wanderRadius).add(center));
        return PVector.sub(target, me.getPos()).setMag(me.getMaxSpeed());
    }

    public PVector getCenter() {
        return center;
    }

    public PVector getTarget() {
        return target;
    }

    public float getWanderRadius() {
        return wanderRadius;
    }
}