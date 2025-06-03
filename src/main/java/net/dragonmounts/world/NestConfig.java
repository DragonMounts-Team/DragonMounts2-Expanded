package net.dragonmounts.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.List;

public class NestConfig {
    public static NestConfig parse(JsonObject json, JsonContext context) {
        NestPlacement placement = NestPlacement.byName(JsonUtils.getString(json, "placement"));
        if (placement == null) throw new JsonSyntaxException("Unknown placement");
        ImmutableList.Builder<ResourceLocation> templates = ImmutableList.builder();
        for (JsonElement element : JsonUtils.getJsonArray(json, "templates")) {
            if (element.isJsonPrimitive()) {
                templates.add(new ResourceLocation(context.appendModId(element.getAsString())));
            } else throw new JsonParseException("Unexpected element in string only array");
        }
        if (json.has("island")) {
            String name = context.appendModId(JsonUtils.getString(json, "island"));
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
            if (block == null) throw new JsonSyntaxException("Unknown block: " + name);
            return new NestConfig(
                    JsonUtils.getFloat(json, "weight", 1.0F),
                    placement,
                    block.getDefaultState(),
                    templates.build()
            );
        }
        return new NestConfig(JsonUtils.getFloat(json, "weight", 1.0F), placement, null, templates.build());
    }

    public final float weight;
    public final NestPlacement placement;
    public final IBlockState island;
    public final List<ResourceLocation> templates;

    public NestConfig(float weight, NestPlacement placement, IBlockState island, List<ResourceLocation> templates) {
        this.weight = weight;
        this.placement = placement;
        this.island = island;
        this.templates = templates;
    }

    public NestConfig(NestPlacement placement, List<ResourceLocation> templates) {
        this(1.0F, placement, null, templates);
    }

    public NestConfig(NestPlacement placement, IBlockState island, List<ResourceLocation> templates) {
        this(1.0F, placement, island, templates);
    }
}
