package cn.zswltech.mithras.budget.application.excel;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author vico
 * @description 资产减值预测记录表
 * @date 2025-09-28
 */
@Data
@ApiModel("资产减值预测记录表新增-请求体")
public class EclExecutePredictRecordExcelModel extends ExcelModel {

    /**
     * 客户id
     */
    @SimpleExcelHeader(headerName = "客户id")
    private Long clientId;

    /**
     * 客户名称
     */
    @SimpleExcelHeader(headerName = "客户名称")
    private String clientName;

    /**
     * 合同id
     */
    @SimpleExcelHeader(headerName = "合同id")
    private Long contractId;

    /**
     * 合同编号
     */
    @SimpleExcelHeader(headerName = "合同编号")
    private String contractCode;

    private Long receiptId;

    @SimpleExcelHeader(headerName = "借据编号")
    private String receiptCode;

    @SimpleExcelHeader(headerName = "业务类型。租赁、保理、转租赁")
    private String contractBizType;

    @SimpleExcelHeader(headerName = "租赁类型。直租、回租、经营性租赁")
    private String contractLeaseType;

    @SimpleExcelHeader(headerName = "项目类别")
    private String projClassify;

    @SimpleExcelHeader(headerName = "利润所属部门id")
    private Long profitBelongDeptId;

    @SimpleExcelHeader(headerName = "业务部门")
    private String profitBelongDeptName;

    /**
     * 内评评级
     */
    @SimpleExcelHeader(headerName = "内评评级")
    private String innerMdLevel;

    /**
     * ecl违约概率
     */
    @SimpleExcelHeader(headerName = "ecl违约概率")
    private String eclPd;

    /**
     * 外评级别
     */
    @SimpleExcelHeader(headerName = "外评级别")
    private String outerLevel;

    /**
     * ecl外评违约概率
     */
    @SimpleExcelHeader(headerName = "ecl外评违约概率")
    private String eclOuterPd;

    /**
     * 所属分组
     */
    @SimpleExcelHeader(headerName = "所属分组")
    private String group;

    /**
     * 五级分类
     */
    @SimpleExcelHeader(headerName = "五级分类")
    private String classify;

    /**
     * 逾期天数
     */
    @SimpleExcelHeader(headerName = "逾期天数")
    private Integer lateDay;

    /**
     * 租赁物类型
     */
    @SimpleExcelHeader(headerName = "租赁物类型")
    private String leaseType;

    /**
     * 剩余本金
     */
    @SimpleExcelHeader(headerName = "剩余本金")
    private String remainPrincipal;

    /**
     * 应计利息
     */
    @SimpleExcelHeader(headerName = "应计利息")
    private String accruedInterest;

    /**
     * 保证金
     */
    @SimpleExcelHeader(headerName = "保证金")
    private String deposit;

    /**
     * 下一期租金
     */
    @SimpleExcelHeader(headerName = "下一期租金")
    private String nextRent;

    /**
     * 风险敞口
     */
    @SimpleExcelHeader(headerName = "风险敞口")
    private String riskExposure;

    /**
     * 风险等级
     */
    @SimpleExcelHeader(headerName = "风险等级")
    private String riskLevel;

    /**
     * 本月风险余额
     */
    @SimpleExcelHeader(headerName = "本月风险余额")
    private String profitCurrent;

    /**
     * 上月风险余额
     */
    @SimpleExcelHeader(headerName = "上月风险余额")
    private String profitTotal;

    /**
     * 本月风险金计提/转回
     */
    @SimpleExcelHeader(headerName = "本月风险金计提/转回")
    private String bonusCurrent;

    /**
     * 合同到期日
     */
    @SimpleExcelHeader(headerName = "合同到期日")
    private LocalDate contractExpirationDate;

    /**
     * 备注
     */
    @SimpleExcelHeader(headerName = "备注")
    private String remark;


}
