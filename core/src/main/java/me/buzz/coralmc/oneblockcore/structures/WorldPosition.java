package me.buzz.coralmc.oneblockcore.structures;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Getter
public class WorldPosition implements Cloneable {

    private final String world;
    private final float x, y, z;
    private final float yaw, pitch;

    public WorldPosition(String world, float x, float y, float z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        yaw = 0;
        pitch = 0;
    }

    public WorldPosition(String world, float x, float y, float z, float yaw, float pitch) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
