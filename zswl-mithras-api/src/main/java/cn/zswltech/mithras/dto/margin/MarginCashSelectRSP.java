package cn.zswltech.mithras.dto.margin;

import cn.zswltech.mithras.dto.SelectRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @create: 2022-08-17
 **/
@Data
public class MarginCashSelectRSP {

    @ApiModelProperty("code")
    private String label;
    @ApiModelProperty("下拉选项值")
    private String value;
    @ApiModelProperty("期项列表")
    private List<SelectRSP> children;
}
