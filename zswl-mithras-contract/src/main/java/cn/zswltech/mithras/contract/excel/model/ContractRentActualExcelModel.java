package cn.zswltech.mithras.contract.excel.model;

import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractRentActualExcelModel extends CashFlowExcelModel {
    private Long id;

    @SimpleExcelHeader(headerName = "现金流编号", headerOrder = 0)
    private String cashFlowCode;

    @SimpleExcelHeader(headerName = "是否已收款", headerOrder = 80)
    private String received;

    @SimpleExcelHeader(headerName = "收款时间", headerOrder = 90, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate receivedDate;

    @SimpleExcelHeader(headerName = "收款金额", headerOrder = 100)
    private String receivedAmount;
}
