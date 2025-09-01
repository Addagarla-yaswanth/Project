package com.charter.rewards.serviceImpl;

import com.charter.rewards.dto.*;
import com.charter.rewards.entity.Customer;
import com.charter.rewards.entity.Transaction;
import com.charter.rewards.repository.CustomerRepository;
import com.charter.rewards.repository.TransactionRepository;
import com.charter.rewards.service.RewardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private Environment env;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public CustomerResponseDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = mapper.map(customerDTO, Customer.class);

        if (customer.getTransactions() != null) {
            customer.getTransactions().forEach(tx -> tx.setCustomer(customer));
        }
        customer.setPhoneNo(passwordEncoder.encode(customer.getPhoneNo()));
        Customer savedCustomer = customerRepository.save(customer);
        return mapper.map(savedCustomer, CustomerResponseDTO.class);
    }

    @Override
    public List<SummaryResponseDTO> getRewardSummary() {
        return customerRepository.findAll().stream()
                .map(customer -> {
                    List<Transaction> transactions = transactionRepository.findByCustomerId(customer.getId());
                    return buildResponse(customer, transactions, SummaryResponseDTO.class, null, null);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TransactionDTO> getCustomerTransactions(Long customerId) {
        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

        return transactions.stream().map(tx -> {
            TransactionDTO dto = mapper.map(tx, TransactionDTO.class);
            dto.setRewardPoints(calculatePoints(tx.getAmount()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public RewardResponseDTO getRewardsForCustomer(Long customerId, LocalDate startDate, LocalDate endDate) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("%s %d", env.getProperty("customer.notfound", "Customer not found:"), customerId)
                ));

        List<Transaction> transactions = transactionRepository
                .findByCustomerIdAndDateBetween(customerId, startDate, endDate);

        if (transactions.isEmpty()) {
            throw new RuntimeException(env.getProperty("transaction.notfound", "No transactions found"));
        }

        return buildResponse(customer, transactions, RewardResponseDTO.class, startDate, endDate);
    }

    @SuppressWarnings("unchecked")
    private <T> T buildResponse(Customer customer, List<Transaction> transactions,
                                Class<T> responseType,
                                LocalDate startDate, LocalDate endDate) {

        Map<String, Integer> monthlyRewards = new HashMap<>();
        int totalRewards = 0;
        List<TransactionDTO> txDtos = new ArrayList<>();

        for (Transaction tx : transactions) {
            int points = calculatePoints(tx.getAmount());
            totalRewards += points;

            String monthKey = YearMonth.from(tx.getDate()).toString();
            monthlyRewards.put(monthKey, monthlyRewards.getOrDefault(monthKey, 0) + points);

            TransactionDTO dto = mapper.map(tx, TransactionDTO.class);
            dto.setRewardPoints(points);
            txDtos.add(dto);
        }

        Object response;
        if (responseType.equals(SummaryResponseDTO.class)) {
            response = new SummaryResponseDTO();
        } else if (responseType.equals(RewardResponseDTO.class)) {
            response = new RewardResponseDTO();
        } else {
            throw new IllegalArgumentException("Unsupported response type: " + responseType.getName());
        }

        // Set common fields for both DTOs
        setCommonFields(response, customer, txDtos, monthlyRewards, totalRewards);

        // Only for RewardResponseDTO, set time frame if provided
        if (response instanceof RewardResponseDTO rewardResp && startDate != null && endDate != null) {
            Map<String, String> timeFrame = new HashMap<>();
            timeFrame.put("startDate", startDate.toString());
            timeFrame.put("endDate", endDate.toString());
            rewardResp.setTimeFrame(timeFrame);
        }

        return (T) response;
    }

    // Private helper to set common fields using reflection (works for both DTOs)
    private void setCommonFields(Object response, Customer customer, List<TransactionDTO> txDtos,
                                 Map<String, Integer> monthlyRewards, int totalRewards) {
        try {
            response.getClass().getMethod("setCustomerId", Long.class).invoke(response, customer.getId());
            response.getClass().getMethod("setCustName", String.class).invoke(response, customer.getCustName());
            response.getClass().getMethod("setPhoneNo", String.class).invoke(response, customer.getPhoneNo());
            response.getClass().getMethod("setTransactions", List.class).invoke(response, txDtos);
            response.getClass().getMethod("setMonthlyRewards", Map.class).invoke(response, monthlyRewards);
            response.getClass().getMethod("setTotalRewards", int.class).invoke(response, totalRewards);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set common fields on response DTO", e);
        }
    }

    // Reward calculation
    private int calculatePoints(double amount) {
        if (amount <= 50) return 0;
        if (amount <= 100) return (int) (amount - 50);
        return (int) ((amount - 100) * 2 + 50);
    }
}
