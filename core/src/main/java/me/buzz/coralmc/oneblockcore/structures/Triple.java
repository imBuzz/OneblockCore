package me.buzz.coralmc.oneblockcore.structures;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class Triple<K, V, Y> {

    private K first;
    private V second;
    private Y third;

}
