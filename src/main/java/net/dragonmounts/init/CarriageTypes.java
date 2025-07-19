package net.dragonmounts.init;

import net.dragonmounts.registry.CarriageType;
import net.minecraft.item.Item;

import java.util.function.Supplier;

import static net.dragonmounts.DragonMounts.applyId;
import static net.dragonmounts.DragonMounts.makeId;

public class CarriageTypes {
    // Item instances in `DMItems` have not been initialized yet, use lambda instead
    public static final CarriageType OAK = create("oak", () -> DMItems.OAK_CARRIAGE);
    public static final CarriageType SPRUCE = create("spruce", () -> DMItems.SPRUCE_CARRIAGE);
    public static final CarriageType BIRCH = create("birch", () -> DMItems.BIRCH_CARRIAGE);
    public static final CarriageType JUNGLE = create("jungle", () -> DMItems.JUNGLE_CARRIAGE);
    public static final CarriageType ACACIA = create("acacia", () -> DMItems.ACACIA_CARRIAGE);
    public static final CarriageType DARK_OAK = create("dark_oak", () -> DMItems.DARK_OAK_CARRIAGE);

    static CarriageType create(String name, Supplier<Item> item) {
        return applyId(new CarriageType.Default(item, makeId("textures/entities/dragon_carriage/carriage_" + name + ".png")), name);
    }
}
