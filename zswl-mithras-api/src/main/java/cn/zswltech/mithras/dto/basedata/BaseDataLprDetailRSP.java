package cn.zswltech.mithras.dto.basedata;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
@Data
@ApiModel("基础数据-LPR设置信息-返回体")
public class BaseDataLprDetailRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("LPR报价日，格式：yyyy-MM-dd")
    private String lprDate;

    @ApiModelProperty("1年期LPR报价，单位：百分比")
    private String oneYear;

    @ApiModelProperty("5年期LPR报价，单位：百分比")
    private String fiveYear;
}
