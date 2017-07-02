package friendlymobs.client.gui;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import friendlymobs.api.event.GuiSelectedEvent.OnMobSelectedEvent;
import friendlymobs.api.event.GuiSelectedEvent.PostMobSelectedEvent;
import friendlymobs.core.Config;
import friendlymobs.util.ArrayListExtended;
import friendlymobs.util.FriendlyUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectMob extends GuiScreen
{
	protected final GuiScreen parent;
	protected ArrayEntry configElement;

	protected MobList mobList;
	protected GuiButton doneButton;
	protected GuiTextField filterTextField;
	protected HoverChecker selectedHoverChecker;

	protected Collection<ResourceLocation> presetMobs;

	public GuiSelectMob(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectMob(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.configElement = entry;
	}

	public GuiSelectMob setPresetMobs(Collection<ResourceLocation> mobs)
	{
		presetMobs = mobs;

		return this;
	}

	public Collection<ResourceLocation> getPresetMobs()
	{
		return presetMobs;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(true);

		if (mobList == null)
		{
			mobList = new MobList();
		}

		mobList.setDimensions(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 + 10;
		doneButton.y = height - doneButton.height - 4;

		buttonList.clear();
		buttonList.add(doneButton);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(1, fontRenderer, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.x = width / 2 - filterTextField.width - 5;
		filterTextField.y = height - filterTextField.height - 6;

		if (selectedHoverChecker == null)
		{
			selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					List<ResourceLocation> selected = mobList.getSelected();
					OnMobSelectedEvent event = new OnMobSelectedEvent(selected.toArray(new ResourceLocation[selected.size()]));
					MinecraftForge.EVENT_BUS.post(event);

					if (!event.getResult().equals(Result.DENY) && event.mobs != null)
					{
						MinecraftForge.EVENT_BUS.post(new PostMobSelectedEvent(event.mobs));

						if (configElement != null)
						{
							String[] entries = new String[event.mobs.length];

							for (int i = 0; i < entries.length; ++i)
							{
								entries[i] = event.mobs[i].toString();
							}

							configElement.setListFromChildScreen(entries);
						}
					}

					mc.displayGuiScreen(parent);

					if (parent == null)
					{
						mc.setIngameFocus();
					}

					mobList.selected.clear();
					mobList.scrollToTop();
					break;
				default:
					mobList.actionPerformed(button);
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		filterTextField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		if (mobList == null || doneButton == null || filterTextField == null)
		{
			return;
		}

		mobList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRenderer, I18n.format(Config.LANG_KEY + "select.mob"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (!mobList.selected.isEmpty())
		{
			List<String> selected = mobList.getSelectedMobs();

			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.mob.selected", selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				drawHoveringText(selected, mouseX, mouseY);
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int code) throws IOException
	{
		super.mouseClicked(x, y, code);

		filterTextField.mouseClicked(x, y, code);
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		mobList.handleMouseInput();
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (filterTextField.isFocused())
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				filterTextField.setFocused(false);
			}

			String prev = filterTextField.getText();

			filterTextField.textboxKeyTyped(c, code);

			String text = filterTextField.getText();
			boolean changed = text != prev;

			if (Strings.isNullOrEmpty(text) && changed)
			{
				mobList.setFilter(null);
			}
			else if (changed || code == Keyboard.KEY_RETURN)
			{
				mobList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);

				if (parent == null)
				{
					mc.setIngameFocus();
				}
			}
			else if (code == Keyboard.KEY_BACK)
			{
				mobList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++mobList.nameType > 1)
				{
					mobList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				mobList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				mobList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				mobList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				mobList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				mobList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				mobList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				mobList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				mobList.selected.clear();
				mobList.selected.addAll(mobList.contents);
			}
		}
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	protected class MobList extends GuiListSlot<ResourceLocation> implements Comparator<ResourceLocation>
	{
		protected final ArrayListExtended<ResourceLocation> mobs = new ArrayListExtended<>();
		protected final ArrayListExtended<ResourceLocation> contents = new ArrayListExtended<>();
		protected final ArrayListExtended<ResourceLocation> selected = new ArrayListExtended<>();
		protected final Map<String, List<ResourceLocation>> filterCache = Maps.newHashMap();

		protected int nameType;

		private boolean clickFlag;

		protected MobList()
		{
			super(GuiSelectMob.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			mobs.clear();
			contents.clear();
			selected.clear();
			filterCache.clear();

			for (Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries())
			{
				EntityEntry entityEntry = entry.getValue();
				Class<? extends Entity> entityClass = entityEntry.getEntityClass();

				if (EntityMob.class == entityClass || EntityLiving.class == entityClass || !EntityLiving.class.isAssignableFrom(entityClass))
				{
					continue;
				}

				mobs.addIfAbsent(entry.getKey());
			}

			Collections.sort(mobs, this);

			contents.addAll(mobs);

			if (presetMobs != null && !presetMobs.isEmpty())
			{
				for (ResourceLocation key : presetMobs)
				{
					selected.addIfAbsent(key);
				}
			}
			else if (configElement != null)
			{
				for (Object obj : configElement.getCurrentValues())
				{
					ResourceLocation key = new ResourceLocation(obj.toString());

					selected.addIfAbsent(key);
				}
			}

			if (!selected.isEmpty())
			{
				Collections.sort(selected, this);
			}
		}

		@Override
		protected List<ResourceLocation> getContents()
		{
			return contents;
		}

		@Override
		protected List<ResourceLocation> getSelected()
		{
			return selected;
		}

		protected List<String> getMobs()
		{
			return Lists.transform(contents, entry -> I18n.format("entity." + EntityList.getTranslationName(entry) + ".name"));
		}

		protected List<String> getSelectedMobs()
		{
			return Lists.transform(getSelected(), entry -> I18n.format("entity." + EntityList.getTranslationName(entry) + ".name"));
		}

		@Override
		protected void drawBackground()
		{
			GuiSelectMob.this.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int insideLeft, int par3, int par4, int mouseX, int mouseY, float ticks)
		{
			ResourceLocation key = contents.get(index, null);

			if (key == null)
			{
				return;
			}

			String name;

			switch (nameType)
			{
				case 1:
					name = key.toString();
					break;
				default:
					name = I18n.format("entity." + EntityList.getTranslationName(key) + ".name");
					break;
			}

			GuiSelectMob.this.drawCenteredString(GuiSelectMob.this.fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			ResourceLocation key = contents.get(index, null);

			if (key != null && (clickFlag = !clickFlag == true) && !selected.remove(key))
			{
				selected.addIfAbsent(key);

				Collections.sort(selected, this);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			ResourceLocation key = contents.get(index, null);

			return key != null && selected.contains(key);
		}

		@Override
		public int compare(ResourceLocation o1, ResourceLocation o2)
		{
			return StringUtils.compare(o1.toString(), o2.toString());
		}

		protected void setFilter(final String filter)
		{
			FriendlyUtils.getPool().execute(() ->
			{
				List<ResourceLocation> result;

				if (Strings.isNullOrEmpty(filter))
				{
					result = mobs;
				}
				else if (filter.equals("selected"))
				{
					result = selected;
				}
				else
				{
					if (!filterCache.containsKey(filter))
					{
						filterCache.put(filter, Lists.newArrayList(Collections2.filter(mobs, input -> filterMatch(input, filter))));
					}

					result = filterCache.get(filter);
				}

				if (!contents.equals(result))
				{
					contents.clear();
					contents.addAll(result);
				}
			});
		}

		protected boolean filterMatch(ResourceLocation key, String filter)
		{
			if ("monster".equalsIgnoreCase(filter))
			{
				Class<? extends Entity> entityClass = EntityList.getClass(key);

				if (IMob.class.isAssignableFrom(entityClass))
				{
					return true;
				}
			}
			else if ("animal".equalsIgnoreCase(filter))
			{
				Class<? extends Entity> entityClass = EntityList.getClass(key);

				if (IMob.class.isAssignableFrom(entityClass))
				{
					return false;
				}

				if (IAnimals.class.isAssignableFrom(entityClass))
				{
					return true;
				}
			}

			return StringUtils.containsIgnoreCase(key.toString(), filter) || StringUtils.containsIgnoreCase(I18n.format("entity." + EntityList.getTranslationName(key) + ".name"), filter);
		}
	}
}