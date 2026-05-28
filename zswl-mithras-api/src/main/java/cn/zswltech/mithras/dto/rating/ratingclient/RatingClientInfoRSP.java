package cn.zswltech.mithras.dto.rating.ratingclient;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class RatingClientInfoRSP {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "客户编号")
    private String clientCode;

    @ApiModelProperty(value = "客户名称")
    private String clientName;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区、县")
    private String district;

    @ApiModelProperty(value = "成立日期")
    private LocalDate establishDate;

    @ApiModelProperty(value = "注册资本")
    private Long registerCapital;

    @ApiModelProperty(value = "行业分类")
    private String industryType;

    @ApiModelProperty(value = "行业分类名称")
    private String industryTypeName;

    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty(value = "业务范围")
    private String bizScope;

    @ApiModelProperty(value = "航运客户标记")
    private Boolean hymxFlag;

}
