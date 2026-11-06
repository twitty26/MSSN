package tp2.agentes;

import java.util.ArrayList;
import java.util.List;

import processing.core.PVector;

/*
 * Patrol: visita ciclicamente os pontos de um caminho. Faz seek ao ponto atual e, quando chega a menos de
 * reachRadius, passa ao seguinte (depois do último volta ao primeiro).
 */
public class Patrol extends Behavior {

    private List<PVector> path;
    private final float reachRadius;
    private int index;

    public Patrol(List<PVector> path, float reachRadius, float weight) {
        super(weight);
        this.reachRadius = reachRadius;
        this.path = new ArrayList<>(path);
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        if (path.isEmpty()) {
            return me.getVel().copy();
        }
        PVector target = path.get(index);
        if (PVector.dist(me.getPos(), target) < reachRadius) {
            index = (index + 1) % path.size();
            target = path.get(index);
        }
        return PVector.sub(target, me.getPos()).setMag(me.getMaxSpeed());
    }

    public void setPath(List<PVector> path) {
        this.path = new ArrayList<>(path);
        index = 0;
    }

    public List<PVector> getPath() {
        return path;
    }
}