package friendlymobs.client.gui;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
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

	protected Collection<String> presetMobs;

	public GuiSelectMob(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectMob(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.configElement = entry;
	}

	public GuiSelectMob setPresetMobs(Collection<String> mobs)
	{
		presetMobs = mobs;

		return this;
	}

	public Collection<String> getPresetMobs()
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

		doneButton.xPosition = width / 2 + 10;
		doneButton.yPosition = height - doneButton.height - 4;

		buttonList.clear();
		buttonList.add(doneButton);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(1, fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;

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
					List<String> selected = mobList.getSelected();
					OnMobSelectedEvent event = new OnMobSelectedEvent(selected.toArray(new String[selected.size()]));
					MinecraftForge.EVENT_BUS.post(event);

					if (!event.getResult().equals(Result.DENY) && event.mobs != null)
					{
						MinecraftForge.EVENT_BUS.post(new PostMobSelectedEvent(event.mobs));

						if (configElement != null)
						{
							configElement.setListFromChildScreen(event.mobs);
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

		drawCenteredString(fontRendererObj, I18n.format(Config.LANG_KEY + "select.mob"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (!mobList.selected.isEmpty())
		{
			List<String> selected = mobList.getSelectedMobs();

			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Config.LANG_KEY + "select.mob.selected", selected.size()), 5, 5, 0xEFEFEF);
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

	protected class MobList extends GuiListSlot<String>
	{
		protected final ArrayListExtended<String> mobs = new ArrayListExtended<>();
		protected final ArrayListExtended<String> contents = new ArrayListExtended<>();
		protected final ArrayListExtended<String> selected = new ArrayListExtended<>();
		protected final Map<String, List<String>> filterCache = Maps.newHashMap();

		protected int nameType;

		private boolean clickFlag;

		protected MobList()
		{
			super(GuiSelectMob.this.mc, 0, 0, 0, 0, 18);
			this.initEntries();
		}

		protected void initEntries()
		{
			FriendlyUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					mobs.clear();
					contents.clear();
					selected.clear();
					filterCache.clear();

					for (Iterator<Entry<String, Class<? extends Entity>>> iterator = EntityList.stringToClassMapping.entrySet().iterator(); iterator.hasNext();)
					{
						Entry<String, Class<? extends Entity>> entry = iterator.next();
						String name = entry.getKey();
						Class<? extends Entity> clazz = entry.getValue();

						if (!Strings.isNullOrEmpty(name) && EntityMob.class != clazz && EntityLiving.class != clazz && EntityLiving.class.isAssignableFrom(clazz))
						{
							mobs.addIfAbsent(name);
						}
					}

					Collections.sort(mobs);

					contents.addAll(mobs);

					if (presetMobs != null && !presetMobs.isEmpty())
					{
						selected.addAll(presetMobs);
					}
					else if (configElement != null)
					{
						selected.addAllObject(configElement.getCurrentValues());
					}
				}
			});
		}

		@Override
		protected List<String> getContents()
		{
			return contents;
		}

		@Override
		protected List<String> getSelected()
		{
			Set<String> set = Sets.newHashSet(selected);

			selected.clear();

			for (String name : set)
			{
				if (!Strings.isNullOrEmpty(name))
				{
					selected.add(name);
				}
			}

			Collections.sort(selected);

			return selected;
		}

		protected List<String> getMobs()
		{
			return Lists.transform(contents, new Function<String, String>()
			{
				@Override
				public String apply(String input)
				{
					return FriendlyUtils.getEntityLocalizedName(input);
				}
			});
		}

		protected List<String> getSelectedMobs()
		{
			return Lists.transform(getSelected(), new Function<String, String>()
			{
				@Override
				public String apply(String input)
				{
					return FriendlyUtils.getEntityLocalizedName(input);
				}
			});
		}

		@Override
		protected void drawBackground()
		{
			GuiSelectMob.this.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, int mouseX, int mouseY)
		{
			String entry = contents.get(index, null);

			if (entry == null || entry.isEmpty())
			{
				return;
			}

			String name;

			switch (nameType)
			{
				case 1:
					name = entry;
					break;
				default:
					name = FriendlyUtils.getEntityLocalizedName(entry);
					break;
			}

			GuiSelectMob.this.drawCenteredString(GuiSelectMob.this.fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			String entry = contents.get(index, null);

			if (entry != null && !entry.isEmpty() && (clickFlag = !clickFlag == true) && !selected.remove(entry))
			{
				selected.addIfAbsent(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			String entry = contents.get(index, null);

			return entry != null && !entry.isEmpty() && selected.contains(entry);
		}

		protected void setFilter(final String filter)
		{
			FriendlyUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<String> result;

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
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(mobs, new Predicate<String>()
							{
								@Override
								public boolean apply(String input)
								{
									return StringUtils.containsIgnoreCase(input, filter) || StringUtils.containsIgnoreCase(FriendlyUtils.getEntityLocalizedName(input), filter);
								}
							})));
						}

						result = filterCache.get(filter);
					}

					if (!contents.equals(result))
					{
						contents.clear();
						contents.addAll(result);
					}
				}
			});
		}
	}
}