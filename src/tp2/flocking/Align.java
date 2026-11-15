package tp2.flocking;

import processing.core.PVector;
import tp2.agentes.Behavior;
import tp2.agentes.Boid;

/*
 * Alinhamento (flocking): seguir na mesma direção que os vizinhos (média das velocidades deles).
 */
public class Align extends Behavior {

    public Align(float weight) {
        super(weight);
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        if (me.getEye().getNeighbors().isEmpty()) {
            return null;
        }
        PVector average = new PVector();
        for (Boid b : me.getEye().getNeighbors()) {
            average.add(b.getVel());
        }
        return average.setMag(me.getMaxSpeed());
    }
}
