package cn.zswltech.mithras.filingmaterials.model;

import cn.zswltech.mithras.filingmaterials.enums.FilingMaterialsProcessStatusEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 
 * @author lllin
 * @TableName filing_materials 资料归档
 * @date 2025-12-3
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="filing_materials")
@Data
@SponsorField(value = "userId", belongDeptField = "deptId")
public class FilingMaterials extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 客户id
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;
    /**
     * 项目/融资编号
     */
    @TableField(value = "proj_code")
    private String projCode;

    /**
     * 流程发起时间
     */
    @TableField(value = "start_date")
    private LocalDateTime startDate;

    /**
     * 首岗提交时间
     */
    @TableField(value = "first_commit_date")
    private LocalDateTime firstCommitDate;

    /**
     * 退回时间
     */
    @TableField(value = "return_date")
    private LocalDate returnDate;
    /**
     * 审批完成时间
     */
    @TableField(value = "approve_date")
    private LocalDateTime approveDate;

    /**
     * 归档类型
     */
    @TableField(value = "filing_type")
    private String filingType;
    /**
     * 流程编号
     */
    @TableField(value = "flow_id")
    private String flowId;
    /**
     * 发起方式
     */
    @TableField(value = "initiation_method")
    private String initiationMethod;
    /**
     * 审批状态
     */
    @TableField(value = "approve_status")
    private String approveStatus;
    /**
     * 是否存在担保
     */
    @TableField(value = "guarantee_flag")
    private int guaranteeFlag;
    /**
     * 最新合同审批通过版本
     */
    @TableField(value = "contract_version")
    private String contractVersion;
    /**
     * 关联id
     */
    @TableField(value = "object_id")
    private Long objectId;
    /**
     * 关联类型
     */
    @TableField(value = "object_type")
    private String objectType;
    /**
     * 资料类型
     */
    @TableField(value = "materials_desc")
    private String materialsDesc;

    /**
     * 是否归档超期
     */
    @TableField(value = "filing_overdue")
    private int filingOverdue;

    /**
     * 是否补充材料超期
     */
    @TableField(value = "supplement_filing_overdue")
    private int supplementFilingOverdue;

    /**
     * 档案管理初审最后提交时间
     */
    @TableField(value = "archive_pre_review_last_submit_time")
    private LocalDateTime archivePreReviewLastSubmitTime;
    /**
     * 档案管理复核最后提交时间
     */
    @TableField(value = "archive_review_last_submit_time")
    private LocalDateTime archiveReviewLastSubmitTime;
    /**
     * 档案管理初审退回次数
     */
    @TableField(value = "archive_pre_review_reject_count")
    private int archivePreReviewRejectCount;
    /**
     * 档案管理复核退回次数
     */
    @TableField(value = "archive_review_reject_count")
    private int archiveReviewRejectCount;
    /**
     * 档案管理初审退回原因汇总
     */
    @TableField(value = "archive_pre_review_reject_reason_sum")
    private String archivePreReviewRejectReasonSum;
    /**
     * 档案管理复核退回原因汇总
     */
    @TableField(value = "archive_review_reject_reason_sum")
    private String archiveReviewRejectReasonSum;

    /**
     * 发起人
     */
    @TableField(value = "USER_ID")
    private Long userId;
    /**
     * 发起人所属部门
     */
    @TableField(value = "DEPT_ID")
    private Long deptId;

    /**
     * 全流程耗时（工作日）
     */
    @TableField(value = "duration")
    private Long duration;

    /**
     * 档案管理初审耗时（工作日）
     */
    @TableField(value = "materials_manager_review_duration")
    private Long materialsManagerReviewDuration;

    /**
     * 档案管理复核耗时（工作日）
     */
    @TableField(value = "materials_manager_re_review_duration")
    private Long materialsManagerReReviewDuration;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public static final FilingMaterials initFilingMaterials(Long clientId,Long contractId,String projCode,
                                                            String filingType,String initiationMethod,
                                                            String contractVersion,Long objectId,String objectType){
        FilingMaterials filingMaterials = new FilingMaterials();
        filingMaterials.setClientId(clientId);
        filingMaterials.setContractId(contractId);
        filingMaterials.setProjCode(projCode);
        filingMaterials.setStartDate(LocalDateTime.now());
        filingMaterials.setApproveStatus(FilingMaterialsProcessStatusEnum.UN_SUBMIT.name());
        filingMaterials.setFilingType(filingType);
        filingMaterials.setInitiationMethod(initiationMethod);
        filingMaterials.setContractVersion(contractVersion);
        filingMaterials.setObjectId(objectId);
        filingMaterials.setObjectType(objectType);
        return filingMaterials;
    }
}