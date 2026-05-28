package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
  * @author dingqi
  * @date 2024/12/12
  * @description 
  */
@Data
public class YeWuYunXingFenXiStatisticREQ extends YeWuYunXingFenXiDetailREQ {
    public static final String GROUP_TYPE_DEPT = "DEPT";
    public static final String GROUP_TYPE_MONTH = "MONTH";

    @NotBlank(message = "统计纬度不能为空")
    @ApiModelProperty("统计纬度，按部门-DEPT，按月份-MONTH")
    private String groupType;
}
