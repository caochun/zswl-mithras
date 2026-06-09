package cn.zswltech.mithras.service.fund.direct.excel;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/19 10:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NewFundDirectFinancingPledgeInfoExcelModel extends ExcelModel {


    @SimpleExcelHeader(headerName = "关联编号", headerOrder = 10)
    private String pledgeCode;


    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 20)
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 30)
    private String projName;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 40)
    private String contractCode;


    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 50)
    private String bizTypeName;


    @SimpleExcelHeader(headerName = "是否质押", headerOrder = 60)
    private String isPledgeName;


    @SimpleExcelHeader(headerName = "是否监管", headerOrder = 70)
    private String isSuperviseName;


    @SimpleExcelHeader(headerName = "银行账号", headerOrder = 80)
    private String accountNumber;


    @SimpleExcelHeader(headerName = "开户银行", headerOrder = 90)
    private String accountBank;


    @SimpleExcelHeader(headerName = "户名", headerOrder = 100)
    private String accountName;


    @SimpleExcelHeader(headerName = "合同金额(元)", headerOrder = 110, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal contractAmount;


    //合同起期 ~ 合同止期
    @SimpleExcelHeader(headerName = "合同期限", headerOrder = 120)
    private String contractTrem;


    @SimpleExcelHeader(headerName = "剩余未还本金(元)", headerOrder = 130, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal remainingUnpaidPrincipal;


}
