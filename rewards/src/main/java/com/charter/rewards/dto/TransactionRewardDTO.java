package com.charter.rewards.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class TransactionRewardDTO {
    private Long transactionId;
    private LocalDate date;
    private String product;
    private Double amount;
    private Integer rewardPoints;
}
