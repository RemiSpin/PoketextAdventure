package Utils;

import javafx.concurrent.Task;
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Uses Java Sound API with proper mixing support
 */
public class MusicManager {
    private static MusicManager instance;
    private final Map<String, String> locationMusicMap;
    private final AtomicReference<String> currentTrack = new AtomicReference<>();
    private final AtomicReference<String> previousTrack = new AtomicReference<>();
    private final AtomicReference<String> intendedTrack = new AtomicReference<>();
    private final AtomicBoolean isPlaying = new AtomicBoolean(false);
    private final AtomicBoolean isMuted = new AtomicBoolean(false);
    private volatile Clip currentClip;
    private volatile float currentVolume = 0.7f; // Default volume (70%)
    private volatile Task<Void> musicTask;

    // Settings file configuration
    private static final String SETTINGS_DIR = System.getProperty("user.home") + File.separator + ".poketextadventure";
    private static final String SETTINGS_FILE = SETTINGS_DIR + File.separator + "audio_settings.properties";
    private static final String VOLUME_KEY = "volume";
    private static final String MUTED_KEY = "muted";

    private MusicManager() {
        locationMusicMap = new HashMap<>();
        initializeMusicMappings();

        // Load saved settings
        loadSettings();

        // Set system properties for better audio compatibility with PipeWire/PulseAudio
        // Remove DirectAudioDevice to allow mixing with other applications
        System.setProperty("javax.sound.sampled.Clip", "com.sun.media.sound.MixerProvider");
        System.setProperty("javax.sound.sampled.Port", "com.sun.media.sound.PortMixerProvider");
        System.setProperty("javax.sound.sampled.SourceDataLine", "com.sun.media.sound.MixerProvider");
        System.setProperty("javax.sound.sampled.TargetDataLine", "com.sun.media.sound.MixerProvider");

        // Enable software mixing for better compatibility
        System.setProperty("javax.sound.sampled.Clip.bufferSize", "4096");
    }

    public static MusicManager getInstance() {
        if (instance == null) {
            synchronized (MusicManager.class) {
                if (instance == null) {
                    instance = new MusicManager();
                }
            }
        }
        return instance;
    }

    private void initializeMusicMappings() {
        // Town music mappings
        locationMusicMap.put("Pallet Town", "PalletTown.mp3");
        locationMusicMap.put("Viridian City", "ViridianCity.mp3");
        locationMusicMap.put("Pewter City", "ViridianCity.mp3");

        // Building music mappings
        locationMusicMap.put("Pokemon Center", "Center.mp3");
        locationMusicMap.put("Viridian Pokemon Center", "Center.mp3");
        locationMusicMap.put("Pewter Pokemon Center", "Center.mp3");
        locationMusicMap.put("Professor Oak's Laboratory", "PalletTown.mp3");
        locationMusicMap.put("Pewter Gym", "Gym.mp3");
        locationMusicMap.put("Player Home", "PalletTown.mp3");

        // Route music mappings
        locationMusicMap.put("Route 1", "R1.mp3");
        locationMusicMap.put("Route 22", "R22.mp3");
        locationMusicMap.put("Route 2 South", "R1.mp3");
        locationMusicMap.put("Route 2 North", "R1.mp3");
        locationMusicMap.put("Viridian Forest", "Forest.mp3");

        // Battle music mappings
        locationMusicMap.put("BATTLE_WILD", "Wild.mp3");
        locationMusicMap.put("BATTLE_TRAINER", "Trainer.mp3");
        locationMusicMap.put("BATTLE_GYM_LEADER", "GymLeader.mp3");
    }

    /**
     * Play music for a specific location
     */
    public void playLocationMusic(String locationName) {
        // Handle special location name mappings
        String normalizedName = normalizeLocationName(locationName);
        String musicFile = locationMusicMap.get(normalizedName);

        if (musicFile != null) {
            // Always update intended track, even when muted
            intendedTrack.set(musicFile);

            // Only play if not muted and it's a different track
            if (!isMuted.get() && !musicFile.equals(currentTrack.get())) {
                playMusic(musicFile);
            }
        }
    }

    /**
     * Play battle music
     */
    public void playBattleMusic(String battleType) {
        // Store current intended track as previous (for restoration after battle)
        previousTrack.set(intendedTrack.get());

        String musicKey = "BATTLE_" + battleType.toUpperCase();
        String musicFile = locationMusicMap.get(musicKey);

        if (musicFile != null) {
            // Update intended track
            intendedTrack.set(musicFile);

            // Play if not muted
            if (!isMuted.get()) {
                playMusic(musicFile);
            }
        }
    }

