package cn.zswltech.mithras.dto.budget;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 预算管理-参数设置
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-参数设置列表-返回体")
public class BudgetParameterConfigListRSP {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 参数key
    */
    @ApiModelProperty(value = "参数key BudgetConfigTypeEnum")
    private String configKey;

    /**
    * 参数name
    */
    @ApiModelProperty(value = "参数name")
    private String configName;

    @ApiModelProperty(value = "基础参数-未转换")
    private String configValue;

    @ApiModelProperty(value = "ftp参数")
    private List<BudgetParameterFtpBO> ftpConfigValue;

    @ApiModelProperty(value = "风险准备金")
    private List<BudgetParameterRiskBO> riskConfigValue;

    @ApiModelProperty(value = "费用比例")
    private List<BudgetParameterExpenseRatioBO> expenseRatioConfigValue;

    @ApiModelProperty(value = "update_time")
    private LocalDateTime updateTime;

    @ApiModelProperty("update_by")
    private Long updateBy;

    @ApiModelProperty("update_by")
    private String updateByName;


    public void setConfigValue() {
        if (ObjectUtil.isEmpty(configKey) || ObjectUtil.isEmpty(this.configValue)) {
            return;
        }
        if (ObjectUtil.equals(this.configKey, "FTP_PRICE")) {
            this.ftpConfigValue = JSONUtil.toList(this.configValue, BudgetParameterFtpBO.class);
        } else if (ObjectUtil.equals(this.configKey, "RISK_RATIO")) {
            this.riskConfigValue = JSONUtil.toList(this.configValue, BudgetParameterRiskBO.class);
        } else if (ObjectUtil.equals(this.configKey, "EXPENSE_RATIO")) {
            this.expenseRatioConfigValue = JSONUtil.toList(this.configValue, BudgetParameterExpenseRatioBO.class);
        }
    }

    @Data
    public static class BudgetParameterFtpBO {
        /**
         * ftp行业分类
         */
        @ApiModelProperty(value = "ftp行业分类 FtpIndustryCategoryEnum")
        private String ftpIndustryClassification;

        /**
         * 一年期
         */
        @ApiModelProperty(value = "一年期")
        private Integer oneYearTerm;

        /**
         * 一到三年期
         */
        @ApiModelProperty(value = "一到三年期")
        private Integer oneToThreeYearTerm;

        /**
         * 三年以上
         */
        @ApiModelProperty(value = "三年以上")
        private Integer moreThanThreeYears;
    }

    @Data
    public static class BudgetParameterRiskBO {
        /**
         * 风险准备金
         */
        @ApiModelProperty(value = "风险准备金计提比例")
        private Integer riskReserve;

        /**
         * FTP行业分类
         */
        @ApiModelProperty(value = "FTP行业分类")
        private String ftpIndustryCategory;

        /**
         * 年限
         */
        @ApiModelProperty(value = "年限")
        private String termRange;
    }

    @Data
    public static class BudgetParameterExpenseRatioBO {

        @ApiModelProperty(value = "部门id")
        private Long deptId;

        @ApiModelProperty(value = "部门名称")
        private String deptName;

        @ApiModelProperty(value = "费用比例")
        private Integer expenseRatio;
    }

}
