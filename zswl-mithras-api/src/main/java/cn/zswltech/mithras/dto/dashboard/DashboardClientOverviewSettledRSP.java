package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Data
public class DashboardClientOverviewSettledRSP {
    @ApiModelProperty("客户ID")
    private Long clientId;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;

    private List<OverviewSettledProjRSP> projReviewList;

    @Data
    public static class OverviewSettledProjRSP{
        @ApiModelProperty("项目id")
        private Long projReviewId;

        @ApiModelProperty("项目名称")
        private String projName;

        private List<OverviewSettledContractRSP> contractList;
    }

    @Data
    public static class OverviewSettledContractRSP{
        //ContractBaseInfoDetailRSP
        @ApiModelProperty(value = "合同编号")
        private String contractCode;
        @ApiModelProperty(value = "业务类型。租赁、保理、转租赁")
        private String bizType;
        @ApiModelProperty(value = "租赁类型。直租、回租、经营性租赁")
        private String leaseType;
        @ApiModelProperty(value = "保理类型")
        private String factoringType;
        @ApiModelProperty(value = "转让类型")
        private String zrType;
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
    }
}
