package com.indivaragroup.jdt17.spring.rest.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indivaragroup.jdt17.spring.rest.api.entity.Account;
import com.indivaragroup.jdt17.spring.rest.api.entity.Customer;
import com.indivaragroup.jdt17.spring.rest.api.models.request.*;
import com.indivaragroup.jdt17.spring.rest.api.repositories.AccountRepository;
import com.indivaragroup.jdt17.spring.rest.api.repositories.CustomerRepository;
import com.indivaragroup.jdt17.spring.rest.api.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EndpointIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
    }

    // ==========================================
    // CUSTOMER ENDPOINTS TESTS
    // ==========================================

    @Test
    void testRegisterCustomer_Positive() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("08123456789");
        request.setAddress("Jakarta");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // code is in body, status is 200 OK
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.data.name", is("John Doe")))
                .andExpect(jsonPath("$.data.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.data.id", notNullValue()));
    }

    @Test
    void testRegisterCustomer_Negative_ValidationFailed() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName(""); // Invalid name
        request.setEmail("invalid-email"); // Invalid email
        request.setPhone("");
        request.setAddress("Jakarta");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.data", containsString("must be a well-formed email address")));
    }

    @Test
    void testGetCustomerById_Positive() throws Exception {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPhone("08111222333");
        customer.setAddress("Bandung");
        Customer saved = customerRepository.save(customer);

        mockMvc.perform(get("/api/customers/" + saved.getCustomerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.data.name", is("Jane Doe")));
    }

    @Test
    void testGetCustomerById_Negative_NotFound() throws Exception {
        mockMvc.perform(get("/api/customers/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.data", is("Customer tidak ditemukan")));
    }

    // ==========================================
    // ACCOUNT ENDPOINTS TESTS
    // ==========================================

    @Test
    void testCreateAccount_Positive() throws Exception {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPhone("08111222333");
        customer.setAddress("Bandung");
        Customer saved = customerRepository.save(customer);

        AccountRequest request = new AccountRequest(saved.getCustomerId());

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(201)))
                .andExpect(jsonPath("$.status", is("CREATED")))
                .andExpect(jsonPath("$.data.accountNumber", startsWith("100")))
                .andExpect(jsonPath("$.data.customer.id", is(saved.getCustomerId())));
    }

    @Test
    void testCreateAccount_Negative_CustomerNotFound() throws Exception {
        AccountRequest request = new AccountRequest(UUID.randomUUID().toString());

        mockMvc.perform(post("/api/accounts/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.data", is("Customer not found")));
    }

    @Test
    void testGetAccountByAccountNumber_Positive() throws Exception {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPhone("08111222333");
        customer.setAddress("Bandung");
        Customer savedCustomer = customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber("10012345678");
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(savedCustomer);
        accountRepository.save(account);

        mockMvc.perform(get("/api/accounts/account-number/10012345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.data.accountNumber", is("10012345678")));
    }

    // ==========================================
    // TRANSACTION ENDPOINTS TESTS
    // ==========================================

    @Test
    void testTopUp_Positive() throws Exception {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPhone("08111222333");
        customer.setAddress("Bandung");
        Customer savedCustomer = customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber("10012345678");
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(savedCustomer);
        Account savedAccount = accountRepository.save(account);

        TopUpRequest request = new TopUpRequest(savedAccount.getAccountId(), new BigDecimal("50000.00"));

        mockMvc.perform(post("/api/transactions/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.transactionType", is("TOP_UP")))
                .andExpect(jsonPath("$.data.amount", is(50000.00)))
                .andExpect(jsonPath("$.data.balanceBefore", is(0)))
                .andExpect(jsonPath("$.data.balanceAfter", is(50000.00)));
    }

    @Test
    void testTopUp_Negative_BelowMinimum() throws Exception {
        TopUpRequest request = new TopUpRequest("some-id", new BigDecimal("5000.00")); // Below 10k

        mockMvc.perform(post("/api/transactions/topup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.data", containsString("Minimum Top Up is 10.000")));
    }

    @Test
    void testWithdraw_Positive() throws Exception {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPhone("08111222333");
        customer.setAddress("Bandung");
        Customer savedCustomer = customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber("10012345678");
        account.setBalance(new BigDecimal("100000.00")); // Setup starting balance
        account.setCustomer(savedCustomer);
        Account savedAccount = accountRepository.save(account);

        WithDrawRequest request = new WithDrawRequest(savedAccount.getAccountId(), new BigDecimal("60000.00"));

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.transactionType", is("WITHDRAW")))
                .andExpect(jsonPath("$.data.amount", is(60000.00)))
                .andExpect(jsonPath("$.data.balanceBefore", is(100000.00)))
                .andExpect(jsonPath("$.data.balanceAfter", is(40000.00)));
    }

    @Test
    void testWithdraw_Negative_InsufficientBalance() throws Exception {
        Customer customer = new Customer();
        customer.setName("Jane Doe");
        customer.setEmail("jane.doe@example.com");
        customer.setPhone("08111222333");
        customer.setAddress("Bandung");
        Customer savedCustomer = customerRepository.save(customer);

        Account account = new Account();
        account.setAccountNumber("10012345678");
        account.setBalance(new BigDecimal("30000.00"));
        account.setCustomer(savedCustomer);
        Account savedAccount = accountRepository.save(account);

        WithDrawRequest request = new WithDrawRequest(savedAccount.getAccountId(), new BigDecimal("60000.00")); // Insufficient

        mockMvc.perform(post("/api/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.data", is("Insufficient balance")));
    }

    @Test
    void testTransfer_Positive() throws Exception {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setEmail("john@example.com");
        customer.setPhone("08111");
        customer.setAddress("Jakarta");
        Customer c1 = customerRepository.save(customer);

        Account a1 = new Account();
        a1.setAccountNumber("10011111");
        a1.setBalance(new BigDecimal("100000.00"));
        a1.setCustomer(c1);
        Account source = accountRepository.save(a1);

        Account a2 = new Account();
        a2.setAccountNumber("10022222");
        a2.setBalance(BigDecimal.ZERO);
        a2.setCustomer(c1);
        Account destination = accountRepository.save(a2);

        TransferRequest request = new TransferRequest(source.getAccountId(), destination.getAccountId(), new BigDecimal("20000.00"));

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.data", is("Transfer berhasil dilakukan")));
    }

    @Test
    void testTransfer_Negative_SameAccount() throws Exception {
        TransferRequest request = new TransferRequest("acc1", "acc1", new BigDecimal("20000.00"));

        mockMvc.perform(post("/api/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(400)))
                .andExpect(jsonPath("$.data", is("Source and destination account cannot be the same")));
    }
}
