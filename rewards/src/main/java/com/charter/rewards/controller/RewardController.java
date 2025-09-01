package com.charter.rewards.controller;
import com.charter.rewards.dto.CustomerDTO;
import com.charter.rewards.dto.CustomerResponseDTO;
import com.charter.rewards.dto.RewardResponseDTO;
import com.charter.rewards.dto.SummaryResponseDTO;
import com.charter.rewards.dto.TransactionDTO;
import com.charter.rewards.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate; import java.util.List;
@RestController
@RequestMapping("/api/rewards")
public class RewardController {
@Autowired
private RewardService rewardService; //Create customer with transactions
@PostMapping("/customers")
public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerDTO customerDTO) {
CustomerResponseDTO response = rewardService.createCustomer(customerDTO);
return ResponseEntity.ok(response);
}
@GetMapping("/customers/{customerId}/transactions")
public ResponseEntity<List<TransactionDTO>> getCustomerTransactions(@PathVariable Long customerId) {
return ResponseEntity.ok(rewardService.getCustomerTransactions(customerId));
}
//Get reward summary for ALL customers (all transactions)
@GetMapping("/summary")
public ResponseEntity<List<SummaryResponseDTO>> getRewardSummary() {
return ResponseEntity.ok(rewardService.getRewardSummary());
}
//Get rewards for a given customer within timeframe
@GetMapping("/customers/{customerId}/rewards")
public ResponseEntity<RewardResponseDTO> getRewardsForCustomer( @PathVariable Long customerId, @RequestParam("startDate")@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
return ResponseEntity.ok( rewardService.getRewardsForCustomer(customerId, startDate, endDate) );
}
}
