package cn.zswltech.mithras.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author luyi
 */
@Data
@ApiModel("下拉数据-返回体")
public class SelectRSP {
    @ApiModelProperty("下拉展示文本")
    private String label;
    @ApiModelProperty("下拉选项值")
    private String value;
    @ApiModelProperty(value = "子集")
    private List<SelectRSP> children;
    @ApiModelProperty("子枚举的名称")
    private String childSelectName;
    @ApiModelProperty("枚举层级")
    private int level;

    @ApiModelProperty("状态 0-禁用，1-启用")
    private Integer state;
    public SelectRSP(String label, String value) {
        this.label = label;
        this.value = value;
    }
    public SelectRSP(String label, String value, Integer state) {
        this.label = label;
        this.value = value;
        this.state = state;
    }

    public SelectRSP() {
    }
}
