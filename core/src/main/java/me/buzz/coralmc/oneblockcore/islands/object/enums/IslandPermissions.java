package me.buzz.coralmc.oneblockcore.islands.object.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum IslandPermissions {

    BREAK_CORE("Rottura Nucleo"),
    PLACE_BLOCKS("Piazzare Blocchi"),
    BREAK_BLOCKS("Rottura Blocchi"),
    OPEN_CHEST("Apertura Casse"),
    INTERACT_WITH_DOOR("Interagire con le Porte"),

    PROMOTE_PLAYERS("Promuovere Player"),
    DEMOTE_PLAYERS("Depromuovere Player"),

    BAN_PLAYERS("Bannare Player"),
    UNBAN_PLAYERS("Sbannare Player"),

    MODIFY_SETTINGS("Modificare le Impostazioni"),
    MODIFY_PERMISSIONS("Modificare Permessi"),

    CHANGE_VISITATOR_SPAWN("Cambiare lo Spawn per i Visitatori"),
    CHANGE_SPAWN("Cambiare lo Spawn"),
    CHANGE_CORE("Cambiare la posizione del Nucleo"),

    KICK_PLAYER("Kickare i Membri"),
    KICK_VISITOR("Kickare i Visitatori"),
    INVITE_PLAYER("Invitare i Player");

    private final String name;

}
