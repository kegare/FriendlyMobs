/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.util;

import java.util.concurrent.ForkJoinPool;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import com.kegare.friendlymobs.core.FriendlyMobs;

public class FriendlyUtils
{
	private static ForkJoinPool pool;

	public static ForkJoinPool getPool()
	{
		if (pool == null || pool.isShutdown())
		{
			pool = new ForkJoinPool();
		}

		return pool;
	}

	public static ModContainer getModContainer()
	{
		ModContainer mod = Loader.instance().getIndexedModList().get(FriendlyMobs.MODID);

		if (mod == null)
		{
			mod = Loader.instance().activeModContainer();

			if (mod == null || mod.getModId() != FriendlyMobs.MODID)
			{
				return new DummyModContainer(FriendlyMobs.metadata);
			}
		}

		return mod;
	}

	public static String getEntityLocalizedName(String name)
	{
		String key = "entity." + name + ".name";
		String localized = I18n.format(key);

		if (key.equals(localized))
		{
			localized = name;
		}

		return localized;
	}
}