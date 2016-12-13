package friendlymobs.handler;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Strings;

import friendlymobs.api.IFriendlyMobsAPI;
import friendlymobs.core.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

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

		ResourceLocation entryName = EntityList.getKey(entity);

		if (entryName == null)
		{
			return false;
		}

		String name = entryName.toString();

		return !Strings.isNullOrEmpty(name) && ArrayUtils.contains(getFriendlyMobs(), name);
	}
}