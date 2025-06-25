package dev.akarah.qh.client.mixin;

import dev.akarah.qh.client.ClientUtil;
import dev.akarah.qh.client.MainClient;
import dev.akarah.qh.client.data.EquipmentSlotGetter;
import dev.akarah.qh.client.data.GetSweepData;
import dev.akarah.qh.packets.C2SPacket;
import dev.akarah.qh.packets.MemberStatus;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(at = @At("HEAD"), method = "handleSystemChat", cancellable = true)
    public void sweepMessage(ClientboundSystemChatPacket par1, CallbackInfo ci) {
        var r = GetSweepData.detectSweepDetailImpl(par1);
        if(r) {
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void sendStatus(CallbackInfo ci) {
        MainClient.netClient().ifPresent(netClient -> {
            var entity = netClient.entity();
            entity.writePacket(new C2SPacket.UpdateStatus(
                    new MemberStatus(
                            MainClient.SWEEP_DATA.logs(),
                            ClientUtil.localPlayer().map(Entity::position).orElse(new Vec3(0, 0, 0)),
                            new MemberStatus.Equipment(
                                    EquipmentSlotGetter.playerSlotOrEmpty(EquipmentSlot.MAINHAND).getHoverName(),
                                    EquipmentSlotGetter.playerSlotOrEmpty(EquipmentSlot.HEAD).getHoverName(),
                                    EquipmentSlotGetter.playerSlotOrEmpty(EquipmentSlot.CHEST).getHoverName(),
                                    EquipmentSlotGetter.playerSlotOrEmpty(EquipmentSlot.LEGS).getHoverName(),
                                    EquipmentSlotGetter.playerSlotOrEmpty(EquipmentSlot.FEET).getHoverName()
                            )
                    )
            ));
        });
    }
}
