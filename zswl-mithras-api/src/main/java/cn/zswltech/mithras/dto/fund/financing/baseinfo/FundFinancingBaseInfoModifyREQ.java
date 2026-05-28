package cn.zswltech.mithras.dto.fund.financing.baseinfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/20
 * @description
 */
@Data
@ApiModel("融资管理-基本信息保存-请求体")
public class FundFinancingBaseInfoModifyREQ {
    @ApiModelProperty("主键id")
    @NotNull(message = "基本信息id不能为空")
    private Long id;

    @ApiModelProperty("融资期限类型")
    @NotBlank(message = "融资期限类型不能为空")
    private String timeLimitType;

    @ApiModelProperty("业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

//    @ApiModelProperty("担保信息列表")
//    @Valid
//    private List<GuaranteeInfoREQ> guaranteeInfoList;

    @ApiModelProperty("资金用途")
    @NotBlank(message = "资金用途不能为空")
    private String fundsPurpose;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否期初一次性收息 0-否 1-是")
    @NotNull(message = "是否期初一次性收息不能为空")
    private Integer initialInterestReceivedOnce;

    @ApiModelProperty("融资机构信息 - 银团需要填")
    private List<organizationInfoREQ> organizationInfoList;

    @Data
    public static class GuaranteeInfoREQ {
        @ApiModelProperty("担保机构id")
        @NotNull(message = "担保机构id不能为空")
        private Long guaranteeAgencyId;

        @ApiModelProperty("担保金额")
        @NotNull(message = "担保金额不能为空")
        private Long guaranteeAmount;
    }


    @Data
    public static class organizationInfoREQ {

        @ApiModelProperty("融资机构id")
        @NotNull(message = "融资机构id不能为空")
        private Long organizationId;

        @ApiModelProperty("融资机构")
        @NotNull(message = "融资机构名称不能为空")
        private String organizationName;

        @ApiModelProperty("机构融资金额")
        @NotNull(message = "融资金额不能为空")
        private Long organizationAmount;

    }
}
