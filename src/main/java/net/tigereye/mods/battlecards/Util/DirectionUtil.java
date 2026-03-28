package net.tigereye.mods.battlecards.Util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DirectionUtil {

    public static Vec3d deriveVecFromPolarPlusRotationDirection(float pitch, float yaw, Direction direction){
        switch (direction) {
            case UP: pitch = pitch-90; return Vec3d.fromPolar(pitch,yaw);
            case DOWN: pitch = pitch+90; return Vec3d.fromPolar(pitch,yaw);
            case NORTH: return Vec3d.fromPolar(pitch,yaw);
            case SOUTH: return Vec3d.fromPolar(pitch,yaw).multiply(-1);
            case WEST: yaw = yaw-90; return Vec3d.fromPolar(pitch,yaw);
            case EAST: yaw = yaw+90; return Vec3d.fromPolar(pitch,yaw);
            default: return Vec3d.ZERO;
        }
    }

    public static Direction deriveDirectionFromPolarAndRotationDirection(float pitch, float yaw, Direction direction){
        Vec3d midstep = deriveVecFromPolarPlusRotationDirection(pitch,yaw,direction);
        return Direction.getFacing(midstep.getX(),midstep.getY(),midstep.getZ());
    }
}
