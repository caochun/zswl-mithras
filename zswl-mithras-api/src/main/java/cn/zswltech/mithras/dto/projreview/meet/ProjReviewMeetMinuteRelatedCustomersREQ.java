package cn.zswltech.mithras.dto.projreview.meet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
@ApiModel("项目评审会议纪要表编辑-请求体")
public class ProjReviewMeetMinuteRelatedCustomersREQ {

    /**
     * 关联的评审id
     */
    @ApiModelProperty(value = "关联的评审id")
    //@NotNull(message = "评审id不能为空")
    private Long projReviewId;

    private Long meetMinuteId;

}
