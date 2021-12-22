package me.buzz.coralmc.oneblockcore.commands.impl;

import me.buzz.coralmc.oneblockcore.OneblockCore;
import me.buzz.coralmc.oneblockcore.commands.OneblockCommand;
import me.buzz.coralmc.oneblockcore.commands.impl.list.IsCreate;
import me.buzz.coralmc.oneblockcore.commands.impl.list.IsGo;

public class IslandBase extends OneblockCommand {
    private final OneblockCore core = OneblockCore.get();

    public IslandBase() {
        super("island");
        permission = "oneblock.island.base";

        aliases.add("is");
        aliases.add("oneblock");

        children.put("create", new IsCreate());
        children.put("go", new IsGo());
    }


}
