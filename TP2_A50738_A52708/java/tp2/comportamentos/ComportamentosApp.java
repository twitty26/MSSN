package tp2.comportamentos;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;
import tp2.agentes.Arrive;
import tp2.agentes.Boid;
import tp2.agentes.Eye;
import tp2.agentes.Flee;
import tp2.agentes.Patrol;
import tp2.agentes.Seek;
import tp2.agentes.Wander;

/*
 * D. Agentes Autónomos - Comportamentos individuais. Teclas 1 a 5 escolhem o ponto do enunciado:
 * 1 Seek ao rato com acelerador e travão  (b travão, + / - velocidade máxima, f / v força máxima)
 * 2 Arrive ao ponto clicado                (+ / - parâmetro de travagem k)
 * 3 Patrol                                 (cliques gravam pontos, p usa-os como caminho, x apaga)
 * 4 Wander de Reynolds                     (mostra o círculo de wander)
 * 5 Comutação: vagueia e foge do inimigo (vermelho, segue o rato) quando o vê
 */
public class ComportamentosApp implements IProcessingApp {

    private static final float SAFE_DISTANCE = 300;

    private int mode = 1;
    private Boid boid;
    private Boid enemy;
    private final PVector mouse = new PVector();
    private final PVector target = new PVector();
    private Arrive arrive;
    private Patrol patrol;
    private Wander wander;
    private Flee flee;
    private boolean fleeing;
    private final List<PVector> recorded = new ArrayList<>();

    @Override
    public void setup(PApplet p) {
        reset(p);
    }

    private void reset(PApplet p) {
        PVector center = new PVector(p.width / 2f, p.height / 2f);
        boid = new Boid(center, 200, 300, 10, p.color(80, 200, 255));
        enemy = null;
        switch (mode) {
            case 1 -> boid.addBehavior(new Seek(mouse, 1));
            case 2 -> {
                target.set(center.x + 200, center.y);
                arrive = new Arrive(target, 150, 1, 1);
                boid.addBehavior(arrive);
            }
            case 3 -> {
                recorded.clear();
                patrol = new Patrol(defaultPath(p), 15, 1);
                boid.addBehavior(patrol);
            }
            case 4 -> {
                wander = new Wander(80, 40, 0.3f, 1);
                boid.addBehavior(wander);
            }
            case 5 -> {
                enemy = new Boid(new PVector(), 220, 400, 10, p.color(255, 60, 60));
                enemy.addBehavior(new Seek(mouse, 1));
                wander = new Wander(80, 40, 0.3f, 1);
                flee = new Flee(enemy.getPos(), SAFE_DISTANCE, 0);
                fleeing = false;
                boid.addBehavior(wander);
                boid.addBehavior(flee);
                boid.setEye(new Eye(boid, new ArrayList<>(), 180, PApplet.PI / 2));
            }
            default -> {
            }
        }
    }

    private List<PVector> defaultPath(PApplet p) {
        return List.of(new PVector(150, 150), new PVector(p.width - 150, 150),
                new PVector(p.width - 150, p.height - 150), new PVector(150, p.height - 150));
    }

    /*
     * Pedido no enunciado: procedimento que regista a sequência de pontos do caminho.
     * Os pontos são gravados com cliques do rato (lista recorded); este método devolve-os como caminho.
     */
    public ArrayList<PVector> definePath() {
        ArrayList<PVector> path = new ArrayList<>();
        for (PVector point : recorded) {
            path.add(point.copy());
        }
        return path;
    }

    @Override
    public void draw(PApplet p, float dt) {
        dt = Math.min(dt, 0.1f);
        mouse.set(p.mouseX, p.mouseY);
        if (mode == 5) {
            updateFleeing(p);
            enemy.applyBehaviors();
            enemy.move(dt);
        }
        boid.applyBehaviors();
        boid.move(dt);
        boid.wrap(p.width, p.height);

        p.background(20);
        drawHelpers(p);
        boid.display(p);
        if (enemy != null) {
            enemy.display(p);
        }
        drawInfo(p);
    }

