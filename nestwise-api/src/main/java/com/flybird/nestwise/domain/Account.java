package com.flybird.nestwise.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounts_id_gen")
    @SequenceGenerator(name = "accounts_id_gen", sequenceName = "accounts_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "bank_account_id", nullable = false)
    private String bankAccountId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "currency_code", updatable = false, insertable = false)
    private Integer currencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @Column(name = "credit_limit")
    private Long creditLimit;

    @Column(name = "iban", nullable = false)
    private String iban;

    @Column(name = "last_transaction_date")
    private Instant lastTransactionDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "user_id", updatable = false, insertable = false)
    private Long userId;

    @Column(name = "bank_id", updatable = false, insertable = false)
    private Long bankId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_id", referencedColumnName = "id", nullable = false)
    private Bank bank;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id")
    private List<Card> cards;

    public void addCards(List<Card> cards) {
        this.cards = cards;
        this.cards.forEach(c -> c.setAccount(this));
    }
}