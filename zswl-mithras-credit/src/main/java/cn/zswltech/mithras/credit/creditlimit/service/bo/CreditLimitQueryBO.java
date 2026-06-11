package cn.zswltech.mithras.credit.creditlimit.service.bo;

import cn.zswltech.mithras.credit.creditlimit.enums.CreditLimitBizTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditLimitQueryBO {
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
}
