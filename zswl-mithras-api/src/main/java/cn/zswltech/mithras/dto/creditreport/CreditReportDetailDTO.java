package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
public class CreditReportDetailDTO {

    @ApiModelProperty("征信报告id")
    private Long id;

    @ApiModelProperty("查询编号")
    private String creditCode;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("统一社会信用代码")
    private String cscCode;

    @ApiModelProperty("中征码")
    private String zhongZhengCode;

    @ApiModelProperty("查询目的")
    private String selectGoal;


    @ApiModelProperty("关联项目编号")
    private String projCode;

    @ApiModelProperty("关联项目名称")
    private String projName;

    @ApiModelProperty("查询版本")
    private String selectVersion;


    @ApiModelProperty("信用报告封装格式")
    private String reportFormat;

    @ApiModelProperty("申请状态")
    private String auditStatus;

    @ApiModelProperty("查询状态")
    private String searchStatus;

    /**
     * 授信开始时间
     */
    @ApiModelProperty("授信开始时间")
    private LocalDate authorizationBeganDate;

    /**
     * 授信结束时间
     */
    @ApiModelProperty("授信结束时间")
    private LocalDate authorizationEndDate;

    @ApiModelProperty("客户信息列表")
    private List<CreditReportClientInfo> clientInfos;
}
