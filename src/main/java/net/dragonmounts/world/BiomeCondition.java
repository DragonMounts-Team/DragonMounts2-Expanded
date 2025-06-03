package net.dragonmounts.world;

import com.google.common.base.Predicates;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.BiomeDictionary;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class BiomeCondition {
    public static Predicate<Set<BiomeDictionary.Type>> parse(JsonObject json) {
        switch (JsonUtils.getString(json, "condition", "")) {
            case "any_match":
                return anyMatch(collectTypes(json));
            case "all_match":
                return allMatch(collectTypes(json));
            case "any_of":
                return anyOf(collectTerms(json));
            case "all_of":
                return allOf(collectTerms(json));
            case "inverted":
                return parse(JsonUtils.getJsonObject(json, "term")).negate();
            default:
                return Predicates.alwaysTrue();
        }
    }

    public static Predicate<Set<BiomeDictionary.Type>> anyOf(List<Predicate<Set<BiomeDictionary.Type>>> terms) {
        return types -> {
            for (Predicate<Set<BiomeDictionary.Type>> term : terms) {
                if (term.test(types)) return true;
            }
            return false;
        };
    }

    public static Predicate<Set<BiomeDictionary.Type>> allOf(List<Predicate<Set<BiomeDictionary.Type>>> terms) {
        return types -> {
            for (Predicate<Set<BiomeDictionary.Type>> term : terms) {
                if (!term.test(types)) return false;
            }
            return true;
        };
    }

    public static Predicate<Set<BiomeDictionary.Type>> anyMatch(Set<BiomeDictionary.Type> types) {
        return all -> {
            for (BiomeDictionary.Type type : types) {
                if (all.contains(type)) return true;
            }
            return false;
        };
    }

    public static Predicate<Set<BiomeDictionary.Type>> allMatch(Set<BiomeDictionary.Type> types) {
        return all -> all.containsAll(types);
    }

    public static Set<BiomeDictionary.Type> collectTypes(JsonObject json) {
        ReferenceOpenHashSet<BiomeDictionary.Type> types = new ReferenceOpenHashSet<>();
        for (JsonElement element : JsonUtils.getJsonArray(json, "types")) {
            if (element.isJsonPrimitive()) {
                types.add(BiomeDictionary.Type.getType(element.getAsString()));
            } else throw new JsonParseException("Unexpected element in string only array");
        }
        return types;
    }

    public static List<Predicate<Set<BiomeDictionary.Type>>> collectTerms(JsonObject json) {
        ObjectArrayList<Predicate<Set<BiomeDictionary.Type>>> conditions = new ObjectArrayList<>();
        for (JsonElement element : JsonUtils.getJsonArray(json, "terms")) {
            if (element.isJsonObject()) {
                conditions.add(parse(element.getAsJsonObject()));
            } else throw new JsonParseException("Unexpected element in object only array");
        }
        return conditions;
    }
}
