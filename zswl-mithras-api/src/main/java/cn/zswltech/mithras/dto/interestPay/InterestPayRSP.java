package cn.zswltech.mithras.dto.interestPay;

import cn.zswltech.mithras.dto.PageReq;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class InterestPayRSP extends PageReq {

    @ApiModelProperty("id序列号")
    private Long id;

    @ApiModelProperty("融资编号")
    private String financingCode;

    @ApiModelProperty("融资id")
    private Long financingId;

    @ApiModelProperty("融资渠道")
    private String organizationName;

    @ApiModelProperty(value = "起息日")
    private LocalDate valueDate;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("业务类型展示")
    private String bizTypeDisplay;

    @ApiModelProperty("借款性质")
    private String loanProperty;

    @ApiModelProperty("利率")
    private Integer financingRate;

    @ApiModelProperty("日利率")
    private Integer dailyRate;

    @ApiModelProperty("当年累计应付利息")
    private Long yearCapitalCost;

    @ApiModelProperty("当年累计应付利息（税后）")
    private Long yearCapitalCostAfterTax;

    @ApiModelProperty("当期应付利息")
    private Long termCapitalCost;

    @ApiModelProperty("当期应付利息（税后）")
    private Long termCapitalCostAfterTax;

    @ApiModelProperty("融资类型，直融/间融")
    private String type;

    @ApiModelProperty("会计期间")
    private String calculateTime;

    @ApiModelProperty("更新日期")
    private LocalDate updateTime;

    @ApiModelProperty("是否确认")
    private boolean isConfirmed;
}
