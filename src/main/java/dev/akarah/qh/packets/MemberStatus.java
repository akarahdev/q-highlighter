package dev.akarah.qh.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public record MemberStatus(
        double logs,
        Vec3 coordinates,
        Equipment equipment
) {
    public static StreamCodec<RegistryFriendlyByteBuf, MemberStatus> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, MemberStatus::logs,
            Vec3.STREAM_CODEC, MemberStatus::coordinates,
            Equipment.STREAM_CODEC, MemberStatus::equipment,
            MemberStatus::new
    );

    public record Equipment(
            Component mainItem,
            Component helmetItem,
            Component chestplateItem,
            Component leggingsItem,
            Component bootsItem
    ) {
        public static StreamCodec<RegistryFriendlyByteBuf, Equipment> STREAM_CODEC = StreamCodec.composite(
                ComponentSerialization.STREAM_CODEC, Equipment::mainItem,
                ComponentSerialization.STREAM_CODEC, Equipment::helmetItem,
                ComponentSerialization.STREAM_CODEC, Equipment::chestplateItem,
                ComponentSerialization.STREAM_CODEC, Equipment::leggingsItem,
                ComponentSerialization.STREAM_CODEC, Equipment::bootsItem,
                Equipment::new
        );

        public Stream<Component> stream() {
            return Stream.of(
                    this.mainItem,
                    this.helmetItem,
                    this.chestplateItem,
                    this.leggingsItem,
                    this.bootsItem
            );
        }
    }
}
