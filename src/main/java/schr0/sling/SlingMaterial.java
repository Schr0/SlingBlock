package schr0.sling;

public enum SlingMaterial
{

	NORMAL(500, 5);

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
