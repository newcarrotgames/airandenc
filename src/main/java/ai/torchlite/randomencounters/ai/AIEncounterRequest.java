package ai.torchlite.randomencounters.ai;

import net.minecraft.entity.player.EntityPlayer;

/**
 * Base class for AI encounter generation requests
 */
public class AIEncounterRequest {

    // Player Context
    private String playerName;
    private String playerUUID;
    private int playerLevel;
    private double playerHealth;
    private double playerMaxHealth;

    // Location Context
    private String biome;
    private int posX;
    private int posY;
    private int posZ;
    private String dimension;
    private boolean isNearStructure;
    private String timeOfDay; // "dawn", "day", "dusk", "night"
    private String weather; // "clear", "rain", "storm"

    // Difficulty Context
    private String gameDifficulty;
    private float localDifficultyRating; // 0.0-1.0

    public AIEncounterRequest() {
        // Default constructor
    }

    // Getters and setters
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getPlayerUUID() { return playerUUID; }
    public void setPlayerUUID(String playerUUID) { this.playerUUID = playerUUID; }

    public int getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }

    public double getPlayerHealth() { return playerHealth; }
    public void setPlayerHealth(double playerHealth) { this.playerHealth = playerHealth; }

    public double getPlayerMaxHealth() { return playerMaxHealth; }
    public void setPlayerMaxHealth(double playerMaxHealth) { this.playerMaxHealth = playerMaxHealth; }

    public String getBiome() { return biome; }
    public void setBiome(String biome) { this.biome = biome; }

    public int getPosX() { return posX; }
    public void setPosX(int posX) { this.posX = posX; }

    public int getPosY() { return posY; }
    public void setPosY(int posY) { this.posY = posY; }

    public int getPosZ() { return posZ; }
    public void setPosZ(int posZ) { this.posZ = posZ; }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }

    public boolean isNearStructure() { return isNearStructure; }
    public void setNearStructure(boolean nearStructure) { isNearStructure = nearStructure; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public String getGameDifficulty() { return gameDifficulty; }
    public void setGameDifficulty(String gameDifficulty) { this.gameDifficulty = gameDifficulty; }

    public float getLocalDifficultyRating() { return localDifficultyRating; }
    public void setLocalDifficultyRating(float localDifficultyRating) { this.localDifficultyRating = localDifficultyRating; }
}
