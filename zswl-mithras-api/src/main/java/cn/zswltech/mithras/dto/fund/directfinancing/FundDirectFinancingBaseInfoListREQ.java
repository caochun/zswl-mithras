package cn.zswltech.mithras.dto.fund.directfinancing;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 直接融资-详情信息
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-详情信息列表-请求体")
public class FundDirectFinancingBaseInfoListREQ extends PageReq {

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资金额范围-开始")
    private Long financingAmountFrom;

    @ApiModelProperty(value = "融资金额范围-结束")
    private Long financingAmountTo;

    @ApiModelProperty(value = "项目类型")
    private String directFinancingType;
    @ApiModelProperty(value = "资金经理")
    private Long fundManagerId;

    @ApiModelProperty(value = "创建时间起始")
    private LocalDate createTimeFrom;
    @ApiModelProperty(value = "创建时间结束")
    private LocalDate createTimeTo;
    @ApiModelProperty(value = "更新时间起始")
    private LocalDate updateTimeFrom;
    @ApiModelProperty(value = "更新时间结束")
    private LocalDate updateTimeTo;

    @ApiModelProperty(value = "起息日开始")
    private LocalDate carryInterestTimeFrom;
    @ApiModelProperty(value = "起息日结束")
    private LocalDate carryInterestTimeTo;
    @ApiModelProperty(value = "到期日开始")
    private LocalDate durationTimeFrom;
    @ApiModelProperty(value = "到期日结束")
    private LocalDate durationTimeTo;
    @ApiModelProperty(value = "融资状态")
    private List<String> financingStatusList;

}
