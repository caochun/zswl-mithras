package cn.zswltech.mithras.contract.excel.model;

import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/5 14:26
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractExcelModel2 extends ExcelModel {
    @SimpleExcelHeader(headerName = "主键", headerOrder = 10)
    private Long id;

    private Long projSponsorUserId;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 30)
    private String contractCode;

    @SimpleExcelHeader(headerName = "主办", headerOrder = 40)
    private String projSponsorUserName;

    private Long clientId;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 70)
    private String clientName;

    @SimpleExcelHeader(headerName = "担保人", headerOrder = 80)
    private String guarantors;

    private String riskControlManagerId;

    @SimpleExcelHeader(headerName = "风控经理", headerOrder = 90)
    private String riskControlManagerName;

    @SimpleExcelHeader(headerName = "风控行业分类", headerOrder = 100)
    private String riskControlIndustryClassify;
}
