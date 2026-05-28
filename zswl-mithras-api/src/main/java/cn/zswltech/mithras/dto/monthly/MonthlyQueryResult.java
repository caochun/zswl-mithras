package cn.zswltech.mithras.dto.monthly;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MonthlyQueryResult {

    private Long id;
    private Long receiptId;
    private Long clientId;
    private String receiptCode;
    private Long contractId;
    private String projName;
    private String contractCode;
    private String clientName;
    private String leaseType;
    private Long incomeSum;
    private Long incomeWithoutTaxSum;
    private String bizType;
    private LocalDate incomeDate;
    private LocalDate actualLeaseDate;

}
