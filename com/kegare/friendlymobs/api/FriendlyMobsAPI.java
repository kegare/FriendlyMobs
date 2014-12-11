package com.kegare.friendlymobs.api;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.config.Configuration;

/**
 * NOTE: Do NOT access to this class fields.
 * You should use this API from this class methods.
 */
public final class FriendlyMobsAPI
{
	public static final String
	MODID = "kegare.friendlymobs",
	API_VERSION = "1.0.0";

	public static IFriendlyMobsAPI instance;

	private FriendlyMobsAPI() {}

	/**
	 * Returns the current mod version of FriendlyMobs mod.
	 */
	public static String getVersion()
	{
		return instance == null ? API_VERSION : instance.getVersion();
	}

	/**
	 * Returns the configuration of FriendlyMobs mod.
	 */
	public static Configuration getConfig()
	{
		return instance == null ? null : instance.getConfig();
	}

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