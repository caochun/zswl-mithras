package cn.zswltech.mithras.dto.kpi;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 资产减值记录表
 * @author vico
 * @date 2025-09-28
 */
@Data
@ApiModel("资产减值记录表新增-请求体")
public class EclExecuteRecordAddREQ {

    /**
     *拨备id
     **/
    @ApiModelProperty("拨备id")
    private Long provisionId;

    /**
    * 客户id
    */
    @ApiModelProperty(value = "客户id")
    private Long clientId;

    /**
    * 客户名称
    */
    @ApiModelProperty(value = "客户名称")
    private String clientName;

    /**
    * 合同id
    */
    @ApiModelProperty(value = "合同id")
    private Long contractId;

    /**
    * 合同编号
    */
    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    private Long receiptId;

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String contractBizType;

    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private String contractLeaseType;

    @ApiModelProperty(value = "项目类别")
    private String projClassify;

    @ApiModelProperty(value = "利润所属部门id")
    private Long profitBelongDeptId;

    @ApiModelProperty(value = "业务部门")
    private String profitBelongDeptName;

    /**
    * 内评评级
    */
    @ApiModelProperty(value = "内评评级")
    private String innerMdLevel;

    /**
    * ecl违约概率
    */
    @ApiModelProperty(value = "ecl违约概率")
    private String eclPd;

    /**
    * 外评级别
    */
    @ApiModelProperty(value = "外评级别")
    private String outerLevel;

    /**
    * ecl外评违约概率
    */
    @ApiModelProperty(value = "ecl外评违约概率")
    private String eclOuterPd;

    /**
    * 所属分组
    */
    @ApiModelProperty(value = "所属分组")
    private String group;

    /**
    * 五级分类
    */
    @ApiModelProperty(value = "五级分类")
    private String classify;

    /**
    * 逾期天数
    */
    @ApiModelProperty(value = "逾期天数")
    private Integer lateDay;

    /**
    * 租赁物类型
    */
    @ApiModelProperty(value = "租赁物类型")
    private String leaseType;

    /**
    * 剩余本金
    */
    @ApiModelProperty(value = "剩余本金")
    private String remainPrincipal;

    /**
    * 应计利息
    */
    @ApiModelProperty(value = "应计利息")
    private String accruedInterest;

    /**
    * 保证金
    */
    @ApiModelProperty(value = "保证金")
    private String deposit;

    /**
    * 下一期租金
    */
    @ApiModelProperty(value = "下一期租金")
    private String nextRent;

    /**
    * 风险敞口
    */
    @ApiModelProperty(value = "风险敞口")
    private String riskExposure;

    /**
     * 风险等级
     */
    @ApiModelProperty("风险等级")
    private String riskLevel;

    /**
     * 本月风险余额
     */
    @ApiModelProperty("本月风险余额")
    private String profitCurrent;

    /**
     * 上月风险余额
     */
    @ApiModelProperty(value = "上月风险余额")
    private String profitTotal;

    /**
     * 本月风险金计提/转回
     */
    @ApiModelProperty("本月风险金计提/转回")
    private String bonusCurrent;

    /**
    * 合同到期日
    */
    @ApiModelProperty(value = "合同到期日")
    private LocalDate contractExpirationDate;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

}
