package cn.zswltech.mithras.dto.rating.ratingclient;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/23
 * @description
 */
@Data
public class RatingClientAreaIndicatorBatchModifyREQ {
    @NotNull(message = "客户评级id不能为空")
    private Long ratingClientId;

    @Valid
    @NotEmpty(message = "指标数据不能为空")
    private List<RatingClientAreaIndicatorModifyREQ> indicatorValueList;

    @lombok.Data
    public static class Data {
        private Long bizId;
        @NotNull(message = "指标数值不能为空")
        private BigDecimal indicatorValue;
    }
}
