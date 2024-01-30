package com.bitee.fintechbank.controller.impl;

import com.bitee.fintechbank.controller.TransactionController;
import com.bitee.fintechbank.entity.Transaction;
import com.bitee.fintechbank.service.BankStatementService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
public class TransactionControllerImpl implements TransactionController {

    @Autowired
    private BankStatementService bankStatementService;
    @Override
    public List<Transaction> generateBankStatement(String accountNumber, String startDate, String endDate) throws DocumentException, FileNotFoundException {
        return bankStatementService.generateStatement(accountNumber,startDate,endDate);
    }


}
