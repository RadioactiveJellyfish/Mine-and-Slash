package com.robertx22.mine_and_slash.mmorpg;

import com.robertx22.mine_and_slash.a_libraries.curios.GenerateCurioDataJsons;
import com.robertx22.mine_and_slash.a_libraries.curios.RegisterCurioSlots;
import com.robertx22.mine_and_slash.data_generation.DimConfigsDatapackManager;
import com.robertx22.mine_and_slash.data_generation.EntityConfigsDatapackManager;
import com.robertx22.mine_and_slash.data_generation.affixes.AffixDataPackManager;
import com.robertx22.mine_and_slash.data_generation.base_gear_types.BaseGearTypeDatapackManager;
import com.robertx22.mine_and_slash.data_generation.compatible_items.CompatibleItemDataPackManager;
import com.robertx22.mine_and_slash.data_generation.mob_affixes.MobAffixDataPackManager;
import com.robertx22.mine_and_slash.data_generation.rarities.GearRarityManager;
import com.robertx22.mine_and_slash.data_generation.rarities.SkillGemRarityManager;
import com.robertx22.mine_and_slash.data_generation.tiers.TierDatapackManager;
import com.robertx22.mine_and_slash.data_generation.unique_gears.UniqueGearDatapackManager;
import com.robertx22.mine_and_slash.database.registrators.CurrencyItems;
import com.robertx22.mine_and_slash.uncommon.error_checks.base.ErrorChecks;
import com.robertx22.mine_and_slash.mmorpg.proxy.ClientProxy;
import com.robertx22.mine_and_slash.mmorpg.proxy.IProxy;
import com.robertx22.mine_and_slash.mmorpg.proxy.ServerProxy;
import com.robertx22.mine_and_slash.mmorpg.registers.client.ClientSetup;
import com.robertx22.mine_and_slash.mmorpg.registers.common.CapabilityRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.common.ConfigRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.common.PacketRegister;
import com.robertx22.mine_and_slash.mmorpg.registers.server.CommandRegister;
import com.robertx22.mine_and_slash.auto_comp.DeterminePowerLevels;
import com.robertx22.mine_and_slash.event_hooks.data_gen.OnGatherData;
import com.robertx22.exiled_lib.registry.SlashRegistry;
import com.robertx22.mine_and_slash.uncommon.testing.tests.CheckWeaponDpsBalanceTest;
import com.robertx22.mine_and_slash.uncommon.testing.tests.CountUniqueGearTypes;
import com.robertx22.mine_and_slash.uncommon.develeper.CreateLangFile;
import com.robertx22.mine_and_slash.uncommon.testing.TestManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.logging.Logger;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(Ref.MODID)
@EventBusSubscriber
public class MMORPG {

    // DISABLE WHEN PUBLIC BUILD
    public static boolean RUN_DEV_TOOLS = true;

    public static boolean RUN_MIXIN_LOGS() {
        return false;
    }

    public static void mixinLog(String str) {
        if (RUN_MIXIN_LOGS()) {
            System.out.println(str);
        }
    }

    public static boolean statEffectDebuggingEnabled() {
        return false && RUN_DEV_TOOLS;
    }

    public static Logger LOGGER = Logger.getLogger(Ref.MOD_NAME);

    public static void devToolsLog(String string) {
        if (RUN_DEV_TOOLS) {
            System.out.println(string);
        }
    }

