package friendlymobs.network;

import com.google.common.base.Joiner;

import friendlymobs.core.Config;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
		Config.config.getCategory(Configuration.CATEGORY_GENERAL).get("friendlyMobs").set(message.mobs);

		Config.syncConfig();

		return null;
	}
}