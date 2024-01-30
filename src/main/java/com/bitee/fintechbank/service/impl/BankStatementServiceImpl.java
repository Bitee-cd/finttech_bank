package com.bitee.fintechbank.service.impl;

import com.bitee.fintechbank.dto.EmailDetails;
import com.bitee.fintechbank.entity.Transaction;
import com.bitee.fintechbank.entity.User;
import com.bitee.fintechbank.repository.TransactionRepository;
import com.bitee.fintechbank.repository.UserRepository;
import com.bitee.fintechbank.service.BankStatementService;
import com.bitee.fintechbank.service.EmailService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class BankStatementServiceImpl implements BankStatementService {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    public final String FILE ="/home/bit33/code/java/fintec_doc/MyStatement.pdf";
    /**
     * retrieve list of transactions within a date range an account number
     * generate a pdf file of transactions
     * send file via email
     */
    @Override
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException, FileNotFoundException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate,DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction ->  transaction.getCreatedAt().isAfter(start.minusDays(1)) &&
                        transaction.getCreatedAt().isBefore(end.plusDays(1)))
                .toList();
        User user =  userRepository.findByAccountNumber(accountNumber);
        designStatement(transactionList,startDate,endDate,user);

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached!")
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);
        return transactionList;

    }

    private void designStatement(List<Transaction> transactions,String start, String end,User user ) throws FileNotFoundException,
            DocumentException {
        //create a document
        Rectangle PageSize = new Rectangle(com.itextpdf.text.PageSize.A4);
        Document document = new Document(PageSize);

        //open document
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();


        PdfPTable bankInfoTable = new PdfPTable(1);

        //write bank Name
        Phrase title = new Phrase("Bitee Bank Limited");
        PdfPCell bankName = new PdfPCell(title);
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        //write address
        Phrase text= new Phrase("23 Ma'aji Street Tsauni Kura GRA Kaduna ");
        PdfPCell bankAddress = new PdfPCell(text);
        bankAddress.setBorder(0);

        //add name and address to table
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        //write customerInfo to document
        PdfPTable statementInfo = new PdfPTable(2);
        Phrase startDate = new Phrase("Start Date: "+start);
        PdfPCell customerInfo = new PdfPCell(startDate);
        customerInfo.setBorder(0);

        Phrase statementText =new Phrase("STATEMENT OF ACCOUNT");
        PdfPCell statement = new PdfPCell(statementText);
        statement.setBorder(0);

        Phrase endDate = new Phrase("End Date: "+end);
        PdfPCell stopDate = new PdfPCell(endDate);
        stopDate.setBorder(0);

        // add customer Name to Document
        String userName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
        Phrase customerNameText = new Phrase(userName);
        PdfPCell customerName = new PdfPCell(customerNameText);
        customerName.setBorder(0);

        //Add space
        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        //Add customer address
        Phrase customerAddressText = new Phrase("Customer Address: "+ user.getAddress());
        PdfPCell customerAddress = new PdfPCell(customerAddressText);
        customerAddress.setBorder(0);


        //Create transactions table headers
        PdfPTable transactionsTable = new PdfPTable(4);

        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("DATE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("DATE"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);

        PdfPCell status = new PdfPCell(new Phrase("DATE"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);


        transactions.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType().toString()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transaction.getStatus().toString()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(customerName);
        statementInfo.addCell(space);
        statementInfo.addCell(customerAddress);

        //add tables to document
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();
    }


}
