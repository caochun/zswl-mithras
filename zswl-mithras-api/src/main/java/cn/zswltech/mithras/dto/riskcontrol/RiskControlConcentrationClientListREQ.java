package cn.zswltech.mithras.dto.riskcontrol;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@Data
@ApiModel("客户集中度列表-请求体")
public class RiskControlConcentrationClientListREQ extends PageReq {

    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("所属集团的名称")
    private String groupName;
    @ApiModelProperty("预警状态")
    private String state;
    @ApiModelProperty("是否浙江省内集团协同业务（1是0否）")
    private Integer zhejiangInnerGroup;
    @ApiModelProperty("数据时点")
    @NotNull
    private LocalDate dataTimePoint;
}
