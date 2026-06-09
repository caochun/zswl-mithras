package cn.zswltech.mithras.creditlimit.service.bo;

import cn.zswltech.mithras.creditlimit.enums.CreditLimitBizTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitModifyBO {
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
}
