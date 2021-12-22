package me.buzz.coralmc.oneblockcore.database.queues.impl;

import dev.morphia.query.experimental.filters.Filters;
import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.database.DataService;
import me.buzz.coralmc.oneblockcore.database.queues.AbstractQueue;
import me.buzz.coralmc.oneblockcore.events.LoadedUserEvent;
import me.buzz.coralmc.oneblockcore.players.User;
import me.buzz.coralmc.oneblockcore.structures.actions.OperationAction;
import org.bukkit.Bukkit;

public class LoadUserQueue extends AbstractQueue<OperationAction<String>> {
    private final OneblockCore core = OneblockCore.get();
    private final DataService dataService = (DataService) core.getServiceHandler().getService(DataService.class);

    public LoadUserQueue() {
        super("User Loader Thread");
    }

    @Override
    public void compute(OperationAction<String> object) {
        if (object.getType() == OperationAction.OperationType.LOADING) {
            String player = object.getObject();

            User user = dataService.getDatastore().find(User.class).filter(Filters.eq("_id", player)).first();
            if (user == null) user = new User(player);

            core.getGame().getUsers().put(player, user);
            core.getLogger().info("Loaded a new User: " + player);

            Bukkit.getPluginManager().callEvent(new LoadedUserEvent(user));

            if (!user.hasIsland()) return;

            /*try (Jedis jedis = core.getDictation().getRedisManager().getJedis()){
                if (jedis.exists(RedisService.ISLAND_PREFIX + user.getIslandUUID())){
                    Island island = OneblockCore.GSON.fromJson(jedis.get(RedisService.ISLAND_PREFIX + user.getIslandUUID()), Island.class);
                    if (!island.isInIsland(player)){
                      user.setIslandUUID("");
                    }
                }
                else {
                    Island island = dataService.getDatastore().find(Island.class).filter(Filters.eq("_id", user.getIslandUUID())).first();
                    if (island == null || !island.isInIsland(player)) user.setIslandUUID("");
                    else {
                        jedis.set(RedisService.ISLAND_PREFIX + user.getIslandUUID(), OneblockCore.GSON.toJson(island));
                    }
                }
            }*/


        } else {
            dataService.getDatastore().save(core.getGame().getUsers().get(object.getObject()));
            core.getGame().getUsers().remove(object.getObject());

            core.getLogger().info("Saved a User: " + object.getObject());
        }

    }


}
