package net.dragonmounts.world;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.List;
import java.util.Random;

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
        return new NestConfig(JsonUtils.getFloat(json, "weight", 1.0F), placement, templates.build());
    }

    public final float weight;
    public final NestPlacement placement;
    public final List<ResourceLocation> templates;

    public NestConfig(float weight, NestPlacement placement, List<ResourceLocation> templates) {
        this.weight = weight;
        this.placement = placement;
        this.templates = templates;
    }

    public NestConfig(NestPlacement placement, List<ResourceLocation> templates) {
        this(1.0F, placement, templates);
    }

    public static NestConfig drawConfig(List<NestConfig> configs, Random random) {
        if (configs.size() > 1) {
            float total = 0.0F;
            for (NestConfig candidate : configs) {
                total += candidate.weight;
            }
            float target = random.nextFloat() * total;
            for (NestConfig candidate : configs) {
                if ((target -= candidate.weight) < 0.0F) return candidate;
            }
        }
        return configs.get(0);
    }
}
