package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RatingModelQueryREQ {

    @ApiModelProperty("业务类型，客户评级还是债项评级 RatingBizTypeEnum")
    private String bizType;

}
