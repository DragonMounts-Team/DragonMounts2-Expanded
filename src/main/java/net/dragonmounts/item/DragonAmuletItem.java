package net.dragonmounts.item;

import net.dragonmounts.objects.entity.entitytameabledragon.EntityTameableDragon;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static net.dragonmounts.util.EntityUtil.notOwner;

public class DragonAmuletItem extends AmuletItem<EntityTameableDragon> {
    public final DragonType type;

    public DragonAmuletItem(DragonType type) {
        super(EntityTameableDragon.class);
        this.type = type;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        return false;
    }

    @Override
    public ItemStack saveEntity(EntityTameableDragon dragon) {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = IEntityContainer.simplifyData(dragon.writeToNBT(new NBTTagCompound()));
        EntityLivingBase owner = dragon.getOwner();
        if (owner != null) {
            data.setString("OwnerName", owner.getName());
        }
        data.removeTag("UUIDMost");
        data.removeTag("UUIDLeast");
        root.setTag("EntityTag", data);
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(root);
        return stack;
    }

    @Nullable
    @Override
    public EntityTameableDragon loadEntity(World level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos, boolean yOffset, String feedback) {
        EntityTameableDragon dragon = new EntityTameableDragon(level);
        NBTTagCompound root = stack.getTagCompound();
        boolean flag = root == null;
        if (!flag) {
            NBTTagCompound data = root.getCompoundTag("EntityTag");
            if (!data.isEmpty()) {
                if (notOwner(data, player, "dragon.notOwned")) return null;
                flag = !data.hasKey("Variant");
                dragon.readFromNBT(data);
            }
        }
        if (flag) {
            dragon.setVariant(this.type.variants.draw(level.rand, null));
        }
        EntityUtil.finalizeSpawn(level, dragon, pos, true, null);
        if (stack.hasDisplayName()) {
            dragon.setCustomNameTag(stack.getDisplayName());
        }
        ItemMonsterPlacer.applyItemEntityDataToEntity(level, player, stack, dragon);
        level.playSound(null, pos, SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1);
        return dragon;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return this;
    }

    @Override
    public boolean isEmpty(@Nullable NBTTagCompound tag) {
        return false;
    }

    @Override
    protected DragonType getDragonType(NBTTagCompound data) {
        return this.type;
    }
}
