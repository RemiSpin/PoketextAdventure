import java.util.Random;

public class Pokemon {
        public byte level;
        public int number;
        public String name;
        public String nickname;
        public int ivHP;
        public int ivAttack;
        public int ivDefense;
        public int ivSpAtk;
        public int ivSpDef;
        public int ivSpeed;
        public int Hp;
        public int Attack;
        public int Defense;
        public int SpecialAttack;
        public int SpecialDefense;
        public int Speed;
        public String type1;
        public String type2;
        public String evolution;
        public int evolutionLevel;
        public StatusCondition statusCondition; // Added StatusCondition

        public void setNumber(int number) {
        this.number = number;
    }

        public void setName(String name) {
        this.name = name;
    }

        public void setIvHP(int ivHP) {
        this.ivHP = ivHP;
    }

        public void setIvAttack(int ivAttack) {
        this.ivAttack = ivAttack;
    }

        public void setIvDefense(int ivDefense) {
        this.ivDefense = ivDefense;
    }

        public void setIvSpAtk(int ivSpAtk) {
        this.ivSpAtk = ivSpAtk;
    }

        public void setIvSpDef(int ivSpDef) {
        this.ivSpDef = ivSpDef;
    }

        public void setIvSpeed(int ivSpeed) {
        this.ivSpeed = ivSpeed;
    }

        public void setHp(int Hp) {
        this.Hp = Hp;
    }
        public void setAttack(int Attack) {
        this.Attack = Attack;
    }
        public void setDefense(int Defense) {
        this.Defense = Defense;
    }
        public void setSpecialAttack(int SpecialAttack) {
        this.SpecialAttack = SpecialAttack;
    }
        public void setSpecialDefense(int SpecialDefense) {
        this.SpecialDefense = SpecialDefense;
    }
        public void setSpeed(int Speed) {
        this.Speed = Speed;
    }
        public void setType1(String type1) {
        this.type1 = type1;
    }

        public void setType2(String type2) {
        this.type2 = type2;
    }
    public void setEvolution(String evolution) {
        this.evolution = evolution;
    }

    public void setEvolutionLevel(int evolutionLevel) {
        this.evolutionLevel = evolutionLevel;
    }

    public Pokemon(String name, byte level) {
        this.level = level;
        this.name = name;
        this.nickname = null;

        // Initialize IVs
        Random random = new Random();
        ivHP = random.nextInt(33);
        ivAttack = random.nextInt(33);
        ivDefense = random.nextInt(33);
        ivSpAtk = random.nextInt(33);
        ivSpDef = random.nextInt(33);
        ivSpeed = random.nextInt(33);

        // Initialize status condition
        statusCondition = StatusCondition.NONE;
    }

        public String getName() {
        return name;
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


        public enum StatusCondition {
            NONE, BURNED, PARALYZED, ASLEEP, FROZEN, POISONED;
        }

        @Override
        public String toString() {
        return "Name: " + name +
                "\nSpecies Name: " + nickname +
                "\nType 1: " + type1 +
                "\nType 2: " + type2 +
                "\nStatus: " + statusCondition;
    }
}