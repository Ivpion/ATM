package com.atm.service;

import com.atm.entity.CardEntity;
import com.atm.entity.CardTransactionEntity;
import com.atm.exception.AuthenticationException;
import com.atm.exception.ValidationException;
import com.atm.model.CardTransactionHistoryModel;
import com.atm.model.PageData;
import com.atm.repository.CardHistoryRepository;
import com.atm.repository.CardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardService extends BaseCardService {

    private final CardHistoryRepository cardHistoryRepository;
    private final EntityManager manager;



    public CardService(CardRepository cardRepository,
                       CardHistoryRepository cardHistoryRepository,
                       EntityManager manager) {
        super(cardRepository);
        this.cardHistoryRepository = cardHistoryRepository;
        this.manager = manager;
    }

    public BigDecimal getBalance(String card, String passHash) throws AuthenticationException {
        return getCardWithPass(card, passHash).getBalance();
    }

    @Transactional
    public BigDecimal withdrawFromCard(String cardNumber, String passHash, BigDecimal amount) throws AuthenticationException, ValidationException {
        CardEntity card = getCardWithPass(cardNumber, passHash);
        CardTransactionEntity cardWithdrawalTransactionEntity = CardTransactionEntity.createCardWithdrawalTransactionEntity(card, amount);
        try {
            if (cardWithdrawalTransactionEntity.isSuccess()) {
                card.setBalance(card.getBalance().subtract(amount));
            } else {
                throw new ValidationException("Not enough money");
            }
            return amount;
        } finally {
            Optional.ofNullable(card.getTransactions()).orElseGet(() -> {
                card.setTransactions(new ArrayList<>());
                return card.getTransactions();
            }).add(cardWithdrawalTransactionEntity);
            manager.merge(card);
        }
    }

    @Transactional
    public BigDecimal replenishmentToCard(String cardNumber, BigDecimal amount) throws AuthenticationException {
        CardEntity card = getCard(cardNumber);
        CardTransactionEntity cardWithdrawalTransactionEntity = CardTransactionEntity.createCardReplenishmentTransactionEntity(card, amount);
        card.setBalance(card.getBalance().add(amount));
        Optional.ofNullable(card.getTransactions()).orElseGet(() -> {
            card.setTransactions(new ArrayList<>());
            return card.getTransactions();
        }).add(cardWithdrawalTransactionEntity);
        manager.merge(card);
        return cardWithdrawalTransactionEntity.getAmount();
    }

    public PageData<CardTransactionHistoryModel> getTransactionHistory(String cardNumber, String passHash, PageRequest pageRequest) throws AuthenticationException {
        CardEntity cardWithPass = getCardWithPass(cardNumber, passHash);
        Page<CardTransactionEntity> allByCard = cardHistoryRepository.findAllByCard(cardWithPass, pageRequest);
        List<CardTransactionHistoryModel> collect = allByCard.stream().map(ent ->
                CardTransactionHistoryModel.builder()
                        .amount(ent.getAmount())
                        .balanceBefore(ent.getBeforeBalance())
                        .finallyBalance(ent.getFinalBalance())
                        .success(ent.isSuccess())
                        .time(ent.getCreateTime())
                        .build()
        ).collect(Collectors.toList());
        return PageData.<CardTransactionHistoryModel>builder()
                .content(collect)
                .metadata(PageData.PageMetadata.builder()
                        .number(allByCard.getNumber() + 1)
                        .size(allByCard.getSize())
                        .totalElements(allByCard.getTotalElements())
                        .totalPages(allByCard.getTotalPages())
                        .build())
                .build();
    }


}
