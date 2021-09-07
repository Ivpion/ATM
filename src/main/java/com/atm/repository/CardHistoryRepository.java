package com.atm.repository;

import com.atm.entity.CardEntity;
import com.atm.entity.CardTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CardHistoryRepository extends JpaRepository<CardTransactionEntity, Integer> {


    Page<CardTransactionEntity> findAllByCard(CardEntity card, Pageable pageable);
}
