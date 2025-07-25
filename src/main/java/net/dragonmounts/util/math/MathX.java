/*
 ** 2012 Januar 8
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.util.math;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

/**
 * Math helper class.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class MathX {
    public static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    /// @see RenderLivingBase#prepareScale
    public static final float MOJANG_MODEL_OFFSET_Y = 1.501F;
    /// @see RenderLivingBase#prepareScale
    public static final float MOJANG_MODEL_SCALE = 0.0625F;
    public static final float PI_F = (float) Math.PI;
    /**
     * Constant by which to multiply an angular value in degrees to obtain an
     * angular value in radians.
     */
    private static final float DEGREES_TO_RADIANS = PI_F / 180.0F;

    /**
     * Constant by which to multiply an angular value in radians to obtain an
     * angular value in degrees.
     */
    private static final float RADIANS_TO_DEGREES = 180.F / PI_F;

    /// convert degrees to radians
    public static float toRadians(float angdeg) {
        return angdeg * DEGREES_TO_RADIANS;
    }

    /// convert radians to degrees
    public static float toDegrees(float angrad) {
        return angrad * RADIANS_TO_DEGREES;
    }

    /**
     * return a random value from a truncated gaussian distribution with
     * mean and standard deviation = threeSigma/3
     * distribution is truncated to +/- threeSigma.
     *
     * @param mean       the mean of the distribution
     * @param threeSigma three times the standard deviation of the distribution
     */
    public static double getTruncatedGaussian(Random rand, double mean, double threeSigma) {
        return mean + MathHelper.clamp(
                rand.nextGaussian(),
                -3.0,
                +3.0
        ) * threeSigma / 3.0;
    }

    @SuppressWarnings("ManualMinMaxCalculation")
    public static float clamp(float value) {
        return (value < 0F ? 0F : (value > 1F ? 1F : value));
    }

    public static float clampedRotate(float target, float center, float range) {
        return center + MathHelper.clamp(MathHelper.wrapDegrees(target - center), -range, range);
    }

    /**
     * Float Linear Interpolation
     * @return a + (b - a) * x
     */
    public static float lerp(float a, float b, float x) {
        return a + (b - a) * x;
    }

    /**
     * Double Linear Interpolation
     * @return a + (b - a) * x
     */
    public static double lerp(double a, double b, double x) {
        return a + (b - a) * x;
    }

    public static void slerpArrays(float[] a, float[] b, float[] c, float x) {
        if (a.length != b.length || b.length != c.length) {
            throw new IllegalArgumentException();
        }

        if (x <= 0) {
            System.arraycopy(a, 0, c, 0, c.length);
            return;
        }

        if (x >= 1) {
            System.arraycopy(b, 0, c, 0, c.length);
            return;
        }

        x = x * x * (3.0F - 2.0F * x);
        for (int i = 0; i < c.length; ++i) {
            c[i] = lerp(a[i], b[i], x);
        }
    }

    /**
     * Double trigonometric interpolation
     */
    public static double terp(double a, double b, double x) {
        if (x <= 0) {
            return a;
        }
        if (x >= 1) {
            return b;
        }

        double mu2 = 0.5 * (1 - Math.cos(x * Math.PI));
        return a * (1 - mu2) + b * mu2;
    }

    public final static double MINIMUM_SIGNIFICANT_DIFFERENCE = 1e-3;

    public static boolean isApproximatelyEqual(double x1, double x2) {
        return Math.abs(x1 - x2) <= MINIMUM_SIGNIFICANT_DIFFERENCE;
    }

    public static boolean isSignificantlyDifferent(double left, double right) {
        return Math.abs(left - right) > MINIMUM_SIGNIFICANT_DIFFERENCE;
    }

    /**
     * return the modulus (always positive)
     * @return calculates the numerator modulus by divisor, always positive
     */
    public static int modulus(int numerator, int divisor) {
        return (numerator % divisor + divisor) % divisor;
    }

    /**
     * interpolate from vector 1 to vector 2 using fraction
     *
     * @param delta 0 - 1; 0 = vector1, 1 = vector2
     * @return interpolated vector
     */
    public static Vec3d interpolateVec(Vec3d start, Vec3d end, float delta) {
        if (start == end) return end;
        return new Vec3d(
                start.x + (end.x - start.x) * delta,
                start.y + (end.y - start.y) * delta,
                start.z + (end.z - start.z) * delta
        );
    }

    public static float extractColor(int color, int channel) {
        return (color >> (channel << 3) & 0xFF) / 255F;
    }

    private MathX() {}
}
