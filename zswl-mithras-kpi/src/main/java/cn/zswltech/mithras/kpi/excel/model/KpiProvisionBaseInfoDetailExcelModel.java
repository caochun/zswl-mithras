package cn.zswltech.mithras.kpi.excel.model;

import cn.zswltech.mithras.foundation.excel.ColumnStyleEnum;
import cn.zswltech.mithras.foundation.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.foundation.excel.model.ExcelModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author vico
 * @description 绩效-拨备表
 * @date 2023-06-19
 */
@Data
@ApiModel("绩效-拨备表列表-返回体")
public class KpiProvisionBaseInfoDetailExcelModel extends ExcelModel {

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @SimpleExcelHeader(headerName = "业务类型", headerOrder = 10)
    private String bizTypeName;

    /**
     * 项目类别
     */
    @SimpleExcelHeader(headerName = "项目类型", headerOrder = 20)
    private String projClassifyName;

    /**
     * 利润所属部门name
     */
    @SimpleExcelHeader(headerName = "业务部门", headerOrder = 30)
    private String profitBelongDeptName;

    /**
     * 客户name
     */
    @SimpleExcelHeader(headerName = "客户名称", headerOrder = 40)
    private String clientName;

    /**
     * 合同编号
     */
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 50)
    private String contractCode;

    /**
     * 到期日
     */
    @SimpleExcelHeader(headerName = "到期日", headerOrder = 60)
    private String endDate;

    /**
     * 剩余期限
     **/
    @SimpleExcelHeader(headerName = "剩余期限", headerOrder = 70)
    private String residualMaturity;

    /**
     * 剩余本金
     */
    @SimpleExcelHeader(headerName = "剩余本金", headerOrder = 80, columnStyle = ColumnStyleEnum.MONEY)
    private String remainingPrincipal;

    /**
     * 保证金余额
     */
    @SimpleExcelHeader(headerName = "保证金", headerOrder = 90, columnStyle = ColumnStyleEnum.MONEY)
    private String earnestBalance;

    /**
     * 敞口
     */
    @SimpleExcelHeader(headerName = "敞口", headerOrder = 100, columnStyle = ColumnStyleEnum.MONEY)
    private String exposure;

    /**
     * 风险等级
     */
    @SimpleExcelHeader(headerName = "风险等级", headerOrder = 110)
    private String riskLevel;

    /**
     * 计提比例
     */
   /* @SimpleExcelHeader(headerName = "计提比例", headerOrder = 120, columnStyle = ColumnStyleEnum.MONEY)
    private String withdrawalRatio;*/

    /**
     * 本月风险余额
     */
    @SimpleExcelHeader(headerName = "本月风险金余额", headerOrder = 130, columnStyle = ColumnStyleEnum.MONEY)
    private String profitCurrent;

    /**
     * 上月风险余额
     */
    @SimpleExcelHeader(headerName = "上月风险金余额", headerOrder = 140, columnStyle = ColumnStyleEnum.MONEY)
    private String profitTotal;

    /**
     * 本月风险金计提/转回
     */
    @SimpleExcelHeader(headerName = "本月风险金计提/转回", headerOrder = 150, columnStyle = ColumnStyleEnum.MONEY)
    private String bonusCurrent;

    @SimpleExcelHeader(headerName = "应计利息", headerOrder = 160, columnStyle = ColumnStyleEnum.DEFAULT)
    private String accruedInterest;

    @SimpleExcelHeader(headerName = "下期租金", headerOrder = 170, columnStyle = ColumnStyleEnum.DEFAULT)
    private String nextRent;

    @SimpleExcelHeader(headerName = "备注", headerOrder = 180, columnStyle = ColumnStyleEnum.DEFAULT)
    private String remark;

}
