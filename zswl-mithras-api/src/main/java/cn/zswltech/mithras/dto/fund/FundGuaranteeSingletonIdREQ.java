package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Data
@ApiModel("请求体")
public class FundGuaranteeSingletonIdREQ extends PageReq {
    @ApiModelProperty(value = "id")
    @NotNull(message = "id不得为空")
    private Long id;

}
