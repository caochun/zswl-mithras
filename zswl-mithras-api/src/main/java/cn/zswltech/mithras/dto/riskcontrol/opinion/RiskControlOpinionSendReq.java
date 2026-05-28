package cn.zswltech.mithras.dto.riskcontrol.opinion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName RiskControlOpinionListRsp
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/6 6:54 下午
 * @Version 1.0
 **/
@Data
@ApiModel("风控舆情通知主办-请求体")
public class RiskControlOpinionSendReq {

    @ApiModelProperty(value = "舆情id")
    @NotEmpty(message = "舆情id不能为空")
    private List<Long> ids;

}
