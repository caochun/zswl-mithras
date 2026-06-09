package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.Department;
import cn.zswltech.mithras.system.user.SysUserService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 部门人均值计算父类  单位:万元
 * @author: zhaozhengkang
 * @date: 2023/4/19 14:33
 */
@Slf4j
public abstract class DepartmentPerCapitalCalculator implements FinancialCloudMetricCalculator {

    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private SysUserService sysUserService;

    private static final Map<String, Long> DEPT_ID_CODE = new HashMap<>();
    private final Object lock = new Object();

    /**
     * @return 部门code
     * @see Department
     */
    protected abstract String departmentCode();

    protected abstract String totalMetricCode();

    /**
     * 新增投放规模总值
     */
    protected static final Map<String, BigDecimal> NEWLY_ADDED_INVESTMENT_SCALE = new HashMap<>();

    /**
     * 存量投放余额
     */
    protected static final Map<String, BigDecimal> STOCK_INVESTMENT_BALANCE = new HashMap<>();

    public static void clear() {
        NEWLY_ADDED_INVESTMENT_SCALE.clear();
        STOCK_INVESTMENT_BALANCE.clear();
    }


    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        if (DEPT_ID_CODE.isEmpty()) {
            synchronized (lock) {
                if (DEPT_ID_CODE.isEmpty()) {
                    Set<String> specificDept = Arrays.stream(Department.values()).map(Enum::name).collect(Collectors.toSet());
                    Map<String, Long> deptId2Code = orgDOMapper.queryAll().stream()
                            .filter(orgDO -> specificDept.contains(orgDO.getCode()))
                            .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId));
                    DEPT_ID_CODE.putAll(deptId2Code);
                }
            }
        }
        // 计算部门人数并调用子类方法进行除法计算
        int personCount = sysUserService.countUserByDeptId(DEPT_ID_CODE.get(departmentCode()));
        // 如果部门下没有人，那么将值设置成1，防止计算平均数除法出现ArithmeticException
        if (personCount == 0) {
            log.warn("部门{}下的人数为0，计算指标报错，兼容成1", departmentCode());
            personCount = 1;
        }
        BigDecimal bigDecimal = STOCK_INVESTMENT_BALANCE.get(departmentCode());
        if (bigDecimal != null) {
            return bigDecimal.divide(new BigDecimal(10000), 4, RoundingMode.HALF_UP)
                    .divide(new BigDecimal(personCount), 2, RoundingMode.HALF_UP);
        }
        return doCal(personCount);
    }

    protected abstract BigDecimal doCal(int personCount);

    public static void putNewlyAddedInvestmentScale(String departmentCode, BigDecimal value) {
        NEWLY_ADDED_INVESTMENT_SCALE.put(departmentCode, value);
    }

    public static void putStockInvestmentBalance(String departmentCode, BigDecimal value) {
        STOCK_INVESTMENT_BALANCE.put(departmentCode, value);
    }

}
