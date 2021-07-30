package me.gamma.infbanlay;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Infbanlay.MOD_ID)
public class Infbanlay {

	public static final String MOD_ID = "infbanlay";

	private static final Logger logger = LogManager.getLogger();

	public Infbanlay() {
		InfbanlayPacketHandler.register();
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
		MinecraftForge.EVENT_BUS.register(this);

		logger.log(Level.INFO, "Creating Infbanlay Instance!");
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	@SubscribeEvent
	public void onPlayerJoin(PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();
		if (player instanceof ServerPlayerEntity) {
			logger.log(Level.INFO, "Update Max Banner Layers for Player {} to {}!", player.getName().getString(), getMaxBannerLayers());
			InfbanlayPacketHandler.sendToClient((ServerPlayerEntity) player);
		}
	}

	public static class Common {

		public final IntValue maxBannerLayers;

		public Common(ForgeConfigSpec.Builder builder) {
			this.maxBannerLayers = builder.comment("Amount of banner layers a banner can have.").worldRestart().defineInRange("max-banner-layers", 6, 1, Integer.MAX_VALUE);
		}
	}

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static {
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}

	public static int getMaxBannerLayers() {
		return COMMON.maxBannerLayers.get();
	}

	public static void setMaxBannerLayers(int maxBannerLayers) {
		logger.log(Level.INFO, "Set Max Banner Layers to {}!", maxBannerLayers);
		COMMON.maxBannerLayers.set(maxBannerLayers);
	}

}
