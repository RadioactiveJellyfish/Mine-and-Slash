package com.robertx22.database.stat_mods.multi.offence;

import com.robertx22.database.stat_types.offense.PhysicalDamage;
import com.robertx22.stats.Stat;
import com.robertx22.stats.StatMod;
import com.robertx22.uncommon.enumclasses.StatTypes;

public class LessPhysicalDamageMulti extends StatMod {

    @Override
    public String GUID() {
	return "LessPhysicalDamageMulti";
    }

    @Override
    public int Min() {
	return -5;
    }

    @Override
    public int Max() {
	return -10;
    }

    @Override
    public StatTypes Type() {
	return StatTypes.Multi;
    }

    @Override
    public Stat GetBaseStat() {
	return new PhysicalDamage();
    }

}