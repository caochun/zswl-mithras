package cn.zswltech.mithras.dto.leaseholdproperty;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/9/3
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppraisalCompanyDetailRSP extends ListBaseRSP {
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "评估机构名称")
    private String companyName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String uscCode;

    @ApiModelProperty(value = "成立日期")
    private LocalDate establishDate;

    @ApiModelProperty(value = "营业许可证到期日期")
    private LocalDate licenseExpireDate;

    @ApiModelProperty(value = "营业许可证是否长期")
    private Integer licenseIsLongTerm;

    @ApiModelProperty(value = "业务范围")
    private String businessScope;

    @ApiModelProperty(value = "状态")
    private String recordStatus;

    @ApiModelProperty(value = "流程状态")
    private String processStatus;

    @ApiModelProperty(value = "评估机构白名单生效日期")
    private LocalDate recordEffectDate;

    @ApiModelProperty(value = "评估机构白名单到期日期")
    private LocalDate recordExpireDate;

    @ApiModelProperty(value = "出库原因")
    private String outReason;

    @ApiModelProperty(value = "评估机构池对应id")
    private Long companyId;

    @ApiModelProperty(value = "关联项目信息")
    private List<Pair<Long, String>> relatedProjectList;

    @ApiModelProperty(value = "创建人id")
    private Long createBy;
}
