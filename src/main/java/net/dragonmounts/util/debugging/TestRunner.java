package net.dragonmounts.util.debugging;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathPower;
import net.dragonmounts.entity.breath.DragonHeadPositionHelper;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.item.TestRunnerItem;
import net.dragonmounts.util.LogUtil;
import net.minecraft.command.CommandClone;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Example test runner which is called when the player activate the testrunner item.
 * Created by TGG on 4/01/2016.
 */
public class TestRunner {
    public static void register(TestRunnerItem registry) {
        registry.register(Side.SERVER, 1, (level, player, stack) -> {
/*
  Test the forest breath.  Intended to be run on a blank test world SuperFlat.  Use the TestRunner item (debug config mode)
  Created by TGG on 24/01/2016.
 */
            // dummy test: check the correct functioning of the breath weapons
            // testA - test various ignition sources for generating an explosion when breathed upon
            BlockPos sourceRegionOrigin = new BlockPos(0, 204, 0);
            final int SOURCE_REGION_SIZE_X = 20;
            final int SOURCE_REGION_SIZE_Y = 10;
            final int SOURCE_REGION_SIZE_Z = 5;

            // put long line of dirt blocks to support our test items
            for (int x = 0; x < SOURCE_REGION_SIZE_X; ++x) {
                level.setBlockState(sourceRegionOrigin.add(x, 0, 2), Blocks.DIRT.getDefaultState());
            }

            level.setBlockState(sourceRegionOrigin.add(1, 1, 2), Blocks.OBSIDIAN.getDefaultState());
            level.setBlockState(sourceRegionOrigin.add(2, 1, 2), Blocks.LAVA.getDefaultState());
            level.setBlockState(sourceRegionOrigin.add(3, 1, 2), Blocks.OBSIDIAN.getDefaultState());

            level.setBlockState(sourceRegionOrigin.add(6, 1, 2), Blocks.TORCH.getDefaultState());

            level.setBlockState(sourceRegionOrigin.add(10, 0, 2), Blocks.NETHERRACK.getDefaultState());
            level.setBlockState(sourceRegionOrigin.add(10, 1, 2), Blocks.FIRE.getDefaultState());

            BlockPos testRegionOriginA = new BlockPos(0, 204, 8);
            //    BlockPos testRegionOriginB = new BlockPos(0, 204, 0);
            //    BlockPos testRegionOriginC = new BlockPos(0, 204, 0);

            TestRunner.teleportPlayerToTestRegion(player, testRegionOriginA.south(10));  // teleport the player nearby so you can watch

            // copy the test blocks to the destination region
            TestRunner.copyTestRegion(player, sourceRegionOrigin, testRegionOriginA,
                    SOURCE_REGION_SIZE_X, SOURCE_REGION_SIZE_Y, SOURCE_REGION_SIZE_Z);
            //todo reinstate for testing

//    DragonBreed forestDragonBreed = DragonBreedRegistry.getInstance().getBreedByName("forest");
//
//    EntityTameableDragon dragon = new EntityTameableDragon(worldIn);
//    BreathWeapon breathWeapon = forestDragonBreed.getBreathWeapon(dragon);  // just a dummy dragon
//
//    BreathAffectedBlock bab = new BreathAffectedBlock();
//    bab.addHitDensity(EnumFacing.DOWN, 1);
//    breathWeapon.affectBlock(worldIn, testRegionOriginA.add(2, 1, 2), bab);
//
//    breathWeapon.affectBlock(worldIn, testRegionOriginA.add(6, 1, 2), bab);
//
//    breathWeapon.affectBlock(worldIn, testRegionOriginA.add(10, 1, 2), bab);

            //    copyTestRegion(playerIn, sourceRegionOrigin, testRegionOriginB,
//                   SOURCE_REGION_SIZE_X, SOURCE_REGION_SIZE_Y, SOURCE_REGION_SIZE_Z);
//    copyTestRegion(playerIn, sourceRegionOrigin, testRegionOriginC,
//                   SOURCE_REGION_SIZE_X, SOURCE_REGION_SIZE_Y, SOURCE_REGION_SIZE_Z);

//    boolean success = true;
//    // testA: replace stone with wood; ladder should remain
//    worldIn.setBlockState(testRegionOriginA.add(1, 0, 1), Blocks.log.getDefaultState());
//    success &= worldIn.getBlockState(testRegionOriginA.add(2, 0, 1)).getBlock() == Blocks.ladder;
//
//    // testB: replace stone with glass; ladder should be destroyed
//    worldIn.setBlockState(testRegionOriginB.add(1, 0, 1), Blocks.glass.getDefaultState());
//    success &= worldIn.getBlockState(testRegionOriginB.add(2, 0, 1)).getBlock() == Blocks.air;
//
//    // testC: replace stone with diamond block; ladder should remain
//    worldIn.setBlockState(testRegionOriginC.add(1, 0, 1), Blocks.diamond_block.getDefaultState());
//    success &= worldIn.getBlockState(testRegionOriginC.add(2, 0, 1)).getBlock() == Blocks.ladder;

            return true;
        });
        registry.register(Side.SERVER, 2, (level, player, stack) -> {
            TameableDragonEntity dragon = new TameableDragonEntity(level);
            BreathPower power = BreathPower.SMALL;
            ++testCounter;
            Vec3d origin = new Vec3d(0, 24, 0);
            Vec3d target = new Vec3d(0, 4, 0);
            if (testCounter == 1) {
                origin = new Vec3d(0, 24, 0);
                target = new Vec3d(0, 4, 0);
            }
            if (testCounter == 2) {
                origin = new Vec3d(0, 24, 0);
                target = new Vec3d(0, 4, 0);
                power = BreathPower.MEDIUM;
            }
            if (testCounter == 3) {
                origin = new Vec3d(0, 24, 0);
                target = new Vec3d(0, 4, 0);
                power = BreathPower.LARGE;
                testCounter = 0;
            }
            //todo reinstate test for later if required
//        EntityBreathProjectileGhost entity = new EntityBreathProjectileGhost(worldIn, dragon, origin, target, power);
//        worldIn.spawnEntityInWorld(entity);
            System.out.println("Lighting spawned: mouth at [x,y,z] = " + origin + "to destination [x,y,z,] = " + target);
            return true;
        });
        registry.register(Side.SERVER, 60, (level, player, stack) -> {
            TameableDragonEntity dragon = new TameableDragonEntity(level);
            DragonHeadPositionHelper helper = new DragonHeadPositionHelper(dragon, 7);
            for (float scale = 0.0f; scale <= 1.0F; scale += 0.01F) {
                float headsize = helper.getRelativeHeadSize(scale);
                System.out.println("scale=" + scale + ", relativeheadsize=" + headsize);
            }
            return true;
        });
        registry.register(Side.SERVER, 61, (level, player, stack) -> {
            final int ARBITRARY_MINUS = -1000000;
            final int ARBITRARY_LARGE = 1000000;
            int minTick = DragonLifeStage.clipTickCountToValid(ARBITRARY_MINUS);
            int maxTick = DragonLifeStage.clipTickCountToValid(ARBITRARY_LARGE);
            System.out.println("Minimum tick:" + minTick);
            System.out.println("Maximum tick:" + maxTick);
            DragonLifeStage lastStage = null;
            int printAnywayTicks = 0;
            for (int i = minTick - 3; i <= maxTick + 10000; ++i) {
                boolean printCalcs = false;
                DragonLifeStage thisStage = DragonLifeStage.getLifeStageFromTickCount(i);
                if (thisStage != lastStage) {
                    lastStage = thisStage;
                    System.out.println("Changed to " + thisStage + " at tick=" + i);
                    printAnywayTicks = 1000;
                    printCalcs = true;
                } else if (--printAnywayTicks <= 0) {
                    printAnywayTicks = 1000;
                    printCalcs = true;
                }
                if (printCalcs) {
                    System.out.println("At tick=" + i + ": " +
                            "Scale = " + DragonLifeStage.getScaleFromTickCount(i) + ", " +
                            "StageProgress = " + DragonLifeStage.getStageProgressFromTickCount(i));
                }
            }
            System.out.println("Final stage was:" + lastStage);
            return true;
        });
        registry.register(Side.SERVER, 3, TestRunner::getTps);
    }

