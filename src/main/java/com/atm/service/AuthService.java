package com.atm.service;

import com.atm.entity.CardEntity;
import com.atm.exception.AuthenticationException;
import com.atm.model.CardAndPassModel;
import com.atm.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.atm.utils.CardUtils.generateCardNumber;
import static com.atm.utils.CardUtils.generateCardPass;
import static com.atm.utils.CardUtils.cardPassEncoder;

@Service
public class AuthService extends BaseCardService{


    public AuthService(CardRepository cardRepository) {
        super(cardRepository);
    }

    @Transactional
    public CardAndPassModel cardRegistration() {
        String cardPass = generateCardPass();
        CardEntity card = CardEntity.createCard(generateCardNumber(), cardPassEncoder(cardPass));
        cardRepository.save(card);
        return CardAndPassModel.builder()
                .pass(cardPass)
                .card(card.getCardNumber())
                .build();
    }

    public void cardLogin(CardAndPassModel model) throws AuthenticationException {
        getCardWithPass(model.getCard(), model.getPass());
    }

}
