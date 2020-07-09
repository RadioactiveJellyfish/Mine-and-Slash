package com.robertx22.mine_and_slash.database.rarities.gears;

import com.robertx22.mine_and_slash.database.MinMax;
import com.robertx22.mine_and_slash.database.rarities.GearRarity;
import com.robertx22.mine_and_slash.database.rarities.base.BaseUncommon;

public class UncommonGear extends BaseUncommon implements GearRarity {
    UncommonGear() {
    }

    public static UncommonGear getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public float unidentifiedChance() {
        return 0;
    }

    @Override
    public int AffixChance() {
        return 15;
    }

    @Override
    public MinMax affixStatPercents() {
        return StatPercents();
    }

    @Override
    public MinMax uniqueStatPercents() {
        return StatPercents();
    }

    @Override
    public MinMax StatPercents() {
        return new MinMax(15, 50);
    }

    @Override
    public float salvageLotteryWinChance() {
        return 1.5F;
    }

    @Override
    public int Weight() {
        return 2000;
    }

    @Override
    public int maxAffixes() {
        return 2;
    }

    @Override
    public float itemTierPower() {
        return 1.2F;
    }

    private static class SingletonHolder {
        private static final UncommonGear INSTANCE = new UncommonGear();
    }
}
