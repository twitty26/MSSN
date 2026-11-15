package tp2.sistema_solar;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tp2.fisica.Body;
import tp2.fisica.ParticleSystem;

/*
 * C. Sistema Solar & Sistemas de Partículas.
 * Cada corpo evolui segundo dr/dt = v, dv/dt = a, a = (soma das forças) / m, com a força de atração de Newton.
 *
 * Teclas: 1 um planeta (C.1); 2 N planetas atraídos só pelo Sol (C.2a); 3 todos se atraem (C.2b);
 *         espaço pausa. Rato: clicar cria um planeta em órbita circular nesse ponto.
 * Sistemas de partículas (C.3): a coroa do Sol e a cauda do cometa.
 *
 * Unidades livres (C.2b): distâncias em píxeis, tempo em segundos, G = 1. A massa do Sol foi escolhida
 * para um planeta a 150 px dar uma volta em cerca de 8 s.
 */
public class SistemaSolarApp implements IProcessingApp {

    private static final float G = 1;
    private static final float SUN_MASS = 2e6f;
    private static final float STEP = 0.002f; // passo de Euler (vários passos por frame, para ser preciso)

    private Body sun;
    private final List<Body> planets = new ArrayList<>();
    private Body comet;
    private ParticleSystem corona;
    private ParticleSystem tail;
    private int mode = 1;
    private boolean paused;
    private float timer;

    @Override
    public void setup(PApplet p) {
        reset(p);
    }

    private void reset(PApplet p) {
        planets.clear();
        comet = null;
        tail = null;
        PVector center = new PVector(p.width / 2f, p.height / 2f);
        sun = new Body(center, new PVector(), SUN_MASS, 25, p.color(255, 200, 0));
        corona = new ParticleSystem(center, new PVector(), 60, 100, 1.5f, 4, p.color(255, 120, 0));
        switch (mode) {
            case 1 -> planets.add(orbit(randomOffset(p, 150), 10, 8, p.color(80, 160, 255)));
            case 2 -> addPlanets(p, 10);
            case 3 -> {
                addPlanets(p, 2e4f);
                cancelSunMomentum();
            }
            default -> {
            }
        }
        if (mode > 1) {
            float r = 380;
            float speed = 0.55f * (float) Math.sqrt(G * SUN_MASS / r); // abaixo da velocidade circular: elipse
            comet = new Body(PVector.add(center, new PVector(r, 0)), new PVector(0, -speed), 1, 4,
                    p.color(200, 255, 255));
            tail = new ParticleSystem(comet.getPos(), new PVector(), 10, 120, 1.5f, 2, p.color(150, 220, 255));
        }
    }

    private void addPlanets(PApplet p, float mass) {
        float[] radii = { 70, 110, 160, 220, 280 };
        int[] colors = { p.color(180, 180, 180), p.color(230, 180, 90), p.color(80, 160, 255),
                p.color(230, 80, 50), p.color(220, 200, 150) };
        for (int i = 0; i < radii.length; i++) {
            planets.add(orbit(randomOffset(p, radii[i]), mass, 5 + i, colors[i]));
        }
    }

    /*
     * Planeta em órbita circular à volta do Sol, na posição sun + offset: a gravidade faz de força centrípeta,
     * G M m / r^2 = m v^2 / r, logo v = sqrt(G M / r), perpendicular ao raio.
     */
    private Body orbit(PVector offset, float mass, float radius, int color) {
        PVector vel = new PVector(-offset.y, offset.x).setMag((float) Math.sqrt(G * SUN_MASS / offset.mag()));
        return new Body(PVector.add(sun.getPos(), offset), PVector.add(vel, sun.getVel()), mass, radius, color);
    }

    private PVector randomOffset(PApplet p, float r) {
        return PVector.fromAngle(p.random(PApplet.TWO_PI)).mult(r);
    }

    /*
     * No modo 3 o Sol também se move. Dá-se-lhe a velocidade que anula a quantidade de movimento total,
     * para o sistema não fugir da janela.
     */
    private void cancelSunMomentum() {
        PVector momentum = new PVector();
        for (Body planet : planets) {
            momentum.add(PVector.mult(planet.getVel(), planet.getMass()));
        }
        sun.getVel().set(momentum.div(-SUN_MASS));
    }

    private void step(float dt) {
        List<Body> bodies = new ArrayList<>(planets);
        if (comet != null) {
            bodies.add(comet);
        }
        for (Body b : bodies) {
            b.applyForce(b.attraction(sun, G));
        }
        if (mode == 3) {
            for (Body b : bodies) {
                sun.applyForce(sun.attraction(b, G));
            }
            for (int i = 0; i < planets.size(); i++) {
                for (int j = i + 1; j < planets.size(); j++) {
                    PVector f = planets.get(i).attraction(planets.get(j), G);
                    planets.get(i).applyForce(f);
                    planets.get(j).applyForce(f.mult(-1));
                }
            }
            sun.move(dt);
        }
        for (Body b : bodies) {
            b.move(dt);
        }
    }

    @Override
    public void draw(PApplet p, float dt) {
        dt = Math.min(dt, 0.1f);
        if (!paused) {
            timer += dt;
            while (timer >= STEP) {
                timer -= STEP;
                step(STEP);
            }
            corona.setOrigin(sun.getPos());
            corona.move(p, dt);
            if (comet != null) {
                tail.setOrigin(comet.getPos());
                tail.setDirection(PVector.sub(comet.getPos(), sun.getPos()).setMag(60));
                tail.move(p, dt);
            }
        }
        p.background(0);
        sun.display(p);
        corona.display(p);
        for (Body planet : planets) {
            planet.display(p);
        }
        if (comet != null) {
            tail.display(p);
            comet.display(p);
        }
        p.fill(255);
        p.text("Modo " + mode + "   1 um planeta   2 N planetas (só o Sol atrai)   3 todos se atraem"
                + "   espaço pausa   clique: novo planeta", 8, 16);
    }

    @Override
    public void mousePressed(PApplet p) {
        PVector offset = new PVector(p.mouseX, p.mouseY).sub(sun.getPos());
        if (offset.mag() > sun.getRadius() * 2) {
            int color = p.color(p.random(100, 255), p.random(100, 255), p.random(100, 255));
            planets.add(orbit(offset, mode == 3 ? 2e4f : 10, 6, color));
        }
    }

    @Override
    public void keyPressed(PApplet p) {
        switch (p.key) {
            case '1', '2', '3' -> {
                mode = p.key - '0';
                reset(p);
            }
            case ' ' -> paused = !paused;
            default -> {
            }
        }
    }
}