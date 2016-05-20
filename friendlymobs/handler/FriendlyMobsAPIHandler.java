package friendlymobs.handler;

import org.apache.commons.lang3.ArrayUtils;

import friendlymobs.api.IFriendlyMobsAPI;
import friendlymobs.core.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public class FriendlyMobsAPIHandler implements IFriendlyMobsAPI
{
	@Override
	public String[] getFriendlyMobs()
	{
		return Config.friendlyMobs == null ? new String[0] : Config.friendlyMobs;
	}

	@Override
	public boolean isFriendly(Entity entity)
	{
		if (entity == null)
		{
			return false;
		}

		String name = String.valueOf(EntityList.CLASS_TO_NAME.get(entity.getClass()));

		return name != null && !name.isEmpty() && ArrayUtils.contains(getFriendlyMobs(), name);
	}
}