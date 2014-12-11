/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.ArrayUtils;

import com.kegare.friendlymobs.api.IFriendlyMobsAPI;
import com.kegare.friendlymobs.core.Config;
import com.kegare.friendlymobs.util.Version;

public class FriendlyMobsAPIHandler implements IFriendlyMobsAPI
{
	@Override
	public String getVersion()
	{
		return Version.getCurrent();
	}

	@Override
	public Configuration getConfig()
	{
		return Config.config;
	}

	@Override
	public String[] getFriendlyMobs()
	{
		return Config.friendlyMobs == null ? new String[0] : Config.friendlyMobs;
	}

	@Override
	public boolean isFriendly(Entity entity)
	{
		if (entity == null)
		{
			return false;
		}

		String name = String.valueOf(EntityList.classToStringMapping.get(entity.getClass()));

		return name != null && !name.isEmpty() && ArrayUtils.contains(getFriendlyMobs(), name);
	}
}