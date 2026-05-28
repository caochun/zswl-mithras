package cn.zswltech.mithras.dto.riskcontrol;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 集团关联客户集中度
 * @date 2023-02-27
 */
@Data
@ApiModel("关联客户集中度列表-请求体")
public class RiskControlConcentrationRelateListREQ extends PageReq {
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("数据时点")
    private LocalDate dataTimePoint;
    @ApiModelProperty("预警状态")
    private String state;
}
