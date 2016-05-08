/*
 * FriendlyMobs
 *
 * Copyright (c) 2014 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package friendlymobs.client.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiListSlot<E> extends GuiSlot
{
	protected final Minecraft mc;

	public GuiListSlot(Minecraft mc, int width, int height, int top, int bottom, int slotHeight)
	{
		super(mc, width, height, top, bottom, slotHeight);
		this.mc = mc;
	}

	protected abstract List<E> getContents();

	protected abstract List<E> getSelected();

	@Override
	protected int getSize()
	{
		return getContents().size();
	}

	@Override
	protected void drawContainerBackground(Tessellator tessellator)
	{
		if (mc.theWorld != null)
		{
			Gui.drawRect(left, top, right, bottom, 0x101010);
		}
		else super.drawContainerBackground(tessellator);
	}

	public void scrollUp()
	{
		int i = getAmountScrolled() % getSlotHeight();

		if (i == 0)
		{
			scrollBy(-getSlotHeight());
		}
		else
		{
			scrollBy(-i);
		}
	}

	public void scrollDown()
	{
		scrollBy(getSlotHeight() - getAmountScrolled() % getSlotHeight());
	}

	public void scrollToTop()
	{
		scrollBy(-getAmountScrolled());
	}

	public void scrollToEnd()
	{
		scrollBy(getSlotHeight() * getSize());
	}

	public void scrollToSelected()
	{
		if (!getSelected().isEmpty())
		{
			int amount = 0;

			for (E element : getSelected())
			{
				amount = getContents().indexOf(element) * getSlotHeight();

				if (getAmountScrolled() != amount)
				{
					break;
				}
			}

			scrollToTop();
			scrollBy(amount);
		}
	}

	public void scrollToPrev()
	{
		scrollBy(-(getAmountScrolled() % getSlotHeight() + (bottom - top) / getSlotHeight() * getSlotHeight()));
	}

	public void scrollToNext()
	{
		scrollBy(getAmountScrolled() % getSlotHeight() + (bottom - top) / getSlotHeight() * getSlotHeight());
	}
}