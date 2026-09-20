package tp0.logo;

import java.util.ArrayList;

import processing.core.PApplet;
import setup.IProcessingApp;

public class Logo implements IProcessingApp {

    private static final float TAMANHO = 100;
    private static final float VELOCIDADE = 3;

    private static class Instancia {
        float x, y, alvoX, alvoY;

        Instancia(float x, float y, float alvoX, float alvoY) {
            this.x = x;
            this.y = y;
            this.alvoX = alvoX;
            this.alvoY = alvoY;
        }
    }

    private ArrayList<Instancia> logos;
    private float temporizador;
    private boolean cliquePendente;
    private float cliqueX, cliqueY;

    @Override
    public void setup(PApplet p) {
        logos = new ArrayList<>();
        temporizador = 0;
        cliquePendente = false;
    }

    // ---------- 1b: logo novo de 2 em 2 s, sem movimento ----------
    @Override
    public void draw(PApplet p, float dt) {
        temporizador += dt;
        if (temporizador >= 2) {
            float x = p.random(50, p.width - 50);
            float y = p.random(50, p.height - 50);
            logos.add(new Instancia(x, y, x, y));
            temporizador -= 2;
        }

        p.background(255);
        desenharTodos(p);
    }

    // ---------- 1c: como o 1b, mas nasce na origem e desliza (easing) ----------
    // @Override
    // public void draw(PApplet p, float dt) {
    // temporizador += dt;
    // if (temporizador >= 2) {
    // logos.add(new Instancia(0, 0, p.random(50, p.width - 50), p.random(50,
    // p.height - 50)));
    // temporizador -= 2;
    // }
    //
    // moverTodos(dt);
    // p.background(255);
    // desenharTodos(p);
    // }

    // ---------- 1d: o clique do rato define o destino ----------
    // @Override
    // public void draw(PApplet p, float dt) {
    // if (cliquePendente) {
    // logos.add(new Instancia(0, 0, cliqueX, cliqueY));
    // cliquePendente = false;
    // }
    //
    // moverTodos(dt);
    // p.background(255);
    // desenharTodos(p);
    // }

    @Override
    public void mousePressed(PApplet p) {
        cliquePendente = true;
        cliqueX = p.mouseX;
        cliqueY = p.mouseY;
    }

    @Override
    public void keyPressed(PApplet p) {
    }

    private void moverTodos(float dt) {
        float fator = 1 - (float) Math.exp(-VELOCIDADE * dt);
        for (Instancia l : logos) {
            l.x += (l.alvoX - l.x) * fator;
            l.y += (l.alvoY - l.y) * fator;
        }
    }

    private void desenharTodos(PApplet p) {
        for (Instancia l : logos) {
            desenharLogo(p, l.x, l.y, TAMANHO);
        }
    }

    private void desenharLogo(PApplet p, float x, float y, float diametro) {
        p.pushStyle();
        p.pushMatrix();

        p.translate(x, y);
        p.scale(diametro / 200f);

        p.noStroke();
        p.fill(0);
        p.triangle(-18, -68, 18, -68, 0, -27);
        p.triangle(0, 28, -18, 67, 18, 67);
        desenharLado(p, 1);
        desenharLado(p, -1);

        p.noFill();
        p.stroke(0);
        p.strokeWeight(9.5f);
        p.circle(0, 0, 190.5f);

        p.popMatrix();
        p.popStyle();
    }

    private void desenharLado(PApplet p, float lado) {
        p.beginShape();
        p.vertex(lado * -38, -57);
        p.vertex(0, -2);
        p.vertex(0, 2);
        p.vertex(lado * -26, 27);
        p.vertex(lado * -57, -40);
        p.endShape(PApplet.CLOSE);

        p.beginShape();
        p.vertex(lado * -68, -14);
        p.vertex(lado * -71, 6);
        p.vertex(lado * -67, 21);
        p.vertex(lado * -56, 42);
        p.vertex(lado * -34, 60);
        p.endShape(PApplet.CLOSE);
    }
}
