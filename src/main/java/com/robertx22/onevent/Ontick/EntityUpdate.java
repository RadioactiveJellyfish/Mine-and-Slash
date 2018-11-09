package com.robertx22.onevent.ontick;

import java.util.List;

import com.robertx22.mmorpg.Main;
import com.robertx22.network.EntityPackage;
import com.robertx22.uncommon.capability.EntityData;
import com.robertx22.uncommon.datasaving.UnitSaving;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityUpdate {
	public EntityUpdate(EntityPlayerMP player, List<EntityLivingBase> entities) {
		this.player = player;
		this.entities = entities;
	}

	public EntityPlayerMP player;
	public List<EntityLivingBase> entities;
	public int current = 0;

	public boolean isFinished() {
		return current >= entities.size();
	}

	public void update() {

		if (!isFinished()) {

			String json = entities.get(current).getCapability(EntityData.Data, null).getNBT()
					.getString(UnitSaving.DataLocation);

			if (json != null && !json.isEmpty()) {
				EntityPackage mobpacket = new EntityPackage(json);
				Main.Network.sendTo(mobpacket, (EntityPlayerMP) player);
				current++;
			}
		}

	}

	public static boolean IsEntityCloseTo(EntityLivingBase first, EntityLivingBase second) {

		double distance = first.getDistance(second);

		return distance < 200;

	}

	public static void syncEntityToClient(EntityLivingBase entity) {

		String json = entity.getCapability(EntityData.Data, null).getNBT().getString(UnitSaving.DataLocation);

		if (json != null && !json.isEmpty()) {
			EntityPackage mobpacket = new EntityPackage(json);

			Main.Network.sendToAllAround(mobpacket,
					new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 150));

		}

	}

}