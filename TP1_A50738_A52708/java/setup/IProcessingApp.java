package setup;

import processing.core.PApplet;

/*
 * Todas as simulações futuras iram necessitar obrigatóriamente dos métodos presentes nesta interface
 * Esta Interface é um contrato.
*/
public interface IProcessingApp {

    void setup(PApplet p);

    void draw(PApplet p, float dt);

    void mousePressed(PApplet p);

    void keyPressed(PApplet p);
}
