package com.iafenvoy.wamt;

//? fabric {
/*import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
*///?} else neoforge {
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
//?} else forge {
/*import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
*///?}

import java.util.List;

public final class Platform {
    public static List<String> gatherAllModIds() {
        //? fabric {
        /*return FabricLoader.getInstance().getAllMods().stream().map(ModContainer::getMetadata).map(ModMetadata::getId).toList();
         *///?} else {
        return ModList.get().getMods().stream().map(IModInfo::getModId).toList();
        //?}
    }
}
