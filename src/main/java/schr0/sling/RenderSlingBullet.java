package schr0.sling;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderSlingBullet extends RenderSnowball<EntitySlingBullet>
{

	public RenderSlingBullet(RenderManager renderManagerIn, RenderItem itemRendererIn)
	{
		super(renderManagerIn, Items.APPLE, itemRendererIn);
	}

	@Override
	public ItemStack getStackToRender(EntitySlingBullet entity)
	{
		return entity.getEntityItem();
	}

}
