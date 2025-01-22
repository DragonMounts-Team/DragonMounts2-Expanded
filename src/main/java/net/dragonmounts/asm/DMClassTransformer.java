package net.dragonmounts.asm;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.function.Function;

import static net.dragonmounts.asm.DragonMountsPlugin.PLUGIN_LOGGER;

public class DMClassTransformer implements IClassTransformer {
    public static final Object2ObjectOpenHashMap<String, Function<byte[], byte[]>> TRANSFORMERS;

    static {
        Object2ObjectOpenHashMap<String, Function<byte[], byte[]>> transformers = new Object2ObjectOpenHashMap<>();
        transformers.put("net.minecraft.client.renderer.entity.layers.LayerCustomHead", DMClassTransformers::transformLayerCustomHead);
        TRANSFORMERS = transformers;
    }

    @Override
    public byte[] transform(String raw, String clazz, byte[] bytecodes) {
        Function<byte[], byte[]> transformer = TRANSFORMERS.get(clazz);
        try {
            return transformer == null ? bytecodes : transformer.apply(bytecodes);
        } catch (Exception e) {
            PLUGIN_LOGGER.error("Failed to transform class: {}", clazz, e);
            return bytecodes;
        }
    }
}
