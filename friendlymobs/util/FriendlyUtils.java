package friendlymobs.util;

import java.util.concurrent.ForkJoinPool;

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
}