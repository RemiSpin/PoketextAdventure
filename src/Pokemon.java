import java.util.Random;

public class Pokemon {
    private String name;
    private int healthPoints;
    private int ivHP;
    private int ivAttack;
    private int ivDefense;
    private int ivSpAtk;
    private int ivSpDef;
    private int ivSpeed;
    private String type1;
    private String type2;
    private int baseHP;
    private StatusCondition statusCondition; // Added StatusCondition

    public Pokemon(String name, int baseHP, int attack, int defense, int spAtk, int spDef, int speed, String type1, String type2) {
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
        this.baseHP = baseHP;

        // Initialize IVs
        Random random = new Random();
        ivHP = random.nextInt(32);
        ivAttack = random.nextInt(32);
        ivDefense = random.nextInt(32);
        ivSpAtk = random.nextInt(32);
        ivSpDef = random.nextInt(32);
        ivSpeed = random.nextInt(32);

        // Initialize status condition
        statusCondition = StatusCondition.NONE;
    }

    public String getName() {
        return name;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public int getIvHP() {
        return ivHP;
    }

    public int getIvAttack() {
        return ivAttack;
    }

    public int getIvDefense() {
        return ivDefense;
    }

    public int getIvSpAtk() {
        return ivSpAtk;
    }

    public int getIvSpDef() {
        return ivSpDef;
    }

    public int getIvSpeed() {
        return ivSpeed;
    }

    public StatusCondition getStatusCondition() {
        return statusCondition;
    }

    public void setStatusCondition(StatusCondition condition) {
        statusCondition = condition;
    }

    // Other methods...

    public enum StatusCondition {
        NONE, BURNED, PARALYZED, ASLEEP, FROZEN, POISONED;
    }

    @Override
    public String toString() {
        return "Name: " + name +
                "\nHealth Points: " + healthPoints +
                "\nType 1: " + type1 +
                "\nType 2: " + type2 +
                "\nIV HP: " + ivHP +
                "\nIV Attack: " + ivAttack +
                "\nIV Defense: " + ivDefense +
                "\nIV Special Attack: " + ivSpAtk +
                "\nIV Special Defense: " + ivSpDef +
                "\nIV Speed: " + ivSpeed +
                "\nStatus: " + statusCondition; // Added status condition
    }
}
