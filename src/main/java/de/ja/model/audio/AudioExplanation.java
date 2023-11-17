package de.ja.model.audio;

import javax.sound.sampled.*;
import java.io.IOException;

public class AudioExplanation implements LineListener {

    private final Clip audioClip;
    private volatile boolean playBackActive;

    public AudioExplanation(AudioInputStream audioInputStream) {
        try {
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.addLineListener(this);
            audioClip.open(audioInputStream);
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Clip getAudioClip() {
        return audioClip;
    }

    public void play() {
        playBackActive = true;
        audioClip.setFramePosition(0);
        audioClip.start();
    }

    public void pause() {
        playBackActive = false;
        audioClip.stop();
    }

    public void resume() {
        if(!playBackActive && audioClip.getMicrosecondPosition() > 0) {
            playBackActive = true;
            audioClip.start();
        }
    }

    public boolean isPlayBackActive() {
        return playBackActive;
    }

    public boolean isResumed() {
        return audioClip.getMicrosecondPosition() > 0;
    }

    public boolean isPlayBackFinished() {
        return audioClip.getFramePosition() == audioClip.getFrameLength();
    }

    public String getClipLengthString() {
        long totalSec = audioClip.getMicrosecondLength() / 1_000_000;
        long sec = totalSec % 60;
        long min = (totalSec / 60) % 60;

        return String.format("%02d:%02d", min, sec);
    }

    @Override
    public void update(LineEvent event) {
        if(event.getType().equals(LineEvent.Type.STOP)) {
            playBackActive = false;
        }
    }
}
