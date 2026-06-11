package cn.zswltech.mithras.dto.rating.ratingclient;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2025/3/19
 * @description
 */
@Data
public class RatingClientAreaIndicatorModifyREQ {
    @NotNull(message = "客户评级id不能为空")
    private Long ratingClientId;

//    @NotNull(message = "bizId不能为空")
    private Long bizId;

//    @NotNull(message = "指标数值不能为空")
    private String indicatorValue;
}
