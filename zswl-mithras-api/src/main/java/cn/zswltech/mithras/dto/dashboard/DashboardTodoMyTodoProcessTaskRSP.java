package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/6/15
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardTodoMyTodoProcessTaskRSP extends DashboardTodoProcessRSP {
    @ApiModelProperty("流程到达时间")
    private LocalDateTime currentNodeStartTime;

}
