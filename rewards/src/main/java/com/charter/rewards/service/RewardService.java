package com.charter.rewards.service;

import com.charter.rewards.dto.CustomerDTO;
import com.charter.rewards.dto.CustomerResponseDTO;
import com.charter.rewards.dto.RewardResponseDTO;
import com.charter.rewards.dto.SummaryResponseDTO;
import com.charter.rewards.dto.TransactionDTO;

import java.time.LocalDate;
import java.util.List;

public interface RewardService {

    CustomerResponseDTO createCustomer(CustomerDTO customerDTO);
    List<SummaryResponseDTO> getRewardSummary();
    RewardResponseDTO getRewardsForCustomer(Long customerId, LocalDate startDate, LocalDate endDate);
	List<TransactionDTO> getCustomerTransactions(Long customerId);
}
