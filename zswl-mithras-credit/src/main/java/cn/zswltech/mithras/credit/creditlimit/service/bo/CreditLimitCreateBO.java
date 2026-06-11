package cn.zswltech.mithras.credit.creditlimit.service.bo;

import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author dingqi
 * @date 2024/9/4
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitCreateBO {
    /**
     * 业务类型 {@link CreditLimitBizTypeEnum#name()}
     */
    @NotBlank
    private String bizType;

    /**
     * 授信主体key（比如资金端是机构id，项目端是客户id）
     */
    @NotBlank
    private String grantSubjectKey;

    /**
     * 业务源key，能唯一标识一个授信（比如资金端可以是授信id，项目端可以是评审id）
     */
    @NotBlank
    private String bizSourceKey;

    /**
     * 授信总额度，单位：毫厘
     */
    @NotNull
    private Long totalLimit;

    /**
     * 担保额度，单位：毫厘
     */
    @NotNull
    private Long guaranteeLimit;

    /**
     * 信用额度，单位：毫厘
     */
    @NotNull
    private Long creditLimit;

    /**
     * 有效期-起
     */
    @NotNull
    private LocalDate effectiveDateFrom;

    /**
     * 有效期-止
     */
    @NotNull
    private LocalDate effectiveDateTo;

    /**
     * 额度是否可循环 {@link YesOrNoNumberEnum#getCode()}
     */
    @NotNull
    private Integer recyclable;

    /**
     * 非循环授信必填，授信下合同的剩余本金
     */
    private Map<Long, Long> remainingAmountMap;
}
