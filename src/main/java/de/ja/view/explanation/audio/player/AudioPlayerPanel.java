package de.ja.view.explanation.audio.player;

import com.ibm.icu.text.SimpleDateFormat;
import de.ja.model.audio.AudioExplanation;
import de.ja.model.audio.PlayBackThread;
import de.ja.model.audio.SpeechResult;
import net.miginfocom.swing.MigLayout;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;

public class AudioPlayerPanel extends JPanel implements ActionListener,  LineListener {

    private final JLabel timePlayedLabel;

    private final JSlider playProgress;

    private final JLabel lengthLabel;

    private final JButton playButton;

    private File audioFile;

    private AudioExplanation audioExplanation;

    private final ImageIcon playIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/play-16x16.png")));
    private final ImageIcon pauseIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/pause-16x16.png")));

    public AudioPlayerPanel() {
        MigLayout imagePanelMigLayout = new MigLayout("" , "3[]20[]10[fill, grow]10[]3", "3[]3");
        setLayout(imagePanelMigLayout);
        setBorder(new TitledBorder("Generated Audio"));

        //UIManager.put("ProgressBar.foreground", Color.white);

        timePlayedLabel = new JLabel("00:00");
        timePlayedLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        timePlayedLabel.setVerticalTextPosition(SwingConstants.CENTER);

        playProgress = new JSlider();
        playProgress.setValue(0);

        lengthLabel = new JLabel("00:00");
        lengthLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        lengthLabel.setVerticalTextPosition(SwingConstants.CENTER);

        playButton = new JButton();
        playButton.setEnabled(false);
        playButton.setIcon(playIcon);
        playButton.addActionListener(this);

        add(playButton);
        add(timePlayedLabel);
        add(playProgress);
        add(lengthLabel);
    }

    public void setSpeechResult(SpeechResult speechResult) throws Exception {

        // Audio in Ordner speichern.
        Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/explanations/audio/"));
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy_HHmmss").format(new Date());
        String fileName = String.format("explanations/audio/%s-audio", timeStamp);
        String path = String.format("%s.%s", fileName, speechResult.getAudioType().subtype());
        File saveAudio = new File(path);
        Files.write(saveAudio.toPath(), speechResult.getBytes());

        audioFile = new File(String.format("%s.%s", fileName, "wav"));

        //Audio Attributes
        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(128000);
        audio.setChannels(2);
        audio.setSamplingRate(44100);

        //Encoding attributes
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audio);

        //Encode
        Encoder encoder = new Encoder();
        encoder.encode(new MultimediaObject(saveAudio), audioFile, attrs);

        if(audioFile.exists()) {
            playButton.setEnabled(true);
        }

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            audioExplanation = new AudioExplanation(audioInputStream);
            audioExplanation.getAudioClip().addLineListener(this);
            lengthLabel.setText(audioExplanation.getClipLengthString());
        } catch (IOException | UnsupportedAudioFileException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void resetSpeechResult() {
        this.audioFile = null;
        playButton.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(audioExplanation != null) {
            PlayBackThread playBackThread = new PlayBackThread(timePlayedLabel, playProgress);
            playBackThread.setAudioClip(audioExplanation.getAudioClip());
            if(audioExplanation.isPlayBackActive()) {
                System.out.println("Playback active... pausing.");
                audioExplanation.pause();
                playBackThread.pausePlayBackTimer();
            } else {
                if(audioExplanation.isPlayBackFinished()) {
                    System.out.println("Playback not active and already finished... starting.");
                    audioExplanation.play();
                    playBackThread.resetPlayBackTimer();
                    playBackThread.start();
                } else {
                    if(audioExplanation.isResumed()) {
                        System.out.println("Playback not active and paused... resuming.");
                        audioExplanation.resume();
                        playBackThread.resumePlayBackTimer();
                    } else {
                        System.out.println("Playback not active, not yet started and never resumed... starting.");
                        audioExplanation.play();
                        playBackThread.start();
                    }
                }
            }
        }
    }

    @Override
    public void update(LineEvent event) {
        if(event.getType().equals(LineEvent.Type.STOP)) {
            playButton.setIcon(playIcon);
        } else if (event.getType().equals(LineEvent.Type.START)) {
            playButton.setIcon(pauseIcon);
        }
    }
}
