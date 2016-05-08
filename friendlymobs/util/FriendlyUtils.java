package friendlymobs.util;

import java.util.concurrent.ForkJoinPool;

import net.minecraft.util.text.translation.I18n;

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

	public static String getEntityLocalizedName(String name)
	{
		String key = "entity." + name + ".name";
		String localized = I18n.translateToLocal(key);

		if (key.equals(localized))
		{
			localized = name;
		}

		return localized;
	}
}