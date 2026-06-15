package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.kpi.mapper.PerformanceBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.PerformanceMainInfoMapper;
import cn.zswltech.mithras.kpi.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.model.PerformanceMainInfo;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.BizDeptResolver;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/4/25 16:05
 * @description
 */
@Component
public abstract class DepartmentBaseCalculator implements FinancialCloudMetricCalculator {

    @Resource
    protected PerformanceMainInfoMapper performanceMainInfoMapper;
    @Resource
    protected PerformanceBaseInfoMapper performanceBaseInfoMapper;
    @Resource
    private BizDeptResolver bizDeptResolver;

    private static final Object lock = new Object();

    // 缓存 key: 部门id value: 部门业绩目标
    public static final Map<Long, PerformanceBaseInfo> targetCache = new ConcurrentHashMap<>();
    public static final Map<String, OrgDO> orgCache = new ConcurrentHashMap<>();

    @Override
    public abstract String metricCode();

    abstract String departmentCode();

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        // 找到所有部门指标，做缓存，提供清除接口，务必使用完成后清理，因为是放在容器内的，缓存的数据会一直存在
        init(dateTime);
        if (CollUtil.isNotEmpty(targetCache)) {
            // 找到部门
            OrgDO orgInfo = orgCache.get(departmentCode());
            // 找到部门业绩目标
            PerformanceBaseInfo performanceBaseInfo = targetCache.get(orgInfo.getId());
            if (Objects.nonNull(performanceBaseInfo)) {
                // 避免除零
                if (performanceBaseInfo.getAdvertisingAmount() == 0) {
                    // 保存
                    return BigDecimal.ZERO;
                }
                return BigDecimal.valueOf(LongUtil.null2zero(performanceBaseInfo.getAdvertisingAmount()));
            }
        }
        return BigDecimal.ZERO;
    }

    public static void clear() {
        synchronized (lock) {
            targetCache.clear();
            orgCache.clear();
        }
    }

    // 缓存初始化
    final protected void init(LocalDate dateTime) {
        if (targetCache.isEmpty()) {
            synchronized (lock) {
                if (targetCache.isEmpty()) {
                    PerformanceMainInfo mainInfo = performanceMainInfoMapper.selectOne(Wrappers.<PerformanceMainInfo>lambdaQuery()
                            .eq(PerformanceMainInfo::getYear, dateTime.getYear())
                            .orderByDesc(PerformanceMainInfo::getCreateTime)
                            // FIXME 为了兼容目标未完成的需求，临时使用，后续需要修复
                            //.eq(PerformanceMainInfo::getStatus, YesOrNoNumberEnum.YES.getCode())
                            .last(StringUtil.mysqlLimitOne()));
                    if (Objects.isNull(mainInfo)) {
                        throw MithrasException.newException("未找到" + LocalDate.now().getYear() + "年业绩目标");
                    }

                    // 去找年度目标
                    List<PerformanceBaseInfo> baseInfoList = performanceBaseInfoMapper.selectList(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                            .eq(PerformanceBaseInfo::getMainId, mainInfo.getId())
                            .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name())
                            .eq(PerformanceBaseInfo::getBusinessType, BusinessTypeEnum.DEPT_TOTAL.name())
                    );

                    if (CollUtil.isEmpty(baseInfoList)) {
                        throw MithrasException.newException("未找到" + LocalDate.now().getYear() + "年部门业绩目标");
                    }

                    // 按照部门进行分组
                    Map<Long, PerformanceBaseInfo> map = baseInfoList.stream().collect(Collectors.toMap(PerformanceBaseInfo::getBelongDeptId, Function.identity(), (a, b) -> a));
                    targetCache.putAll(map);
                }
                if (orgCache.isEmpty()) {
                    orgCache.putAll(bizDeptResolver.listBizDept().stream().collect(Collectors.toMap(OrgDO::getCode, Function.identity())));
                }
            }
        }
    }
}
