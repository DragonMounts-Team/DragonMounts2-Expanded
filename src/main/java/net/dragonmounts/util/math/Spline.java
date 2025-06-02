/*
 ** 2012 Februar 2
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package net.dragonmounts.util.math;

import net.minecraft.util.math.MathHelper;

/**
 * Borrowed from http://www.java-gaming.org/index.php?topic=24122.0
 */

public class Spline {

    public static final float CR00 = -0.5f;
    public static final float CR01 = 1.5f;
    public static final float CR02 = -1.5f;
    public static final float CR03 = 0.5f;
    public static final float CR10 = 1.0f;
    public static final float CR11 = -2.5f;
    public static final float CR12 = 2.0f;
    public static final float CR13 = -0.5f;
    public static final float CR20 = -0.5f;
    public static final float CR21 = 0.0f;
    public static final float CR22 = 0.5f;
    public static final float CR23 = 0.0f;
    public static final float CR30 = 0.0f;
    public static final float CR31 = 1.0f;
    public static final float CR32 = 0.0f;
    public static final float CR33 = 0.0f;

    public static void interp(float x, float[] result, float[]... knots) {
        int nknots = knots.length;
        int nspans = nknots - 3;
        int knot = 0;
        if (nspans < 1) {
            System.out.println(Spline.class.getName()
                    + " Spline has too few knots");
            return;
        }
        x = MathHelper.clamp(x, 0.0F, 0.9999F) * nspans;
        // println("clamped x: " + x);
        int span = (int) x;
        // println("span before: " + span);
        if (span >= nknots - 3) {
            span = nknots - 3;
        }
        // println("span after: " + span);
        x -= span;
        knot += span;

        // println("knot: " + knot + " knots.length: " + knots.length);

        int dimension = result.length;
        for (int i = 0; i < dimension; i++) {
            float knot0 = knots[knot][i];
            float knot1 = knots[knot + 1][i];
            float knot2 = knots[knot + 2][i];
            float knot3 = knots[knot + 3][i];

            float c3 = CR00 * knot0 + CR01 * knot1 + CR02 * knot2 + CR03
                    * knot3;
            float c2 = CR10 * knot0 + CR11 * knot1 + CR12 * knot2 + CR13
                    * knot3;
            float c1 = CR20 * knot0 + CR21 * knot1 + CR22 * knot2 + CR23
                    * knot3;
            float c0 = CR30 * knot0 + CR31 * knot1 + CR32 * knot2 + CR33
                    * knot3;

            result[i] = ((c3 * x + c2) * x + c1) * x + c0;
        }
    }
}
