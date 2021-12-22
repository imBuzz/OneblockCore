package me.buzz.coralmc.oneblockcore.islands.object.player.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BannedIslandPlayer {

    private String name;
    private long date;
    private String executor;


}
