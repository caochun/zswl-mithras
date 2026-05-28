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
@ApiModel("项目评审会议纪要表-担保措施")
@NoArgsConstructor
public class ProjReviewMeetMinuteGuaranteeMeasuresDTO {

    @ApiModelProperty("客户信息")
    private List<ClientInfo> clientInfoList;

    @ApiModelProperty("担保比例")
    private Integer guaranteeRate;

    /**
     * 是否上报征信 0不上报，1上报
     */
    @ApiModelProperty(value = "是否上报征信 0不上报，1上报")
    private Integer isReport;

    @ApiModelProperty("客户角色 GuaranteeMeasuresTypeEnum")
    private String clientRole;

    @ApiModelProperty("其他")
    private String otherMessage;

}
