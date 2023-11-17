package de.ja.model.audio;

import javax.sound.sampled.Clip;
import javax.swing.*;

/**
 * PlayBackThread zum Handhaben und Abarbeiten von
 * Informationen für {@link de.ja.view.explanation.audio.player.AudioPlayerPanel}
 */
public class PlayBackThread extends Thread {

    private final JLabel timePlayedLabel;
    private final JSlider playBackSlider;

    private Clip audioClip;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private boolean isReset = false;

    public PlayBackThread(JLabel timePlayedLabel, JSlider playBackSlider) {
        this.timePlayedLabel = timePlayedLabel;
        this.playBackSlider = playBackSlider;
    }

    public void setAudioClip(Clip audioClip) {
        this.audioClip = audioClip;
        playBackSlider.setMaximum((int) (audioClip.getMicrosecondLength()));
    }

    @Override
    public void run() {
        isRunning = true;
        while(isRunning) {
            if(isReset) {
                playBackSlider.setValue(0);
                isRunning = false;
                break;
            }
            if(!isPaused) {
                if(audioClip != null && audioClip.isRunning()) {
                    timePlayedLabel.setText(timeString());
                    int currentSecond = (int) audioClip.getMicrosecondPosition();
                    playBackSlider.setValue(currentSecond);
                }
            }
        }
    }

    public void resetPlayBackTimer() {
        isReset = true;
        isRunning = false;
    }

    public void pausePlayBackTimer() {
        isPaused = true;
    }

    public void resumePlayBackTimer() {
        isPaused = false;
    }

    private String timeString() {
        long totalSec = audioClip.getMicrosecondPosition() / 1_000_000;
        long sec = totalSec % 60;
        long min = (totalSec / 60) % 60;

        return String.format("%02d:%02d", min, sec);
    }
}
