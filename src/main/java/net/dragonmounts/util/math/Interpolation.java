/*
** 2016 March 05
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package net.dragonmounts.util.math;

/**
 * Interpolation utility class.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class Interpolation {
    public static float linear(float a, float b, float x) {
        if (x <= 0) {
            return a;
        }
        if (x >= 1) {
            return b;
        }
        return a * (1 - x) + b * x;
    }
    
    public static float smoothStep(float a, float b, float x) {
        if (x <= 0) {
            return a;
        }
        if (x >= 1) {
            return b;
        }
        x = x * x * (3 - 2 * x);
        return a * (1 - x) + b * x;
    }

    // http://www.java-gaming.org/index.php?topic=24122.0

    private Interpolation() {}

    public static void splineArrays(float x, boolean shift, float[] result, float[]... nodes) {
        // uncomment to disable interpolation
//        if (true) {
//            if (shift) {
//                System.arraycopy(nodes[(int) (x + 1) % nodes.length], 0, result, 0, nodes.length);
//            } else {
//                System.arraycopy(nodes[(int) x % nodes.length], 0, result, 0, nodes.length);
//            }
//            return;
//        }

        int i1 = (int) x % nodes.length;
        int i2 = (i1 + 1) % nodes.length;
        int i3 = (i1 + 2) % nodes.length;

        float[] a1 = nodes[i1];
        float[] a2 = nodes[i2];
        float[] a3 = nodes[i3];

        float xn = x % nodes.length - i1;

        if (shift) {
            Spline.interp(xn, result, a2, a3, a1, a2);
        } else {
            Spline.interp(xn, result, a1, a2, a3, a1);
        }
    }
}
