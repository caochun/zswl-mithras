package cn.zswltech.mithras.projectprocess.application.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/12/13
 * @description
 */
@Data
@Accessors(chain = true)
public class CashFlowBO {
    private LocalDate standardCashFlowDate;
    private LocalDate cashFlowDate;
    private Integer cashFlowPhase;
    private Long cashFlowAmount;
    private Long rent;
    private Long principal;
    private Long interest;
    private Long remainingPrincipal;
}
