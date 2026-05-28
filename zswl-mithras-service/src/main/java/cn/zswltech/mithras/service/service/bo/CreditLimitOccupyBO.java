package cn.zswltech.mithras.service.service.bo;

import cn.zswltech.mithras.service.enums.CreditLimitBizTypeEnum;
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
public class CreditLimitOccupyBO {
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
     * 业务目标key，能唯一标识一个占用授信的业务数据（比如资金端可以是融资id，项目端可以是合同id）
     */
    @NotBlank
    private String bizTargetKey;

    /**
     * 占用额度，单位：毫厘
     */
    @NotNull
    private Long amount;

    /**
     * 占用担保额度，单位：毫厘
     */
    @NotNull
    private Long guaranteeAmount;

    /**
     * 发生日期
     */
    @NotNull
    private LocalDate happenDate;
}
