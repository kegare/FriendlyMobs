/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.core;

import static com.kegare.friendlymobs.core.FriendlyMobs.*;

import java.util.Map;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.kegare.friendlymobs.api.FriendlyMobsAPI;
import com.kegare.friendlymobs.handler.FriendlyEventHooks;
import com.kegare.friendlymobs.handler.FriendlyMobsAPIHandler;
import com.kegare.friendlymobs.network.MobsSelectedMessage;
import com.kegare.friendlymobs.network.SelectMobMessage;
import com.kegare.friendlymobs.util.Version;

@Mod
(
	modid = MODID,
	acceptedMinecraftVersions = "[1.8,)",
	guiFactory = MOD_PACKAGE + ".client.config.FriendlyGuiFactory"
)
public class FriendlyMobs
{
	public static final String
	MODID = FriendlyMobsAPI.MODID,
	MOD_PACKAGE = "com.kegare.friendlymobs",
	CONFIG_LANG = "friendlymobs.config.";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = MOD_PACKAGE + ".client.ClientProxy", serverSide = MOD_PACKAGE + ".core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = new SimpleNetworkWrapper(MODID);

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		FriendlyMobsAPI.instance = new FriendlyMobsAPIHandler();

		Version.versionCheck();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.initializeConfigEntries();

		Config.syncConfig();

		int i = 0;
		network.registerMessage(Config.class, Config.class, i++, Side.CLIENT);
		network.registerMessage(SelectMobMessage.class, SelectMobMessage.class, i++, Side.CLIENT);
		network.registerMessage(MobsSelectedMessage.class, MobsSelectedMessage.class, i++, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		FMLCommonHandler.instance().bus().register(FriendlyEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(FriendlyEventHooks.instance);
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandFriendlyMobs());

		if (event.getSide().isServer() && (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated()))
		{
			event.getServer().logInfo(StatCollector.translateToLocalFormatted("friendlymobs.version.message", "FriendlyMobs") + ": " + Version.getLatest());
		}
	}

	@NetworkCheckHandler
	public boolean netCheckHandler(Map<String, String> mods, Side side)
	{
		return true;
	}
}