package com.kegare.friendlymobs.api;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.config.Configuration;

public interface IFriendlyMobsAPI
{
	public String getVersion();

	public Configuration getConfig();

	public String[] getFriendlyMobs();

	public boolean isFriendly(Entity entity);
}