package friendlymobs.core;

import friendlymobs.network.DisplayGuiMessage;
import friendlymobs.network.DisplayGuiMessage.GuiType;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandFriendlyMobs extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "friendlymobs";
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/" + getCommandName();
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (sender instanceof EntityPlayerMP)
		{
			FriendlyMobs.network.sendTo(new DisplayGuiMessage(GuiType.SELECT_MOB), (EntityPlayerMP)sender);
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return server.isSinglePlayer() || super.checkPermission(server, sender);
	}
}