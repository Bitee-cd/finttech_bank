package com.bitee.fintechbank.service;

import com.bitee.fintechbank.entity.Transaction;
import com.itextpdf.text.DocumentException;

import java.io.FileNotFoundException;
import java.util.List;

public interface BankStatementService {
    public List<Transaction> generateStatement(String accountNumber,String startDate, String endDate) throws DocumentException, FileNotFoundException;
}
