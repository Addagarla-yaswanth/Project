package com.charter.rewards.serviceImpl;

import com.charter.rewards.dto.*;
import com.charter.rewards.entity.Customer;
import com.charter.rewards.entity.Transaction;
import com.charter.rewards.repository.CustomerRepository;
import com.charter.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class RewardServiceImplTest {

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private Environment env;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private Customer customer;
    private Transaction tx1, tx2;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setCustName("John Doe");
        customer.setPhoneNo("1234567890");

        // Transaction entities
        tx1 = new Transaction();
        tx1.setAmount(40.0); // below 50 → 0 points
        tx1.setDate(LocalDate.of(2025, 8, 1));
        tx1.setProduct("Product A");
        tx1.setCustomer(customer);

        tx2 = new Transaction();
        tx2.setAmount(120.0); // above 100 → 90 points
        tx2.setDate(LocalDate.of(2025, 8, 10));
        tx2.setProduct("Product B");
        tx2.setCustomer(customer);

        // TransactionDTOs for CustomerDTO
        TransactionDTO txDto1 = new TransactionDTO();
        txDto1.setAmount(40.0);
        txDto1.setDate(LocalDate.of(2025, 8, 1));
        txDto1.setProduct("Product A");

        TransactionDTO txDto2 = new TransactionDTO();
        txDto2.setAmount(120.0);
        txDto2.setDate(LocalDate.of(2025, 8, 10));
        txDto2.setProduct("Product B");

        customerDTO = new CustomerDTO();
        customerDTO.setCustName("John Doe");
        customerDTO.setPhoneNo("1234567890");
        customerDTO.setTransactions(Arrays.asList(txDto1, txDto2));

        // Always return a new DTO to avoid NPE in buildResponse()
        when(mapper.map(any(Transaction.class), eq(TransactionDTO.class))).thenAnswer(i -> {
            Transaction t = i.getArgument(0);
            TransactionDTO dto = new TransactionDTO();
            dto.setAmount(t.getAmount());
            dto.setDate(t.getDate());
            dto.setProduct(t.getProduct());
            return dto;
        });
    }

    // ---------------- createCustomer Tests ----------------
    @Test
    void testCreateCustomerSuccess() {
        when(mapper.map(customerDTO, Customer.class)).thenReturn(customer);
        when(customerRepository.save(customer)).thenReturn(customer);
        when(mapper.map(customer, CustomerResponseDTO.class)).thenReturn(new CustomerResponseDTO());

        CustomerResponseDTO response = rewardService.createCustomer(customerDTO);
        assertNotNull(response);
        verify(customerRepository, times(1)).save(customer);
    }

    // ---------------- getRewardSummary Tests ----------------
    @Test
    void testGetRewardSummaryWithTransactions() {
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));
        when(transactionRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(tx1, tx2));
        when(mapper.map(any(), eq(SummaryResponseDTO.class))).thenCallRealMethod();

        List<SummaryResponseDTO> result = rewardService.getRewardSummary();
        assertEquals(1, result.size());
        SummaryResponseDTO summary = result.get(0);
        assertEquals(90, summary.getTotalRewards());
        assertEquals(2, summary.getTransactions().size());
        assertTrue(summary.getMonthlyRewards().containsKey("2025-08"));
    }

    @Test
    void testGetRewardSummaryEmptyCustomerList() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        List<SummaryResponseDTO> summary = rewardService.getRewardSummary();
        assertTrue(summary.isEmpty());
    }

    // ---------------- getCustomerTransactions Tests ----------------
    @Test
    void testGetCustomerTransactionsSuccess() {
        when(transactionRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(tx1, tx2));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(2, txList.size());
        assertEquals(0, txList.get(0).getRewardPoints());
        assertEquals(90, txList.get(1).getRewardPoints());
    }

    @Test
    void testGetCustomerTransactionsEmpty() {
        when(transactionRepository.findByCustomerId(1L)).thenReturn(Collections.emptyList());
        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertTrue(txList.isEmpty());
    }

    // ---------------- getRewardsForCustomer Tests ----------------
    @Test
    void testGetRewardsForCustomerSuccess() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
                .thenReturn(Arrays.asList(tx1, tx2));

        RewardResponseDTO response = rewardService.getRewardsForCustomer(
                1L, LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 31)
        );

        assertEquals(90, response.getTotalRewards());
        assertEquals(2, response.getTransactions().size());
        assertTrue(response.getMonthlyRewards().containsKey("2025-08"));
        assertEquals("2025-08-01", response.getTransactions().get(0).getDate().toString().substring(0, 10));
    }

    @Test
    void testGetRewardsForCustomerNoTransactions() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateBetween(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());
        when(env.getProperty("transaction.notfound", "No transactions found")).thenReturn("No transactions found");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                rewardService.getRewardsForCustomer(1L, LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 31))
        );
        assertEquals("No transactions found", ex.getMessage());
    }

    @Test
    void testGetRewardsForCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Mock environment to return a string WITHOUT the ID
        when(env.getProperty("customer.notfound", "Customer not found:"))
                .thenReturn("Customer not found");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                rewardService.getRewardsForCustomer(1L, LocalDate.now(), LocalDate.now())
        );

        // Service will append the customerId dynamically
        assertEquals("Customer not found 1", ex.getMessage());
    }


    // ---------------- Reward Points Edge Cases ----------------
    @Test
    void testRewardPointsBelow50() {
        Transaction txLow = new Transaction();
        txLow.setAmount(20.0);
        txLow.setDate(LocalDate.now());
        txLow.setProduct("Low Product");
        txLow.setCustomer(customer);

        when(transactionRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(txLow));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(0, txList.get(0).getRewardPoints());
    }

    @Test
    void testRewardPointsBetween50And100() {
        Transaction txMid = new Transaction();
        txMid.setAmount(75.0);
        txMid.setDate(LocalDate.now());
        txMid.setProduct("Mid Product");
        txMid.setCustomer(customer);

        when(transactionRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(txMid));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(25, txList.get(0).getRewardPoints());
    }

    @Test
    void testRewardPointsAbove100() {
        Transaction txHigh = new Transaction();
        txHigh.setAmount(150.0);
        txHigh.setDate(LocalDate.now());
        txHigh.setProduct("High Product");
        txHigh.setCustomer(customer);

        when(transactionRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(txHigh));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(150, txList.get(0).getRewardPoints()); // (150-100)*2 +50
    }

    @Test
    void testRewardPointsExactly50And100() {
        Transaction tx50 = new Transaction();
        tx50.setAmount(50.0);
        tx50.setDate(LocalDate.now());
        tx50.setProduct("Fifty");
        tx50.setCustomer(customer);

        Transaction tx100 = new Transaction();
        tx100.setAmount(100.0);
        tx100.setDate(LocalDate.now());
        tx100.setProduct("Hundred");
        tx100.setCustomer(customer);

        when(transactionRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(tx50, tx100));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(0, txList.get(0).getRewardPoints());   // 50 → 0
        assertEquals(50, txList.get(1).getRewardPoints());  // 100 → 50
    }

    @Test
    void testRewardPointsZeroAndNegative() {
        Transaction txZero = new Transaction();
        txZero.setAmount(0.0);
        txZero.setDate(LocalDate.now());
        txZero.setProduct("Zero");
        txZero.setCustomer(customer);

        Transaction txNegative = new Transaction();
        txNegative.setAmount(-20.0);
        txNegative.setDate(LocalDate.now());
        txNegative.setProduct("Negative");
        txNegative.setCustomer(customer);

        when(transactionRepository.findByCustomerId(1L)).thenReturn(Arrays.asList(txZero, txNegative));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(0, txList.get(0).getRewardPoints());
        assertEquals(0, txList.get(1).getRewardPoints());
    }

    @Test
    void testRewardPointsLargeAmount() {
        Transaction txLarge = new Transaction();
        txLarge.setAmount(1000.0);
        txLarge.setDate(LocalDate.now());
        txLarge.setProduct("Large");
        txLarge.setCustomer(customer);

        when(transactionRepository.findByCustomerId(1L)).thenReturn(Collections.singletonList(txLarge));

        List<TransactionDTO> txList = rewardService.getCustomerTransactions(1L);
        assertEquals(1850, txList.get(0).getRewardPoints());
    }
}
