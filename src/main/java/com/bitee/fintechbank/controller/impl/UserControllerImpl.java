package com.bitee.fintechbank.controller.impl;

import com.bitee.fintechbank.controller.UserController;
import com.bitee.fintechbank.dto.*;
import com.bitee.fintechbank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return  userService.createAccount(userRequest);
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        return userService.balanceEnquiry(enquiryRequest);
    }

    @Override
    public String nameEnquiry(EnquiryRequest enquiryRequest) {
        return userService.nameEnquiry(enquiryRequest);
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        return userService.creditAccount(request);
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        return userService.debitAccount(request);
    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        return userService.transfer(request);
    }
}
