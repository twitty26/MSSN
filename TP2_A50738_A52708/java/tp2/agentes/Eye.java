package tp2.agentes;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import tp2.fisica.Body;

/*
 * Campo de visão de um Boid: um setor circular (raio e meio-ângulo) centrado na direção do movimento.
 * look() guarda a lista dos Boids vistos (vizinhos), usada pelos comportamentos de grupo.
 */
public class Eye {

    private final Boid owner;
    private final List<Boid> others;
    private final List<Boid> neighbors = new ArrayList<>();
    private float radius;
    private final float halfAngle;

    public Eye(Boid owner, List<Boid> others, float radius, float halfAngle) {
        this.owner = owner;
        this.others = others;
        this.radius = radius;
        this.halfAngle = halfAngle;
    }

    public void look() {
        neighbors.clear();
        for (Boid b : others) {
            if (b != owner && sees(b)) {
                neighbors.add(b);
            }
        }
    }

    public boolean sees(Body other) {
        PVector toOther = PVector.sub(other.getPos(), owner.getPos());
        if (toOther.mag() > radius) {
            return false;
        }
        PVector forward = PVector.fromAngle(owner.getHeading());
        return PVector.angleBetween(forward, toOther) <= halfAngle;
    }

    // Mostra o campo de visão (arco) e o vetor velocidade (linha)
    public void display(PApplet p) {
        PVector pos = owner.getPos();
        p.noStroke();
        p.fill(255, 255, 255, 40);
        p.arc(pos.x, pos.y, 2 * radius, 2 * radius, owner.getHeading() - halfAngle, owner.getHeading() + halfAngle,
                PApplet.PIE);
        p.stroke(255, 255, 0);
        p.strokeWeight(2);
        p.line(pos.x, pos.y, pos.x + owner.getVel().x * 0.5f, pos.y + owner.getVel().y * 0.5f);
        p.strokeWeight(1);
    }

    public List<Boid> getNeighbors() {
        return neighbors;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}