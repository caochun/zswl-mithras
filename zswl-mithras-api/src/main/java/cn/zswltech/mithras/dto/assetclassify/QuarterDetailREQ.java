package cn.zswltech.mithras.dto.assetclassify;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName QuarterDetailREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/1/4 1:59 下午
 * @Version 1.0
 **/
@Data
@ApiModel("五级分类-季度选择入参")
public class QuarterDetailREQ {

    @ApiModelProperty("年份")
    @NotNull(message = "年份不能为空")
    private Integer year;
}
