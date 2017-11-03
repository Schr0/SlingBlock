package schr0.sling;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemSling extends Item
{

	private static final int USING_COUNT_MIN = (1 * 20);

	private static final int CHAGE_AMOUNT_MIN = 1;
	private static final int CHAGE_AMOUNT_MAX = 10;

	private static final int CHAGE_INTERVAL = (20 / 2);

	private int enchantability;

	public ItemSling(SlingMaterial material)
	{
		super();

		this.maxStackSize = 1;
		this.setMaxDamage(material.getMaxUses());
		this.enchantability = material.getEnchantability();
		this.addPropertyOverride(new ResourceLocation("throw"), new IItemPropertyGetter()
		{

			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
			{
				if (entityIn != null && entityIn.getActiveItemStack().isItemEqual(stack))
				{
					if (entityIn.isHandActive())
					{
						return 1.0F;
					}

					if (entityIn.isSwingInProgress)
					{
						return 2.0F;

					}
				}

				return 0.0F;
			}

		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D()
	{
		return true;
	}

	@Override
	public int getItemEnchantability()
	{
		return this.enchantability;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		if (enchantment.type == EnumEnchantmentType.BOW)
		{
			return true;
		}

		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.BOW;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (this.getBullet(playerIn).isEmpty())
		{
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}
		else
		{
			playerIn.setActiveHand(handIn);

			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
	{
		World world = player.getEntityWorld();

		if (world.isRemote || !(player instanceof EntityPlayer))
		{
			return;
		}

		int usingCount = this.getUsingCount(stack, count);

		if (usingCount < USING_COUNT_MIN)
		{
			return;
		}

		if (usingCount % 20 == 0)
		{
			EntityPlayer entityPlayer = (EntityPlayer) player;
			int chageAmmount = this.getChageAmmount(stack, usingCount);

			entityPlayer.addExhaustion(0.15F);

			if (chageAmmount == CHAGE_AMOUNT_MAX)
			{
				SlingPackets.DISPATCHER.sendToAll(new MessageParticleEntity(entityPlayer, SlingParticles.ENTITY_SILING_CHAGE_MAX));

				world.playSound(null, entityPlayer.getPosition(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.25F, 1.0F);
			}
			else
			{
				SlingPackets.DISPATCHER.sendToAll(new MessageParticleEntity(entityPlayer, SlingParticles.ENTITY_SILING_CHAGE));

				world.playSound(null, entityPlayer.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.25F, (0.5F + ((float) chageAmmount / 10)));
			}
		}
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft)
	{
		if (worldIn.isRemote || !(entityLiving instanceof EntityPlayer))
		{
			return;
		}

		int usingCount = this.getUsingCount(stack, timeLeft);
		ItemStack bullet = this.getBullet(entityLiving);

		if ((usingCount < USING_COUNT_MIN) || bullet.isEmpty())
		{
			return;
		}

		EntityPlayer player = (EntityPlayer) entityLiving;
		float chageAmmount = (float) this.getChageAmmount(stack, usingCount);

		EntitySlingBullet entitySlingBullet = new EntitySlingBullet(worldIn, player, bullet, (int) chageAmmount);

		int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

		if (0 < punch)
		{
			entitySlingBullet.setKnockbackStrength(punch);
		}

		int flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack);

		if (0 < flame)
		{
			entitySlingBullet.setFire(flame * 200);
		}

		float velocity = (1.0F + (chageAmmount / 10));
		float inaccuracy = (1.1F - (chageAmmount / 10));
		entitySlingBullet.setHeadingFromThrower(player, player.rotationPitch, player.rotationYaw, 0.0F, velocity, inaccuracy);

		worldIn.spawnEntity(entitySlingBullet);

		if (!player.capabilities.isCreativeMode)
		{
			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) <= 0)
			{
				bullet.shrink(1);
			}

			stack.damageItem(1, entityLiving);
		}

		player.getCooldownTracker().setCooldown(this, CHAGE_INTERVAL);

		player.addStat(StatList.getObjectUseStats(this));

		SlingPackets.DISPATCHER.sendToAll(new MessagePlayerAction(player, SlingActionTypes.SWING_ARM));
	}

	// TODO /* ======================================== MOD START =====================================*/

	public int getUsingCount(ItemStack stack, int timeLeft)
	{
		return (this.getMaxItemUseDuration(stack) - timeLeft);
	}

	public int getChageAmmount(ItemStack stack, int usingCount)
	{
		int chageAmount = (usingCount / 20) + EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
		chageAmount = Math.min(chageAmount, CHAGE_AMOUNT_MAX);
		chageAmount = Math.max(chageAmount, CHAGE_AMOUNT_MIN);

		return chageAmount;
	}

	public boolean isBullet(ItemStack stack)
	{
		if (stack.getItem() instanceof ItemBlock)
		{
			return true;
		}

		return false;
	}

	public ItemStack getBullet(EntityLivingBase entityLivingBase)
	{
		if (entityLivingBase instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityLivingBase;
			ItemStack offHandStack = player.getHeldItem(EnumHand.OFF_HAND);
			ItemStack mainHandStack = player.getHeldItem(EnumHand.MAIN_HAND);

			if (this.isBullet(offHandStack))
			{
				return offHandStack;
			}
			else
			{
				if (this.isBullet(mainHandStack))
				{
					return mainHandStack;
				}
				else
				{
					for (int slot = 0; slot < player.inventory.getSizeInventory(); ++slot)
					{
						ItemStack slotStack = player.inventory.getStackInSlot(slot);

						if (this.isBullet(slotStack))
						{
							return slotStack;
						}
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

}
