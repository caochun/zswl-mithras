package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 租金催收数据请求体
 *
 * @author ylzhang5
 * @date 2025/12/01
 */
@Data
@ApiModel("租金催收数据请求体")
public class RentCollectionBaseInfo {

    @ApiModelProperty("合同ID")
    private Long contractId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("客户ID")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("承租人名称")
    private String lesseeName;

    @ApiModelProperty("期项")
    private Integer phase;

    @ApiModelProperty("计划还款日-年")
    private String planYear;

    @ApiModelProperty("计划还款日-月")
    private String planMonth;

    @ApiModelProperty("计划还款日-日")
    private String planDay;

    @ApiModelProperty("收款明细ID")
    private Long collectionId;

    @ApiModelProperty("名义价款")
    private String lNominalPrice;

}
