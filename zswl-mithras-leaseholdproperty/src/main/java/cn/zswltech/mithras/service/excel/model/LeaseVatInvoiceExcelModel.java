package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yupengfei
 * @date 2024/5/13 18:50
 */
@Data
@Accessors(chain = true)
public class LeaseVatInvoiceExcelModel extends ExcelModel {

    @SimpleExcelHeader(headerName = "文件名")
    private String fileName;

    @SimpleExcelHeader(headerName = "发票号码")
    private String invoiceNo;

    @SimpleExcelHeader(headerName = "开票日期")
    private String invoiceIssueDate;

    @SimpleExcelHeader(headerName = "购买方")
    private String invoicePayerName;

    @SimpleExcelHeader(headerName = "销售方")
    private String invoiceSellerName;

    @SimpleExcelHeader(headerName = "开票内容")
    private String invoiceGoods;

    @SimpleExcelHeader(headerName = "规格型号")
    private String invoicePlateSpecific;

    @SimpleExcelHeader(headerName = "单位")
    private String invoiceElectransUnit;

    @SimpleExcelHeader(headerName = "数量")
    private String invoiceElectransQuantity;

    @SimpleExcelHeader(headerName = "税率")
    private String invoiceTaxRate;

    @SimpleExcelHeader(headerName = "税额（小写）")
    private String invoiceTax;

    @SimpleExcelHeader(headerName = "金额（不含税）")
    private String taxNotIncluded;

    @SimpleExcelHeader(headerName = "金额（含税）")
    private String invoicePrice;

    @SimpleExcelHeader(headerName = "验真结果")
    private String verifyResult;

    @SimpleExcelHeader(headerName = "发票状态")
    private String status;

    @SimpleExcelHeader(headerName = "是否盖章")
    private String existStample;

    @SimpleExcelHeader(headerName = "备注")
    private String note;

    @SimpleExcelHeader(headerName = "车架号")
    private String vehicleInvoiceCarVin;
}
