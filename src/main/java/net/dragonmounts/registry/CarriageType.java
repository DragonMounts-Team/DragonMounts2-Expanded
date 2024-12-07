package net.dragonmounts.registry;

import net.dragonmounts.entity.CarriageEntity;
import net.dragonmounts.init.CarriageTypes;
import net.dragonmounts.util.RegisteredObjectSerializer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

import static net.dragonmounts.DragonMounts.makeId;

public abstract class CarriageType extends IForgeRegistryEntry.Impl<CarriageType> {
    public static final ResourceLocation DEFAULT_KEY = makeId("oak");
    public static final DeferredRegistry<CarriageType> REGISTRY = new DeferredRegistry<>(makeId("carriage_type"), CarriageType.class, new RegistryBuilder<CarriageType>().setDefaultKey(DEFAULT_KEY));
    public static final RegisteredObjectSerializer<CarriageType> SERIALIZER = new RegisteredObjectSerializer<>(REGISTRY);

    public static CarriageType byName(String name) {
        CarriageType type = REGISTRY.getValue(new ResourceLocation(name));
        return type == null ? CarriageTypes.OAK : type;
    }

    public abstract Item getItem(CarriageEntity entity);

    public abstract ResourceLocation getTexture(CarriageEntity entity);

    public final ResourceLocation getSerializedName() {
        ResourceLocation key = this.getRegistryName();
        return key == null ? DEFAULT_KEY : key;
    }

    public static class Default extends CarriageType {
        public final Supplier<? extends Item> item;
        public final ResourceLocation texture;

        public Default(Supplier<? extends Item> item, ResourceLocation texture) {
            this.item = item;
            this.texture = texture;
        }

        @Override
        public Item getItem(CarriageEntity entity) {
            return this.item.get();
        }

        @Override
        public ResourceLocation getTexture(CarriageEntity entity) {
            return this.texture;
        }
    }
}
