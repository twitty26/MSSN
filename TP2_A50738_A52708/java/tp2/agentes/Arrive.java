package tp2.agentes;

import processing.core.PVector;

/*
 * Arrive: como o Seek, mas trava ao chegar perto do alvo.
 * Velocidade desejada: vmax se d >= R; vmax * (d / R)^k se d < R.
 * k = 1 trava de forma linear; k > 1 trava mais cedo e mais suave; k < 1 trava mais tarde e mais bruscamente.
 */
public class Arrive extends Behavior {

    private final PVector target;
    private final float slowRadius;
    private float k;

    public Arrive(PVector target, float slowRadius, float k, float weight) {
        super(weight);
        this.target = target;
        this.slowRadius = slowRadius;
        this.k = k;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        PVector toTarget = PVector.sub(target, me.getPos());
        float d = toTarget.mag();
        float speed = me.getMaxSpeed();
        if (d < slowRadius) {
            speed *= (float) Math.pow(d / slowRadius, k);
        }
        return toTarget.setMag(speed);
    }

    public float getSlowRadius() {
        return slowRadius;
    }

    public float getK() {
        return k;
    }

    public void setK(float k) {
        this.k = k;
    }
}