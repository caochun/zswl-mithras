package cn.zswltech.mithras.service.fund.direct.excel;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 11:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FundDirectFinancingSubscriptionDetailExcelModel extends ExcelModel {

    /**
     * 认购机构
     */
    @SimpleExcelHeader(headerName = "认购机构", headerOrder = 10)
    private String orgnizationName;

    /**
     * 认购证券
     */
    @SimpleExcelHeader(headerName = "认购证券", headerOrder = 20)
    private String productName;

    /**
     * 认购额度（万元）
     */
    @SimpleExcelHeader(headerName = "认购额度（万元）", headerOrder = 30)
    private String subscriptionLimit;

    @SimpleExcelHeader(headerName = "是否授信机构", headerOrder = 40)
    private String isCreditOrg;

    /**
     * 备注
     */
    @SimpleExcelHeader(headerName = "备注", headerOrder = 50)
    private String remark;
}
