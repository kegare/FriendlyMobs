package friendlymobs.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DisplayGuiMessage implements IMessage, IMessageHandler<DisplayGuiMessage, IMessage>
{
	@SideOnly(Side.CLIENT)
	public static GuiType guiType;

	private int id;

	public DisplayGuiMessage()
	{
		this.id = -1;
	}

	public DisplayGuiMessage(GuiType type)
	{
		this.id = type.ordinal();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(id);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(DisplayGuiMessage message, MessageContext ctx)
	{
		if (message.id < 0)
		{
			guiType = null;
		}
		else
		{
			guiType = GuiType.values()[message.id];
		}

		return null;
	}

	public enum GuiType
	{
		NONE,
		SELECT_MOB
	}
}