    /*
     * Comutação: começa a fugir quando vê o inimigo e só volta a vaguear quando ele fica longe (> SAFE_DISTANCE).
     * Sem esta margem, mal se virava de costas deixava de ver o inimigo e voltava logo a vaguear na direção dele.
     */
    private void updateFleeing(PApplet p) {
        if (boid.getEye().sees(enemy)) {
            fleeing = true;
        } else if (PVector.dist(boid.getPos(), enemy.getPos()) > SAFE_DISTANCE) {
            fleeing = false;
        }
        flee.setWeight(fleeing ? 1 : 0);
        wander.setWeight(fleeing ? 0 : 1);
        boid.setColor(fleeing ? p.color(255, 200, 0) : p.color(80, 200, 255));
    }

    private void drawHelpers(PApplet p) {
        p.noFill();
        p.stroke(255, 120);
        switch (mode) {
            case 2 -> {
                p.circle(target.x, target.y, 2 * arrive.getSlowRadius());
                p.circle(target.x, target.y, 8);
            }
            case 3 -> {
                drawPath(p, patrol.getPath(), p.color(255, 120));
                drawPath(p, recorded, p.color(0, 255, 120));
            }
            case 4 -> {
                p.circle(wander.getCenter().x, wander.getCenter().y, 2 * wander.getWanderRadius());
                p.line(boid.getPos().x, boid.getPos().y, wander.getTarget().x, wander.getTarget().y);
            }
            case 5 -> boid.getEye().display(p);
            default -> {
            }
        }
    }

    private void drawPath(PApplet p, List<PVector> path, int color) {
        p.stroke(color);
        for (int i = 0; i < path.size(); i++) {
            PVector a = path.get(i);
            PVector b = path.get((i + 1) % path.size());
            p.line(a.x, a.y, b.x, b.y);
            p.circle(a.x, a.y, 10);
        }
    }

    private void drawInfo(PApplet p) {
        p.fill(255);
        String text = switch (mode) {
            case 1 -> String.format("1 Seek   b travão [%s]   +/- vmax %.0f   f/v força máx %.0f",
                    boid.isBraking() ? "ligado" : "desligado", boid.getMaxSpeed(), boid.getMaxForce());
            case 2 -> String.format("2 Arrive   clique: novo alvo   +/- k = %.2f", arrive.getK());
            case 3 -> "3 Patrol   cliques gravam pontos (" + recorded.size() + ")   p usar caminho   x apagar";
            case 4 -> "4 Wander de Reynolds";
            default -> "5 Comutação: vagueia; foge quando vê o inimigo (o rato controla o inimigo)";
        };
        p.text(text + "   |   teclas 1-5 mudam de exercício", 8, 16);
    }

    @Override
    public void mousePressed(PApplet p) {
        if (mode == 2) {
            target.set(p.mouseX, p.mouseY);
        } else if (mode == 3) {
            recorded.add(new PVector(p.mouseX, p.mouseY));
        }
    }

    @Override
    public void keyPressed(PApplet p) {
        char key = p.key;
        if (key >= '1' && key <= '5') {
            mode = key - '0';
            reset(p);
            return;
        }
        if (mode == 1) {
            switch (key) {
                case 'b' -> boid.setBraking(!boid.isBraking());
                case '+' -> boid.setMaxSpeed(boid.getMaxSpeed() * 1.25f);
                case '-' -> boid.setMaxSpeed(boid.getMaxSpeed() / 1.25f);
                case 'f' -> boid.setMaxForce(boid.getMaxForce() * 1.25f);
                case 'v' -> boid.setMaxForce(boid.getMaxForce() / 1.25f);
                default -> {
                }
            }
        } else if (mode == 2) {
            if (key == '+') {
                arrive.setK(arrive.getK() * 2);
            } else if (key == '-') {
                arrive.setK(arrive.getK() / 2);
            }
        } else if (mode == 3) {
            if (key == 'p' && recorded.size() >= 2) {
                patrol.setPath(definePath());
                recorded.clear();
            } else if (key == 'x') {
                recorded.clear();
            }
        }
    }
}