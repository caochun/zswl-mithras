package cn.zswltech.mithras.riskcontrol.exposure;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/2/13 14:29
 */
@Data
@Accessors(chain = true)
public class RemainingPrincipalQueryDto {
    private Set<Long> clientIds;
    private Set<Long> contractIds;
    private LocalDate endDate;
}
