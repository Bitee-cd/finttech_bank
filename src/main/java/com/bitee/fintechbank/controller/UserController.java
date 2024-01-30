package com.bitee.fintechbank.controller;

import com.bitee.fintechbank.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/api/user")
@Tag(name="User Management APIs")
public interface UserController {

    @Operation(
            summary = "Create New Account",
            description = "Creating a new user and assigning an account to it"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequest userRequest);

    @Operation(
            summary = "Balance Enquiry",
            description = "Request Account Balance for a user"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("balance-enquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest);

    @GetMapping("name-enquiry")
    public  String nameEnquiry(@RequestBody EnquiryRequest enquiryRequest);

    @PostMapping("credit")
    public  BankResponse creditAccount(@RequestBody CreditDebitRequest request);
    @PostMapping("debit")
    public  BankResponse debitAccount(@RequestBody CreditDebitRequest request);

    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest request);
}
