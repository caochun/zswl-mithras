package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.ColumnStyleEnum;
import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author ylzhang5
 * @date 2025/12/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PropertyExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "融资编号", headerOrder = 10)
    private String financingCode;

    @SimpleExcelHeader(headerName = "融资机构", headerOrder = 20)
    private String financingOrg;

    @SimpleExcelHeader(headerName = "融资金额", headerOrder = 30, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal financingAmount;

    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 40)
    private String contractCode;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 50)
    private String clientName;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 60)
    private String projName;

    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 70)
    private String bizTypeName;

    @SimpleExcelHeader(headerName = "合同金额", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal contractAmount;

    @SimpleExcelHeader(headerName = "账户名称", headerOrder = 90)
    private String accountName;

    @SimpleExcelHeader(headerName = "银行账号", headerOrder = 101)
    private String accountNumber;

    @SimpleExcelHeader(headerName = "开户银行", headerOrder = 110)
    private String accountBank;

    @SimpleExcelHeader(headerName = "出款金额", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private BigDecimal putoutAmount;

    @SimpleExcelHeader(headerName = "出款日期", headerOrder = 130, columnStyle = ColumnStyleEnum.DATE)
    private LocalDate putoutDate;
}
