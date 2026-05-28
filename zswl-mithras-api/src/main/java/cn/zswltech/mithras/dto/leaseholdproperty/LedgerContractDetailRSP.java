package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.zswltech.mithras.dto.client.client.ClientInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author yangxiong
 * @since 2023-09-19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LedgerContractDetailRSP {
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    @ApiModelProperty(value = "项目编号")
    private String projectCode;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "租赁类型")
    private List<String> leaseType;

    @ApiModelProperty(value = "合同编号")
    private Set<String> contractCode;

    @ApiModelProperty(value = "承租人")
    private List<ClientInfo> tenant;

    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryType;

    @ApiModelProperty(value = "项目主办")
    private String projectOrganizer;

    @ApiModelProperty(value = "项目协办")
    private List<String> projectCoOrganizer;

    @ApiModelProperty(value = "业务部门")
    private String businessDepartment;

    @ApiModelProperty(value = "业务部门负责人")
    private String businessDepartmentHead;

    @ApiModelProperty("关联的评审id")
    private Long projReviewId;
}
