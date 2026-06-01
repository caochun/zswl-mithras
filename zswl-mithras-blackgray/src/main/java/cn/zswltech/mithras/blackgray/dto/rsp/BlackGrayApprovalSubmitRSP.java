package cn.zswltech.mithras.blackgray.dto.rsp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BlackGrayApprovalSubmitREQ
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/12/4 7:37 下午
 * @Version 1.0
 **/
@Data
@ApiModel("黑灰名单提交审批-返回体")
public class BlackGrayApprovalSubmitRSP {

    /**
     * 业务id
     */
    @ApiModelProperty(value = "业务id")
    private Long bizId;

    /**
     * 业务关联审批任务id
     */
    @ApiModelProperty(value = "业务关联审批任务id")
    private String taskId;

}
