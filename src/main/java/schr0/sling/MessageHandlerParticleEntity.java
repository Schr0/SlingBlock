package schr0.sling;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MessageHandlerParticleEntity implements IMessageHandler<MessageParticleEntity, IMessage>
{

	@Override
	public IMessage onMessage(MessageParticleEntity message, MessageContext ctx)
	{
		World world = FMLClientHandler.instance().getClient().world;

		if (world != null)
		{
			Random random = world.rand;
			Entity entity = message.getEntity(world);

			switch (message.getParticleType())
			{
				case SlingParticles.ENTITY_SILING_CHAGE :

					particleSilingChage(world, entity, random);

					break;

				case SlingParticles.ENTITY_SILING_CHAGE_MAX :

					particleSilingChageMax(world, entity, random);

					break;
			}
		}

		return (IMessage) null;
	}

	// TODO /* ======================================== MOD START =====================================*/

	private static void particleSilingChage(World world, Entity entity, Random random)
	{
		for (int count = 0; count < 20; count++)
		{
			world.spawnParticle(EnumParticleTypes.CRIT, entity.posX + (double) (random.nextFloat() * entity.width * 2.0F) - (double) entity.width, entity.posY + (double) (random.nextFloat() * entity.height), entity.posZ + (double) (random.nextFloat() * entity.width * 2.0F) - (double) entity.width, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	private static void particleSilingChageMax(World world, Entity entity, Random random)
	{
		for (int count = 0; count < 20; count++)
		{
			world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, entity.posX + (double) (random.nextFloat() * entity.width * 2.0F) - (double) entity.width, entity.posY + (double) (random.nextFloat() * entity.height), entity.posZ + (double) (random.nextFloat() * entity.width * 2.0F) - (double) entity.width, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

}
