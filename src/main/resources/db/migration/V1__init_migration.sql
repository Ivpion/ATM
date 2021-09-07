create table atm_card
(
    id                INT               NOT NULL AUTO_INCREMENT PRIMARY KEY,
    create_time       TIMESTAMP         NOT NULL,
    card_number       VARCHAR(19)       NOT NULL UNIQUE ,
    pass_hash         VARCHAR(200)      NOT NULL,
    balance           DECIMAL(14, 2)    NOT null,
    iso_currency_code INT               NOT NULL
) DEFAULT CHARSET = utf8mb4;

create table atm_card_history
(
    id                INT               NOT NULL AUTO_INCREMENT PRIMARY KEY,
    card_id           INT               NOT NULL,
    create_time       TIMESTAMP         NOT NULL,
    balance_before    DECIMAL(14, 2)    NOT NULL,
    amount            DECIMAL(14, 2)    NOT NULL,
    final_balance     DECIMAL(14, 2)   ,
    success           boolean           NOT NULL ,

    CONSTRAINT atm_card_history_card_fk
    FOREIGN KEY (card_id)
      REFERENCES atm_card (id)
      ON DELETE CASCADE ON UPDATE CASCADE
) DEFAULT CHARSET = utf8mb4;

