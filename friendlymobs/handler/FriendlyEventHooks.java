package friendlymobs.handler;

import com.google.common.collect.Sets;

import friendlymobs.api.FriendlyMobsAPI;
import friendlymobs.api.event.GuiSelectedEvent.OnMobSelectedEvent;
import friendlymobs.client.gui.GuiSelectMob;
import friendlymobs.core.Config;
import friendlymobs.core.FriendlyMobs;
import friendlymobs.network.DisplayGuiMessage;
import friendlymobs.network.MobsSelectedMessage;
import friendlymobs.util.FriendlyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FriendlyEventHooks
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FriendlyMobs.MODID))
		{
			Config.syncConfig();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onMobSelected(OnMobSelectedEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (event.mobs != null && mc.currentScreen != null && mc.currentScreen instanceof GuiSelectMob)
		{
			GuiSelectMob gui = (GuiSelectMob)mc.currentScreen;

			if (gui.getPresetMobs() != null)
			{
				if (mc.isSingleplayer())
				{
					Config.config.getCategory(Configuration.CATEGORY_GENERAL).get("friendlyMobs").set(event.mobs);

					Config.syncConfig();
				}
				else
				{
					Config.friendlyMobs = event.mobs;

					FriendlyMobs.NETWORK.sendToServer(new MobsSelectedMessage(event.mobs));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			 return;
		}

		if (DisplayGuiMessage.guiType != null)
		{
			Minecraft mc = FMLClientHandler.instance().getClient();

			switch (DisplayGuiMessage.guiType)
			{
				case SELECT_MOB:
					mc.displayGuiScreen(new GuiSelectMob(mc.currentScreen).setPresetMobs(Sets.newHashSet(FriendlyMobsAPI.getFriendlyMobs())));
					break;
				default:
			}

			DisplayGuiMessage.guiType = null;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientDisconnect(ClientDisconnectionFromServerEvent event)
	{
		Config.syncConfig();
	}

	@SubscribeEvent
	public void onServerConnect(ServerConnectionFromClientEvent event)
	{
		event.getManager().sendPacket(FriendlyMobs.NETWORK.getPacketFrom(new Config()));
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingSetAttack(LivingSetAttackTargetEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (entity != null && !entity.world.isRemote && FriendlyMobsAPI.isFriendly(entity))
		{
			if (FriendlyUtils.hasRevengeTarget(entity))
			{
				FriendlyUtils.resetRevengeTarget(entity);
				FriendlyUtils.resetRevengeTimer(entity);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onLivingAttack(LivingAttackEvent event)
	{
		Entity entity = event.getSource().getTrueSource();

		if (entity != null && !entity.world.isRemote && FriendlyMobsAPI.isFriendly(entity))
		{
			event.setCanceled(true);
		}

		entity = event.getSource().getImmediateSource();

		if (entity != null && !entity.world.isRemote && FriendlyMobsAPI.isFriendly(entity))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEnderTeleport(EnderTeleportEvent event)
	{
		if (FriendlyMobsAPI.isFriendly(event.getEntityLiving()))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onExplosion(ExplosionEvent.Start event)
	{
		Explosion explosion = event.getExplosion();

		if (explosion != null && FriendlyMobsAPI.isFriendly(explosion.getExplosivePlacedBy()))
		{
			event.setCanceled(true);
		}
	}
}