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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "atm_card")
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "pass_hash")
    private String passHash;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "iso_currency_code")
    private int isoCurrencyCode;

    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CardTransactionEntity> transactions;


    public static CardEntity createCard(String card, String passHash){
        return CardEntity.builder()
                .balance(BigDecimal.ZERO)
                .cardNumber(card)
                .createTime(LocalDateTime.now())
                //default for uah, in future can be as variable properties
                .isoCurrencyCode(4217)
                .passHash(passHash)
                .transactions(new ArrayList<>())
                .build();
    }

}
