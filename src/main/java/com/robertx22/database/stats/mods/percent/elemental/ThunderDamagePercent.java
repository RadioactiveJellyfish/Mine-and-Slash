package com.robertx22.database.stats.mods.percent.elemental;

import com.robertx22.database.stats.types.elementals.spell_damage.SpellThunderDamage;
import com.robertx22.stats.Stat;
import com.robertx22.stats.StatMod;
import com.robertx22.uncommon.enumclasses.StatTypes;

public class ThunderDamagePercent extends StatMod {

	public ThunderDamagePercent() {
	}

	@Override
	public String GUID() {
		return "ThunderDamagePercent";
	}

	@Override
	public int Min() {
		return 2;
	}

	@Override
	public int Max() {
		return 10;
	}

	@Override
	public StatTypes Type() {
		return StatTypes.Percent;
	}

	@Override
	public Stat GetBaseStat() {
		return new SpellThunderDamage();
	}

}
