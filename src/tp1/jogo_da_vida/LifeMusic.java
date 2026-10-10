package tp1.jogo_da_vida;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import tp1.celulas.Cell;
import tp1.celulas.CellularAutomata;

/*
 * Música com o sintetizador MIDI que já vem no Java (não precisa de biblioteca extra).
 *
 * Melodia: cada linha da grelha é uma nota da escala pentatónica de dó (3 oitavas, de cima
 * para baixo do agudo para o grave) e cada cor é um instrumento no seu próprio canal.
 *
 * Acompanhamento: a cada passagem do cursor muda o acorde de fundo (dó, lá, fá, sol), tocado por um pad.
 * Todos os canais têm reverberação para o som ficar mais suave.
 */
public class LifeMusic {

    private static final int[] PENTATONIC = { 0, 2, 4, 7, 9 };
    private static final int[] INSTRUMENTS = { 11, 12, 46, 8, 10 }; // vibrafone, marimba, harpa, celesta, caixa de
                                                                    // música
    private static final int MELODY_BASE = 60; // dó 4
    private static final int OCTAVES = 3;
    private static final int MAX_NOTES = 4;

    private static final int PAD_CHANNEL = 8; // o canal 9 é a bateria, por isso não se usa
    private static final int PAD_INSTRUMENT = 89; // pad "warm"
    private static final int[] CHORD_ROOTS = { 48, 45, 41, 43 }; // dó, lá, fá, sol
    private static final int[][] CHORD_SHAPES = { { 0, 7, 16 }, { 0, 7, 15 }, { 0, 7, 16 }, { 0, 7, 16 } };

    private MidiChannel[] channels;
    private final List<int[]> playing = new ArrayList<>();
    private boolean muted;

    public LifeMusic(int ninstruments) {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            for (int i = 0; i < ninstruments; i++) {
                setupChannel(i, INSTRUMENTS[i % INSTRUMENTS.length], 100);
            }
            setupChannel(PAD_CHANNEL, PAD_INSTRUMENT, 60);
        } catch (MidiUnavailableException e) {
            System.out.println("MIDI indisponível: a simulação corre sem som.");
            channels = null;
        }
    }

    private void setupChannel(int channel, int instrument, int volume) {
        channels[channel].programChange(instrument);
        channels[channel].controlChange(7, volume); // volume
        channels[channel].controlChange(91, 90); // reverberação
    }

    /*
     * Toca as células vivas de uma faixa de colunas [colStart, colEnd[.
     * Cada linha com células vivas é uma nota candidata; tocam no máximo MAX_NOTES,
     * espalhadas
     * do agudo ao grave, e cada uma com o instrumento da cor da primeira célula
     * viva da linha.
     * As notas do passo anterior são largadas aqui, por isso o som fica ligado, sem
     * cortes secos.
     * Os tempos fortes (accent) tocam mais alto, o que dá ritmo à melodia.
     */
    public void playBand(CellularAutomata ca, int colStart, int colEnd, boolean accent) {
        releaseMelody();
        if (channels == null || muted) {
            return;
        }
        List<int[]> candidates = new ArrayList<>(); // {linha, cor}
        for (int row = 0; row < ca.getNrows(); row++) {
            for (int col = colStart; col < colEnd; col++) {
                Cell c = ca.getCell(row, col);
                if (c.isAlive()) {
                    candidates.add(new int[] { row, c.getColorIndex() });
                    break;
                }
            }
        }
        int count = Math.min(MAX_NOTES, candidates.size());
        int velocity = accent ? 90 : 65;
        int lastNote = -1;
        for (int i = 0; i < count; i++) {
            int[] cand = candidates.get(i * candidates.size() / count);
            int note = noteForRow(cand[0], ca.getNrows());
            if (note == lastNote) {
                continue;
            }
            lastNote = note;
            channels[cand[1]].noteOn(note, velocity);
            playing.add(new int[] { cand[1], note });
        }
    }

    private int noteForRow(int row, int nrows) {
        int degree = (nrows - 1 - row) * PENTATONIC.length * OCTAVES / nrows;
        return MELODY_BASE + 12 * (degree / PENTATONIC.length) + PENTATONIC[degree % PENTATONIC.length];
    }

    private void releaseMelody() {
        if (channels == null) {
            return;
        }
        for (int[] n : playing) {
            channels[n[0]].noteOff(n[1]);
        }
        playing.clear();
    }

    public void playChord(int bar) {
        if (channels == null) {
            return;
        }
        channels[PAD_CHANNEL].allNotesOff();
        if (muted) {
            return;
        }
        int i = bar % CHORD_ROOTS.length;
        for (int interval : CHORD_SHAPES[i]) {
            channels[PAD_CHANNEL].noteOn(CHORD_ROOTS[i] + interval, 50);
        }
    }

    public void stopAll() {
        if (channels == null) {
            return;
        }
        playing.clear();
        for (MidiChannel c : channels) {
            c.allNotesOff();
        }
    }

    public void toggleMute() {
        muted = !muted;
        stopAll();
    }

    public boolean isMuted() {
        return muted;
    }
}