package cn.zswltech.mithras.dto.trackEvent;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class TrackEventContractInfoRSP {

    @ApiModelProperty(value = "业务id")
    private Long bizId;
    @ApiModelProperty(value = "业务来源 -TrackTaskBizSourceEnum")
    private String bizSource;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "项目名称")
    private String projName;
    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
    private String bizType;
    @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
    private List<String> leaseType;

    @ApiModelProperty(value = "客户id")
    private Long clientId;
    @ApiModelProperty(value = "客户Name")
    private String clientName;

    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty(value = "风控经理id")
    private Long riskControlManagerId;

    @ApiModelProperty(value = "项目主办用户id")
    private Long projSponsorUserId;
    @ApiModelProperty(value = "项目主办用户名称")
    private String projSponsorUserName;
    @ApiModelProperty(value = "项目协办方用户id列表")
    private List<Long> projCosponsorUserIds;
    @ApiModelProperty(value = "项目协办方用户名称列表")
    private List<String> projCosponsorUserNames;

    @ApiModelProperty(value = "业务部门id")
    private Long bizDeptId;
    @ApiModelProperty(value = "业务部门名称")
    private String bizDeptName;
    @ApiModelProperty(value = "业务部门负责人id")
    private Long bizDeptLeaderId;
    @ApiModelProperty(value = "业务部门负责人名称")
    private String bizDeptLeaderName;
}


