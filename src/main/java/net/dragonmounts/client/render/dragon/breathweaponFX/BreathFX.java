package net.dragonmounts.client.render.dragon.breathweaponFX;

import net.dragonmounts.objects.entity.entitytameabledragon.breath.DragonBreathMode;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.IEntityParticle;
import net.dragonmounts.objects.entity.entitytameabledragon.breath.nodes.BreathNodeP;
import net.dragonmounts.util.math.Pair;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/** EntityFX used to refer to all BreathFX types
 * Created by TGG on 6/03/2016.
 */
public class BreathFX extends Particle implements IEntityParticle {
  public BreathFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
                  double ySpeedIn,
                  double zSpeedIn) {
    super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
  }

  public void updateBreathMode(DragonBreathMode dragonBreathMode)
  {
    breathNode.changeBreathMode(dragonBreathMode);
  }

  protected BreathNodeP breathNode;

  // a record of which parts of the entity collided with an object during moving
  // each entry is the face of the entity and the zone (AABB) that collided
  // eg (WEST, [3,2,6]-->[3.5, 2, 6] means the west face of the entity collided; the entity tried to move to
  //   x = 3, but got pushed back out to x=3.5
  private List<Pair<EnumFacing, AxisAlignedBB>> collisions = new ArrayList<Pair<EnumFacing, AxisAlignedBB>>();

  protected boolean isCollided;

  @Override
  public double getMotionX() {return motionX;}
  @Override
  public double getMotionY() {return motionY;}
  @Override
  public double getMotionZ() {return motionZ;}
  @Override
  public double getSpeedSQ() {return motionX*motionX + motionY*motionY + motionZ*motionZ;}
  @Override
  public boolean isCollided() {return isCollided;}
  @Override
  public boolean isOnGround() {return onGround;}

  @Override
  public void setMotion(Vec3d newMotion) {
    motionX = newMotion.x;
    motionY = newMotion.y;
    motionZ = newMotion.z;
  }

  @Override
  public boolean isInWater() {
    for (Pair<EnumFacing, AxisAlignedBB> collision : collisions) {
      if (world.isMaterialInBB(collision.getSecond(), Material.WATER)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isInLava() {
    for (Pair<EnumFacing, AxisAlignedBB> collision : collisions) {
      if (world.isMaterialInBB(collision.getSecond(), Material.LAVA)) {
        return true;
      }
    }
    return false;
  }

}
