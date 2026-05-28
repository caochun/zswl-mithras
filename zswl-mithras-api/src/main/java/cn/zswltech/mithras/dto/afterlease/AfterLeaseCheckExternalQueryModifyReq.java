package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @description 租后检查外部查询任务
 * @author zhaozhengkang
 * @date 2022-11-17
 */
@Data
@ApiModel("租后检查外部查询任务编辑-请求体")
public class AfterLeaseCheckExternalQueryModifyReq {

    @ApiModelProperty(value = "主键id")
    @NotNull(message = "id为空")
    private Long id;

    @ApiModelProperty(value = "检查日期")
    @NotNull(message = "检查日期为必填项")
    private LocalDate inspectionDate;

}
