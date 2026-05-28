package cn.zswltech.mithras.dto.liquidityrisk;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractLastDate {
    private Long contractId;
    private LocalDate cashFlowDate;
}
