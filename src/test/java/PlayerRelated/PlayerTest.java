package PlayerRelated;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for Player functionality
 * Demonstrates basic unit testing with JUnit 5
 */
public class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        // Create a fresh player instance before each test
        player = new Player("TestTrainer");
    }

    @Test
    @DisplayName("Test Player Creation")
    void testPlayerCreation() {
        assertNotNull(player, "Player should be created successfully");
        assertEquals("TestTrainer", Player.getName(), "Player name should be set correctly");
        assertEquals(0, player.getMoney(), "New player should start with 0 money");
        assertTrue(player.getParty().isEmpty(), "New player should have empty party");
        assertTrue(player.getPC().isEmpty(), "New player should have empty PC");
        assertTrue(player.getBadges().isEmpty(), "New player should have no badges");
    }

    @Test
    @DisplayName("Test Money Management")
    void testMoneyManagement() {
        // Test initial money
        assertEquals(0, player.getMoney(), "Player should start with 0 money");

        // Test adding money
        player.addMoney(500);
        assertEquals(500, player.getMoney(), "Money should be added correctly");

        // Test setting money
        player.setMoney(1000);
        assertEquals(1000, player.getMoney(), "Money should be set correctly");

        // Test adding more money
        player.addMoney(250);
        assertEquals(1250, player.getMoney(), "Money should accumulate correctly");
    }

    @Test
    @DisplayName("Test Default Constructor")
    void testDefaultConstructor() {
        Player defaultPlayer = new Player();
        assertEquals(1000, defaultPlayer.getMoney(), "Default player should start with 1000 money");
        assertNotNull(defaultPlayer.getParty(), "Party should be initialized");
        assertNotNull(defaultPlayer.getPC(), "PC should be initialized");
        assertNotNull(defaultPlayer.getBadges(), "Badges should be initialized");
    }

    @Test
    @DisplayName("Test Collections Are Unmodifiable")
    void testUnmodifiableCollections() {
        List<String> badges = player.getBadges();
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            badges.add("Boulder Badge");
        }, "Badges list should be unmodifiable");
        assertNotNull(exception, "Exception should be thrown when trying to modify badges list");
    }

    @Test
    @DisplayName("Test Negative Money Handling")
    void testNegativeMoneyHandling() {
        player.setMoney(100);
        player.addMoney(-50);
        assertEquals(50, player.getMoney(), "Should handle negative money additions");

        player.addMoney(-100);
        assertEquals(-50, player.getMoney(), "Should allow negative money values");
    }

    @AfterEach
    void tearDown() {
        // Clean up after each test if needed
        player = null;
    }
}
