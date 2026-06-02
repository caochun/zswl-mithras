package cn.zswltech.mithras.service.util;

import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitorMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class IdGeneratorUtils {
    private final AtomicLong idSequence = new AtomicLong(0);

    @Resource
    private RiskControlOpinionMonitorMapper riskControlOpinionMonitorMapper;

    /**
     * 初始化ID序列（在Bean创建后执行）
     */
    @PostConstruct
    public void init() {
        Long currentMaxId = getCurrentMaxIdFromDatabase();
        if (currentMaxId != null) {
            idSequence.set(currentMaxId);
            log.info("IdGenerator初始化完成，当前最大ID: {}", currentMaxId);
        } else {
            // 如果表为空，从1开始
            idSequence.set(1L);
            log.info("IdGenerator初始化完成，表为空，从1开始");
        }
    }

    /**
     * 生成下一个ID（线程安全）
     */
    public Long generateNextId() {
        return idSequence.incrementAndGet();
    }

    /**
     * 批量生成多个ID
     */
    public List<Long> generateBatchIds(int count) {
        List<Long> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(generateNextId());
        }
        return ids;
    }

    /**
     * 从数据库查询当前最大ID
     */
    private Long getCurrentMaxIdFromDatabase() {
        try {
            LambdaQueryWrapper<RiskControlOpinionMonitor> wrapper =
                    new LambdaQueryWrapper<RiskControlOpinionMonitor>()
                            .select(RiskControlOpinionMonitor::getId)
                            .orderByDesc(RiskControlOpinionMonitor::getId)
                            .last("LIMIT 1");

            RiskControlOpinionMonitor record = riskControlOpinionMonitorMapper.selectOne(wrapper);
            return record != null ? record.getId() : null;

        } catch (Exception e) {
            log.error("查询最大ID失败", e);
            return null;
        }
    }

}
