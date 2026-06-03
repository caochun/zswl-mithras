package cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundDirectFinancingFilingMaterialsResult {

    /**
     * 文件归档 id
     */
    private Long filingMaterialsId;

    /**
     * 归档类型
     */
    private String filingType;

    /**
     * 融资编号
     */
    private String financingCode;

    /**
     * 项目类别
     */
    private String projClassify;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 融资状态
     */
    private String financingStatus;

    /**
     * 起息日
     */
    private LocalDate carryInterestTime;

    /**
     * 到期日
     */
    private LocalDate endDate;

    /**
     * 资金经理id
     */
    private Long fundManagerId;

    /**
     * 审批状态
     */
    private String approveStatus;

    /**
     * 归档时间
     */
    private LocalDateTime archiveDate;
}
