package cn.zswltech.mithras.dto.rating.ratingamount;

import cn.zswltech.mithras.dto.rating.RatingParamRSP;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RatingAmountFinishREQ {

    @ApiModelProperty(value = "债项评级id")
    @NotNull(message = "id不得为空")
    private Long id;

    @ApiModelProperty(value = "操作类型，确认完成评级：true，保存：false")
    @NotNull(message = "操作类型不得为空")
    private boolean operationType;

    @ApiModelProperty("评分参数")
    private List<RatingParamRSP> param;

    @ApiModelProperty("是否需要房地产调整")
    private Boolean isRealEstateAdjust;

    @ApiModelProperty("是否需要股权调整")
    private Boolean isStockRightsAdjust;

}
