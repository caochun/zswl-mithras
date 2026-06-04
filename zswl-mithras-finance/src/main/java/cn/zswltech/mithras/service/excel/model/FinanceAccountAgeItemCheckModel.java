package cn.zswltech.mithras.service.excel.model;

import cn.zswltech.mithras.service.excel.annotation.SimpleExcelHeader;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class FinanceAccountAgeItemCheckModel extends ExcelModel {
    

    /**
     * 核算组织名称 默认 浙江浙商融资租赁有限公司
     */
    @SimpleExcelHeader(headerName = "核算组织名称", headerOrder = 10)
    private String accountancyOrganizationName;

    /**
     * 状态
     */
    @SimpleExcelHeader(headerName = "状态", headerOrder = 20)
    private String sendStatus;

    /**
     * 期初款项原值
     */
    @SimpleExcelHeader(headerName = "期初款项原值", headerOrder = 30)
    private BigDecimal originalValueInitial;

    /**
     * 本期增加额 >0增加 <0减少
     */
    @SimpleExcelHeader(headerName = "本期增加额", headerOrder = 40)
    private BigDecimal originalValueIncrease;

    @SimpleExcelHeader(headerName = "本期减少额", headerOrder = 50)
    private BigDecimal originalValueReduce;

    /**
     * 期末款项原值
     */
    @SimpleExcelHeader(headerName = "期末款项原值(余额)", headerOrder = 60)
    private BigDecimal originalValueFinal;

    /**
     * 币别
     */
    @SimpleExcelHeader(headerName = "币别", headerOrder = 70)
    private String currency;

    /**
     * 科目名称编号
     */
    @SimpleExcelHeader(headerName = "科目名称", headerOrder = 80)
    private String accountNumber;

    /**
     * 款项内容
     */
    @SimpleExcelHeader(headerName = "款项内容", headerOrder = 90)
    private String paymentContent;
    

    /**
     * 客户单位名称， 取合同对应承租人的“客户名称”字段
     */
    @SimpleExcelHeader(headerName = "客户编码（苍穹）", headerOrder = 100)
    private String customerUnitName;

    /**
     * 业务日期
     */
    @SimpleExcelHeader(headerName = "业务日期", headerOrder = 110)
    private String businessDate;

    /**
     * 账龄截止日
     */
    @SimpleExcelHeader(headerName = "账龄截止日", headerOrder = 120)
    private String agingDeadline;

    /**
     * 业务账龄（月）向下取整
     */
    @SimpleExcelHeader(headerName = "账龄", headerOrder = 130)
    private Integer businessAge;
    
    /**
     * 合同编号
     */
    @SimpleExcelHeader(headerName = "合同编号", headerOrder = 140)
    private String contractCode;

    /**
     * 项目名称
     */
    @SimpleExcelHeader(headerName = "合同名称", headerOrder = 150)
    private String projName;

    /**
     * 租金的应收日期 合同逾期日期
     */
    @SimpleExcelHeader(headerName = "合同逾期日期", headerOrder = 160)
    private String planCollectionDate;
    
}