    private static boolean getTps(World world, EntityPlayer player, ItemStack stack) {
        if (player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).server.commandManager.executeCommand(player, "/forge tps");
        }
        return true;
    }

    private static int testCounter = 0;

    /**
     * Teleport the player to the test region (so you can see the results of the test)
     *
     * @param playerIn
     * @param location
     * @return
     */
    public static boolean teleportPlayerToTestRegion(EntityPlayer playerIn, BlockPos location) {
        if (!(playerIn instanceof EntityPlayerMP)) {
            throw new UnsupportedOperationException("teleport not supported on client side; server side only");
        }
        EntityPlayerMP entityPlayerMP = (EntityPlayerMP) playerIn;

        String tpArguments = "@p " + location.getX() + " " + location.getY() + " " + location.getZ();
        String[] tpArgumentsArray = tpArguments.split(" ");

        CommandTeleport commandTeleport = new CommandTeleport();
        try {
            commandTeleport.execute(entityPlayerMP.server, playerIn, tpArgumentsArray);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Copy a cuboid Test Region from one part of the world to another
     * The cuboid is x blocks wide, by y blocks high, by z blocks long
     *
     * @param entityPlayer
     * @param sourceOrigin origin of the source region
     * @param destOrigin   origin of the destination region
     * @param xCount       >=1
     * @param yCount       >=1
     * @param zCount       >=1
     * @return true for success, false otherwise
     */
    public static boolean copyTestRegion(EntityPlayer entityPlayer,
                                         BlockPos sourceOrigin, BlockPos destOrigin,
                                         int xCount, int yCount, int zCount) {
        checkArgument(xCount >= 1);
        checkArgument(yCount >= 1);
        checkArgument(zCount >= 1);

        if (!(entityPlayer instanceof EntityPlayerMP)) {
            throw new UnsupportedOperationException("teleport not supported on client side; server side only");
        }
        try {
            new CommandClone().execute(((EntityPlayerMP) entityPlayer).server, entityPlayer, new String[]{
                    String.valueOf(sourceOrigin.getX()),
                    String.valueOf(sourceOrigin.getY()),
                    String.valueOf(sourceOrigin.getZ()),
                    String.valueOf(sourceOrigin.getX() + xCount - 1),
                    String.valueOf(sourceOrigin.getY() + yCount - 1),
                    String.valueOf(sourceOrigin.getZ() + zCount - 1),
                    String.valueOf(destOrigin.getX()),
                    String.valueOf(destOrigin.getY()),
                    String.valueOf(destOrigin.getZ())
            });
        } catch (Exception e) {
            LogUtil.LOGGER.error("copyTestRegion", e);
            return false;
        }
        return true;
    }
}
