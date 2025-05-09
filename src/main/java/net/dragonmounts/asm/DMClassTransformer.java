package net.dragonmounts.asm;

import net.minecraft.launchwrapper.IClassTransformer;

import java.util.HashMap;
import java.util.function.Function;

import static net.dragonmounts.asm.DragonMountsPlugin.PLUGIN_LOGGER;

public class DMClassTransformer implements IClassTransformer {
    private static final HashMap<String, Function<byte[], byte[]>> TRANSFORMERS;

    static {
        HashMap<String, Function<byte[], byte[]>> transformers = new HashMap<>();
        transformers.put(
                "net.minecraft.client.renderer.entity.layers.LayerCustomHead",
                DMClassTransformers::transformLayerCustomHead
        );
        transformers.put(
                "net.minecraft.entity.item.EntityItem",
                DMClassTransformers::transformEntityItem
        );
        transformers.put(
                "net.minecraftforge.registries.ForgeRegistry$Snapshot",
                DMClassTransformers::transformRegistrySnapshot
        );
        TRANSFORMERS = transformers;
    }

    @Override
    public byte[] transform(String raw, String clazz, byte[] bytecodes) {
        Function<byte[], byte[]> transformer = TRANSFORMERS.get(clazz);
        if (transformer == null) return bytecodes;
        PLUGIN_LOGGER.info("Transforming class: {}", clazz);
        try {
            return transformer.apply(bytecodes);
        } catch (Exception e) {
            PLUGIN_LOGGER.error("Failed to transform class: {}", clazz, e);
            return bytecodes;
        }
    }
}
