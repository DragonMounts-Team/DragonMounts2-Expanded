package net.dragonmounts.util.debugging;

import net.dragonmounts.entity.TameableDragonEntity;
import net.dragonmounts.entity.breath.BreathNode;
import net.dragonmounts.entity.breath.DragonHeadPositionHelper;
import net.dragonmounts.entity.helper.DragonLifeStage;
import net.dragonmounts.item.TestRunnerItem;
import net.dragonmounts.util.debugging.testclasses.TestForestBreath;
import net.minecraft.block.BlockLadder;
import net.minecraft.command.CommandClone;
import net.minecraft.command.server.CommandTeleport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
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
            //success = test1(worldIn, playerIn);  // todo restore
            TestForestBreath testForestBreath = new TestForestBreath();
            testForestBreath.test1(level, player);
            return true;
        });
        registry.register(Side.SERVER, 2, (level, player, stack) -> {
            TameableDragonEntity dragon = new TameableDragonEntity(level);
            BreathNode.Power power = BreathNode.Power.SMALL;
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
                power = BreathNode.Power.MEDIUM;
            }
            if (testCounter == 3) {
                origin = new Vec3d(0, 24, 0);
                target = new Vec3d(0, 4, 0);
                power = BreathNode.Power.LARGE;
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
    }

  static private int testCounter = 0;

  // dummy test: check the correct functioning of the ladder - to see which blocks it can stay attached to
  // The test region contains a ladder attached to a stone block.  We then replace it with different blocks and see
  //   whether the ladder remains or breaks appropriately; eg
  // testA - replace with wood
  // testB - replace with a glass block
  // testC - replace with diamond block
  private boolean test1(World worldIn, EntityPlayer playerIn)
  {
    BlockPos sourceRegionOrigin = new BlockPos(0, 204, 0);
    final int SOURCE_REGION_SIZE_X = 4;
    final int SOURCE_REGION_SIZE_Y = 2;
    final int SOURCE_REGION_SIZE_Z = 3;

    // put a stone block with attached ladder in the middle of our test region
    worldIn.setBlockState(sourceRegionOrigin.add(1, 0, 1), Blocks.STONE.getDefaultState());
    worldIn.setBlockState(sourceRegionOrigin.add(2, 0, 1),
                            Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.EAST));

    BlockPos testRegionOriginA = new BlockPos(5, 204, 0);
    BlockPos testRegionOriginB = new BlockPos(10, 204, 0);
    BlockPos testRegionOriginC = new BlockPos(15, 204, 0);

    teleportPlayerToTestRegion(playerIn, testRegionOriginA.south(5));  // teleport the player nearby so you can watch

    // copy the test blocks to the destination region
    copyTestRegion(playerIn, sourceRegionOrigin, testRegionOriginA,
                          SOURCE_REGION_SIZE_X, SOURCE_REGION_SIZE_Y, SOURCE_REGION_SIZE_Z);
    copyTestRegion(playerIn, sourceRegionOrigin, testRegionOriginB,
                          SOURCE_REGION_SIZE_X, SOURCE_REGION_SIZE_Y, SOURCE_REGION_SIZE_Z);
    copyTestRegion(playerIn, sourceRegionOrigin, testRegionOriginC,
                          SOURCE_REGION_SIZE_X, SOURCE_REGION_SIZE_Y, SOURCE_REGION_SIZE_Z);

    boolean success = true;
    // testA: replace stone with wood; ladder should remain
    worldIn.setBlockState(testRegionOriginA.add(1, 0, 1), Blocks.LOG.getDefaultState());
    success &= worldIn.getBlockState(testRegionOriginA.add(2, 0, 1)).getBlock() == Blocks.LADDER;

    // testB: replace stone with glass; ladder should be destroyed
    worldIn.setBlockState(testRegionOriginB.add(1, 0, 1), Blocks.GLASS.getDefaultState());
    success &= worldIn.getBlockState(testRegionOriginB.add(2, 0, 1)).getBlock() == Blocks.AIR;

    // testC: replace stone with diamond block; ladder should remain
    worldIn.setBlockState(testRegionOriginC.add(1, 0, 1), Blocks.DIAMOND_BLOCK.getDefaultState());
    success &= worldIn.getBlockState(testRegionOriginC.add(2, 0, 1)).getBlock() == Blocks.LADDER;

    return success;
  }

  /**
   * Teleport the player to the test region (so you can see the results of the test)
   * @param playerIn
   * @param location
   * @return
   */
  public static boolean teleportPlayerToTestRegion(EntityPlayer playerIn, BlockPos location)
  {
    if (!(playerIn instanceof EntityPlayerMP)) {
      throw new UnsupportedOperationException("teleport not supported on client side; server side only");
    }
    EntityPlayerMP entityPlayerMP = (EntityPlayerMP)playerIn;

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
   * @param entityPlayer
   * @param sourceOrigin origin of the source region
   * @param destOrigin origin of the destination region
   * @param xCount >=1
   * @param yCount >=1
   * @param zCount >=1
   * @return true for success, false otherwise
   */
  public static boolean copyTestRegion(EntityPlayer entityPlayer,
                                 BlockPos sourceOrigin, BlockPos destOrigin,
                                 int xCount, int yCount, int zCount)
  {
    checkArgument(xCount >= 1);
    checkArgument(yCount >= 1);
    checkArgument(zCount >= 1);
    String [] args = new String[9];

    if (!(entityPlayer instanceof EntityPlayerMP)) {
      throw new UnsupportedOperationException("teleport not supported on client side; server side only");
    }
    EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityPlayer;


    args[0] = String.valueOf(sourceOrigin.getX());
    args[1] = String.valueOf(sourceOrigin.getY());
    args[2] = String.valueOf(sourceOrigin.getZ());
    args[3] = String.valueOf(sourceOrigin.getX() + xCount - 1);
    args[4] = String.valueOf(sourceOrigin.getY() + yCount - 1);
    args[5] = String.valueOf(sourceOrigin.getZ() + zCount - 1);
    args[6] = String.valueOf(destOrigin.getX());
    args[7] = String.valueOf(destOrigin.getY());
    args[8] = String.valueOf(destOrigin.getZ());

    CommandClone commandClone = new CommandClone();
    try {
      commandClone.execute(entityPlayerMP.server, entityPlayer, args);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
