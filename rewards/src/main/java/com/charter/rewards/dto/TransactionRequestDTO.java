package com.charter.rewards.dto;

import java.time.LocalDate;
import lombok.Data;
@Data
public class TransactionRequestDTO {
	private Long customerId;
	private LocalDate date;
	private String product;
	private Double amount;
}