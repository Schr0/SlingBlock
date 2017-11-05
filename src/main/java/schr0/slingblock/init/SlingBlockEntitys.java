package schr0.slingblock.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import schr0.slingblock.SlingBlock;
import schr0.slingblock.entity.EntitySlingBullet;
import schr0.slingblock.entity.RenderSlingBullet;

public class SlingBlockEntitys
{

	public static final int TRACKING_RANGE = 250;
	public static final int UPDATE_FREQUENCY = 1;
	public static final boolean SENDS_VELOCITY_UPDATES = true;

	public static final String NAME_SLING_BULLET = "sling_bullet";
	public static final int ID_SLING_BULLET = 0;

	public void registerEntitys()
	{
		registerEntity(EntitySlingBullet.class, NAME_SLING_BULLET, ID_SLING_BULLET, SlingBlock.instance, TRACKING_RANGE, UPDATE_FREQUENCY, SENDS_VELOCITY_UPDATES);
	}

	@SideOnly(Side.CLIENT)
	public void registerRenders()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntitySlingBullet.class, new IRenderFactory()
		{
			@Override
			public Render createRenderFor(RenderManager renderManager)
			{
				return new RenderSlingBullet(renderManager, Minecraft.getMinecraft().getRenderItem());
			}
		});
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, Object mod, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(SlingBlock.MOD_ID, entityName), entityClass, entityName, id, mod, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

}
