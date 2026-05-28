package cn.zswltech.mithras.metric.emit.model.req.concentration;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author yibin
 */
@Data
@Accessors(chain = true)
public class ConcentrationAddReqBody {
    private String timePoint;

    private List<UploadCustomData> uploadData;


    @Data
    public static class UploadCustomData {
        private String bizType;

        private String subjectName;

        private BigDecimal bizTotalAmount;

        private BigDecimal bizRestAmount;

        private String name;

        private Boolean sameIndustryCustomer;

        private Integer economicComposition;

        private String areaOverseas;

        private BigDecimal lastYearAssets;

        private BigDecimal lastYearDebt;

        private BigDecimal lastYearIncome;

        private BigDecimal lastYearProfit;

        private String bizDept;

        private LocalDate bizDateStart;

        private LocalDate bizDateEnd;

        // @ApiModelProperty(value = "业务基本信息 - 风险暴露扣减项 - 合格质物价值", required = false, example = "111.22")
        private BigDecimal pledge;

        // @ApiModelProperty(value = "业务基本信息 - 风险暴露扣减项 - 合格保证价值", required = false, example = "111.22")
        private BigDecimal ensure;

        // @ApiModelProperty(value = "业务基本信息 - 风险暴露扣减项 - 其他", required = false, example = "111.22")
        private BigDecimal others;

        // @ApiModelProperty(value = "业务基本信息 - 担保人信息 - 担保人名称",required = false,example = "真田信繁")
        private String guaranteeName;

        // @ApiModelProperty(value = "业务基本信息 - 资产质量信息 - 已计提减值", required = true, example = "111.22")
        private BigDecimal decrease;

        // @ApiModelProperty(value = "业务基本信息 - 资产质量信息 - 逾期天数", required = true, example = "10")
        private Integer overdueDay;

        private BigDecimal overdueAmount;

        // @ApiModelProperty(value = "业务基本信息 - 资产质量信息 - 资产质量分类，字典值assetsCategory",required = false,example = "2")
        private Byte assetsCategory;
    }
}
