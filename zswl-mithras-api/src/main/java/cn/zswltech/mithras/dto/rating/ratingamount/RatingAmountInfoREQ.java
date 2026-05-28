package cn.zswltech.mithras.dto.rating.ratingamount;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingAmountInfoREQ {

    @ApiModelProperty(value = "评审id")
    @NotNull(message = "评审id不得为空")
    private Long projReviewId;

}
