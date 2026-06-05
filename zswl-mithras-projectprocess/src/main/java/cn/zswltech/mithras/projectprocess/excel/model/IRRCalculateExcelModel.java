package cn.zswltech.mithras.projectprocess.excel.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
@Data
public class IRRCalculateExcelModel {
    private List<CashFlowAdjustExcelModel> cashFlowAdjustExcelModelList;
    private BigDecimal irrPerPhase;
    private BigDecimal irr;

    @Data
    public static class CashFlowAdjustExcelModel {
        private LocalDate cashFlowDate;
        private LocalDate adjustCashFlowDate;
        private Integer cashFlowPhase;
        private BigDecimal adjustCashFlowPhase;
        private BigDecimal cashFlowAmount;
        private BigDecimal adjustCashFlowAmount;
    }
}
