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
public class CreditLimitReleaseBO {
    /**
     * 业务类型 {@link CreditLimitBizTypeEnum#name()}
     */
    @NotBlank
    private String bizType;

    /**
     * 业务目标key，能唯一标识一个占用授信的业务数据（比如资金端可以是融资id，项目端可以是合同id）
     */
    @NotBlank
    private String bizTargetKey;

    /**
     * 释放额度，单位：毫厘
     */
    @NotNull
    private Long amount;

    /**
     * 发生日期
     */
    @NotNull
    private LocalDate happenDate;
}
