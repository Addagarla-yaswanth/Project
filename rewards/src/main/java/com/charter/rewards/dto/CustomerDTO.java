package com.charter.rewards.dto;

import java.util.*;

import lombok.Data;
@Data
public class CustomerDTO {
    private String custName;
    private String phoneNo;
    private List<TransactionDTO> transactions;
}