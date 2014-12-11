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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.google.common.base.Joiner;
import com.kegare.friendlymobs.api.FriendlyMobsAPI;
import com.kegare.friendlymobs.core.Config;

public class MobsSelectedMessage implements IMessage, IMessageHandler<MobsSelectedMessage, IMessage>
{
	private String[] mobs;

	public MobsSelectedMessage() {}

	public MobsSelectedMessage(String[] mobs)
	{
		this.mobs = mobs;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		mobs = ByteBufUtils.readUTF8String(buf).split(";");
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, Joiner.on(";").skipNulls().join(mobs));
	}

	@Override
	public IMessage onMessage(MobsSelectedMessage message, MessageContext ctx)
	{
		FriendlyMobsAPI.getConfig().getCategory(Configuration.CATEGORY_GENERAL).get("friendlyMobs").set(message.mobs);

		Config.syncConfig();

		return null;
	}
}