package hg.reserve_buy.itemserviceapi.core.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ItemType {
    GENERAL,
    TIME_DEAL;

    @JsonCreator
    public static ItemType from(String s) {
        return ItemType.valueOf(s.toUpperCase());
    }
}
