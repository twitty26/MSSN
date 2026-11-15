package tp2.flocking;

import java.util.List;

import processing.core.PVector;
import tp2.agentes.Behavior;
import tp2.agentes.Boid;

/*
 * Evasão: fugir do predador mais próximo, mas só se estiver a menos de panicRadius.
 * Foge da posição futura do predador (posição + velocidade * lookAhead), o contrário do Pursuit.
 */
public class Evade extends Behavior {

    private final List<? extends Boid> threats;
    private final float panicRadius;
    private final float lookAhead;

    public Evade(List<? extends Boid> threats, float panicRadius, float lookAhead, float weight) {
        super(weight);
        this.threats = threats;
        this.panicRadius = panicRadius;
        this.lookAhead = lookAhead;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        Boid nearest = null;
        float best = panicRadius;
        for (Boid t : threats) {
            float d = PVector.dist(me.getPos(), t.getPos());
            if (d < best) {
                best = d;
                nearest = t;
            }
        }
        if (nearest == null) {
            return null;
        }
        PVector future = PVector.add(nearest.getPos(), PVector.mult(nearest.getVel(), lookAhead));
        return PVector.sub(me.getPos(), future).setMag(me.getMaxSpeed());
    }
}
