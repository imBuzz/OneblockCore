package me.buzz.coralmc.oneblockcore.structures.actions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OperationAction<V> {

    private final V object;
    private final OperationType type;

    public enum OperationType {

        SAVING,
        LOADING;


    }


}
