package cn.zswltech.mithras.dto.flow.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/3/17
 * @description
 */
@Data
public class SetReviewMeetingPlanDateREQ {
    @ApiModelProperty("评审会预计召开时间")
    private String reviewMeetingPlanDate;
}
