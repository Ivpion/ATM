package com.atm.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class CardModel {

    private final String card;

    @JsonCreator
    public CardModel(String card) {
        this.card = card;
    }
}
