package tp1.dla;

import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

/*
 * Partícula do DLA: vagueia ao acaso (WANDER) até tocar numa partícula parada; aí pode parar
 * (STOPPED) e passa a fazer parte do agregado.
 */
public class Walker {

    public enum State {
        STOPPED, WANDER
    }

    private static final int RADIUS = 2;
    public static int num_wanders = 0;
    public static int num_stopped = 0;

    private final PVector pos;
    private State state;
    private int colour;

    /*
     * Partícula em movimento numa posição ao acaso da janela. Não nasce num círculo à volta do
     * centro porque, com inicializações em linha, círculo ou quadrado, o agregado não está no centro.
     */
    public Walker(PApplet p) {
        pos = new PVector(p.random(p.width), p.random(p.height));
        setState(p, State.WANDER);
    }

    /*
     * Partícula parada numa posição escolhida (semente).
     */
    public Walker(PApplet p, PVector pos) {
        this.pos = pos;
        setState(p, State.STOPPED);
    }

    /*
     * Critério de cor: as paradas são coloridas pela ordem em que pararam (a cor roda no círculo HSB),
     * por isso os anéis de cor mostram como o agregado cresceu; as que se movem são brancas.
     */
    private void setState(PApplet p, State state) {
        if (this.state == State.WANDER) {
            num_wanders--;
        }
        this.state = state;
        if (state == State.STOPPED) {
            colour = p.color((num_stopped * 0.5f) % 360, 80, 100);
            num_stopped++;
        } else {
            colour = p.color(0, 0, 100, 100);
            num_wanders++;
        }
    }

    public State getState() {
        return state;
    }

    /*
     * Se tocar numa partícula parada, pára com probabilidade stickiness (1 = pára sempre).
     * Duas partículas tocam-se quando a distância entre centros é menor que dois raios.
     */
    public void updateState(PApplet p, List<Walker> walkers, float stickiness) {
        for (Walker w : walkers) {
            if (w.state == State.STOPPED && PVector.dist(pos, w.pos) < 2 * RADIUS) {
                if (p.random(1) < stickiness) {
                    setState(p, State.STOPPED);
                }
                return;
            }
        }
    }

    /*
     * Passeio aleatório: um passo de comprimento 1 numa direção ao acaso, sem sair da janela.
     */
    public void wander(PApplet p) {
        pos.add(PVector.random2D());
        pos.x = PApplet.constrain(pos.x, 0, p.width);
        pos.y = PApplet.constrain(pos.y, 0, p.height);
    }

    public void display(PApplet p) {
        p.fill(colour);
        p.circle(pos.x, pos.y, 2 * RADIUS);
    }

    public static int getDiameter() {
        return 2 * RADIUS;
    }
}
