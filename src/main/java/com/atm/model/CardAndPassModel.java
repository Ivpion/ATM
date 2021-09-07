package com.atm.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CardAndPassModel extends CardModel {

    private final String pass;

    @JsonCreator
    @Builder
    public CardAndPassModel(@JsonProperty("card") String card,
                            @JsonProperty("pass") String pass) {
        super(card);
        this.pass = pass;
    }
}
