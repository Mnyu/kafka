package com.learner.bank.model;

import com.learner.bank.model.BankTransaction.BankTransactionState;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankBalance {

  private Long id;
  private BigDecimal amount = BigDecimal.ZERO;
  private Date lastUpdate;
  private BankTransaction latestTransaction;

  public BankBalance process(BankTransaction bankTransaction) {
    this.id = bankTransaction.getBalanceId();
    this.latestTransaction = bankTransaction;
    BigDecimal newAmount = this.amount.add(bankTransaction.getAmount());
    if (newAmount.compareTo(BigDecimal.ZERO) >= 0) {
      this.latestTransaction.setState(BankTransactionState.APPROVED);
      this.amount = newAmount;
    } else {
      this.latestTransaction.setState(BankTransactionState.REJECTED);
    }
    this.lastUpdate = bankTransaction.getTime();
    return this;
  }
}