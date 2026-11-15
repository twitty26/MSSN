package tp2.flocking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tp2.agentes.Arrive;
import tp2.agentes.Boid;
import tp2.fisica.ParticleSystem;

/*
 * E. Agentes Autónomos - Comportamentos de grupo (Flocking). Teclas 1 e 2 escolhem o ponto do enunciado:
 * 1 Predador a perseguir uma presa: o predador persegue sempre a presa mais próxima (critério escolhido);
 *   as presas formam um flock e fogem quando o predador está a menos de PANIC_RADIUS.
 * 2 Boids explosivos: cada Boid tem um temporizador próprio; nos últimos segundos fica vermelho e, quando
 *   o tempo acaba (ou quando se clica nele), rebenta num sistema de partículas.
 * 3 Flock com liderança: o líder (vermelho) é controlado com W A S D; os outros fazem flocking e arrive
 *   até ao líder.
 * 4 Debugging: o Boid observado (verde) mostra o campo de visão e o vetor velocidade; os Boids que ele vê
 *   ficam amarelos. Clicar perto de outro Boid passa a observá-lo.
 * Teclas: r recomeça o ponto atual.
 */
public class FlockingApp implements IProcessingApp {

    private static final int N_BOIDS = 80;
    private static final float PANIC_RADIUS = 120;
    private static final float WARNING_TIME = 3;
    private static final float CLICK_RADIUS = 15;
    private static final float LEADER_SPEED = 110;

    private int mode = 1;
    private Flock flock;
    private int baseColor;
    private int warningColor;

    private final List<Boid> predators = new ArrayList<>();
    private Pursuit pursuit;
    private int caught;

    private final Map<Boid, Float> fuses = new HashMap<>();
    private final List<ParticleSystem> explosions = new ArrayList<>();

    private Boid leader;

    private Boid watched;

    @Override
    public void setup(PApplet p) {
        baseColor = p.color(80, 200, 255);
        warningColor = p.color(255, 40, 40);
        reset(p);
    }

    private void reset(PApplet p) {
        flock = new Flock(p, N_BOIDS, baseColor);
        predators.clear();
        fuses.clear();
        explosions.clear();
        leader = null;
        watched = null;
        caught = 0;
        switch (mode) {
            case 1 -> {
                Boid predator = new Boid(new PVector(), 140, 300, 10, p.color(255, 60, 60));
                pursuit = new Pursuit(0.5f, 1);
                predator.addBehavior(pursuit);
                predators.add(predator);
                for (Boid b : flock.getBoids()) {
                    b.addBehavior(new Evade(predators, PANIC_RADIUS, 0.5f, 3));
                }
            }
            case 2 -> {
                for (Boid b : flock.getBoids()) {
                    fuses.put(b, p.random(3, 20));
                }
            }
            case 3 -> {
                leader = new Boid(new PVector(p.width / 2f, p.height / 2f), LEADER_SPEED, 0, 10, p.color(255, 60, 60));
                leader.getVel().set(LEADER_SPEED, 0);
                for (Boid b : flock.getBoids()) {
                    b.addBehavior(new Arrive(leader.getPos(), 150, 1, 1.5f));
                }
            }
            case 4 -> watched = flock.getBoids().get(0);
            default -> {
            }
        }
    }

    @Override
    public void draw(PApplet p, float dt) {
        dt = Math.min(dt, 0.1f);
        switch (mode) {
            case 1 -> updatePredator(p, dt);
            case 2 -> updateFuses(p, dt);
            case 3 -> {
                leader.move(dt);
                leader.wrap(p.width, p.height);
            }
            default -> {
            }
        }
        flock.update(p, dt);
        if (mode == 4) {
            colorNeighbors(p);
        }

        p.background(20);
        if (watched != null) {
            watched.getEye().display(p);
        }
        flock.display(p);
        for (Boid predator : predators) {
            predator.display(p);
        }
        for (ParticleSystem e : explosions) {
            e.display(p);
        }
        if (leader != null) {
            leader.display(p);
        }
        drawInfo(p);
    }

    /*
     * Debugging: todos voltam à cor base, os vizinhos que o Boid observado vê (calculados pelo look() do
     * Eye dentro do flock.update) ficam amarelos e o observado fica verde.
     */
    private void colorNeighbors(PApplet p) {
        for (Boid b : flock.getBoids()) {
            b.setColor(baseColor);
        }
        for (Boid b : watched.getEye().getNeighbors()) {
            b.setColor(p.color(255, 230, 0));
        }
        watched.setColor(p.color(0, 255, 120));
    }

