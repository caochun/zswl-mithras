package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-10-21
 **/

@Data
public class ProjStageTotalRSP {

    @ApiModelProperty("总项目数")
    private Stage projTotal;

    @ApiModelProperty("立项阶段")
    private Stage projEstablishTotal;

    @ApiModelProperty("评审阶段")
    private Stage projReviewTotal;

    @ApiModelProperty("合同阶段")
    private Stage contractTotal;

    @ApiModelProperty("结清阶段")
    private Stage settledTotal;

    @Data
    public static class Stage {
        @ApiModelProperty("总数量")
        private Long allTotal;

        @ApiModelProperty("本月新增数量")
        private Long currentMonthAdded;
    }

}
