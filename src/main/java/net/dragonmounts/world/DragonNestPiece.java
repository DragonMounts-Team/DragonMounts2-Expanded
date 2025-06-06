package net.dragonmounts.world;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponentTemplate;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.TemplateManager;

import java.util.Random;

public class DragonNestPiece extends StructureComponentTemplate {
    private ResourceLocation structure;

    public DragonNestPiece() {
        this.placeSettings = new PlacementSettings().setReplacedBlock(Blocks.AIR).setIgnoreEntities(false);
    }

    public DragonNestPiece(
            TemplateManager manager,
            ResourceLocation structure,
            BlockPos pos,
            Rotation rotation,
            Mirror mirror
    ) {
        this();
        this.structure = structure;
        this.templatePosition = pos;
        this.placeSettings.setRotation(rotation).setMirror(mirror);
        this.loadTemplate(manager);
    }

    private void loadTemplate(TemplateManager manager) {
        this.setup(
                manager.getTemplate(null, this.structure),
                this.templatePosition,
                this.placeSettings
        );
    }

    @Override
    protected void writeStructureToNBT(NBTTagCompound tag) {
        super.writeStructureToNBT(tag);
        tag.setString("TID", this.structure.toString());
        tag.setString("Rot", this.placeSettings.getRotation().name());
        tag.setString("Mi", this.placeSettings.getMirror().name());
    }

    @Override
    protected void readStructureFromNBT(NBTTagCompound tag, TemplateManager manager) {
        super.readStructureFromNBT(tag, manager);
        this.structure = new ResourceLocation(tag.getString("TID"));
        this.placeSettings.setRotation(Rotation.valueOf(tag.getString("Rot")))
                .setMirror(Mirror.valueOf(tag.getString("Mi")));
        this.loadTemplate(manager);
    }

    @Override
    protected void handleDataMarker(String type, BlockPos pos, World level, Random random, StructureBoundingBox box) {
        switch (type) {
            case "Sentry": {
                EntityShulker shulker = new EntityShulker(level);
                shulker.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                shulker.setAttachmentPos(pos);
                level.spawnEntity(shulker);
                break;
            }
            case "Elytra": {
                EntityItemFrame frame = new EntityItemFrame(level, pos, this.placeSettings.getRotation().rotate(EnumFacing.NORTH));
                frame.setDisplayedItem(new ItemStack(Items.ELYTRA));
                level.spawnEntity(frame);
                break;
            }
        }
    }
}
