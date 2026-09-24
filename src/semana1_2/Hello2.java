package semana1_2;

import processing.core.PApplet;
import setup.IProcessingApp;

public class Hello2 implements IProcessingApp {

    public void setup(PApplet p) {
        p.fill(0, 255, 0);
    }

    @Override
    public void draw(PApplet p, float dt) {
        p.circle(p.mouseX, p.mouseY, 50);
    }

    @Override
    public void mousePressed(PApplet p) {
    }

    @Override
    public void keyPressed(PApplet p) {
    }
}
