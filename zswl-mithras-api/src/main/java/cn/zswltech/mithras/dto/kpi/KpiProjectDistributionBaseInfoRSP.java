package cn.zswltech.mithras.dto.kpi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Data
@ApiModel("绩效考核-项目分配-基本信息-返回参数")
public class KpiProjectDistributionBaseInfoRSP {
    @ApiModelProperty("基本信息id")
    private Long id;

    @ApiModelProperty("项目分配id")
    private Long projectDistributionId;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目类型")
    private String projClassify;

    @ApiModelProperty("项目来源")
    private String projSource;

    @ApiModelProperty("合同开始时间")
    private String contractStartDate;

    @ApiModelProperty("合同终止时间")
    private String contractEndDate;

    @ApiModelProperty("利润所属部门id")
    private Long belongDeptId;

    @ApiModelProperty("利润所属部门名称")
    private String belongDeptName;

    @ApiModelProperty("合同所属部门id")
    private Long contractBelongDeptId;

    @ApiModelProperty("团队长用户id")
    private Long teamLeaderId;

    @ApiModelProperty("团队长用户名称")
    private String teamLeaderName;

    @ApiModelProperty("项目交接备注")
    private String remark;

    /**
     * 说明
     **/
    @ApiModelProperty("说明")
    private String suppleDescribe;
}
