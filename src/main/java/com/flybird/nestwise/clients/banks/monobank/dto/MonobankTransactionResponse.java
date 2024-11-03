package com.flybird.nestwise.clients.banks.monobank.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class MonobankTransactionResponse {
    /** Unique transaction ID */
    private String id;
    
    /** Transaction time in seconds in Unix time format */
    private long time;
    
    /** Transaction description */
    private String description;
    
    /** Merchant Category Code according to ISO 18245 */
    private int mcc;
    
    /** Original Merchant Category Code according to ISO 18245 */
    private int originalMcc;
    
    /** Status of the amount hold (more details in wiki) */
    private boolean hold;
    
    /** Amount in account currency in the smallest units (cents, pennies) */
    private long amount;
    
    /** Amount in transaction currency in the smallest units (cents, pennies) */
    private long operationAmount;
    
    /** Account currency code according to ISO 4217 */
    private int currencyCode;
    
    /** Commission amount in the smallest units (cents, pennies) */
    private long commissionRate;
    
    /** Cashback amount in the smallest units (cents, pennies) */
    private long cashbackAmount;
    
    /** Account balance in the smallest units (cents, pennies) */
    private long balance;
    
    /** User-entered comment for the transfer. If not specified, the field will be absent */
    private String comment;
    
    /** Receipt number for check.gov.ua. The field may be absent */
    private String receiptId;
    
    /** Invoice number for individual entrepreneur, appears if it is a credit operation */
    private String invoiceId;
    
    /** Counterparty's EDRPOU, present only for individual entrepreneur account statements */
    private String counterEdrpou;
    
    /** Counterparty's IBAN, present only for individual entrepreneur account statements */
    private String counterIban;
    
    /** Counterparty's name */
    private String counterName;
}
