package friendlymobs.core;

import java.util.Map;

import friendlymobs.api.FriendlyMobsAPI;
import friendlymobs.handler.FriendlyEventHooks;
import friendlymobs.handler.FriendlyMobsAPIHandler;
import friendlymobs.network.DisplayGuiMessage;
import friendlymobs.network.MobsSelectedMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod
(
	modid = FriendlyMobs.MODID,
	guiFactory = "friendlymobs.client.config.FriendlyGuiFactory"
)
public class FriendlyMobs
{
	public static final String MODID = FriendlyMobsAPI.MODID;

	@SidedProxy(modId = MODID, clientSide = "friendlymobs.client.ClientProxy", serverSide = "friendlymobs.core.CommonProxy")
	public static CommonProxy proxy;

	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		FriendlyMobsAPI.instance = new FriendlyMobsAPIHandler();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		proxy.initializeConfigEntries();

		Config.syncConfig();

		network.registerMessage(Config.class, Config.class, 0, Side.CLIENT);
		network.registerMessage(DisplayGuiMessage.class, DisplayGuiMessage.class, 1, Side.CLIENT);
		network.registerMessage(MobsSelectedMessage.class, MobsSelectedMessage.class, 2, Side.SERVER);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new FriendlyEventHooks());
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandFriendlyMobs());
	}

	@NetworkCheckHandler
	public boolean netCheckHandler(Map<String, String> mods, Side side)
	{
		return true;
	}
}