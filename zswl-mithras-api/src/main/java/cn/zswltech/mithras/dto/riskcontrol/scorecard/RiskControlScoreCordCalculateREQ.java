package cn.zswltech.mithras.dto.riskcontrol.scorecard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName AreaSearchRSP
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/2 4:03 下午
 * @Version 1.0
 **/
@Data
public class RiskControlScoreCordCalculateREQ {

    @ApiModelProperty(value = "客户id")
    @NotNull(message = "客户id不能为空")
    private Long clientId;

    @ApiModelProperty(value = "分控行业分类")
    private String suitTrade;

}
