package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/12/14 15:05
 */
@ApiModel("授信详情返回体")
@Data
public class FundCreditDetailRSP {

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "授信机构")
    private Long organizationId;
    @ApiModelProperty(value = "授信机构名称")
    private String organizationName;
    @ApiModelProperty(value = "银行联号或统一社会信用代码")
    private String organizationCode;
    @ApiModelProperty(value = "授信额度")
    private Long totalCreditLimit;
    @ApiModelProperty(value = "授信编号")
    private String creditCode;
    @ApiModelProperty(value = "资金用途")
    private String fundUsage;
    @ApiModelProperty(value = "增信方式")
    private List<String> enhanceCreditMethod;
    @ApiModelProperty(value = "额度是否可循环")
    private Integer recyclable;
    @ApiModelProperty(value = "担保明细")
    private List<FundCreditGuaranteeDetailDto> guaranteeDetail;
    @ApiModelProperty(value = "授信生效时间")
    private LocalDate effectiveDateFrom;
    @ApiModelProperty(value = "授信生效时间")
    private LocalDate effectiveDateTo;
    @ApiModelProperty(value = "资金经理")
    private Long fundManager;
    @ApiModelProperty(value = "资金经理姓名")
    private String fundManagerName;
    @ApiModelProperty(value = "资金经理部门")
    private String fundManagerDept;
    @ApiModelProperty(value = "资金经理部门负责人")
    private String fundManagerDeptLeader;
    @ApiModelProperty(value = "资金经理分管领导")
    private String fundManagerDivisionLeader;
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty("已占用授信额度")
    private Long usedTotalCreditAmount;
}
