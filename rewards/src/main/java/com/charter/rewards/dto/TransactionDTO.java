package com.charter.rewards.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class TransactionDTO {
	private LocalDate date;
	private Double amount;
	private String product;
	private int rewardPoints;

}
