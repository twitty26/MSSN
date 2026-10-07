package tp1.joga_da_vida;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/*
 * Som com o sintetizador MIDI que já vem no Java (não precisa de biblioteca extra).
 * Cada linha da grelha é uma nota da escala pentatónica e cada cor é um instrumento,
 * tocado no seu próprio canal MIDI.
 */
public class LifeMusic {

    private static final int[] PENTATONIC = { 0, 2, 4, 7, 9 };
    private static final int[] INSTRUMENTS = { 0, 11, 46, 13 }; // piano, vibrafone, harpa, xilofone
    private static final int BASE_NOTE = 36; // dó 2
    private static final int VELOCITY = 70;

    private MidiChannel[] channels;
    private boolean muted;

    public LifeMusic(int ninstruments) {
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            for (int i = 0; i < ninstruments; i++) {
                channels[i].programChange(INSTRUMENTS[i % INSTRUMENTS.length]);
            }
        } catch (MidiUnavailableException e) {
            System.out.println("MIDI indisponível: a simulação corre sem som.");
            channels = null;
        }
    }

    /*
     * A linha de cima é a mais aguda. A cada 5 linhas sobe-se uma oitava (12 meios-tons).
     */
    public void play(int row, int nrows, int instrument) {
        if (channels == null || muted) {
            return;
        }
        int degree = nrows - 1 - row;
        int note = BASE_NOTE + 12 * (degree / PENTATONIC.length) + PENTATONIC[degree % PENTATONIC.length];
        channels[instrument].noteOn(note, VELOCITY);
    }

    public void stopAll() {
        if (channels == null) {
            return;
        }
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