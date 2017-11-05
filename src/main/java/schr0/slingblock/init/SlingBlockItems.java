package schr0.slingblock.init;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import schr0.slingblock.SlingBlock;
import schr0.slingblock.item.ItemSlingNormal;
import schr0.slingblock.util.SlingBlockCreativeTabs;

public class SlingBlockItems
{

	public static final Item SLING_NORMAL;

	public static final String NAME_SLING_NORMAL = "sling_normal";

	static
	{
		SLING_NORMAL = new ItemSlingNormal().setUnlocalizedName(NAME_SLING_NORMAL).setCreativeTab(SlingBlockCreativeTabs.ITEM);
	}

	public void registerItems(IForgeRegistry<Item> registry)
	{
		registerItem(registry, SLING_NORMAL, NAME_SLING_NORMAL);
	}

	@SideOnly(Side.CLIENT)
	public void registerModels()
	{
		registerModel(SLING_NORMAL);
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void registerItem(IForgeRegistry<Item> registry, Item item, int meta, String name)
	{
		item.setRegistryName(SlingBlock.MOD_ID, name);

		registry.register(item);

		String domain = SlingBlock.MOD_ID + ".";

		if (meta <= 0)
		{
			OreDictionary.registerOre(domain + name, item);
		}
		else
		{
			for (int i = 0; i < meta; i++)
			{
				OreDictionary.registerOre(domain + name + "_" + i, new ItemStack(item, 1, i));
			}
		}
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item, String name)
	{
		registerItem(registry, item, 0, name);
	}

	@SideOnly(Side.CLIENT)
	private static void registerModel(Item item, int meta)
	{
		if (meta == 0)
		{
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			ModelBakery.registerItemVariants(item, item.getRegistryName());
		}
		else
		{
			ArrayList<ResourceLocation> models = Lists.newArrayList();

			for (int i = 0; i <= meta; i++)
			{
				ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(item.getRegistryName() + "_" + i, "inventory"));

				models.add(new ResourceLocation(item.getRegistryName() + "_" + i));
			}

			ModelBakery.registerItemVariants(item, models.toArray(new ResourceLocation[models.size()]));
		}
	}

	@SideOnly(Side.CLIENT)
	private static void registerModel(Item item)
	{
		registerModel(item, 0);
	}

}