    /*
     * Critério: o alvo é a presa mais próxima. Se lhe tocar (distância menor que a soma dos raios), come-a.
     */
    private void updatePredator(PApplet p, float dt) {
        Boid predator = predators.get(0);
        Boid prey = flock.nearest(predator.getPos(), Float.MAX_VALUE);
        pursuit.setTarget(prey);
        predator.applyBehaviors();
        predator.move(dt);
        predator.wrap(p.width, p.height);
        if (prey != null && PVector.dist(predator.getPos(), prey.getPos()) < predator.getRadius() + prey.getRadius()) {
            flock.remove(prey);
            caught++;
        }
    }

    /*
     * Desconta dt ao temporizador de cada Boid. Nos últimos WARNING_TIME segundos a cor passa de azul a
     * vermelho; quando chega a 0 rebenta. Os que rebentam são guardados à parte e só removidos no fim,
     * porque não se pode remover de uma lista enquanto se percorre com for-each.
     */
    private void updateFuses(PApplet p, float dt) {
        List<Boid> toExplode = new ArrayList<>();
        for (Boid b : flock.getBoids()) {
            float t = fuses.get(b) - dt;
            fuses.put(b, t);
            if (t <= 0) {
                toExplode.add(b);
            } else if (t < WARNING_TIME) {
                b.setColor(p.lerpColor(warningColor, baseColor, t / WARNING_TIME));
            }
        }
        for (Boid b : toExplode) {
            explode(p, b);
        }
        for (ParticleSystem e : explosions) {
            e.move(p, dt);
        }
        explosions.removeIf(ParticleSystem::isFinished);
    }

    /*
     * Explosão: um sistema de partículas sem emissão contínua (rate 0) que lança 40 partículas de uma vez,
     * em todas as direções, com a cor que o Boid tinha.
     */
    private void explode(PApplet p, Boid b) {
        ParticleSystem e = new ParticleSystem(b.getPos(), new PVector(), 150, 0, 1, 3, b.getColor());
        e.burst(p, 40);
        explosions.add(e);
        flock.remove(b);
        fuses.remove(b);
    }

    private void drawInfo(PApplet p) {
        p.fill(255);
        String text = switch (mode) {
            case 1 -> "1 Predador e presa   apanhadas: " + caught + "   restantes: " + flock.getBoids().size();
            case 2 -> "2 Boids explosivos   clique rebenta um Boid   restantes: " + flock.getBoids().size();
            case 3 -> "3 Flock com liderança   W A S D controlam o líder (vermelho)";
            default -> "4 Debugging   vizinhos vistos: " + watched.getEye().getNeighbors().size()
                    + "   clique escolhe outro Boid";
        };
        p.text(text + "   |   teclas 1-2 mudam de ponto   r recomeça", 8, 16);
    }

    @Override
    public void mousePressed(PApplet p) {
        Boid b = flock.nearest(new PVector(p.mouseX, p.mouseY), CLICK_RADIUS);
        if (b == null) {
            return;
        }
        if (mode == 2) {
            explode(p, b);
        } else if (mode == 4) {
            watched = b;
        }
    }

    @Override
    public void keyPressed(PApplet p) {
        if (p.key >= '1' && p.key <= '4') {
            mode = p.key - '0';
            reset(p);
        } else if (p.key == 'r') {
            reset(p);
        } else if (mode == 3) {
            steerLeader(Character.toLowerCase(p.key));
        }
    }

    /*
     * Cada tecla aponta o líder numa direção e ele continua nessa direção até se carregar noutra.
     * Assim basta o keyPressed que a interface IProcessingApp já tem (não é preciso keyReleased).
     */
    private void steerLeader(char key) {
        switch (key) {
            case 'w' -> leader.getVel().set(0, -LEADER_SPEED);
            case 's' -> leader.getVel().set(0, LEADER_SPEED);
            case 'a' -> leader.getVel().set(-LEADER_SPEED, 0);
            case 'd' -> leader.getVel().set(LEADER_SPEED, 0);
            default -> {
            }
        }
    }
}
