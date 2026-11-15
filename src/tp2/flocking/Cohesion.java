package tp2.flocking;

import processing.core.PVector;
import tp2.agentes.Behavior;
import tp2.agentes.Boid;

/*
 * Coesão (flocking): ir para o centro (posição média) dos vizinhos, para o grupo não se desfazer.
 */
public class Cohesion extends Behavior {

    public Cohesion(float weight) {
        super(weight);
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        if (me.getEye().getNeighbors().isEmpty()) {
            return null;
        }
        PVector center = new PVector();
        for (Boid b : me.getEye().getNeighbors()) {
            center.add(b.getPos());
        }
        center.div(me.getEye().getNeighbors().size());
        return PVector.sub(center, me.getPos()).setMag(me.getMaxSpeed());
    }
}
