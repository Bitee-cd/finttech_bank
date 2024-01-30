package com.bitee.fintechbank.controller;

import com.bitee.fintechbank.entity.Transaction;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.FileNotFoundException;
import java.util.List;

@RequestMapping("/api")
@Tag(name="Transaction Management APIs")
public interface TransactionController {

        @GetMapping("bankStatement")
        public List<Transaction> generateBankStatement(@RequestParam String accountNumber,
                                                       @RequestParam String startDate, @RequestParam String endDate) throws DocumentException, FileNotFoundException;

}
