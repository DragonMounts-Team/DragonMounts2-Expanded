package net.dragonmounts.item;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.init.DMItems;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.registry.DragonVariant;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;
import static net.dragonmounts.util.EntityUtil.notOwner;

public class DragonAmuletItem extends AmuletItem<TameableDragonEntity> {
    public static final String TRANSLATION_KEY = TRANSLATION_KEY_PREFIX + "dragon_amulet";
    public final DragonType type;

    public DragonAmuletItem(DragonType type) {
        super(TameableDragonEntity.class);
        this.type = type;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        return false;
    }

    @Override
    public ItemStack saveEntity(TameableDragonEntity dragon) {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = IEntityContainer.simplifyData(dragon.writeToNBT(new NBTTagCompound()));
        EntityLivingBase owner = dragon.getOwner();
        if (owner != null) {
            data.setString("OwnerName", owner.getName());
        }
        root.setTag("EntityTag", data);
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(root);
        return stack;
    }

    @Nullable
    @Override
    public ServerDragonEntity loadEntity(WorldServer level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos, boolean yOffset, @Nullable String feedback) {
        ServerDragonEntity dragon = new ServerDragonEntity(level);
        NBTTagCompound root = stack.getTagCompound();
        boolean flag = root == null;
        if (!flag) {
            NBTTagCompound data = root.getCompoundTag("EntityTag");
            if (!data.isEmpty()) {
                if (notOwner(data, player, "message.dragonmounts.dragon.notOwner")) return null;
                flag = !data.hasKey(DragonVariant.DATA_PARAMETER_KEY);
                dragon.readFromNBT(data);
            }
        }
        if (flag) {
            dragon.setVariant(this.type.variants.draw(level.rand, null));
        }
        if (!EntityUtil.finalizeSpawn(level, dragon, pos, true, null)) return null;
        if (stack.hasDisplayName()) {
            dragon.setCustomNameTag(stack.getDisplayName());
        }
        ItemMonsterPlacer.applyItemEntityDataToEntity(level, player, stack, dragon);
        level.playSound(null, pos, SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1);
        return dragon;
    }

    @Nullable
    @Override
    public Item getContainerItem() {
        return DMItems.AMULET;
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
