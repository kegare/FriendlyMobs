/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kegare.friendlymobs.api.FriendlyMobsAPI;
import com.kegare.friendlymobs.api.event.GuiSelectedEvent.OnMobSelectedEvent;
import com.kegare.friendlymobs.client.gui.GuiSelectMob;
import com.kegare.friendlymobs.core.Config;
import com.kegare.friendlymobs.core.FriendlyMobs;
import com.kegare.friendlymobs.network.MobsSelectedMessage;
import com.kegare.friendlymobs.util.Version;
import com.kegare.friendlymobs.util.Version.Status;

public class FriendlyEventHooks
{
	public static final FriendlyEventHooks instance = new FriendlyEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.modID.equals(FriendlyMobs.MODID))
		{
			Config.syncConfig();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobSelected(OnMobSelectedEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (event.mobs != null && mc.currentScreen != null && mc.currentScreen instanceof GuiSelectMob)
		{
			GuiSelectMob gui = (GuiSelectMob)mc.currentScreen;

			if (gui.getPresetMobs() != null)
			{
				if (mc.isSingleplayer())
				{
					FriendlyMobsAPI.getConfig().getCategory(Configuration.CATEGORY_GENERAL).get("friendlyMobs").set(event.mobs);

					Config.syncConfig();
				}
				else
				{
					Config.friendlyMobs = event.mobs;

					FriendlyMobs.network.sendToServer(new MobsSelectedMessage(event.mobs));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event)
	{
		if (Version.getStatus() == Status.PENDING || Version.getStatus() == Status.FAILED)
		{
			Version.versionCheck();
		}
		else if (Version.DEV_DEBUG || Config.versionNotify && Version.isOutdated())
		{
			IChatComponent component = new ChatComponentTranslation("friendlymobs.version.message", EnumChatFormatting.AQUA + "FriendlyMobs" + EnumChatFormatting.RESET);
			component.appendText(" : " + EnumChatFormatting.YELLOW + Version.getLatest());
			component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, FriendlyMobs.metadata.url));

			FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().printChatMessage(component);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientDisconnect(ClientDisconnectionFromServerEvent event)
	{
		Config.syncConfig();
	}

	@SubscribeEvent
	public void onServerConnect(ServerConnectionFromClientEvent event)
	{
		event.manager.sendPacket(FriendlyMobs.network.getPacketFrom(new Config()));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingSetAttack(LivingSetAttackTargetEvent event)
	{
		EntityLivingBase entity = event.entityLiving;

		if (entity != null && !entity.worldObj.isRemote && FriendlyMobsAPI.isFriendly(entity))
		{
			if (entity.getAITarget() != null)
			{
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, (EntityLivingBase)null, "entityLivingToAttack", "field_70755_b");
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, 0, "revengeTimer", "field_70756_c");
			}

			if (entity.getLastAttacker() != null)
			{
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, (EntityLivingBase)null, "lastAttacker", "field_110150_bn");
				ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, 0, "lastAttackerTime", "field_142016_bo");
			}

			if (entity instanceof EntityLiving)
			{
				EntityLiving living = (EntityLiving)entity;

				if (living.getAttackTarget() != null)
				{
					ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, living, (EntityLivingBase)null, "attackTarget", "field_70696_bz");
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingAttack(LivingAttackEvent event)
	{
		Entity entity = event.source.getSourceOfDamage();

		if (entity != null && !entity.worldObj.isRemote && FriendlyMobsAPI.isFriendly(entity))
		{
			event.setCanceled(true);
		}
	}
}