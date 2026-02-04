package com.iafenvoy.wamt.mixin;

import com.iafenvoy.wamt.KeyRecorder;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ClientLanguage.class)
public class ClientLanguageMixin {
    @Shadow
    @Final
    private Map<String, String> storage;
    @Unique
    private static Map<String, String> CACHE_MAP = new HashMap<>();

    // This is the only point needed to inject
    // Although in Language class there's also a lambda class, that one will be replaced immediately in resource reloading
    @Inject(method = "getOrDefault", at = @At("HEAD"))
    private void checkAndRecordMissing(String key, String defaultValue, CallbackInfoReturnable<String> cir) {
        if (!this.storage.containsKey(key)) KeyRecorder.recordAllMissingKeys(key);
    }

    // Layered details can only be read when reloading, so we should also clear cache
    @Inject(method = "loadFrom", at = @At(value = "HEAD"))
    private static void onStartLoad(CallbackInfoReturnable<ClientLanguage> cir) {
        KeyRecorder.reload();
        CACHE_MAP = new HashMap<>();
    }

    @Inject(method = "loadFrom", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    private static void onReloading(CallbackInfoReturnable<ClientLanguage> cir, @Local(ordinal = 0) Map<String, String> map, @Local String language) {
        KeyRecorder.recordUnchanged(CACHE_MAP, map, language);
        CACHE_MAP = new HashMap<>(map);
    }
}
