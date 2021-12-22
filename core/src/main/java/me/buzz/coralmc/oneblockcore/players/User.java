package me.buzz.coralmc.oneblockcore.players;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity("users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    private String nameID;
    @Setter
    private String islandUUID;
    private transient boolean talkingInIsland = false;

    public User(String nameID) {
        this.nameID = nameID;
    }

    public boolean hasIsland() {
        return islandUUID != null && !islandUUID.isEmpty();
    }


}
