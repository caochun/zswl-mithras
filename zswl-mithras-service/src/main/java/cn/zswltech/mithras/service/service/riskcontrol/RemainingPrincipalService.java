package cn.zswltech.mithras.service.service.riskcontrol;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/8/22 10:44
 */
public interface RemainingPrincipalService {

    /**
     * 计算系统内的剩余本金之和
     *
     * @param endDate 计算截止时间
     * @return 剩余本金之和
     */
    BigDecimal remainingPrincipal(LocalDate endDate);

    /**
     * 计算指定合同的剩余本金
     *
     * @param contractIds 指定的合同列表
     * @param endDate     计算截止时间
     * @return key:合同id value:剩余本金
     */
    Map<Long, BigDecimal> remainingPrincipal(Set<Long> contractIds, LocalDate endDate);

    /**
     * 计算指定合同的剩余本金
     *
     * @param contractId 合同id
     * @param endDate    计算截止时间
     * @return 剩余本金
     */
    BigDecimal remainingPrincipal(Long contractId, LocalDate endDate);

    /**
     * 返回客户维度的剩余本金
     *
     * @param dto 查询条件
     * @return key:客户id value:剩余本金
     */
    Map<Long, Long> remainingPrincipalGroupByClientId(RemainingPrincipalQueryDto dto);


    /**
     * 清理缓存
     */
    void clear();
}
