package cn.zswltech.mithras.dto.rating.ratingamount;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RatingAmountPageREQ extends PageReq {

    @ApiModelProperty("项目评审id")
    @NotNull(message = "项目评审id不得为空")
    private Long projReviewId;
}
