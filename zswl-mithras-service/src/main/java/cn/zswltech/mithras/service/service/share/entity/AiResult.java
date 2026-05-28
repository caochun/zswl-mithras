package cn.zswltech.mithras.service.service.share.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: ldhu
 * @Data: 2026/3/26
 * @desc:
 */
@Setter
@Getter
public class AiResult {
    private String businessNo;// 业务编号
    private String reportAuditResult; // AI审核结论
    private String summary; // Ai审核概要
    private String approveTime; // 审批时间
    private String detail; // AI详细信息
    private String tip; // 提示
}