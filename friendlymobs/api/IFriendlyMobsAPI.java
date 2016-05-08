package friendlymobs.api;

import net.minecraft.entity.Entity;

public interface IFriendlyMobsAPI
{
	public String[] getFriendlyMobs();

	public boolean isFriendly(Entity entity);
}