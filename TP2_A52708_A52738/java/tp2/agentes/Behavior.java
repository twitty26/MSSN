package tp2.agentes;

import processing.core.PVector;

/*
 * Comportamento de um Boid. Cada comportamento só diz qual é a velocidade que o Boid "deseja";
 * é o Boid que junta os vários comportamentos (média pesada pelo weight) e calcula a força de direção.
 * Devolver a velocidade atual do Boid equivale a "não quero mudar nada" (força zero).
 */
public abstract class Behavior {

    private float weight;

    protected Behavior(float weight) {
        this.weight = weight;
    }

    public abstract PVector getDesiredVelocity(Boid me);

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}