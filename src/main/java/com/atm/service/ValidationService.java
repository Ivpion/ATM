package com.atm.service;

import com.atm.exception.ValidationException;
import com.atm.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private final static Pattern cardPattern = Pattern.compile("^[0-9]{4} ?[0-9]{4} ? [0-9]{4} ?[0-9]{4}$");
    private final static BigDecimal MAX_REPLENISHMENT_VALUE = BigDecimal.valueOf(5000);
    private final static BigDecimal MIN_REPLENISHMENT_VALUE = BigDecimal.ZERO;

    public void validateAuthRequest(String card) throws ValidationException {
        if (!cardPattern.matcher(card).matches()){
            throw new ValidationException("Invalid data");
        }
    }

    public void validateAmountData(BigDecimal bigDecimal) throws ValidationException {

        if (bigDecimal.compareTo(MIN_REPLENISHMENT_VALUE) <= 0 || bigDecimal.compareTo(MAX_REPLENISHMENT_VALUE) > 0){
            throw new ValidationException("Invalid data");
        }
    }
}
