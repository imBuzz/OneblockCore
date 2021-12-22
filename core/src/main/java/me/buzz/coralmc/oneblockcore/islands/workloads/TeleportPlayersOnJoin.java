package me.buzz.coralmc.oneblockcore.islands.workloads;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.database.queues.AbstractQueue;
import me.buzz.coralmc.oneblockcore.islands.IslandGame;
import me.buzz.coralmc.oneblockcore.players.User;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportPlayersOnJoin extends AbstractQueue<User> {

    private final IslandGame islandGame = (IslandGame) OneblockCore.get().getGame();

    public TeleportPlayersOnJoin() {
        super("Teleport Players on Islands Thread");
    }

    @Override
    public boolean applyPreconditions() {
        User user = queue.peek();
        if (user == null) return false;
        return Bukkit.getPlayer(user.getNameID()) != null;
    }

    @Override
    public void compute(User user) {
        Player player = Bukkit.getPlayer(user.getNameID());
        if (player == null) {
            OneblockCore.get().getLogger().severe("Player on Teleport to island null: " + user.getNameID());
            return;
        }

        islandGame.getWorkloadThread().addLoad(() -> {
            World world = islandGame.getIslandWorld().get(DataService.ISLAND_WORLD_TAG + user.getIslandUUID()).get();
            if (world == null) {
                OneblockCore.get().getLogger().severe("Mondo: " + DataService.ISLAND_WORLD_TAG + user.getIslandUUID() + " nullo");
                return;
            }
            player.teleport(world.getSpawnLocation());
        });
    }
}
