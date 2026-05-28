package cn.zswltech.mithras.api.riskcontrol.model.gljy.report;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
@ApiModel("关联交易记录列表-返回体")
public class GljyReportListRSP {

    @ApiModelProperty("id")
    private Long id;

    /**
     * 关联交易金额（万）
     * 小数点前最大15位，保留两位小数
     * 必须
     */
    @ApiModelProperty("关联交易金额（万）")
    private Long amount;
    /**
     * 交易概述
     * 必须
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 重大交易原因 字典值。当交易级别为重大交易时，此字段必须。
     */
    @ApiModelProperty("重大交易原因")
    private String importantReason;

    /**
     * 关联交易级别类型 字典值
     * 必须
     */
    @ApiModelProperty("关联交易级别")
    private String level;

    /**
     * 董事会决议、关联交易控制委员会的意见或决议
     * 非必须
     */
    @ApiModelProperty("董事会/委员会意见")
    private String opinion;

    /**
     * 交易目的
     * 必须
     */
    @ApiModelProperty("交易目的")
    private String purpose;

    /**
     * 本次交易风险提示，以及对财务状况、经营成果的影响
     * 非必须
     */
    @ApiModelProperty("风险/影响")
    private String risk;


    /**
     * 关联交易一级类型名称，字典值
     * 必须
     */
    @ApiModelProperty("一级分类")
    private String tradeCategoryParentName;

    /**
     * 关联交易二级类型名称，字典值
     * 必须
     */
    @ApiModelProperty("二级分类")
    private String tradeCategoryName;

    /**
     * 交易时间
     * 必须
     * yyyy-MM-dd格式
     */
    @ApiModelProperty("交易日期")
    private LocalDate tradeDate;

    /**
     * 交易对手上一年度末审计净资产（万）
     * 非必须
     */
    @ApiModelProperty("交易对手上一年末审计净资产（万）")
    private Long tradePartyAssets;

    /**
     * 关联交易对手名称
     * 必须
     */
    @ApiModelProperty("关联交易对手名称")
    private String tradePartyName;

    /**
     * 关联交易主体机构名称
     * 必须
     */
    @ApiModelProperty("主体机构")
    private String subjectPartyName;


    @ApiModelProperty("报送状态")
    private String reportStatus;
}
