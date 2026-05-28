package cn.zswltech.mithras.dto.fileledger;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 项目端归档资料查询条件
 *
 * @author gxy
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjSideArchivedMaterialsQueryREQ extends PageReq {

    @ApiModelProperty("客户id")
    public Long clientId;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty("合同编号")
    private String contractCode;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("档案复核人id")
    private Long materialsReReviewUserId;

    @ApiModelProperty("流程ID")
    private String processInstanceId;

    @ApiModelProperty("归档超期标识")
    private String archiveOverDueFlag;

    @ApiModelProperty("补充材料超期标识")
    private String supplementDocOverdueFlag;

    @ApiModelProperty(value = "发起时间-开始")
    private LocalDate createTimeFrom;

    @ApiModelProperty(value = "发起时间-结束")
    private LocalDate createTimeTo;

    @ApiModelProperty("结束时间-开始")
    private LocalDate endTimeFrom;

    @ApiModelProperty("结束时间-结束")
    private LocalDate endTimeTo;
}
