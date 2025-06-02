package net.dragonmounts.network;

import net.dragonmounts.entity.ServerDragonEntity;

public abstract class DragonStates {
    /// Synchronization delegated
    public static final int SITTING_STATE = 1;
    /// Synchronization delegated
    public static final int LOCKED_STATE = 2;
    /// Synchronization required manually
    public static final int FOLLOWING_STATE = 3;

    public static int getStateToSync(ServerDragonEntity dragon) {
        return dragon.followOwner ? 1 : 0;
    }

    public static boolean isFollowing(int state) {
        return (state & 0b01) == 0b01;
    }
}
