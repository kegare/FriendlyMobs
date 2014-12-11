/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.kegare.friendlymobs.core;

import java.awt.Desktop;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.Loader;

import com.kegare.friendlymobs.network.SelectMobMessage;
import com.kegare.friendlymobs.util.Version;

public class CommandFriendlyMobs implements ICommand
{
	@Override
	public int compareTo(Object obj)
	{
		return getName().compareTo(((ICommand)obj).getName());
	}

	@Override
	public String getName()
	{
		return "friendlymobs";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "commands.generic.notFound";
	}

	@Override
	public List getAliases()
	{
		return Collections.emptyList();
	}


	@Override
	public void execute(ICommandSender sender, final String[] args)
	{
		if (args.length <= 0 || args[0].equalsIgnoreCase("version"))
		{
			ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, FriendlyMobs.metadata.url);
			IChatComponent component;
			IChatComponent message = new ChatComponentText(" ");

			component = new ChatComponentText("FriendlyMobs");
			component.getChatStyle().setColor(EnumChatFormatting.AQUA);
			message.appendSibling(component);
			message.appendText(" " + Version.getCurrent());

			if (Version.DEV_DEBUG)
			{
				message.appendText(" ");
				component = new ChatComponentText("dev");
				component.getChatStyle().setColor(EnumChatFormatting.RED);
				message.appendSibling(component);
			}

			message.appendText(" for " + Loader.instance().getMCVersionString() + " ");
			component = new ChatComponentText("(Latest: " + Version.getLatest() + ")");
			component.getChatStyle().setColor(EnumChatFormatting.GRAY);
			message.appendSibling(component);
			message.getChatStyle().setChatClickEvent(click);
			sender.addChatMessage(message);

			message = new ChatComponentText("  ");
			component = new ChatComponentText(FriendlyMobs.metadata.description);
			component.getChatStyle().setChatClickEvent(click);
			message.appendSibling(component);
			sender.addChatMessage(message);

			message = new ChatComponentText("  ");
			component = new ChatComponentText(FriendlyMobs.metadata.url);
			component.getChatStyle().setColor(EnumChatFormatting.DARK_GRAY).setChatClickEvent(click);
			message.appendSibling(component);
			sender.addChatMessage(message);
		}
		else if (args[0].equalsIgnoreCase("forum") || args[0].equalsIgnoreCase("url"))
		{
			try
			{
				Desktop.getDesktop().browse(new URI(FriendlyMobs.metadata.url));
			}
			catch (Exception e) {}
		}
		else if (args[0].equalsIgnoreCase("mobs") || args[0].equalsIgnoreCase("config") || args[0].equalsIgnoreCase("cfg"))
		{
			if (sender instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)sender;

				if (player.mcServer.getConfigurationManager().canSendCommands(player.getGameProfile()))
				{
					FriendlyMobs.network.sendTo(new SelectMobMessage(), player);
				}
			}
		}
	}

	@Override
	public boolean canCommandSenderUse(ICommandSender sender)
	{
		return sender instanceof MinecraftServer || sender instanceof EntityPlayerMP;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
	{
		return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, "version", "forum", "mobs") : null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return false;
	}
}