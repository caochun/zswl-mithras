package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class RatingParamREQ {

    @ApiModelProperty("字段标识")
    @NotNull(message = "字段标识不得为空")
    private String fieldName;

    @ApiModelProperty("数据类型")
    @NotNull(message = "数据类型不得为空")
    private String dataType;

    @ApiModelProperty("所选选项")
    @NotNull(message = "结果不得为空")
    private Object fieldValue;

    @ApiModelProperty("数据时点")
    private LocalDateTime date;
}
