package cn.zswltech.mithras.dto.rating.ratingclient;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class RatingClientDetailREQ {

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不得为空")
    private Long id;

}
