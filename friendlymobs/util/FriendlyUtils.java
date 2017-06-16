package friendlymobs.util;

import java.util.concurrent.ForkJoinPool;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class FriendlyUtils
{
	private static ForkJoinPool pool;

	public static ForkJoinPool getPool()
	{
		if (pool == null || pool.isShutdown())
		{
			pool = new ForkJoinPool();
		}

		return pool;
	}

	public static boolean hasRevengeTarget(EntityLivingBase entity)
	{
		if (entity instanceof EntityLiving)
		{
			EntityLiving living = (EntityLiving)entity;

			if (living.getAttackTarget() != null)
			{
				return true;
			}
		}

		EntityLivingBase target = ObfuscationReflectionHelper.getPrivateValue(EntityLivingBase.class, entity, "revengeTarget", "field_70755_b");

		return target != null;
	}

	public static void resetRevengeTarget(EntityLivingBase entity)
	{
		if (entity instanceof EntityLiving)
		{
			EntityLiving living = (EntityLiving)entity;

			if (living.getAttackTarget() != null)
			{
				ObfuscationReflectionHelper.setPrivateValue(EntityLiving.class, living, null, "attackTarget", "field_70696_bz");
			}
		}

		ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, null, "revengeTarget", "field_70755_b");
	}

	public static void resetRevengeTimer(EntityLivingBase entity)
	{
		ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, 0, "revengeTimer", "field_70756_c");
	}
}