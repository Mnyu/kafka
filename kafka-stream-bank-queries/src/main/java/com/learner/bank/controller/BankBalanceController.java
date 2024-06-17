package com.learner.bank.controller;

import com.learner.bank.model.BankBalance;
import com.learner.bank.service.BankBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bank-balance")
@RequiredArgsConstructor
public class BankBalanceController {

  private final BankBalanceService bankBalanceService;

  @GetMapping(value = "/{bankBalanceId}", produces = "application/json")
  public ResponseEntity<BankBalance> getBankBalance(@PathVariable Long bankBalanceId) {
    return ResponseEntity.ok(bankBalanceService.getBankBalanceById(bankBalanceId));
  }

}
