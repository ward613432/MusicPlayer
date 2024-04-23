import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private Clip clip;
    public Timer timer;
    private String playingSong;
    private JLabel currentTime;
    private JLabel totalTime;
    private JComboBox<String> songSelector;

    public static void main(String[] args) {
        MusicPlayer Player = new MusicPlayer();
    }
    public MusicPlayer() {
        String[] songList = {
            "Code Kings.wav",
            "Dumbbells.wav",
            "Fire in My Belly.wav",
            "For Loop.wav",
            "GORG.wav",
            "GUI Mastermind.wav",
            "Hashin_ in the Code.wav",
            "Hey Papi.wav",
            "Mr. Scott.wav",
            "Programming.wav",
            "The Boolean Blues.wav",
            "The Codebrreaker_s Fury.wav"
        };

        JFrame frame = new JFrame("Spopify");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JPanel buttonPanel = new JPanel();
        JButton backwardButton = new JButton("<- 15s");
        JButton playButton = new JButton("Play");
        JButton pauseButton = new JButton("Pause");
        JButton forwardButton = new JButton("15s ->");
        buttonPanel.add(backwardButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(forwardButton);

        JPanel labelPanel = new JPanel();
        currentTime = new JLabel("Current Time: 0s");
        totalTime = new JLabel("Total Length: 0s");
        labelPanel.add(currentTime);
        labelPanel.add(totalTime);

        JPanel dropdownPanel = new JPanel();
        songSelector = new JComboBox<>(songList);
        dropdownPanel.add(songSelector);
        
        backwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getTimerPosSec() - 15 > 0) {
                    clip.setMicrosecondPosition((getTimerPosSec() - 15)*1000000);
                } else {
                    clip.setMicrosecondPosition(0);
                }
                updateLabels();
            }
        });

        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playMusic(songSelector.getSelectedItem().toString());
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseMusic(songSelector.getSelectedItem().toString());
            }
        });
        
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getTimerPosSec() + 15 < getTimerLenSec()) {
                    clip.setMicrosecondPosition((getTimerPosSec() + 15)*1000000);
                } else {
                    clip.setMicrosecondPosition(clip.getMicrosecondLength());
                }
                updateLabels();
            }
        });

        panel.add(dropdownPanel);
        panel.add(buttonPanel);
        panel.add(labelPanel);

        frame.add(panel);
        frame.setVisible(true);
    }

    public void playMusic(String selectedSong) {
        if (clip != null && !clip.isRunning() && playingSong.equalsIgnoreCase(songSelector.getSelectedItem().toString())) {
            clip.start();
            startTimer();
            return;
        }

        try {
            File file = new File(System.getProperty("user.dir") + "/src/audio/" + selectedSong);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            startTimer();
            updateLabels();
            playingSong = selectedSong;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void pauseMusic(String selectedSong) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            stopTimer();
        }
    }

    public void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateLabels();
            }
        });
        timer.start();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void updateLabels() {
        if (clip != null && clip.isOpen()) {
            if (clip.isRunning()) {
                currentTime.setText(String.format("Current Time: %ss", getTimerPosSec()));
                totalTime.setText(String.format("Total Length: %ss", getTimerLenSec()));
            }
        }
    }

    public int getTimerPosSec() {
        return (int)clip.getMicrosecondPosition()/1000000;
    }

    public int getTimerLenSec() {
        return (int)clip.getMicrosecondLength()/1000000;
    }
}