package me.buzz.coralmc.oneblockcore.commands.impl.list;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.commands.OneblockCommand;
import me.buzz.coralmc.oneblockcore.islands.IslandGame;
import me.buzz.coralmc.oneblockcore.players.User;
import me.buzz.coralmc.oneblockcore.server.ServerType;
import me.buzz.coralmc.oneblockcore.server.redis.RedisService;
import me.buzz.coralmc.oneblockcore.server.redis.messages.ServerMessage;
import me.buzz.coralmc.oneblockcore.utils.Executor;
import me.buzz.coralmc.oneblockcore.utils.Strings;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IsGo extends OneblockCommand {
    private final OneblockCore core = OneblockCore.get();
    private final RedisService redisService = (RedisService) core.getServiceHandler().getService(RedisService.class);

    public IsGo() {
        super("go");
        permission = "oneblock.island.go";
    }

    @Override
    public boolean fakeExecute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        User user = core.getGame().getUsers().get(player.getName());
        if (!user.hasIsland()) {
            player.sendMessage(Strings.translate("&cNon possiedi un'isola nella quale recarti!"));
            return false;
        }

        if (core.getServerInstance().getType() == ServerType.ISLANDS) {
            IslandGame islandGame = ((IslandGame) core.getGame());

            if (islandGame.getIslandWorld().containsKey(user.getIslandUUID())) {
                World world = islandGame.getIslandWorld().get(user.getIslandUUID());
                player.teleport(world.getSpawnLocation());
            }
        } else {
            redisService.lookingForServer(ServerType.ISLANDS, user.getIslandUUID()).whenComplete((value, ex) -> {
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                if (value == null) {
                    player.sendMessage(Strings.translate("&cNon ci sono server disponibili, contatta lo staff!"));
                    return;
                }

                player.sendMessage(Strings.translate("&7In cerca di un luogo in cui mandarti..."));
                Executor.async(() -> core.getDictation().getCommandManager().executeCommand(
                        new ServerMessage(value.getServerID(),
                                ServerMessage.ServerMessageType.ISLAND_REQUEST,
                                user.getIslandUUID(), user.getNameID())), 1L);

            });
        }
        return true;
    }
}
