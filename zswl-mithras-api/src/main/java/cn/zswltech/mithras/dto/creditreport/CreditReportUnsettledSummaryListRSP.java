package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description 征信报告-未结清信贷及授信信息表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-未结清信贷及授信信息表列表-返回体")
public class CreditReportUnsettledSummaryListRSP {

    /**
     * 款项模块
     */
    @ApiModelProperty(value = "款项模块 ")
    private String paymentModule;

    @ApiModelProperty(value = "款项模块名称")
    private String paymentModuleName;

    private List<UnsettledSummaryBody> bodyList;

    @Data
    public static class  UnsettledSummaryBody{
        /**
         * 查询编号
         */
        @ApiModelProperty(value = "查询编号")
        private Long creditCode;

        /**
         * 征信报告基本表id
         */
        @ApiModelProperty(value = "征信报告基本表id")
        private Long creditReportId;

        /**
         * 款项模块
         */
        @ApiModelProperty(value = "款项模块 CreditReportBusinessTypeEnum")
        private String paymentModule;

        /**
         * 款项类型-短期-贴现
         */
        @ApiModelProperty(value = "款项类型-短期-贴现")
        private String paymentType;

        private String paymentTypeName;


        /**
         * 款项分类-正常，关注-不良-合计
         */
        @ApiModelProperty(value = "款项分类-正常，关注-不良-合计")
        private String fundClassification;

        /**
         * 账户数
         */
        @ApiModelProperty(value = "账户数")
        private Integer accountNumber;

        /**
         * 账户余额
         */
        @ApiModelProperty(value = "账户余额")
        private BigDecimal accountAmount;

    }
}
