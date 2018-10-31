package com.robertx22.database.rarities;

import com.robertx22.database.MinMax;
import com.robertx22.saveclasses.gearitem.gear_bases.Rarity;

public abstract class ItemRarity extends Rarity {
	public abstract int AffixChance();

	public abstract MinMax SecondaryStatsAmount();

	public abstract MinMax StatPercents();
}