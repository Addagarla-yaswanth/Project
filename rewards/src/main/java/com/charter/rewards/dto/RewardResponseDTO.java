package com.charter.rewards.dto;

import java.util.*;
import lombok.Data;
@Data
public class RewardResponseDTO {
	private Long customerId;
	private String custName;
	private String phoneNo;
	private Map<String, Integer> monthlyRewards;
	private Integer totalRewards;
	private List<TransactionDTO> transactions;
	private Map<String, String> timeFrame;
}
