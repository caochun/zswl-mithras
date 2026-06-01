package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.service.enums.CreditLimitStatusEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitDetailBO {
    /**
     * 业务类型 {@link CreditLimitBizTypeEnum#name()}
     */
    private String bizType;

    /**
     * 授信主体key（比如资金端是机构id，项目端是客户id）
     */
    private String grantSubjectKey;

    /**
     * 业务源key，能唯一标识一个授信（比如资金端可以是授信id，项目端可以是评审id）
     */
    private String bizSourceKey;

    /**
     * 授信总额度，单位：毫厘
     */
    private Long totalLimit;

    /**
     * 担保额度，单位：毫厘
     */
    private Long guaranteeLimit;

    /**
     * 信用额度，单位：毫厘
     */
    private Long creditLimit;

    /**
     * 有效期-起
     */
    private LocalDate effectiveDateFrom;

    /**
     * 有效期-止
     */
    private LocalDate effectiveDateTo;

    /**
     * 额度是否可循环 {@link YesOrNoNumberEnum#getCode()}
     */
    private Integer recyclable;

    /**
     * 状态 {@link CreditLimitStatusEnum#name()}
     */
    private String status;

    /**
     * 已占用授信额度
     */
    private Long occupyTotalLimit;

    /**
     * 已占用担保额度
     */
    private Long occupyGuaranteeLimit;

    /**
     * 已占用信用额度
     */
    private Long occupyCreditLimit;

    /**
     * 占用明细列表
     */
    private List<CreditLimitOccupyDetailBO> occupyDetailList;

    @Data
    public static class CreditLimitOccupyDetailBO {
        /**
         * 业务目标key
         */
        private String bizTargetKey;

        /**
         * 占用额度
         */
        private Long occupyLimit;

        /**
         * 占用担保额度
         */
        private Long occupyGuaranteeLimit;

        /**
         * 占用信用额度
         */
        private Long occupyCreditLimit;
    }
}
