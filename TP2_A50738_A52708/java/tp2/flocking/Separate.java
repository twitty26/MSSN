package tp2.flocking;

import processing.core.PVector;
import tp2.agentes.Behavior;
import tp2.agentes.Boid;

/*
 * Separação (flocking): afastar-se dos vizinhos que estão a menos de distance.
 * Cada vizinho empurra na direção oposta com força 1/d, por isso os mais próximos empurram mais.
 */
public class Separate extends Behavior {

    private final float distance;

    public Separate(float distance, float weight) {
        super(weight);
        this.distance = distance;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        PVector away = new PVector();
        for (Boid b : me.getEye().getNeighbors()) {
            PVector r = PVector.sub(me.getPos(), b.getPos());
            float d = r.mag();
            if (d > 0 && d < distance) {
                away.add(r.div(d * d));
            }
        }
        if (away.mag() == 0) {
            return null;
        }
        return away.setMag(me.getMaxSpeed());
    }
}
