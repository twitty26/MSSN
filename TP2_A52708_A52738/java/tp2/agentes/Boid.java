package tp2.agentes;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import tp2.fisica.Body;

/*
 * Agente autónomo de Reynolds. A cada passo:
 * 1. junta as velocidades desejadas dos seus comportamentos (média pesada);
 * 2. força de direção = velocidade desejada - velocidade atual, limitada a maxForce;
 * 3. move-se como um corpo físico (Body), com a velocidade limitada a maxSpeed.
 * Com o travão ligado, a força aponta contra a velocidade e o Boid pára aos poucos.
 */
public class Boid extends Body {

    private final List<Behavior> behaviors = new ArrayList<>();
    private float maxSpeed;
    private float maxForce;
    private boolean braking;
    private Eye eye;
    private float heading;

    public Boid(PVector pos, float maxSpeed, float maxForce, float radius, int color) {
        super(pos, PVector.random2D().mult(maxSpeed / 2), 1, radius, color);
        this.maxSpeed = maxSpeed;
        this.maxForce = maxForce;
        heading = vel.heading();
    }

    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    public void applyBehaviors() {
        PVector steer;
        if (braking) {
            steer = PVector.mult(vel, -1);
        } else {
            PVector desired = new PVector();
            float totalWeight = 0;
            for (Behavior b : behaviors) {
                if (b.getWeight() > 0) {
                    desired.add(b.getDesiredVelocity(this).mult(b.getWeight()));
                    totalWeight += b.getWeight();
                }
            }
            if (totalWeight == 0) {
                return;
            }
            desired.div(totalWeight);
            steer = PVector.sub(desired, vel);
        }
        applyForce(steer.limit(maxForce));
    }

    @Override
    public void move(float dt) {
        super.move(dt);
        vel.limit(maxSpeed);
        if (vel.mag() > 1) {
            heading = vel.heading();
        }
    }

    // O que sai por um lado da janela entra pelo outro
    public void wrap(float width, float height) {
        pos.x = (pos.x + width) % width;
        pos.y = (pos.y + height) % height;
    }

    @Override
    public void display(PApplet p) {
        p.pushMatrix();
        p.translate(pos.x, pos.y);
        p.rotate(heading);
        p.noStroke();
        p.fill(color);
        p.triangle(radius * 1.5f, 0, -radius, radius * 0.8f, -radius, -radius * 0.8f);
        p.popMatrix();
    }

    public float getHeading() {
        return heading;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getMaxForce() {
        return maxForce;
    }

    public void setMaxForce(float maxForce) {
        this.maxForce = maxForce;
    }

    public boolean isBraking() {
        return braking;
    }

    public void setBraking(boolean braking) {
        this.braking = braking;
    }

    public Eye getEye() {
        return eye;
    }

    public void setEye(Eye eye) {
        this.eye = eye;
    }
}