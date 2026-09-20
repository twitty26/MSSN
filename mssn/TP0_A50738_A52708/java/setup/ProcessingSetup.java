package setup;

import processing.core.PApplet;
import tp0.face.Face;
// import tp0.logo.Logo;

/*
 * Queres escrever mais classes que usem os métodos do Processing.
 * Mas só uma classe pode herdar de PApplet, e Java não tem herança múltipla.
 * A solução é não herdar: passar a referência.
 * Uma classe fica encarregue do arranque, e as outras recebem o PApplet como parâmetro.
*/
public class ProcessingSetup extends PApplet {

    // O app tem de ser static porque é atribuído dentro do main(), que é static.
    private static IProcessingApp app;

    private int lastUpdate;

    /*
     * Definição do size da janela de simulação
     * O size() só pode ser chamado aqui.
     * O settings() corre antes de o sketch existir, e é por isso
     * que quase nenhuma outra função do Processing funciona lá dentro.
     */
    @Override
    public void settings() {
        size(800, 600);
        smooth(8);
    }

    /*
     * O setup() corre uma vez
     */
    @Override
    public void setup() {
        app.setup(this);
        lastUpdate = millis();
    }

    /*
     * O draw() corre a cada frame
     */
    @Override
    public void draw() {
        int now = millis();
        /*
         * Divisão por 1000 para passar de milis para segundos
         * Usa-se o 1000f porque numa divisão por 1000 a divisão é inteira que faz com
         * que o dt seja sempre 0
         */
        float dt = (now - lastUpdate) / 1000f;
        lastUpdate = now;
        app.draw(this, dt);
    }

    @Override
    public void mousePressed() {
        app.mousePressed(this);
    }

    @Override
    public void keyPressed() {
        app.keyPressed(this);
    }

    public static void main(String[] args) {
        // app = new Hello2();
        // PApplet.main(ProcessingSetup.class);

        // app = new ca.ForestFireApp();
        // PApplet.main(ProcessingSetup.class.getName());

        // app = new Logo();
        // PApplet.main(ProcessingSetup.class);

        app = new Face();
        PApplet.main(ProcessingSetup.class);
    }
}
