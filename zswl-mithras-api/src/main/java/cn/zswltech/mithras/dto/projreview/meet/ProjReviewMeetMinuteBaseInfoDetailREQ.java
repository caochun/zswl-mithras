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
@ApiModel("项目评审会议纪要表新增-请求体")
public class ProjReviewMeetMinuteBaseInfoDetailREQ {


    @ApiModelProperty(value = "项目评审会议纪要id")
    private Long id;

    /**
    * 关联的评审id
    */
    @ApiModelProperty(value = "关联的评审id")
    private Long projReviewId;

    @ApiModelProperty(value = "关联的评审类型 AppProjStageStatus")
    private String projReviewType;

    /**
    * 关联项目流程
    */
    @ApiModelProperty(value = "关联项目流程")
    private String projFlowId;

    @ApiModelProperty(value = "是否生效 1 生效， 其他全部生效 ")
    private Integer isEffect;

    private Long paymentId;

    @ApiModelProperty(value = "合同ID")
    private Long contractId;


}
