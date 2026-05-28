package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @create: 2022-10-21
 **/

@Data
public class ProjStageCountRSP {

    @ApiModelProperty("总项目数")
    private Stage totalProj;

    @ApiModelProperty("立项阶段")
    private Stage projestablish;

    @ApiModelProperty("评审阶段")
    private Stage projreview;

    @ApiModelProperty("合同阶段")
    private Stage contract;

    @ApiModelProperty("结清阶段")
    private Stage contractSettle;

    @Data
    public static class Stage{
        @ApiModelProperty("总数量")
        private Long count;

        @ApiModelProperty("本月新增数量")
        private Long monthCount;
    }

}
