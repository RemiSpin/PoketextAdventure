package Utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for MusicManager utility
 * Demonstrates singleton pattern testing and utility class testing
 */
public class MusicManagerTest {

    private MusicManager musicManager;

    @BeforeEach
    void setUp() {
        musicManager = MusicManager.getInstance();
    }

    @Test
    @DisplayName("Test Singleton Pattern")
    void testSingletonPattern() {
        MusicManager instance1 = MusicManager.getInstance();
        MusicManager instance2 = MusicManager.getInstance();

        assertNotNull(instance1, "First instance should not be null");
        assertNotNull(instance2, "Second instance should not be null");
        assertSame(instance1, instance2, "Both instances should be the same object (singleton)");
    }

    @Test
    @DisplayName("Test Initial State")
    void testInitialState() {
        assertNotNull(musicManager, "MusicManager should be initialized");
        assertFalse(musicManager.isPlaying(), "Music should not be playing initially");
        assertTrue(musicManager.getVolume() >= 0.0f && musicManager.getVolume() <= 1.0f,
                "Volume should be between 0.0 and 1.0");
    }

    @Test
    @DisplayName("Test Volume Control")
    void testVolumeControl() {
        float originalVolume = musicManager.getVolume();

        // Test setting volume
        musicManager.setVolume(0.5f);
        assertEquals(0.5f, musicManager.getVolume(), 0.01f, "Volume should be set to 0.5");

        // Test volume bounds
        musicManager.setVolume(1.5f); // Should be clamped to 1.0
        assertTrue(musicManager.getVolume() <= 1.0f, "Volume should not exceed 1.0");

        musicManager.setVolume(-0.5f); // Should be clamped to 0.0
        assertTrue(musicManager.getVolume() >= 0.0f, "Volume should not be negative");

        // Restore original volume
        musicManager.setVolume(originalVolume);
    }

    @Test
    @DisplayName("Test Mute Functionality")
    void testMuteFunctionality() {
        boolean originalMuteState = musicManager.isMuted();

        // Test muting
        musicManager.mute();
        assertTrue(musicManager.isMuted(), "Music should be muted");

        // Test unmuting
        musicManager.unmute();
        assertFalse(musicManager.isMuted(), "Music should be unmuted");

        // Test toggle - start from unmuted state
        musicManager.unmute(); // Ensure we start unmuted
        boolean beforeToggle = musicManager.isMuted();
        musicManager.toggleMute();
        boolean afterToggle = musicManager.isMuted();
        assertEquals(!beforeToggle, afterToggle, "Mute state should be toggled");

        // Restore original state if needed
        if (originalMuteState != musicManager.isMuted()) {
            musicManager.toggleMute();
        }
    }

    @Test
    @DisplayName("Test Music Track Management")
    void testMusicTrackManagement() {
        String testLocation = "PalletTown";

        // Test that we can call play methods without throwing exceptions
        assertDoesNotThrow(() -> {
            musicManager.playLocationMusic(testLocation);
        }, "Should not throw exception when playing location music");

        assertDoesNotThrow(() -> {
            musicManager.stopMusic();
        }, "Should not throw exception when stopping music");

        assertDoesNotThrow(() -> {
            musicManager.pauseMusic();
        }, "Should not throw exception when pausing music");
    }
}
