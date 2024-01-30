package com.bitee.fintechbank.service.impl;

import com.bitee.fintechbank.dto.*;
import com.bitee.fintechbank.entity.User;
import com.bitee.fintechbank.enums.AccountStatus;
import com.bitee.fintechbank.enums.TransactionStatus;
import com.bitee.fintechbank.enums.TransactionType;
import com.bitee.fintechbank.enums.UserGender;
import com.bitee.fintechbank.repository.TransactionRepository;
import com.bitee.fintechbank.repository.UserRepository;
import com.bitee.fintechbank.service.EmailService;
import com.bitee.fintechbank.service.TransactionService;
import com.bitee.fintechbank.service.UserService;
import com.bitee.fintechbank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    TransactionService transactionService;
    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /**
         * Creating an account - saving a new user into the db
         * @param userRequest
         * check if user already has an account
         * @return BankResponse
         */

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_EXISTS_CODE).responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE).accountInfo(null).build();
        }
        User newUser = User.builder().firstName(userRequest.getFirstName()).lastName(userRequest.getLastName()).otherName(userRequest.getOtherName()).gender(UserGender.valueOf(userRequest.getGender())).address(userRequest.getAddress()).stateOfOrigin(userRequest.getStateOfOrigin()).accountNumber(AccountUtils.generateAccountNumber()).email(userRequest.getEmail()).accountBalance(BigDecimal.ZERO).phoneNumber(userRequest.getPhoneNumber()).alternativePhoneNumber(userRequest.getAlternativePhoneNumber()).status(AccountStatus.ACTIVE).build();

        User savedUser = userRepository.save(newUser);
        //Send email Alert
        EmailDetails emailDetails =
                EmailDetails.builder().recipient(savedUser.getEmail()).subject("ACCOUNT CREATION").messageBody(
                        "Congratulations! Your Account Has been Successfully Created.\n Your Account Details: \n" +
                                "Account Name: " + generateUsername(savedUser) + "\n" + "Account Number: " + savedUser.getAccountNumber() +
                                "\n").build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS).responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE).accountInfo(AccountInfo.builder().accountBalance(savedUser.getAccountBalance()).accountNumber(savedUser.getAccountNumber()).accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName()).build()).build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE).responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).accountInfo(null).build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_FOUND_CODE).responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE).accountInfo(AccountInfo.builder().accountBalance(foundUser.getAccountBalance()).accountNumber(foundUser.getAccountNumber()).accountName(generateUsername(foundUser)).build()).build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return generateUsername(foundUser);
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //check if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE).responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).accountInfo(null).build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);
        TransactionDto transactionDto = TransactionDto.builder()
                .amount(request.getAmount())
                .accountNumber(request.getAccountNumber())
                .transactionType(TransactionType.CREDIT)
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS).responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE).accountInfo(AccountInfo.builder().accountName(generateUsername(userToCredit)).accountNumber(userToCredit.getAccountNumber()).accountBalance(userToCredit.getAccountBalance()).build()).build();

    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder().responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE).responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).accountInfo(null).build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        //check if amount to withdraw is less than amount in account
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .accountInfo(
                            null
                    )
                    .build();
        } else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            TransactionDto transactionDto = TransactionDto.builder()
                    .amount(request.getAmount())
                    .accountNumber(request.getAccountNumber())
                    .transactionType(TransactionType.DEBIT)
                    .build();
            transactionService.saveTransaction(transactionDto);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(
                            AccountInfo.builder()
                                    .accountBalance(userToDebit.getAccountBalance())
                                    .accountNumber(userToDebit.getAccountNumber())
                                    .accountName(generateUsername(userToDebit))
                                    .build()
                    )
                    .build();
        }
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        // get the account to debit
        // check if the amount im debiting is not more than the current balance
        // debit the account
        // get the account to credit
        // credit the account
        boolean isSourceAccountExist = userRepository.existsByAccountNumber(request.getSourceAccount());
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.DEBIT_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.DEBIT_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if (!isSourceAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.SOURCE_ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.SOURCE_ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccount());
        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .build();
        }
        String recipientName = generateUsername(destinationAccountUser);
        String sourceName = generateUsername(sourceAccountUser);
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);
        TransactionDto debitTransactionDto = TransactionDto.builder()
                .amount(request.getAmount())
                .accountNumber(request.getSourceAccount())
                .transactionType(TransactionType.DEBIT)
                .build();
        transactionService.saveTransaction(debitTransactionDto);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of" + request.getAmount() + " has been debited from your account for a transfer" +
                        " to " + recipientName+
                        ". " +
                        " Your " +
                        "current" +
                        " balance is " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);


        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);
        TransactionDto creditTransactionDto = TransactionDto.builder()
                .amount(request.getAmount())
                .accountNumber(request.getDestinationAccountNumber())
                .transactionType(TransactionType.CREDIT)
                .build();
        transactionService.saveTransaction(creditTransactionDto);
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipient(destinationAccountUser.getEmail())
                .messageBody("The sum of" + request.getAmount() + " has been credited from your" + sourceName+ " .  " +
                        "Your " +
                        "current" +
                        " balance is " + destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);
        return BankResponse.builder()
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .accountInfo(null)
                .build();

    }

    public String generateUsername(User user) {
        return user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();
    }
}
