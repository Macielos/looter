package pl.looter.utils;

/**
 * Created by Arjan on 30.04.2016.
 */
public abstract class GameplayUtils {

	private  GameplayUtils() {
	}

	public static int getXpForLevel(int level) {
		return level < 1 ? 0 : level * 1000;
	}
}
