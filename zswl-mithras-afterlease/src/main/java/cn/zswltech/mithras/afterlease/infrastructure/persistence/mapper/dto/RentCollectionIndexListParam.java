package cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.dto;

import cn.zswltech.mithras.service.mapper.dto.BaseAuthDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 租金催收首页
 *
 * @author wangchuanhao
 * @date 2022/11/17 3:38 PM
 */
@Data
public class RentCollectionIndexListParam extends BaseAuthDTO {

    private Long clientId;
    private String contractCode;
    private List<Long> contractIds;
    private Long projSponsorUserId;
    private Long bizDeptId;
    private String bizType;
    private LocalDate planCollectionDateFrom;
    private LocalDate planCollectionDateTo;
    private String contractStatus;

    /**
     * 过滤掉核销完毕的收款
     */
    private Boolean filterFinishFlag;

    /**
     * 只剩下逾期数据
     * 计划收款日期 < 今天 且 核销状态不等于核销完毕
     */
    private Boolean overdueFlag;

    /**
     * 只看未通知的 未核销完毕的 收款
     * 未通知且计划收款日期在7天内
     */
    private Boolean notNoticeYetFlag;

}
