package com.atm.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class CardTransactionHistoryModel {

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime time;
    private final BigDecimal balanceBefore;
    private final BigDecimal amount;
    private final BigDecimal finallyBalance;
    private final boolean success;

    @JsonCreator
    @Builder
    public CardTransactionHistoryModel(@JsonProperty("time") LocalDateTime time,
                                       @JsonProperty("balanceBefore") BigDecimal balanceBefore,
                                       @JsonProperty("amount") BigDecimal amount,
                                       @JsonProperty("finallyBalance") BigDecimal finallyBalance,
                                       @JsonProperty("success") boolean success) {
        this.time = time;
        this.balanceBefore = balanceBefore;
        this.amount = amount;
        this.finallyBalance = finallyBalance;
        this.success = success;
    }
}
