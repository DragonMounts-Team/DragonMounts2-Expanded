package net.dragonmounts.item;

import net.dragonmounts.entity.ServerDragonEntity;
import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.registry.DragonType;
import net.dragonmounts.util.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;

import static net.dragonmounts.DragonMountsTags.TRANSLATION_KEY_PREFIX;

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
        NBTTagCompound data = IEntityContainer.simplifyData(dragon.writeToNBT(new NBTTagCompound()).copy());
        EntityLivingBase owner = dragon.getOwner();
        if (owner != null) {
            data.setString("OwnerName", owner.getName());
        }
        root.setTag("EntityTag", data);
        ItemStack stack = new ItemStack(this);
        stack.setTagCompound(root);
        return stack;
    }

    @Override
    public @Nullable ServerDragonEntity loadEntity(WorldServer level, ItemStack stack, @Nullable EntityPlayer player, BlockPos pos) {
        return EntityUtil.spawnDragonFormStack(level, stack, player, pos, this.type, (world, entity) ->
                world.playSound(null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_ILLAGER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1, 1)
        );
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
