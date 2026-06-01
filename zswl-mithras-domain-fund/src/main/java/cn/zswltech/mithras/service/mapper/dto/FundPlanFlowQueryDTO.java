package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/5/31
 * @description
 */
@Data
public class FundPlanFlowQueryDTO {
    private List<String> writeOffStateList;
    private String planCashFlowDateFrom;
    private String planCashFlowDateTo;
    private String fundChannel;
    private String cashFlowItem;
    private String financingCode;
}
