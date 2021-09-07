package com.atm.service;

import com.atm.entity.CardEntity;
import com.atm.exception.AuthenticationException;
import com.atm.model.CardTransactionHistoryModel;
import com.atm.model.PageData;
import com.atm.repository.CardRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public abstract class BaseCardService {

    protected final CardRepository cardRepository;


    protected CardEntity getCardWithPass(String card, String passHash) throws AuthenticationException {
        CardEntity res = Optional.ofNullable(cardRepository.findByCardNumber(card))
                .orElseThrow(() -> new AuthenticationException("Invalid card data"));
        if (!passHash.equals(res.getPassHash())){
            throw new AuthenticationException("Invalid card data");
        } else {
            return res;
        }
    }

    protected CardEntity getCard(String card) throws AuthenticationException {
        return Optional.ofNullable(cardRepository.findByCardNumber(card))
                .orElseThrow(() -> new AuthenticationException("Invalid card data"));
    }


}
