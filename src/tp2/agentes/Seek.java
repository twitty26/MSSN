package tp2.agentes;

import processing.core.PVector;

/*
 * Seek: ir em direção ao alvo à velocidade máxima.
 * O alvo é uma referência a um PVector (posição do rato, de outro Boid, ...), por isso acompanha o alvo
 * sem ser preciso atualizá-lo aqui.
 */
public class Seek extends Behavior {

    private PVector target;

    public Seek(PVector target, float weight) {
        super(weight);
        this.target = target;
    }

    @Override
    public PVector getDesiredVelocity(Boid me) {
        return PVector.sub(target, me.getPos()).setMag(me.getMaxSpeed());
    }

    public void setTarget(PVector target) {
        this.target = target;
    }
}