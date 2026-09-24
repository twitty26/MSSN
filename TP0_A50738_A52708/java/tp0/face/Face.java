package tp0.face;

import processing.core.PApplet;
import processing.core.PVector;
import setup.IProcessingApp;

public class Face implements IProcessingApp {

    private static final float OLHO_X = 55;
    private static final float OLHO_Y = -60;
    private static final float OLHO_LARGURA = 70;
    private static final float OLHO_ALTURA = 80;
    private static final float PUPILA = 30;
    private static final float DISTANCIA_SURPRESA = 220;

    @Override
    public void setup(PApplet p) {
    }

    @Override
    public void draw(PApplet p, float dt) {
        float cx = p.width / 2f;
        float cy = p.height / 2f;
        float ratoX = p.mouseX - cx;
        float ratoY = p.mouseY - cy;
        boolean surpreso = PApplet.dist(p.mouseX, p.mouseY, cx, cy) < DISTANCIA_SURPRESA;

        p.background(255);
        p.pushStyle();
        p.pushMatrix();
        p.translate(cx, cy);

        p.stroke(30);
        p.strokeWeight(6);
        p.fill(120, 200, 90);
        p.ellipse(0, 0, 320, 400);

        desenharOlho(p, -OLHO_X, OLHO_Y, ratoX, ratoY);
        desenharOlho(p, OLHO_X, OLHO_Y, ratoX, ratoY);
        desenharSobrancelhas(p, surpreso);
        desenharBoca(p, surpreso);

        p.popMatrix();
        p.popStyle();
    }

    @Override
    public void mousePressed(PApplet p) {
    }

    @Override
    public void keyPressed(PApplet p) {
    }

    private void desenharOlho(PApplet p, float olhoX, float olhoY, float ratoX, float ratoY) {
        p.stroke(30);
        p.strokeWeight(6);
        p.fill(255);
        p.ellipse(olhoX, olhoY, OLHO_LARGURA, OLHO_ALTURA);

        PVector desvio = new PVector(ratoX - olhoX, ratoY - olhoY);
        desvio.limit(OLHO_LARGURA / 2 - PUPILA / 2);

        p.noStroke();
        p.fill(20);
        p.circle(olhoX + desvio.x, olhoY + desvio.y, PUPILA);
    }

    private void desenharSobrancelhas(PApplet p, boolean surpreso) {
        float y = surpreso ? OLHO_Y - 70 : OLHO_Y - 50;
        p.stroke(30);
        p.strokeWeight(6);
        p.line(-OLHO_X - 30, y, -OLHO_X + 30, y);
        p.line(OLHO_X - 30, y, OLHO_X + 30, y);
    }

    private void desenharBoca(PApplet p, boolean surpreso) {
        p.stroke(30);
        p.strokeWeight(6);
        if (surpreso) {
            p.fill(60, 20, 30);
            p.ellipse(0, 100, 60, 80);
        } else {
            p.noFill();
            p.arc(0, 70, 140, 90, 0, PApplet.PI);
        }
    }
}
