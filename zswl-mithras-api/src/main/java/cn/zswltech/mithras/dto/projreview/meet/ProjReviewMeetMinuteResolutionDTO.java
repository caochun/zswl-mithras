package cn.zswltech.mithras.dto.projreview.meet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 项目评审会议纪要表
 * @author vico
 * @date 2025-03-18
 */
@Data
@ApiModel("项目评审会议纪要表-决议文件")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjReviewMeetMinuteResolutionDTO {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户角色ClientRole")
    private String clientRole;

    @ApiModelProperty("业务类型ResolutionTypeRateEnum")
    private String resolutionTypeRateEnum;

    @ApiModelProperty("其他")
    private String otherMessage;

}
