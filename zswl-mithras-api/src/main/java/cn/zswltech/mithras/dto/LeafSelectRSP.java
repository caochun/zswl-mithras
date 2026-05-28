package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("下拉数据-返回体")
public class LeafSelectRSP {
    @ApiModelProperty("下拉展示文本")
    private String label;
    @ApiModelProperty("下拉选项值")
    private String value;
    @ApiModelProperty("是否叶子选项")
    private Boolean leaf;
}
