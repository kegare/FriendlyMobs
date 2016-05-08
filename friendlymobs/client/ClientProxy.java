package friendlymobs.client;

import friendlymobs.client.config.SelectMobEntry;
import friendlymobs.core.CommonProxy;
import friendlymobs.core.Config;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	@Override
	public void initializeConfigEntries()
	{
		Config.selectMobEntry = SelectMobEntry.class;
	}
}