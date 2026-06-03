package cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjFilingMaterialsQuery {
    /**
     * 客户 id
     */
    public Long clientId;

    /**
     * 项目编号
     */
    private String projCode;

    /**
     * 项目名称
     */
    private String projName;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 业务部门 id
     */
    private Long bizDeptId;

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
     * 流程 ID
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
     * 发起时间-开始
     */
    private LocalDateTime createTimeFrom;

    /**
     * 发起时间-结束
     */
    private LocalDateTime createTimeTo;

    /**
     * 结束时间-开始
     */
    private LocalDateTime endTimeFrom;

    /**
     * 结束时间-结束
     */
    private LocalDateTime endTimeTo;
}
