package semana1_2;

import processing.core.PApplet;

/*
 * Classe onde se testa o Processing sem criar um processing setup.
*/
public class Hello extends PApplet {

    /*
     * O PApplet.main() encontra a tua classe, cria uma instância, e arranca o ciclo
     * de vida do Processing: chama setup() uma vez, e depois draw() a cada nova
     * frame, para sempre.
     */
    public static void main(String[] args) {
        PApplet.main(Hello.class);
    }

    /*
     * Definição do size da janela de simulação
     * O size() só pode ser chamado aqui.
     * O settings() corre antes de o sketch existir, e é por isso
     * que quase nenhuma outra função do Processing funciona lá dentro.
     */
    @Override
    public void settings() {
        size(800, 600);
    }

    /*
     * O setup() corre uma vez
     */
    @Override
    public void setup() {
        fill(255, 0, 0);
    }

    /*
     * O draw() corre a cada frame
     */
    @Override
    public void draw() {
        circle(mouseX, mouseY, 50);
    }
}