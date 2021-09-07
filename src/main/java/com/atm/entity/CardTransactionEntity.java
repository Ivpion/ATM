package com.atm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "atm_card_history")
public class CardTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "create_time")
    private LocalDateTime createTime;
    @Column(name = "balance_before")
    private BigDecimal beforeBalance;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "final_balance")
    private BigDecimal finalBalance;
    @Column(name = "success")
    private boolean success;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH},
            fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private CardEntity card;


    public static CardTransactionEntity createCardWithdrawalTransactionEntity(CardEntity card, BigDecimal amount) {
        CardTransactionEntityBuilder builder = CardTransactionEntity.builder()
                .amount(amount.negate())
                .beforeBalance(card.getBalance())
                .card(card)
                .createTime(LocalDateTime.now());
        if (card.getBalance().compareTo(amount) >= 0){
            builder.finalBalance(card.getBalance().subtract(amount));
            builder.success(true);
        } else {
            builder.success(false);
        }
        return builder.build();
    }


    public static CardTransactionEntity createCardReplenishmentTransactionEntity(CardEntity card, BigDecimal amount) {
        CardTransactionEntityBuilder builder = CardTransactionEntity.builder()
                .amount(amount)
                .beforeBalance(card.getBalance())
                .card(card)
                .createTime(LocalDateTime.now())
                .finalBalance(card.getBalance().add(amount))
                .success(true);
        return builder.build();
    }
}
