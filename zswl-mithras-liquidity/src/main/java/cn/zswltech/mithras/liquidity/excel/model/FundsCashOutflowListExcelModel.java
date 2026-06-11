package cn.zswltech.mithras.liquidity.excel.model;

import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @create: 2023-05-16
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class FundsCashOutflowListExcelModel extends ExcelModel{
    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 10)
    private List<String> financingOrgs;

    @SimpleExcelHeader(headerName = "融资编码", headerOrder = 20)
    private String financingCode;

    @SimpleExcelHeader(headerName = "融资总额（万元）", headerOrder = 30)
    private String financingAmount;

    @SimpleExcelHeader(headerName = "现金流出时间", headerOrder = 40)
    private String cashOutflowTime;

    @SimpleExcelHeader(headerName = "本金（万元）", headerOrder = 50)
    private String principle;

    @SimpleExcelHeader(headerName = "利息（万元）", headerOrder = 60)
    private String interest;

    @SimpleExcelHeader(headerName = "预估流出现金流合计（万元）", headerOrder = 70)
    private String estimateCashOutflowAmount;
}
