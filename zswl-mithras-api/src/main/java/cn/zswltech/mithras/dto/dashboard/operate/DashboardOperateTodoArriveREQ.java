package cn.zswltech.mithras.dto.dashboard.operate;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOperateTodoArriveREQ{

    public static final String ARRIVE = "ARRIVE";
    public static final String WILL_ARRIVE = "WILL_ARRIVE";

    @ApiModelProperty("已到达/将到达 ARRIVE/WILL_ARRIVE")
    @NotNull(message = "类型不得为空")
    private String type;

}