    /**
     * Stop current music and play a new track
     */
    private void playMusic(String musicFile) {
        // Stop current music if playing
        stopMusic();

        // Start new music in background thread
        musicTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                playMusicFile(musicFile);
                return null;
            }
        };

        Thread musicThread = new Thread(musicTask);
        musicThread.setDaemon(true);
        musicThread.setName("Music-" + musicFile);
        musicThread.start();
    }

    private void playMusicFile(String musicFile) {
        try {
            // Load the audio file from resources
            InputStream audioStream = getClass().getResourceAsStream("/Music/" + musicFile);
            if (audioStream == null) {
                System.err.println("Could not find music file: " + musicFile);
                return;
            }

            // Create a buffered input stream for better performance
            BufferedInputStream bufferedStream = new BufferedInputStream(audioStream);

            // Get audio input stream with MP3 support
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);

            // Get the format and create a decoded stream if necessary
            AudioFormat format = audioInputStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    16,
                    format.getChannels(),
                    format.getChannels() * 2,
                    format.getSampleRate(),
                    false);

            AudioInputStream decodedAudioInputStream = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

            // Create and configure the clip
            currentClip = AudioSystem.getClip();
            currentClip.open(decodedAudioInputStream);

            // Set volume
            setClipVolume(currentClip, currentVolume);

            // Set to loop continuously
            currentClip.loop(Clip.LOOP_CONTINUOUSLY);

            // Update current track
            currentTrack.set(musicFile);
            isPlaying.set(true);

        } catch (Exception e) {
            System.err.println("Error playing music file " + musicFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setClipVolume(Clip clip, float volume) {
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();

            // Convert volume (0.0 to 1.0) to decibel range
            float gain = min + (max - min) * volume;
            gainControl.setValue(gain);
        } catch (Exception e) {
            System.err.println("Could not set volume: " + e.getMessage());
        }
    }

    /**
     * Stop current music
     */
    public void stopMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
        }

        if (musicTask != null && musicTask.isRunning()) {
            musicTask.cancel(true);
        }

        isPlaying.set(false);
        currentTrack.set(null);
    }

    /**
     * Pause current music
     */
    public void pauseMusic() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            isPlaying.set(false);
        }
    }

    /**
     * Resume paused music
     */
    public void resumeMusic() {
        if (currentClip != null && !currentClip.isRunning() && !isMuted.get()) {
            currentClip.start();
            isPlaying.set(true);
        }
    }

    /**
     * Set volume (0.0 to 1.0)
     */
    public void setVolume(float volume) {
        currentVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (currentClip != null) {
            setClipVolume(currentClip, currentVolume);
        }
        // Save settings when volume changes
        saveSettings();
    }

    /**
     * Get current volume
     */
    public float getVolume() {
        return currentVolume;
    }

    /**
     * Toggle mute
     */
    public void toggleMute() {
        if (isMuted.get()) {
            unmute();
        } else {
            mute();
        }
    }

    /**
     * Mute music
     */
    public void mute() {
        isMuted.set(true);
        stopMusic();
        // Save settings when mute state changes
        saveSettings();
    }

    /**
     * Unmute music
     */
    public void unmute() {
        isMuted.set(false);

        // Play the intended track if there is one
        String intended = intendedTrack.get();
        if (intended != null) {
            playMusic(intended);
        }
        // Save settings when mute state changes
        saveSettings();
    }

    /**
     * Check if music is currently playing
     */
    public boolean isPlaying() {
        return isPlaying.get() && currentClip != null && currentClip.isRunning();
    }

    /**
     * Check if music is muted
     */
    public boolean isMuted() {
        return isMuted.get();
    }

    /**
     * Get current track name
     */
    public String getCurrentTrack() {
        return currentTrack.get();
    }

    /**
     * Get intended track name (what should be playing based on current location)
     */
    public String getIntendedTrack() {
        return intendedTrack.get();
    }

    /**
     * Normalize location names to handle variations
     */
    private String normalizeLocationName(String locationName) {
        if (locationName == null) {
            return "";
        }

        // Handle home variations
        if (locationName.endsWith("'s Home")) {
            return "Player Home";
        }

        // Handle Pokemon Center variations
        if (locationName.contains("Pokemon Center") || locationName.contains("Pokémon Center")) {
            if (locationName.contains("Viridian")) {
                return "Viridian Pokemon Center";
            } else if (locationName.contains("Pewter")) {
                return "Pewter Pokemon Center";
            } else {
                return "Pokemon Center";
            }
        }

        return locationName;
    }

    /**
     * Restore the previous music track (used when exiting battles)
     */
    public void restorePreviousMusic() {
        String prevTrack = previousTrack.get();
        if (prevTrack != null) {
            // Update intended track to the previous one
            intendedTrack.set(prevTrack);

            // Play if not muted and it's different from current
            if (!isMuted.get() && !prevTrack.equals(currentTrack.get())) {
                playMusic(prevTrack);
            }
        }
    }

    /**
     * Cleanup resources when shutting down
     */
    public void shutdown() {
        stopMusic();
        if (musicTask != null) {
            musicTask.cancel(true);
        }
    }

    /**
     * Load audio settings from file
     */
    private void loadSettings() {
        try {
            File settingsFile = new File(SETTINGS_FILE);
            if (!settingsFile.exists()) {
                // Create settings directory if it doesn't exist
                File settingsDir = new File(SETTINGS_DIR);
                if (!settingsDir.exists()) {
                    settingsDir.mkdirs();
                }
                // Use default settings
                return;
            }

            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(settingsFile)) {
                props.load(fis);

                // Load volume
                String volumeStr = props.getProperty(VOLUME_KEY, "0.7");
                try {
                    float volume = Float.parseFloat(volumeStr);
                    currentVolume = Math.max(0.0f, Math.min(1.0f, volume));
                } catch (NumberFormatException e) {
                    currentVolume = 0.7f; // Default
                }

                // Load mute state
                String mutedStr = props.getProperty(MUTED_KEY, "false");
                isMuted.set(Boolean.parseBoolean(mutedStr));
            }
        } catch (IOException e) {
            System.err.println("Could not load audio settings: " + e.getMessage());
            // Use defaults if loading fails
            currentVolume = 0.7f;
            isMuted.set(false);
        }
    }

    /**
     * Save audio settings to file
     */
    private void saveSettings() {
        try {
            // Create settings directory if it doesn't exist
            File settingsDir = new File(SETTINGS_DIR);
            if (!settingsDir.exists()) {
                settingsDir.mkdirs();
            }

            Properties props = new Properties();
            props.setProperty(VOLUME_KEY, String.valueOf(currentVolume));
            props.setProperty(MUTED_KEY, String.valueOf(isMuted.get()));

            try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
                props.store(fos, "PokeText Adventure Audio Settings");
            }
        } catch (IOException e) {
            System.err.println("Could not save audio settings: " + e.getMessage());
        }
    }
}
