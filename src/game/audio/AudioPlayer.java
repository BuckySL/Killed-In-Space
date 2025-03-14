package game.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {
    
    private Clip clip;
    private boolean isPlaying = false;
    private FloatControl volumeControl;
    
    public AudioPlayer() {
        // Constructor
    }
    
    public void playMusic(String filePath, boolean loop) {
        try {
            // Stop any currently playing music
            stopMusic();
            
            // Get file from resources
            URL resourceUrl = getClass().getResource(filePath);
            AudioInputStream audioStream;
            
            if (resourceUrl != null) {
                audioStream = AudioSystem.getAudioInputStream(resourceUrl);
            } else {
                // Try as absolute path if not found in resources
                File file = new File(filePath);
                if (file.exists()) {
                    audioStream = AudioSystem.getAudioInputStream(file);
                } else {
                    System.err.println("Could not find audio file: " + filePath);
                    return;
                }
            }
            
            // Get a clip resource
            clip = AudioSystem.getClip();
            
            // Open audio clip and load samples from the audio input stream
            clip.open(audioStream);
            
            // Get the volume control
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }
            
            // Set looping if required
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            
            // Play the clip
            clip.start();
            isPlaying = true;
            
            System.out.println("Now playing: " + filePath);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void stopMusic() {
        if (clip != null) {
            clip.stop();
            clip.close();
            isPlaying = false;
        }
    }
    
    public void pauseMusic() {
        if (clip != null && isPlaying) {
            clip.stop();
            isPlaying = false;
        }
    }
    
    public void resumeMusic() {
        if (clip != null && !isPlaying) {
            clip.start();
            isPlaying = true;
        }
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    // Adjust volume (0.0 to 1.0)
    public void setVolume(float volume) {
        if (volumeControl != null) {
            // Convert linear scale (0.0 to 1.0) to dB scale (-80.0 to 6.0)
            float dB = (float) (Math.log10(volume) * 20.0f);
            // Make sure the value is within the control's range
            dB = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), dB));
            volumeControl.setValue(dB);
        }
    }
}