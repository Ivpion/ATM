package com.atm.repository;

import com.atm.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Integer> {

    CardEntity findByCardNumber(String cardNumber);
}
