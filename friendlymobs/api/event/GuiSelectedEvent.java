package friendlymobs.api.event;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@HasResult
public class GuiSelectedEvent extends Event
{
	/**
	 * OnMobSelectedEvent is fired when mobs are selected on the select gui.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	public static class OnMobSelectedEvent extends GuiSelectedEvent
	{
		public ResourceLocation[] mobs;

		public OnMobSelectedEvent(ResourceLocation[] mobs)
		{
			this.mobs = mobs;
		}
	}

	/**
	 * This event only fires if the OnSelectedMobEvent result is not <b>DENY</b>.<br>
	 * <br>
	 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
	 */
	public static class PostMobSelectedEvent extends OnMobSelectedEvent
	{
		public PostMobSelectedEvent(ResourceLocation[] mobs)
		{
			super(mobs);
		}
	}
}