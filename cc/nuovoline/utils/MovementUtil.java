package cc.nuovoline.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import static java.lang.Math.cos;
import static net.minecraft.util.MathHelper.sin;

public class MovementUtil {

    private static final Minecraft mc = Minecraft.getInstance();

    // Keeps track of off-ground ticks
    private static int offGroundTicks = 0;

    // Determines if the player is currently moving
    public static boolean isMoving() {
        EntityPlayerSP player = mc.player;

        if (player == null) {
            return false;
        }

        // Check if the player is moving in any direction
        return player.motionX != 0 || player.motionZ != 0;
    }



    // Updates the number of offGroundTicks
    public static void updateOffGroundTicks() {
        EntityPlayerSP player = mc.player;

        if (player == null) {
            return;
        }

        if (!player.onGround) {
            offGroundTicks++;
        } else {
            offGroundTicks = 0; // Reset when on the ground
        }
    }

    // Getter for offGroundTicks
    public static int getOffGroundTicks() {
        return offGroundTicks;
    }

    // Resets offGroundTicks
    public static void resetOffGroundTicks() {
        offGroundTicks = 0;
    }

    // Checks if the player is currently flying or falling (off the ground)
    public static boolean isInAir() {
        EntityPlayerSP player = mc.player;

        if (player == null) {
            return false;
        }

        return !player.onGround && !player.isInWater();
    }

    public static boolean hasSpeedPot() {
        return mc.player.isPotionActive(Potion.moveSpeed);
    }

    // Get the player's movement speed
    public static float getMovementSpeed() {
        EntityPlayerSP player = mc.player;

        if (player == null) {
            return 0;
        }

        double baseSpeed = 0.2873;  // Default walking speed
        float sprintSpeed = 1.3F;   // Sprinting speed factor

        if (player.isSprinting()) {
            return (float)(baseSpeed * sprintSpeed);
        } else {
            return (float)baseSpeed;
        }
    }
    public class MoveEvent {
        public double x;
        public double z;

        public void zeroXZ() {
            x = 0;
            z = 0;
        }
    }

    public static class Vec2 {
        public float x;
        public float y;

        public Vec2() {
            this(0, 0);
        }

        public Vec2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Vec2 add(Vec2 other) {
            return new Vec2(this.x + other.x, this.y + other.y);
        }

        public Vec2 subtract(Vec2 other) {
            return new Vec2(this.x - other.x, this.y - other.y);
        }

        public Vec2 scale(float scalar) {
            return new Vec2(this.x * scalar, this.y * scalar);
        }

        public Vec2 scale(double scalar) {
            return scale((float) scalar);
        }

        public float length() {
            return (float) Math.sqrt(x * x + y * y);
        }

        public Vec2 normalize() {
            float len = length();
            return len == 0 ? new Vec2(0, 0) : new Vec2(x / len, y / len);
        }

        @Override
        public String toString() {
            return "Vec2(" + x + ", " + y + ")";
        }
    }


    public class Vec3 {
        public double xCoord;
        public double zCoord;

        public Vec3 strafe(float yaw, double speed, double strength, boolean moveCheck) {
            if (moveCheck) {
                xCoord = 0.0;
                zCoord = 0.0;
                return this;
            }

            double prevX = xCoord * (1.0 - strength);
            double prevZ = zCoord * (1.0 - strength);
            double useSpeed = speed * strength;

            double angle = Math.toRadians(yaw);
            xCoord = (-sin(angle) * useSpeed) + prevX;
            zCoord = (cos(angle) * useSpeed) + prevZ;
            return this;
        }
    }

    public class Player {
        public double motionX;
        public double motionZ;
        public boolean isMoving;
        public float direction;

        public void stopXZ() {
            motionX = 0;
            motionZ = 0;
        }
    }

    public static class Game {
        public static Player thePlayer;

        public static void strafe(float speed, boolean stopWhenNoInput, MoveEvent moveEvent, double strength) {
            if (thePlayer == null) return;

            if (!thePlayer.isMoving) {
                if (stopWhenNoInput) {
                    if (moveEvent != null) {
                        moveEvent.zeroXZ();
                    }
                    thePlayer.stopXZ();
                }
                return;
            }

            double prevX = thePlayer.motionX * (1.0 - strength);
            double prevZ = thePlayer.motionZ * (1.0 - strength);
            double useSpeed = speed * strength;

            float yaw = thePlayer.direction;
            double x = (-sin(yaw) * useSpeed) + prevX;
            double z = (cos(yaw) * useSpeed) + prevZ;

            if (moveEvent != null) {
                moveEvent.x = x;
                moveEvent.z = z;
            }

            thePlayer.motionX = x;
            thePlayer.motionZ = z;
        }
    }


}
