package schr0.sling;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class SlingCreativeTabs
{

	public static final CreativeTabs ITEM = new CreativeTabs(Sling.MOD_ID + "." + "item")
	{

		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(SlingItems.SLING_NORMAL);
		}

	};

}
