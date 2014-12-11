/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Sets;
import com.kegare.friendlymobs.api.FriendlyMobsAPI;
import com.kegare.friendlymobs.client.gui.GuiSelectMob;

public class SelectMobMessage implements IMessage, IMessageHandler<SelectMobMessage, IMessage>
{
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(SelectMobMessage message, MessageContext ctx)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		mc.displayGuiScreen(new GuiSelectMob(mc.currentScreen).setPresetMobs(Sets.newHashSet(FriendlyMobsAPI.getFriendlyMobs())));

		return null;
	}
}