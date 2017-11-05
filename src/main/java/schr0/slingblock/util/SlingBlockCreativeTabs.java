package schr0.slingblock.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import schr0.slingblock.SlingBlock;
import schr0.slingblock.init.SlingBlockItems;

public class SlingBlockCreativeTabs
{

	public static final CreativeTabs ITEM = new CreativeTabs(SlingBlock.MOD_ID + "." + "item")
	{

		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(SlingBlockItems.SLING_NORMAL);
		}

	};

}
