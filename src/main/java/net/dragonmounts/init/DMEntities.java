package net.dragonmounts.init;

import net.dragonmounts.DragonMountsTags;
import net.dragonmounts.entity.CarriageEntity;
import net.dragonmounts.entity.EntityContainerItemEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;

import static net.dragonmounts.DragonMounts.makeId;

public class DMEntities {
    public static final ResourceLocation DRAGON_ID = makeId("dragon");
    public static final EntityEntry DRAGON = EntityEntryBuilder.create()
            .entity(TameableDragonEntity.class)
            .factory(TameableDragonEntity::new)
            .id(DRAGON_ID, 1)
            .tracker(80, 3, true)
            .name("dragonmounts.dragon.name")
            .build();
    public static final EntityEntry CARRIAGE = EntityEntryBuilder.create()
            .entity(CarriageEntity.class)
            .factory(CarriageEntity::new)
            .id(new ResourceLocation(DragonMountsTags.MOD_ID, "carriage"), 2)
            .tracker(32, 3, true)
            .name("carriage")
            .build();
    public static final EntityEntry CONTAINER_ITEM = EntityEntryBuilder.create()
            .entity(EntityContainerItemEntity.class)
            .factory(EntityContainerItemEntity::new)
            .id(new ResourceLocation(DragonMountsTags.MOD_ID, "indestructible"), 3)
            .tracker(64, 20, true)
            .name("container_item")
            .build();
}
