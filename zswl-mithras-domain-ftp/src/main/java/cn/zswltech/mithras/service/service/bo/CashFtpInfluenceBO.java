package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.service.enums.projpricing.ProjectManageLevelEnum;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@Data
public class CashFtpInfluenceBO {
    private Long receiptId;

    /**
     * BL 或者 ZL
     */
    private String bizType;

    /**
     * 风控行业分类
     */
    private String riskControlIndustryClassify;

    /**
     * 资产行业分类
     */
    private String assetIndustryClassify;

    /**
     * 经营地区是否在浙江
     */
    private Boolean zhejiang;

    /**
     * 地区分类
     */
    private String regionClassify;

    /**
     * 区域划分
     */
    private String regionalDivision;

    /**
     * 企业性质
     */
    private String enterpriseNature;

    /**
     * 合同期限
     */
    private Integer contractMonthCount;

    /**
     * 目标日期，取小于等于该日期的最新生效的FTP
     */
    private LocalDate targetDate;

    /**
     * 是否需要判断质押来进行FTP加点
     */
    private boolean judgePledge = false;

    /**
     * 承租人ID
     */
    private Long tenantId;

    /**
     * 担保人ID List
     */
    private List<Long> guarantorIdList;

    /**
     * FTP行业分类 {@link FtpIndustryCategoryEnum#name()}
     */
    private String ftpIndustryCategory;

    /**
     * 项目管理层级 {@link ProjectManageLevelEnum#name()}
     */
    private String projectManageLevel;

    /**
     * 是否AAA评级 {@link YesOrNoNumberEnum#getCode()}
     */
    private Integer isAAA;

    /**
     * 评估主体id
     */
    private Long evaluationSubjectId;
}
