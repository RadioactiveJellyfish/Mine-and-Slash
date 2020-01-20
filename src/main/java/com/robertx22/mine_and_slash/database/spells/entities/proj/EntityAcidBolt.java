package com.robertx22.mine_and_slash.database.spells.entities.proj;

import com.robertx22.mine_and_slash.database.spells.entities.bases.BaseElementalBoltEntity;
import com.robertx22.mine_and_slash.mmorpg.registers.common.EntityRegister;
import com.robertx22.mine_and_slash.uncommon.enumclasses.Elements;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;

public class EntityAcidBolt extends BaseElementalBoltEntity {
    public EntityAcidBolt(EntityType<? extends EntityAcidBolt> type, World world) {
        super(type, world);
    }

    public EntityAcidBolt(World worldIn) {

        super(EntityRegister.ACIDBOLT, worldIn);

    }

    public EntityAcidBolt(FMLPlayMessages.SpawnEntity spawnEntity, World world) {
        super(EntityRegister.ACIDBOLT, world);

    }

    @Override
    public Elements element() {
        return Elements.Nature;
    }

}