package schr0.slingblock.item;

public enum SlingMaterial
{

	NORMAL(128, 5);

	private final int maxUses;
	private final int enchantability;

	private SlingMaterial(int maxUses, int enchantability)
	{
		this.maxUses = maxUses;
		this.enchantability = enchantability;
	}

	public int getMaxUses()
	{
		return this.maxUses;
	}

	public int getEnchantability()
	{
		return this.enchantability;
	}

}
