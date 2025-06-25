package dev.akarah.qh.client.mixin;

import dev.akarah.qh.client.MainClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public abstract UUID getUUID();

    @Inject(at = @At("HEAD"), method = "isCurrentlyGlowing", cancellable = true)
    public void glowInjection(CallbackInfoReturnable<Boolean> cir) {
        MainClient.netClient().ifPresent(client -> {
            if(client.state().groupMembers().contains(this.getUUID())) {
                cir.setReturnValue(true);
            }
        });
    }
}
