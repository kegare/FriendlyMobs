package friendlymobs.api;

import net.minecraft.entity.Entity;

/**
 * NOTE: Do NOT access to this class fields.
 * You should use this API from this class methods.
 */
public final class FriendlyMobsAPI
{
	public static final String
	MODID = "friendlymobs",
	API_VERSION = "1.1.4";

	public static IFriendlyMobsAPI instance;

	private FriendlyMobsAPI() {}

	/**
	 * Returns the configured friendly mobs.
	 */
	public static String[] getFriendlyMobs()
	{
		return instance == null ? new String[0] : instance.getFriendlyMobs();
	}

	/**
	 * Checks if the entity is friendly.
	 * @param entity The entity
	 * @return <tt>true</tt> if the entity is configured friendly.
	 */
	public static boolean isFriendly(Entity entity)
	{
		return instance != null && instance.isFriendly(entity);
	}
}