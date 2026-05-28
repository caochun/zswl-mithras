package cn.zswltech.mithras.dto.projreview.meet;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
@ApiModel("项目评审会议纪要表-质押措施")
@NoArgsConstructor
public class ProjReviewMeetMinutePledgeMeasuresDTO {

    @ApiModelProperty("客户信息")
    private List<ClientInfo> clientInfoList;

    /**
     * 是否上报征信 0不上报，1上报
     */
    @ApiModelProperty(value = "是否上报征信 0不上报，1上报")
    private Integer isReport;

    @ApiModelProperty("客户角色 PledgeMeasuresTypeEnum")
    private String clientRole;

    @ApiModelProperty("抵押类型 ProjReviewPledgeTypeEnum")
    private String pledgedType;

    @ApiModelProperty("质押标的")
    private String pledgedObject;

    @ApiModelProperty("其他")
    private String otherMessage;

}
