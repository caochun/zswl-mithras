package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@ApiModel("保存评分信息-请求体")
@Data
public class RiskControlScoreCordCalculateDetailREQ {

    @ApiModelProperty(value = "地区")
    //@NotEmpty(message = "地区不能为空")
    private String area;

    @ApiModelProperty(value = "省")
    //@NotEmpty(message = "省不能为空")
    private String province;

    @ApiModelProperty(value = "市")
    //@NotEmpty(message = "市不能为空")
    private String city;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "年份")
    private Integer year;


}
