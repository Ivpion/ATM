package com.atm.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class PageDataWrapperModel extends PageData<CardTransactionHistoryModel>{

    @JsonCreator
    public PageDataWrapperModel(@JsonProperty("content") List<CardTransactionHistoryModel> content, @JsonProperty("metadata") PageMetadata metadata) {
        super(content, metadata);
    }
}
