package me.gamma.infbanlay;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class InfbanlayPacketHandler {

	private static final String PROTOCOL_VERSION = "gamma";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Infbanlay.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static void register() {
		INSTANCE.registerMessage(PROTOCOL_VERSION.hashCode(), Integer.class, (i, buffer) -> buffer.writeInt(i), PacketBuffer::readInt, InfbanlayPacketHandler::handle);
	}

	public static void handle(Integer i, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Infbanlay.setMaxBannerLayers(i)));
	}

	public static void sendToClient(ServerPlayerEntity player) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), Infbanlay.getMaxBannerLayers());
	}

}
