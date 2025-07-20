package BattleLogic;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * Test class for moveFactory functionality
 * Demonstrates testing of factory patterns and JSON data loading
 */
public class MoveFactoryTest {

    private moveFactory factory;

    @BeforeEach
    void setUp() {
        factory = new moveFactory();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Test Factory Creation")
        void testFactoryCreation() {
            assertNotNull(factory, "MoveFactory should be created successfully");
            assertNotNull(factory.pokemonLearnsets, "Pokemon learnsets map should be initialized");
        }

        @Test
        @DisplayName("Test Pokemon Learnsets Loading")
        void testPokemonLearnsetsLoading() {
            assertDoesNotThrow(() -> {
                factory.loadPokemonLearnsets();
            }, "Loading Pokemon learnsets should not throw exceptions");

            assertFalse(factory.pokemonLearnsets.isEmpty(),
                    "Pokemon learnsets should not be empty after loading");
        }
    }

    @Nested
    @DisplayName("Move Creation Tests")
    class MoveCreationTests {

        @Test
        @DisplayName("Test Create Moves From JSON")
        void testCreateMovesFromJson() {
            List<Move> moves = assertDoesNotThrow(() -> {
                return moveFactory.createMovesFromJson();
            }, "Creating moves from JSON should not throw exceptions");

            assertNotNull(moves, "Moves list should not be null");
            assertFalse(moves.isEmpty(), "Moves list should not be empty");
        }

        @Test
        @DisplayName("Test Move Properties")
        void testMoveProperties() {
            List<Move> moves = moveFactory.createMovesFromJson();

            if (!moves.isEmpty()) {
                Move firstMove = moves.get(0);
                assertNotNull(firstMove.getName(), "Move name should not be null");
                assertNotNull(firstMove.getType(), "Move type should not be null");
                assertNotNull(firstMove.getCategory(), "Move category should not be null");
                assertTrue(firstMove.getPower() >= 0, "Move power should not be negative");
                assertTrue(firstMove.getAccuracy() >= 0 && firstMove.getAccuracy() <= 100,
                        "Move accuracy should be between 0 and 100");
                assertTrue(firstMove.getPp() > 0, "Move PP should be positive");
            }
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Test Learnset Data Structure")
        void testLearnsetDataStructure() {
            factory.loadPokemonLearnsets();

            for (Map.Entry<String, Map<Integer, List<String>>> entry : factory.pokemonLearnsets.entrySet()) {
                String pokemonName = entry.getKey();
                Map<Integer, List<String>> learnset = entry.getValue();

                assertNotNull(pokemonName, "Pokemon name should not be null");
                assertFalse(pokemonName.trim().isEmpty(), "Pokemon name should not be empty");
                assertNotNull(learnset, "Learnset should not be null");

                for (Map.Entry<Integer, List<String>> moveEntry : learnset.entrySet()) {
                    Integer level = moveEntry.getKey();
                    List<String> movesAtLevel = moveEntry.getValue();

                    assertTrue(level >= 1, "Learn level should be at least 1");
                    assertNotNull(movesAtLevel, "Moves list should not be null");
                    assertFalse(movesAtLevel.isEmpty(), "Moves list should not be empty");

                    for (String moveName : movesAtLevel) {
                        assertNotNull(moveName, "Move name should not be null");
                        assertFalse(moveName.trim().isEmpty(), "Move name should not be empty");
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Test Resource Loading Resilience")
        void testResourceLoadingResilience() {
            // Test that the factory can handle normal operations
            assertDoesNotThrow(() -> {
                moveFactory testFactory = new moveFactory();
                testFactory.loadPokemonLearnsets();
            }, "Factory should handle normal resource loading");
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Test Move Creation Performance")
        @Timeout(value = 5) // Should complete within 5 seconds
        void testMoveCreationPerformance() {
            List<Move> moves = moveFactory.createMovesFromJson();
            assertNotNull(moves, "Moves should be created within time limit");
        }

        @Test
        @DisplayName("Test Learnset Loading Performance")
        @Timeout(value = 3) // Should complete within 3 seconds
        void testLearnsetLoadingPerformance() {
            factory.loadPokemonLearnsets();
            assertFalse(factory.pokemonLearnsets.isEmpty(),
                    "Learnsets should be loaded within time limit");
        }
    }
}
