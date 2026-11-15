package tp2.agentes;

import processing.core.PVector;

/*
 * Flee: fugir da ameaça à velocidade máxima, mas só quando ela está a menos de panicRadius.
 * Longe da ameaça devolve a velocidade atual (não interfere).
 */
public class Flee extends Behavior {

    private final PVector threat;
    private final float panicRadius;

    public Flee(PVector threat, float panicRadius, float weight) {
        super(weight);
        this.threat = threat;
        this.panicRadius = panicRadius;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        PVector away = PVector.sub(me.getPos(), threat);
        if (away.mag() > panicRadius) {
            return me.getVel().copy();
        }
        return away.setMag(me.getMaxSpeed());
    }
}