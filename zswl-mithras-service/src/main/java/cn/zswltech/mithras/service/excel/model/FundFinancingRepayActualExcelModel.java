package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/3/31
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundFinancingRepayActualExcelModel extends FundFinancingRepayEstimateExcelModel {
    /**
     * 现金流编号
     */
    @SimpleExcelHeader(headerName = "现金流编号", headerOrder = 0)
    private String cashFlowCode;
}
