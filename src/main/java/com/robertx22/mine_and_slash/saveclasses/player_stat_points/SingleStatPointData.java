package com.robertx22.mine_and_slash.saveclasses.player_stat_points;

import com.robertx22.mine_and_slash.database.stats.Stat;
import com.robertx22.mine_and_slash.db_lists.registry.SlashRegistry;
import com.robertx22.mine_and_slash.saveclasses.StatData;
import com.robertx22.mine_and_slash.saveclasses.gearitem.gear_bases.IStatsContainer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;

import java.util.Arrays;
import java.util.List;

@Storable

public class SingleStatPointData implements IStatsContainer {

    public SingleStatPointData(Stat stat) {
        this.stat = stat.GUID();
    }

    public SingleStatPointData() {

    }

    @Store
    public int points = 0;

    @Store
    public String stat = "";

    public Stat getStat() {
        return SlashRegistry.Stats().get(stat);
    }

    @Override
    public List<StatData> GetAllStats(int level) {
        StatData data = new StatData(getStat());
        data.Flat += points;
        return Arrays.asList(data);
    }

}
