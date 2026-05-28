package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-详情信息编辑-请求体")
public class FundDirectFinancingBaseInfoModifyREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @NotBlank(message = "承销商不能为空")
    @ApiModelProperty(value = "承销商")
    private String consignee;

    @ApiModelProperty(value = "发行规模")
    @NotNull(message = "发行规模不能为空")
    private Long issuingScale;

    @ApiModelProperty(value = "项目类型")
    @NotNull(message = "项目类型不能为空")
    private String directFinancingType;

    @ApiModelProperty(value = "交易流通场所")
    @NotNull(message = "交易流通场所不能为空")
    private String tradingVenues;

    @ApiModelProperty(value = "发行方式")
    @NotNull(message = "发行方式不能为空")
    private String issuanceMethod;

    @ApiModelProperty(value = "存续时间起")
    @NotNull(message = "存续时间起不能为空")
    private LocalDate durationFrom;

    @ApiModelProperty(value = "存续时间止")
    @NotNull(message = "存续时间止不能为空")
    private LocalDate durationTo;

    @ApiModelProperty(value = "首个兑付日")
    @NotNull(message = "首个兑付日不能为空")
    private LocalDate firstPaymentDate;

    @ApiModelProperty(value = "备注")
    private String remark;
}
