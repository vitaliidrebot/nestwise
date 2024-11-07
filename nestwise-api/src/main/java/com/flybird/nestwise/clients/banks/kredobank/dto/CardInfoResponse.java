package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardInfoResponse {
    private List<Contract> contracts;

    @Data
    public static class Contract {
        private String id;
        private String providerId;
        private String legalNumber;
        private String subproductCode;
        private String mainAccountNumber;
        private String mainAccountCurrency;
        private Long balance;
        private String productTitle;
        private String productSystemKey;
        private String iban;
        private double currentInterestRate;
        private ShowAndOperationRule showAndOperationRule;
        private String accountStateCodeName;
        private double usedCreditLimit;
        private String lastTransactionValue;
        private boolean isBtnMoneyTransferFromPolandToUkrainCashlessFromPolandAllowed;
        private boolean isBtnMoneyTransferFromPolandToUkrainAllowed;
        private boolean isBtnMoneyTransferFromPolandToUkrainCashlessSepaAllowed;
        private String blockedAmounValue;
        private boolean isDebitBlocked;
        private List<Card> cardsList;
        private String rateId;
        private Boolean isOwnAccount;
        private Boolean isBtnMoneyTransferFromPolandToUkrainCashPkoBpAllowed;
        private Object savingAccountNumber;
        private Object savingAccountBalance;
        private String lastTransactionDate;
        private Long creditLimit;
        private Boolean isBtnMoneyTransferFromPolandToUkrainPocztaPolskaAllowed;
        private Boolean isCardEmissionAllowed;
        private Boolean isActiveProduct;

        @Data
        public static class ShowAndOperationRule {
            private boolean mainScreenShowAllowed;
            private boolean redirectToParentObjectAllowed;
            private boolean debitAllowed;
            private List<String> debitAllowedAtOperationsList;
            private boolean creditAllowed;
            private List<String> creditAllowedAtOperationsList;
        }

        @Data
        public static class Card {
            private String id;
            private String cardNumberMask;
            private String expiryMonth;
            private String expiryYear;
            private String ownerName;
            private String status;
            private String statusName;
            private String bankCardType;
            private List<Attribute> attributes;
            private Object limits;
            private boolean cleareFromResponseToUI;
            private Object settings;
            private ShowAndOperationRule showAndOperationRule;

            @Data
            public static class Attribute {
                private String key;
                private String value;
            }
        }
    }
}