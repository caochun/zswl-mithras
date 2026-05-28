package cn.zswltech.mithras.service.mapper.model.filingmaterials;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjFilingMaterialsResult {

    /**
     * 业务部门 id
     */
    private Long bizDeptId;

    /**
     * 客户 id
     */
    public Long clientId;

    /**
     * 项目名称
     */
    private String projName;

    /**
     * 项目编号
     */
    private String projCode;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 风控行业分类
     */
    private String riskControlIndustryClassify;

    /**
     * 项目主办 id
     */
    private Long sponsorUserId;

    /**
     * 档案复核人 id
     */
    private Long materialsReReviewUserId;

    /**
     * 流程 id
     */
    private String processInstanceId;

    /**
     * 归档超期标识
     * @see cn.zswltech.mithras.service.enums.YesOrNoNumberEnum
     */
    private String archiveOverDueFlag;

    /**
     * 补充材料超期标识
     * @see cn.zswltech.mithras.service.enums.YesOrNoNumberEnum
     */
    private String supplementDocOverdueFlag;

    /**
     * 全流程耗时（工作日）
     */
    private Long duration;

    /**
     * 档案管理初审耗时（工作日）
     */
    private Long materialsManagerReviewDuration;

    /**
     * 档案管理复核耗时（工作日）
     */
    private Long materialsManagerReReviewDuration;

    /**
     * 发起时间
     */
    private LocalDateTime createTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 项目主办提交时间
     */
    private LocalDateTime projectHostSubmitTime;

    /**
     * 档案管理初审提交时间
     */
    private LocalDateTime archiveReviewSubmitTime;

    /**
     * 档案管理复核提交时间
     */
    private LocalDateTime archiveReReviewSubmitTime;

    /**
     * 档案管理初审退回次数
     */
    private Integer archiveReviewRejectCount;

    /**
     * 档案管理复核退回次数
     */
    private Integer archiveReReviewRejectCount;

    /**
     * 档案管理初审退回原因
     */
    private String archiveReviewRejectReason;

    /**
     * 档案管理复核退回原因
     */
    private String archiveReReviewRejectReason;
}
