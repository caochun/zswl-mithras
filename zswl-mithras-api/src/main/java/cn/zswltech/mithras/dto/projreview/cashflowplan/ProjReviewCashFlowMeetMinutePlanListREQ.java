package cn.zswltech.mithras.dto.projreview.cashflowplan;

import cn.zswltech.mithras.dto.VersionBaseREQ;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author dingqi
 * @date 2022/8/1
 * @description
 */
@Data
@ApiModel("获取现金流计划表-请求体")
@AllArgsConstructor
@NoArgsConstructor
public class ProjReviewCashFlowMeetMinutePlanListREQ extends VersionBaseREQ {
    /*@ApiModelProperty("项目评审记录id")
    private Long meetMinuteId;*/

    @ApiModelProperty("项目评审记录id")
    private Long projReviewId;

    @ApiModelProperty("流程id")
    private String processInstanceId;
}
