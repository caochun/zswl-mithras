package cn.zswltech.mithras.dto.rating;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RatingModelQueryRSP {

    @ApiModelProperty("模型名称")
    private String name;

    @ApiModelProperty("模型编码")
    private String code;



}
