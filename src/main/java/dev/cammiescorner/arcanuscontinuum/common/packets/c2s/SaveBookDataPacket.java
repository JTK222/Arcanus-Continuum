package dev.cammiescorner.arcanuscontinuum.common.packets.c2s;

import dev.cammiescorner.arcanuscontinuum.Arcanus;
import dev.cammiescorner.arcanuscontinuum.api.spells.Spell;
import dev.cammiescorner.arcanuscontinuum.common.items.SpellBookItem;
import io.netty.buffer.Unpooled;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

public class SaveBookDataPacket {
	public static final Identifier ID = Arcanus.id("save_book_data");

	public static void send(BlockPos pos, Spell spell) {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeBlockPos(pos);
		buf.writeNbt(spell.toNbt());
		ClientPlayNetworking.send(ID, buf);
	}

	public static void handler(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
		BlockPos pos = buf.readBlockPos();
		NbtCompound spellNbt = buf.readNbt();

		server.execute(() -> {
			ServerWorld world = player.getServerWorld();

			if(world.getBlockEntity(pos) instanceof LecternBlockEntity lectern && lectern.getBook().getItem() instanceof SpellBookItem) {
				lectern.getBook().getOrCreateNbt().put("Spell", spellNbt);

				for(ServerPlayerEntity serverPlayer : world.getChunkManager().delegate.getPlayersWatchingChunk(new ChunkPos(pos), false))
					serverPlayer.networkHandler.sendPacket(BlockEntityUpdateS2CPacket.of(lectern, BlockEntity::toNbt));
			}
		});
	}
}
