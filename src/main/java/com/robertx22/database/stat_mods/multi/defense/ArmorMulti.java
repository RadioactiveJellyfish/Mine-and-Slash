package com.robertx22.database.stat_mods.multi.defense;

import com.robertx22.database.stat_types.defense.Armor;
import com.robertx22.stats.Stat;
import com.robertx22.stats.StatMod;
import com.robertx22.uncommon.enumclasses.StatTypes;

public class ArmorMulti extends StatMod {

    @Override
    public String GUID() {
	return "ArmorMulti";
    }

    @Override
    public int Min() {
	return 5;
    }

    @Override
    public int Max() {
	return 10;
    }

    @Override
    public StatTypes Type() {
	return StatTypes.Multi;
    }

    @Override
    public Stat GetBaseStat() {
	return new Armor();
    }

}