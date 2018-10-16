package com.robertx22.database.stats.types;

import java.util.Arrays;
import java.util.List;

import com.robertx22.enumclasses.Elements;
import com.robertx22.stats.IStatEffect;
import com.robertx22.stats.IStatEffects;
import com.robertx22.stats.Stat;
import com.robertx22.stats.StatEffects.CriticalDamageEffect;

public class CriticalDamage extends Stat implements IStatEffects {
	@Override
	public List<IStatEffect> GetEffects() {
		return Arrays.asList(new CriticalDamageEffect());
	}

	public CriticalDamage() {
	}

	@Override
	public String Name() {
		return "Critical Damage";
	}

	@Override
	public boolean ScalesToLevel() {
		return false;
	}

	@Override
	public Elements Element() {
		return null;
	}

	@Override
	public boolean IsPercent() {
		return true;
	}

}
