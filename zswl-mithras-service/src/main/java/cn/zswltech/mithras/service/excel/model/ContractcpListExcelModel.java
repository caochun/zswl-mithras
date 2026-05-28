package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合同收付款 列表
 *
 * @author wangchuanhao
 * @date 2022/8/22 4:39 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractcpListExcelModel extends ExcelModel {
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 10)
    private String contractCode;

    @SimpleExcelHeader(headerName = "项目名称", headerOrder = 20)
    private String projName;

    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 30)
    private String clientName;

    @SimpleExcelHeader(headerName = "合同状态", headerOrder = 40)
    private String contractStatus;

    @SimpleExcelHeader(headerName = "类别", headerOrder = 50)
    private String bizType;

    @SimpleExcelHeader(headerName = "项目主办", headerOrder = 60)
    private String projSponsorUser;

    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 70)
    private String bizDept;

    @SimpleExcelHeader(headerName = "已收本金", headerOrder = 80)
    private String receivedPrincipal;

    @SimpleExcelHeader(headerName = "已收利息", headerOrder = 90)
    private String receivedInterest;

    @SimpleExcelHeader(headerName = "合计", headerOrder = 100)
    private String receivedSum;

    @SimpleExcelHeader(headerName = "剩余本金", headerOrder = 110)
    private String lastPrincipal;

    @SimpleExcelHeader(headerName = "剩余利息", headerOrder = 120)
    private String lastInterest;

    @SimpleExcelHeader(headerName = "名义货价", headerOrder = 130)
    private String nominalLoanPrice;

    @SimpleExcelHeader(headerName = "合计", headerOrder = 140)
    private String lastSum;

    @SimpleExcelHeader(headerName = "逾期未还金额", headerOrder = 150)
    private String overdueAmount;
}
