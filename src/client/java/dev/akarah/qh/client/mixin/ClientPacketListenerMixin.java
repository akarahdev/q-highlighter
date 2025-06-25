package dev.akarah.qh.client.mixin;

import dev.akarah.qh.client.data.GetSweepData;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(at = @At("HEAD"), method = "handleSystemChat", cancellable = true)
    public void sweepMessage(ClientboundSystemChatPacket par1, CallbackInfo ci) {
        var r = GetSweepData.detectSweepDetailImpl(par1);
        if(r) {
            ci.cancel();
        }
    }
}
