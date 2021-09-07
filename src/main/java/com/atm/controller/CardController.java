package com.atm.controller;

import com.atm.exception.AuthenticationException;
import com.atm.exception.ValidationException;
import com.atm.model.CardTransactionHistoryModel;
import com.atm.model.CardModel;
import com.atm.model.PageData;
import com.atm.service.CardService;
import com.atm.service.ValidationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RestController
@RequestMapping("/card")
public class CardController extends BaseController {

    private final CardService cardService;

    protected CardController(ValidationService validator, CardService cardService) {
        super(validator);
        this.cardService = cardService;
    }

    @GetMapping(path = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> getCardBalance(@RequestParam("passHash") String passHash,
                                                     HttpServletRequest servletRequest) throws AuthenticationException {
        return ResponseEntity.ok(cardService.getBalance(getCard(servletRequest), passHash));
    }


    @PutMapping(path = "/withdrawal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> withdrawalFromCard(@RequestParam("passHash") String passHash,
                                                         @RequestParam("amount") BigDecimal amount,
                                                         HttpServletRequest servletRequest) throws AuthenticationException, ValidationException {
        validator.validateAmountData(amount);
        return ResponseEntity.ok(cardService.withdrawFromCard(getCard(servletRequest), passHash, amount.setScale(2,RoundingMode.DOWN)));
    }

    @PutMapping(path = "/replenishment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> replenishmentToCard(@RequestBody CardModel cardModel,
                                                          HttpServletRequest servletRequest,
                                                          @RequestParam("amount") BigDecimal amount) throws AuthenticationException, ValidationException {
        String card = Optional.ofNullable(cardModel.getCard()).orElseGet(() -> getCard(servletRequest));
        validator.validateAmountData(amount);
        validator.validateAuthRequest(card);
        return ResponseEntity.ok(cardService.replenishmentToCard(card, amount.setScale(2, RoundingMode.DOWN)));
    }

    @GetMapping(path = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PageData<CardTransactionHistoryModel>> getCardHistory(@RequestParam("passHash") String passHash,
                                                                                HttpServletRequest servletRequest,
                                                                                @RequestParam("page") int page,
                                                                                @RequestParam("size") int size) throws AuthenticationException {

        return ResponseEntity.ok(cardService.getTransactionHistory(getCard(servletRequest), passHash, PageRequest.of(page -1 , size)));
    }

    private String getCard(HttpServletRequest servletRequest) {
        return servletRequest.getHeader("Card");
    }
}
