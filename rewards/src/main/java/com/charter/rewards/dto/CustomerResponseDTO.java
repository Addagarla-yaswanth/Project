package com.charter.rewards.dto;

import java.util.List;
import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String custName;
    private String phoneNo;
    private List<TransactionResponseDTO> transactions;
    
}

