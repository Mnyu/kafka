package com.learner.bank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class BankTransaction {

  private Long id;
  private Long balanceId;
  private BigDecimal amount;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  public Date time;

  @Builder.Default
  public BankTransactionState state = BankTransactionState.CREATED;

  public static enum BankTransactionState {
    CREATED, APPROVED, REJECTED
  }
}
