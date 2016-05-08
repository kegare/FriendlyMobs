package friendlymobs.core;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Config implements IMessage, IMessageHandler<Config, IMessage>
{
	public static Configuration config;

	public static String[] friendlyMobs;

	public static Class<? extends IConfigEntry> selectMobEntry;

	public static final String LANG_KEY = "friendlymobs.config.";

	public static void syncConfig()
	{
		if (config == null)
		{
			File file = new File(Loader.instance().getConfigDir(), "FriendlyMobs.cfg");
			config = new Configuration(file);

			try
			{
				config.load();
			}
			catch (Exception e)
			{
				File dest = new File(file.getParentFile(), file.getName() + ".bak");

				if (dest.exists())
				{
					dest.delete();
				}

				file.renameTo(dest);

				FMLLog.log(Level.ERROR, e, "A critical error occured reading the " + file.getName() + " file, defaults will be used - the invalid file is backed up at " + dest.getName());
			}
		}

		String category = Configuration.CATEGORY_GENERAL;
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		prop = config.get(category, "friendlyMobs", new String[0]);
		prop.setLanguageKey(LANG_KEY + category + "." + prop.getName()).setConfigEntryClass(selectMobEntry);
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		prop.setComment(comment);
		propOrder.add(prop.getName());
		friendlyMobs = prop.getStringList();

		config.setCategoryPropertyOrder(category, propOrder);

		if (config.hasChanged())
		{
			config.save();
		}
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		friendlyMobs = ByteBufUtils.readUTF8String(buf).split(";");
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, Joiner.on(";").skipNulls().join(friendlyMobs));
	}

	@Override
	public IMessage onMessage(Config message, MessageContext ctx)
	{
		return null;
	}
}