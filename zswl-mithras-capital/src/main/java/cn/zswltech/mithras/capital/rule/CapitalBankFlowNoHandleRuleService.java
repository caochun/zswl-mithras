package cn.zswltech.mithras.capital.rule;

import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSnapshot;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CapitalBankFlowNoHandleRuleService {

    private static final String TAX_PENDING_KEYWORD = "待报解";
    private static final String[] INTERNAL_EXPENSE_KEYWORDS = {"报销", "工资", "奖金"};

    public List<Long> resolveNoHandleFlowIds(List<CapitalBankFlowSnapshot> flows,
                                             Collection<String> employeeNames,
                                             Collection<String> ownAccountNumbers) {
        Set<String> accountNumbers = normalizeAccountNumbers(ownAccountNumbers);
        return flows.stream()
                .filter(Objects::nonNull)
                .filter(flow -> shouldMoveToNoHandle(flow, employeeNames, accountNumbers))
                .map(CapitalBankFlowSnapshot::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean shouldMoveToNoHandle(CapitalBankFlowSnapshot flow,
                                         Collection<String> employeeNames,
                                         Set<String> ownAccountNumbers) {
        return containsAny(flow.getOppositeUnit(), employeeNames)
                || contains(flow.getOppositeUnit(), TAX_PENDING_KEYWORD)
                || containsAny(flow.getDescription(), INTERNAL_EXPENSE_KEYWORDS)
                || ownAccountNumbers.contains(normalizeAccountNumber(flow.getOppositeBankNumber()));
    }

    private boolean containsAny(String value, Collection<String> candidates) {
        if (isBlank(value) || candidates == null || candidates.isEmpty()) {
            return false;
        }
        return candidates.stream()
                .filter(candidate -> !isBlank(candidate))
                .anyMatch(value::contains);
    }

    private boolean containsAny(String value, String... candidates) {
        if (isBlank(value) || candidates == null || candidates.length == 0) {
            return false;
        }
        for (String candidate : candidates) {
            if (!isBlank(candidate) && value.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(String value, String candidate) {
        return !isBlank(value) && !isBlank(candidate) && value.contains(candidate);
    }

    private Set<String> normalizeAccountNumbers(Collection<String> accountNumbers) {
        if (accountNumbers == null || accountNumbers.isEmpty()) {
            return new HashSet<>();
        }
        return accountNumbers.stream()
                .map(this::normalizeAccountNumber)
                .filter(accountNumber -> !isBlank(accountNumber))
                .collect(Collectors.toSet());
    }

    private String normalizeAccountNumber(String accountNumber) {
        if (accountNumber == null) {
            return "";
        }
        return accountNumber.replace(" ", "");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
