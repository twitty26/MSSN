package tp2.flocking;

import processing.core.PVector;
import tp2.agentes.Behavior;
import tp2.agentes.Boid;

/*
 * Perseguição: em vez de ir para onde o alvo está (seek), vai para onde ele vai estar daqui a lookAhead
 * segundos (posição + velocidade * lookAhead). Assim corta caminho e apanha alvos em movimento.
 */
public class Pursuit extends Behavior {

    private final float lookAhead;
    private Boid target;

    public Pursuit(float lookAhead, float weight) {
        super(weight);
        this.lookAhead = lookAhead;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        if (target == null) {
            return null;
        }
        PVector future = PVector.add(target.getPos(), PVector.mult(target.getVel(), lookAhead));
        return PVector.sub(future, me.getPos()).setMag(me.getMaxSpeed());
    }

    public Boid getTarget() {
        return target;
    }

    public void setTarget(Boid target) {
        this.target = target;
    }
}
