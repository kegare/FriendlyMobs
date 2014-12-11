/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.client.config;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Lists;
import com.kegare.friendlymobs.api.FriendlyMobsAPI;
import com.kegare.friendlymobs.core.FriendlyMobs;

@SideOnly(Side.CLIENT)
public class FriendlyConfigGui extends GuiConfig
{
	public FriendlyConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), FriendlyMobs.MODID, false, false, I18n.format(FriendlyMobs.CONFIG_LANG + "title"));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.addAll(new ConfigElement(FriendlyMobsAPI.getConfig().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());

		return list;
	}
}