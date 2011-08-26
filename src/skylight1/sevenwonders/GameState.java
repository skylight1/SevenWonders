package skylight1.sevenwonders;

import skylight1.sevenwonders.view.Position;
import android.os.Bundle;

public class GameState {

	public static final float HEIGHT_OF_CARPET_FROM_GROUND = 12f;
	
	private static final String NUMBER_OF_SPELLS_COLLECTED_KEY = 
		GameState.class.getName() + ".NUMBER_OF_SPELLS_COLLECTED_EXTRA";
	
	private static final String PLAYER_WORLD_POSITION_X = 
		GameState.class.getName() + ".PLAYER_WORLD_POSITION_X";
	
	private static final String PLAYER_WORLD_POSITION_Y = 
		GameState.class.getName() + ".PLAYER_WORLD_POSITION_Y";
	
	private static final String PLAYER_WORLD_POSITION_Z = 
		GameState.class.getName() + ".PLAYER_WORLD_POSITION_Z";
	
	private static final String PLAYER_FACING_KEY = 
		GameState.class.getName() + ".PLAYER_FACING_KEY";
	
	private static final String TURNING_VELOCITY_KEY = 
		GameState.class.getName() + ".TURNING_VELOCITY_KEY";
	
	private static final String VELOCITY_KEY = 
		GameState.class.getName() + ".VELOCITY_KEY";
	
	private static final String REMAINING_INVINCIBILITY_TIME_MILLIS = 
		GameState.class.getName() + ".REMAINING_INVINCIBILITY_TIME_MILLIS";
	
	private static final String REMAINING_PASS_THROUGH_OBSTACLES_TIME_MILLIS = 
		GameState.class.getName() + ".REMAINING_PASS_THROUGH_OBSTACLES_TIME_MILLIS";
	
	private static final String NUMBEROF_COINS_COLLECTED = 
		GameState.class.getName() + ".NUMBEROF_COINS_COLLECTED";
	
	private static final String REMAINING_GAME_TIME_MILLIS = 
		GameState.class.getName() + ".REMAINING_GAME_TIME_MILLIS";

	public int numberOfSpellsCollected;
		
	public Position playerWorldPosition = new Position(0, 0, 0);

	public float playerFacing;
	
	public float turningVelocity;

	public float velocity;
	
	private int remainingInvincibilityTimeMillis;
	
	private int remainingPassThroughObstaclesTimeMillis;
	
	private int numberofCoinsCollected;

	private long remainingGameTimeMillis;
	
	public int getRemainingPassThroughSolidsTimeMillis() {
		return remainingPassThroughObstaclesTimeMillis;
	}

	public void setRemainingPassThroughObstaclesTimeMillis(int aRemainingPassThroughSolidsTimeMillis) {
		remainingPassThroughObstaclesTimeMillis = aRemainingPassThroughSolidsTimeMillis;
	}
	
	public boolean isPlayerAbleToFlyThroughObstacles() {
		return remainingPassThroughObstaclesTimeMillis > 0;
	}
	
	public boolean isPlayerInvincible() {
		return remainingInvincibilityTimeMillis > 0;
	}
	
	public int getRemainingInvincibilityTimeMillis() {
		return remainingInvincibilityTimeMillis;
	}

	public void setRemainingInvincibilityTimeMillis(int aRemainingInvincibilityTimeMillis) {
		remainingInvincibilityTimeMillis = aRemainingInvincibilityTimeMillis;
	}

	public void setRemainingGameTimeMillis(long aRemainingGameTimeMillis) {
		remainingGameTimeMillis = aRemainingGameTimeMillis;
	}

	public synchronized void incrementNumberofCoinsCollected() {
		numberofCoinsCollected++;
	}

	public synchronized int getNumberofCoinsCollected() {
		return numberofCoinsCollected;
	}

	public void reduceRemainingTimeMillis(final long aChangeInTime) {
		remainingGameTimeMillis -= aChangeInTime;
		remainingInvincibilityTimeMillis -= aChangeInTime;
		remainingPassThroughObstaclesTimeMillis -= aChangeInTime;
	}

	public long getRemainingGameTimeMillis() {
		return remainingGameTimeMillis;
	}
	
    public Bundle addToBundle(final Bundle aBundle) {
        aBundle.putInt(NUMBER_OF_SPELLS_COLLECTED_KEY, numberOfSpellsCollected);
    	aBundle.putFloat(PLAYER_WORLD_POSITION_X, playerWorldPosition.x);
    	aBundle.putFloat(PLAYER_WORLD_POSITION_Y, playerWorldPosition.y);
    	aBundle.putFloat(PLAYER_WORLD_POSITION_Z, playerWorldPosition.z);
    	aBundle.putFloat(PLAYER_FACING_KEY, playerFacing);
    	aBundle.putFloat(TURNING_VELOCITY_KEY, turningVelocity);
    	aBundle.putFloat(VELOCITY_KEY, velocity);
        aBundle.putInt(REMAINING_INVINCIBILITY_TIME_MILLIS, remainingInvincibilityTimeMillis);
        aBundle.putInt(REMAINING_PASS_THROUGH_OBSTACLES_TIME_MILLIS, remainingPassThroughObstaclesTimeMillis);
        aBundle.putInt(NUMBEROF_COINS_COLLECTED, numberofCoinsCollected);
        aBundle.putLong(REMAINING_GAME_TIME_MILLIS, remainingGameTimeMillis);
        return aBundle;
    }

    public static GameState loadFromBundle(final Bundle aBundle) {
        GameState state = new GameState();
        state.numberOfSpellsCollected = aBundle.getInt(NUMBER_OF_SPELLS_COLLECTED_KEY);
    	
        float playerWorldPositionX = aBundle.getFloat(PLAYER_WORLD_POSITION_X);
    	float playerWorldPositionY = aBundle.getFloat(PLAYER_WORLD_POSITION_Y);
    	float playerWorldPositionZ = aBundle.getFloat(PLAYER_WORLD_POSITION_Z);
    	state.playerWorldPosition = new Position(playerWorldPositionX, playerWorldPositionY, playerWorldPositionZ);

    	state.playerFacing = aBundle.getFloat(PLAYER_FACING_KEY);
    	state.turningVelocity = aBundle.getFloat(TURNING_VELOCITY_KEY);
    	state.velocity = aBundle.getFloat(VELOCITY_KEY);
        state.remainingInvincibilityTimeMillis = aBundle.getInt(REMAINING_INVINCIBILITY_TIME_MILLIS);
        state.remainingPassThroughObstaclesTimeMillis = aBundle.getInt(REMAINING_PASS_THROUGH_OBSTACLES_TIME_MILLIS);
        state.numberofCoinsCollected = aBundle.getInt(NUMBEROF_COINS_COLLECTED);
        state.remainingGameTimeMillis = aBundle.getLong(REMAINING_GAME_TIME_MILLIS);
        return state;
    }
}
