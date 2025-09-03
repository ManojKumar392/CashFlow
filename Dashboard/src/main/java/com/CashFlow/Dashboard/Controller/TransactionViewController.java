package com.CashFlow.Dashboard.Controller;

import com.CashFlow.Dashboard.Entities.Transaction;
import com.CashFlow.Dashboard.Entities.User;
import com.CashFlow.Dashboard.Repository.TransactionRepository;
import com.CashFlow.Dashboard.Services.TransactionService;
import com.CashFlow.Dashboard.Services.UserService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/transactions")
public class TransactionViewController {

    private static final Logger log = LoggerFactory.getLogger(TransactionApiController.class);
    private final TransactionService transactionService;
    

    public TransactionViewController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ======== Page Endpoints (Thymeleaf) ========

    @GetMapping("/view")
    public String viewTransactions(Model model) {
        model.addAttribute("transactions", transactionService.getAllTransactions());
        return "transactions"; // transactions.html
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        return "edit-transaction"; // edit-transaction.html
    }

    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions/view";
    }

    // ======== JSON Endpoints (Add + Update) ========

    @RestController
    @RequestMapping("/transactions")
    public static class TransactionApiController {

        private static final Logger log = LoggerFactory.getLogger(TransactionApiController.class);
        private final TransactionRepository transactionRepository;
        private final TransactionService transactionService;
        private final UserService userService;

        @Autowired
        public TransactionApiController(TransactionRepository transactionRepository, TransactionService transactionService, UserService userService) {
            this.transactionRepository = transactionRepository;
            this.transactionService = transactionService;
            this.userService = userService;
        }

        @PostMapping("/add")
        public ResponseEntity<?> addTransaction(@RequestBody Transaction transaction, Principal principal) {
            User currentUser = userService.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("User not found")); // unwrap Optional safely
            transaction.setUser(currentUser); // attach logged-in user
            Transaction saved = transactionService.addTransaction(transaction);
            return ResponseEntity.ok(saved);
        }

        // ---- Inline Update (JSON) ----
        @PutMapping("/api/update/{id}")
        public ResponseEntity<?> updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction updated) {
            try {
                log.info("Updating transaction id={} with data: {}", id, updated);

                return transactionRepository.findById(id)
                        .map(existing -> {
                            existing.setAmount(updated.getAmount());
                            existing.setType(updated.getType());
                            existing.setCategory(updated.getCategory());
                            existing.setDescription(updated.getDescription());
                            existing.setDate(updated.getDate());

                            Transaction saved = transactionRepository.save(existing);
                            log.info("Updated transaction id={}", saved.getId());
                            return ResponseEntity.ok(saved);
                        })
                        .orElseGet(() -> {
                            log.warn("Transaction with id={} not found for update", id);
                            return ResponseEntity.notFound().build();
                        });

            } catch (Exception e) {
                log.error("Unexpected error while updating transaction id=" + id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Unexpected error: " + e.getMessage());
            }
        }
    }
}

// ======== Reports (Page + API) ========

@Controller
@RequestMapping("/reports")
class ReportController {
    @GetMapping
    public String reports() {
        return "reports"; // reports.html
    }
}

@RestController
@RequestMapping("/api/reports")
class ReportApiController {

    private final TransactionRepository repo;

    @Autowired
    public ReportApiController(TransactionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/monthly-trends")
    public List<Map<String, Object>> getMonthlyTrends() {
        return repo.getMonthlyTrends().stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("income", row[1]);
            map.put("expense", row[2]);
            return map;
        }).toList();
    }

    @GetMapping("/category-breakdown")
    public List<Map<String, Object>> getCategoryBreakdown() {
        return repo.getExpenseCategoryBreakdown().stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("total", row[1]);
            return map;
        }).toList();
    }

    @GetMapping("/cashflow")
    public List<Map<String, Object>> getCashFlow() {
        return repo.getCashFlow().stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", row[0]);
            map.put("income", row[1]);
            map.put("expense", row[2]);
            map.put("net", ((Number) row[1]).doubleValue() - ((Number) row[2]).doubleValue());
            return map;
        }).toList();
    }
}
