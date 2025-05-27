package net.dragonmounts.util;

public interface ICollisionObserver {
    void handleMovement(double desiredX, double desiredY, double desiredZ, double actualX, double actualY, double actualZ);
}