    public static void devToolsErrorLog(String string) {
        if (RUN_DEV_TOOLS) {
            try {
                throw new Exception(string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    private static final String PROTOCOL_VERSION = Integer.toString(1);

    public static final SimpleChannel Network = NetworkRegistry.ChannelBuilder.named(
        new ResourceLocation(Ref.MODID, "main_channel"))
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .simpleChannel();

    public MMORPG() {

        System.out.println("Starting Mine and Slash");

        final IEventBus bus = FMLJavaModLoadingContext.get()
            .getModEventBus();

        SlashRegistry.initRegistries();

        RegisterEvents.register();

        ConfigRegister.registerForgeConfigs(); // MUST BE IN MAIN CLASS

        SlashRegistry.registerAllItems(); // after config registerAll

        SlashRegistry.checkGuidValidity();

        bus.addListener(this::commonSetupEvent);
        bus.addListener(this::interModProcessEvent);
        bus.addListener(this::interModEnqueue);
        bus.addListener(this::onloadComplete);

        bus.addListener(new OnGatherData()::onGatherData);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::clientSetup);
        });

        RegDeffered.register(bus);

    }

    public static void logError(String s) {
        try {
            throw new Exception(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void commonSetupEvent(FMLCommonSetupEvent event) {

        System.out.println(Ref.MODID + ":FMLCommonSetupEvent");

        PacketRegister.register();

        CapabilityRegister.register();

    }

    private void interModEnqueue(final InterModEnqueueEvent event) {
        System.out.println(Ref.MODID + ":InterModEnqueueEvent");
        RegisterCurioSlots.register(event);

    }

    private void interModProcessEvent(final InterModProcessEvent event) {
        System.out.println(Ref.MODID + ":InterModProcessEvent");

        ConfigRegister.registerCustomConfigs();

    }

    public void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.setup(event);
    }

    @SubscribeEvent
    public static void onServerAboutToStart(FMLServerAboutToStartEvent event) {

        new CurrencyItems().registerAll();

        IReloadableResourceManager manager = event.getServer()
            .getResourceManager();

        manager.addReloadListener(new BaseGearTypeDatapackManager());
        manager.addReloadListener(new TierDatapackManager());
        manager.addReloadListener(new AffixDataPackManager());
        manager.addReloadListener(new MobAffixDataPackManager());
        manager.addReloadListener(new UniqueGearDatapackManager());
        manager.addReloadListener(new CompatibleItemDataPackManager());
        manager.addReloadListener(new GearRarityManager());
        manager.addReloadListener(new SkillGemRarityManager());
        manager.addReloadListener(new DimConfigsDatapackManager());
        manager.addReloadListener(new EntityConfigsDatapackManager());

    }

    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) {

    }

    static boolean regDefault = true;

    @SubscribeEvent
    public static void onServerStarted(FMLServerStartedEvent event) {

        DeterminePowerLevels.setupHashMaps();

        CommandRegister.Register(event.getServer());

        SlashRegistry.checkGuidValidity();

        ErrorChecks.getAll()
            .forEach(x -> x.check());
        SlashRegistry.unregisterInvalidEntries();

        regDefault = event.getServer()
            .getGameRules()
            .get(GameRules.NATURAL_REGENERATION)
            .get();

        event.getServer()
            .getGameRules()
            .get(GameRules.NATURAL_REGENERATION)
            .set(false, event.getServer());

        if (RUN_DEV_TOOLS) { // CHANGE ON PUBLIC BUILDS TO FALSE
            TestManager.RunAllTests();
            CreateLangFile.create();
            GenerateCurioDataJsons.generate();
            CountUniqueGearTypes.count();
            CheckWeaponDpsBalanceTest.run();
        }

    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppedEvent event) {
        event.getServer()
            .getGameRules()
            .get(GameRules.NATURAL_REGENERATION)
            .set(regDefault, event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(FMLServerStoppingEvent event) {

    }

    public static <MSG> void sendToTracking(MSG msg, Entity entity) {

        if (msg == null || entity == null) {
            return;
        }
        if (entity.world.isRemote) {
            return;
        }

        Network.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);

        if (entity instanceof PlayerEntity) {
            sendToClient(msg, (ServerPlayerEntity) entity);
        }

    }

    public static <MSG> void sendToTracking(MSG msg, BlockPos pos, World world) {

        if (msg == null || world == null) {
            return;
        }

        PacketDistributor.TargetPoint point = new PacketDistributor.TargetPoint(
            pos.getX(), pos.getY(), pos.getZ(), 50, world.getDimension()
            .getType());

        Network.send(PacketDistributor.NEAR.with(() -> point), msg);

    }

    public static <MSG> void sendToClient(MSG msg, ServerPlayerEntity player) {

        if (player != null && msg != null) {
            Network.sendTo(msg, player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static <MSG> void sendToServer(MSG msg) {

        Network.sendToServer(msg);
    }

    public void onloadComplete(FMLLoadCompleteEvent evt) {

    }

}
