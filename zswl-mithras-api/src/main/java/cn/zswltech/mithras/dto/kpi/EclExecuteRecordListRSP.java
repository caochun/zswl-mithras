package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @description 资产减值记录表
 * @author vico
 * @date 2025-09-28
 */
@Data
@ApiModel("资产减值记录表列表-返回体")
public class EclExecuteRecordListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 调用记录编号
    */
    @ApiModelProperty(value = "调用记录编号")
    private String modelRecordKey;

    /**
    * 拨备计提详情id
    */
    @ApiModelProperty(value = "拨备计提详情id")
    private Long kpiProvisionDetailId;

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
     *
     **/
    @ApiModelProperty("评估主体ID")
    private Long evaluationSubjectId;

    /**
     * 评估主体名称
     **/
    @ApiModelProperty("评估主体名称")
    private String evaluationSubjectName;

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

    @ApiModelProperty(value = "借据编号")
    private String receiptCode;

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
    * ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）
    */
    @ApiModelProperty(value = "ead计算值，融资租赁：max（剩余本金应计利息-剩余保证金，0）经营性租赁：max（拨备计提月份下一期租金，0）")
    private String ead;

    /**
    * 合同到期日
    */
    @ApiModelProperty(value = "合同到期日")
    private LocalDate contractExpirationDate;

    /**
    * 债项阶段
    */
    @ApiModelProperty(value = "债项阶段")
    private String eclStep;

    @ApiModelProperty(value = "上迁后债项阶段")
    private Integer promotionResult;


    /**
    * 期限调整系数t
    */
    @ApiModelProperty(value = "期限调整系数t")
    private String eclFactorT;

    /**
    * ecl基准/乐观/悲观调整因子z
    */
    @ApiModelProperty(value = "ecl基准/乐观/悲观调整因子z")
    private String eclParamZ;

    /**
    * ecl基准/乐观/悲观情景权重
    */
    @ApiModelProperty(value = "ecl基准/乐观/悲观情景权重")
    private String eclParamWeight;

    /**
    * 违约损失率(lgd)
    */
    @ApiModelProperty(value = "违约损失率(lgd)")
    private String lgd;

    /**
    * 基准pdforward
    */
    @ApiModelProperty(value = "基准pdforward")
    private String basePdForward;

    /**
    * 乐观pdforward
    */
    @ApiModelProperty(value = "乐观pdforward")
    private String optPdForward;

    /**
    * 悲观pdforward
    */
    @ApiModelProperty(value = "悲观pdforward")
    private String gloPdForward;

    /**
    * 基准pdifrs9
    */
    @ApiModelProperty(value = "基准pdifrs9")
    private String eclBaseIfrs9;

    /**
    * 乐观pdifrs9
    */
    @ApiModelProperty(value = "乐观pdifrs9")
    private String eclOptIfrs9;

    /**
    * 悲观pdifrs9
    */
    @ApiModelProperty(value = "悲观pdifrs9")
    private String eclGloIfrs9;

    /**
    * 基准ecl
    */
    @ApiModelProperty(value = "基准ecl")
    private String baseEcl;

    /**
    * 乐观ecl
    */
    @ApiModelProperty(value = "乐观ecl")
    private String optEcl;

    /**
    * 悲观ecl
    */
    @ApiModelProperty(value = "悲观ecl")
    private String gloEcl;

    /**
    * ecl
    */
    @ApiModelProperty(value = "ecl")
    private String ecl;

    /**
    * 备注
    */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 来源类型 0自动，1手工添加
     **/
    @ApiModelProperty("来源类型 0自动，1手工添加")
    private Integer sourceType;



    private LocalDateTime createTime;
    @ApiModelProperty(value = "create_by")
    private Long createBy;
    @ApiModelProperty(value = "update_time")
    private LocalDateTime updateTime;
    @ApiModelProperty("update_by")
    private Long updateBy;

}
