package cn.zswltech.mithras.dto.groupcreditreview;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;

import java.time.LocalDate;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
@ApiModel("集团授信评审基本信息表列表-请求体")
public class GroupCreditReviewListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("业务部门")
    private Long bizDeptId;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("评审状态")
    private String groupCreditReviewStatus;

    @ApiModelProperty("审批状态")
    private String groupCreditReviewProcessStatus;

    @ApiModelProperty("创建时间从")
    private LocalDate createFrom;

    @ApiModelProperty("创建时间到")
    private LocalDate createTo;

    @ApiModelProperty("更新时间从")
    private LocalDate updateFrom;

    @ApiModelProperty("更新时间到")
    private LocalDate updateTo;

    @ApiModelProperty("申报授信金额从")
    private Long creditAmountFrom;

    @ApiModelProperty("申报授信金额到")
    private Long creditAmountTo;

}
