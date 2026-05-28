package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RatingExecuteREQ {

    @ApiModelProperty("评级id")
    @NotNull(message = "id不得为空")
    private Long id;

//    @ApiModelProperty("模型code")
//    @NotNull(message = "模型code不得为空")
//    private String code;

    @ApiModelProperty("评分参数")
    @NotEmpty(message = "评分参数不得为空")
    private List<RatingParamREQ> param;

    /**
     * 是否为完成评级时调用
     */
    private boolean finishedCall;



}
