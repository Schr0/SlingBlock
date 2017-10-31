package schr0.sling;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;

public class EntitySlingBullet extends EntityThrowable
{

	public static void registerFixesSlingBullet(DataFixer fixer)
	{
		EntityThrowable.registerFixesThrowable(fixer, SlingEntitys.NAME_SLING_BULLET);
	}

	private static final float ATTACK_AMOUNT_MIN = 1.0F;
	private static final float ATTACK_AMOUNT_MAX = 100.0F;

	private static final String TAG = Sling.MOD_ID + ".";
	private static final String TAG_ITEM = TAG + "item";
	private static final String TAG_CHAGE_AMMOUNT = TAG + "chage_ammount";
	private static final String TAG_KNOCKBACK_STRENGTH = TAG + "knockback_strength";

	private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack> createKey(EntitySlingBullet.class, DataSerializers.ITEM_STACK);
	private int chageAmmount;
	private int knockbackStrength;

	public EntitySlingBullet(World world)
	{
		super(world);
	}

	public EntitySlingBullet(World world, EntityLivingBase thrower, ItemStack stack, int chageAmmount)
	{
		super(world, thrower);

		this.setEntityItem(stack);
		this.setChageAmount(chageAmmount);
	}

	protected void entityInit()
	{
		super.entityInit();

		this.getDataManager().register(ITEM, ItemStack.EMPTY);
	}

	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		compound.setTag(TAG_ITEM, this.getEntityItem().writeToNBT(new NBTTagCompound()));
		compound.setByte(TAG_CHAGE_AMMOUNT, (byte) this.getChageAmount());
		compound.setByte(TAG_KNOCKBACK_STRENGTH, (byte) this.getKnockbackStrength());
	}

	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		this.setEntityItem(new ItemStack(compound.getCompoundTag(TAG_ITEM)));
		this.setChageAmount(compound.getByte(TAG_CHAGE_AMMOUNT));
		this.setKnockbackStrength(compound.getByte(TAG_KNOCKBACK_STRENGTH));
	}

	@Override
	protected float getGravityVelocity()
	{
		return 0.05F - ((float) this.getChageAmount() / 200);
	}

	@Override
	protected void onImpact(RayTraceResult result)
	{
		if (this.world.isRemote)
		{
			return;
		}

		BlockPos resultBlockPos;

		if (result.entityHit != null)
		{
			resultBlockPos = result.entityHit.getPosition();

			Entity target = result.entityHit;

			if (0 < this.getKnockbackStrength())
			{
				float velocity = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

				if (0.0F < velocity)
				{
					target.addVelocity(this.motionX * (double) this.getKnockbackStrength() * 0.6000000238418579D / (double) velocity, 0.1D, this.motionZ * (double) this.getKnockbackStrength() * 0.6000000238418579D / (double) velocity);
				}
			}

			if (this.isBurning() && !(target instanceof EntityEnderman))
			{
				target.setFire(this.getChageAmount() * 20);
			}

			target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.getAttackAmount());
		}
		else
		{
			resultBlockPos = result.getBlockPos().equals(BlockPos.ORIGIN) ? this.getPosition() : result.getBlockPos();

			IBlockState blockState = this.world.getBlockState(resultBlockPos);

			if (EntityWither.canDestroyBlock(blockState.getBlock()))
			{
				float blockHardness = blockState.getBlockHardness(this.world, resultBlockPos);

				if (blockHardness < this.getBulletBlockState().getBlockHardness(this.world, this.getPosition()))
				{
					this.world.destroyBlock(resultBlockPos, true);

					if (blockHardness == 0.0F)
					{
						return;
					}
				}
			}
		}

		FMLLog.info("ChageAmount : %d", this.getChageAmount());
		FMLLog.info("AttackAmount : %f", this.getAttackAmount());

		this.world.playEvent(2001, resultBlockPos, Block.getStateId(this.getBulletBlockState()));

		this.setDead();
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (this.isInWater())
		{
			this.extinguish();
		}
	}

	// TODO /* ======================================== MOD START =====================================*/

	public ItemStack getEntityItem()
	{
		return (ItemStack) this.getDataManager().get(ITEM);
	}

	public void setEntityItem(ItemStack stack)
	{
		this.getDataManager().set(ITEM, stack);
		this.getDataManager().setDirty(ITEM);
	}

	public int getChageAmount()
	{
		return this.chageAmmount;
	}

	public void setChageAmount(int chageAmount)
	{
		this.chageAmmount = chageAmount;
	}

	public int getKnockbackStrength()
	{
		return this.knockbackStrength;
	}

	public void setKnockbackStrength(int knockbackStrengthIn)
	{
		this.knockbackStrength = knockbackStrengthIn;
	}

	public IBlockState getBulletBlockState()
	{
		ItemStack stack = this.getEntityItem();

		if (stack.getItem() instanceof ItemBlock)
		{
			return ((ItemBlock) stack.getItem()).getBlock().getDefaultState();
		}

		return Blocks.AIR.getDefaultState();
	}

	private float getAttackAmount()
	{
		float blockHardness = this.getBulletBlockState().getBlockHardness(this.world, this.getPosition());
		float chageAmount = this.getChageAmount();

		float attackAmount = (blockHardness * chageAmount);
		attackAmount = Math.min(attackAmount, ATTACK_AMOUNT_MAX);
		attackAmount = Math.max(attackAmount, ATTACK_AMOUNT_MIN);

		return attackAmount;
	}

}
