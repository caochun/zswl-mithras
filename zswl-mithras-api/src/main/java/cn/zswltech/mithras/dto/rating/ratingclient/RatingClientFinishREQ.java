package cn.zswltech.mithras.dto.rating.ratingclient;

import cn.zswltech.mithras.dto.rating.RatingParamRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RatingClientFinishREQ {

    @ApiModelProperty(value = "客户评级id")
    @NotNull(message = "id不得为空")
    private Long id;

    @ApiModelProperty(value = "流程id")
    private Long processInstanceId;

    @ApiModelProperty(value = "操作类型，确认完成评级：true，保存：false")
    @NotNull(message = "操作类型不得为空")
    private boolean operationType;

    @ApiModelProperty("评分参数")
    private List<RatingParamRSP> param;




}
