package cn.zswltech.mithras.liquidity.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @create: 2023-05-16
 **/

@EqualsAndHashCode(callSuper = true)
@Data
public class AssetsCashOutflowListExcelModel extends ExcelModel{
    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 10)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 20)
    private String contractCode;

    @SimpleExcelHeader(headerName = "合同总金额（万元）", headerOrder = 30)
    private String contractAmount;

    @SimpleExcelHeader(headerName = "现金流出时间", headerOrder = 40)
    private String cashOutflowTime;

    @SimpleExcelHeader(headerName = "保证金（万元）", headerOrder = 50)
    private String earnestMoney;

    @SimpleExcelHeader(headerName = "预估流出现金流合计（万元）", headerOrder = 70)
    private String estimateCashOutflowAmount;
}
