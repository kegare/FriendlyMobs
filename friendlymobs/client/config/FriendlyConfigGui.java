package friendlymobs.client.config;

import java.util.List;

import com.google.common.collect.Lists;

import friendlymobs.core.Config;
import friendlymobs.core.FriendlyMobs;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FriendlyConfigGui extends GuiConfig
{
	public FriendlyConfigGui(GuiScreen parent)
	{
		super(parent, getConfigElements(), FriendlyMobs.MODID, false, false, I18n.format(Config.LANG_KEY + "title"));
	}

	private static List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> list = Lists.newArrayList();

		list.addAll(new ConfigElement(Config.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());

		return list;
	}
}