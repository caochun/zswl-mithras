package cn.zswltech.mithras.capital.application.writeoff.bo;

import cn.zswltech.mithras.third.mapper.model.FinanceFlowMatchResult;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author bigbear
 * @date 2024/9/24 15:18
 * @description 核销需要使用的状态变量，解决多线程下可能存在的问题
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteOffCommonBO {

    /**
     * 本息业务流水缓存map集合，自动核销实际上只允许一个节点执行，所以这里加上一个类状态不影响实际的使用
     */
    protected Map<LocalDate, FinanceFlowMatchResult> principalInterestCacheMap = new HashMap<>(64);

    /**
     * 待处理完的流水栈
     */
    protected Deque<FinanceFlowRecord> needInsertQueue = new LinkedBlockingDeque<>();
}
