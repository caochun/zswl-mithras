package cn.zswltech.mithras.associationreport.storedata;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.factory.mapper.ContractReceiptBottomMapper;
import cn.zswltech.mithras.factory.model.ContractReceiptBottom;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportCategoryEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationReportPeriodCategoryEnum;
import cn.zswltech.mithras.associationreport.DeleteDataSelector;
import cn.zswltech.mithras.associationreport.StoreDataSelector;
import cn.zswltech.mithras.associationreport.service.AssociationReportService;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2025/4/18
 * @description
 */
@Slf4j
public abstract class AbstractDataStore<T extends BasicAssociationReport> implements DataStore, InitializingBean {
    protected static final String DICT_UNKNOWN_CODE = "DICT_UNKNOWN_CODE";

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;
    @Resource
    protected AssociationReportService associationReportService;
    @Resource
    protected ContractReceiptBottomMapper contractReceiptBottomMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void storeFromExcel(String reportInstanceId, InputStream inputStream) {
        // 解析Excel得到数据
        List<T> dataList = this.parseFromExcel(inputStream);
        if (CollectionUtil.isEmpty(dataList)) {
            throw new MithrasException("没有从Excel中获取到数据，请检查");
        }
        // 数据校验
        this.check(dataList);
        // 查主表数据
        AssociationReport associationReport = associationReportService.findByReportInstanceId(reportInstanceId);
        if (Objects.isNull(associationReport)) {
            throw new MithrasException("报送主表数据不存在");
        }
        this.doValueSave(associationReport, dataList);
    }

    @Override
    public void storeFromSystemJob(String reportInstanceId) {
        // 查主表数据
        AssociationReport associationReport = associationReportService.findByReportInstanceId(reportInstanceId);
        if (Objects.isNull(associationReport)) {
            throw new MithrasException("报送主表数据不存在");
        }
        // 收集数据
        List<T> dataList = this.parseFromSystemData(associationReport);
        if (CollectionUtil.isEmpty(dataList)) {
            log.info("金融局报送【{}】没有从业务数据中收集到任何数据", this.category().getDisplay());
            return;
        }
        // 填充行号
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setRowNum(i + 1);
        }
        this.doValueSave(associationReport, dataList);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StoreDataSelector.register(this.category().name(), this);
    }

    protected abstract List<T> parseFromExcel(InputStream inputStream);

    protected List<T> parseFromSystemData(AssociationReport associationReport) {
        throw new MithrasException(String.format("%s暂不支持系统自动取数", associationReport.getReportCategoryCode()));
    }

    protected abstract void check(List<T> dataList);

    protected abstract IService<T> serviceBean();

    protected abstract AssociationReportCategoryEnum category();

    protected List<ContractReceiptBottom> listContractReceiptBottom(LocalDateTime queryDateFrom, LocalDateTime queryDateTo) {
        return contractReceiptBottomMapper.selectList(
                Wrappers.<ContractReceiptBottom>lambdaQuery()
                        .ge(BaseModel::getCreateTime, queryDateFrom)
                        .lt(BaseModel::getCreateTime, queryDateTo)
        );
    }

    protected BigDecimal parseBigDecimal(Object o) {
        if (Objects.isNull(o)) {
            return null;
        }
        String s = String.valueOf(o);
        if (!NumberUtil.isNumber(s)) {
            return null;
        }
        return new BigDecimal(s);
    }

    //判断值是否不相等
    protected boolean isNotEqualBigDecimalSum(BigDecimal target, BigDecimal... addends) {
        // 处理目标值为 null 的情况（视为 0）
        BigDecimal targetValue = target != null ? target : BigDecimal.ZERO;

        // 计算加数的和（每个加数为 null 时视为 0）
        BigDecimal sum = BigDecimal.ZERO;
        if (addends != null) {
            for (BigDecimal addend : addends) {
                BigDecimal validAddend = (addend != null) ? addend : BigDecimal.ZERO;
                sum = sum.add(validAddend);
            }
        }

        // 比较数值是否相等（compareTo 返回 0 表示数值相等，不考虑精度）
        return !(targetValue.compareTo(sum) == 0);
    }

    protected int[] ensureLastOneYearPeriod(AssociationReport current) {
        if (StrUtil.equals(current.getReportPeriodCategory(), AssociationReportPeriodCategoryEnum.MONTH.name())) {
            if (current.getReportPeriod() == 1) {
                return new int[] {current.getReportYear() - 1, 12};
            } else {
                return new int[] {current.getReportYear(), current.getReportPeriod() - 1};
            }
        } else if (StrUtil.equals(current.getReportPeriodCategory(), AssociationReportPeriodCategoryEnum.QUARTER.name())) {
            if (current.getReportPeriod() == 1) {
                return new int[] {current.getReportYear() - 1, 4};
            } else {
                return new int[] {current.getReportYear(), current.getReportPeriod() - 1};
            }
        } else {
            throw new MithrasException("该周期类型暂不支持");
        }
    }

    protected LocalDate ensureMetricDate(AssociationReport current) {
        if (StrUtil.equals(current.getReportPeriodCategory(), AssociationReportPeriodCategoryEnum.MONTH.name())) {
            return DateUtil.endOfMonth(LocalDate.of(current.getReportYear(), current.getReportPeriod(), 1));
        } else if (StrUtil.equals(current.getReportPeriodCategory(), AssociationReportPeriodCategoryEnum.QUARTER.name())) {
            return DateUtil.ensureQuarterLastDay(current.getReportYear(), current.getReportPeriod());
        } else {
            throw new MithrasException("该周期类型暂不支持");
        }
    }

    public static String generatePeriod(String periodCategory, int period, int year) {
        if (Objects.equals(AssociationReportPeriodCategoryEnum.REALTIME.name(), periodCategory)) {
            return new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        if (Objects.equals(AssociationReportPeriodCategoryEnum.MONTH.name(), periodCategory)) {
            return year + String.format("%02d", period);
        }
        if (Objects.equals(AssociationReportPeriodCategoryEnum.QUARTER.name(), periodCategory)) {
            return year + "Q" + period;
        }
        throw new MithrasException("未定义的周期类型");
    }

    private void doValueSave(AssociationReport associationReport, List<T> dataList) {
        String reportInstanceId = associationReport.getReportInstanceId();
        String period = generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());
        dataList.forEach(e -> {
            //统一社会信用代码：则取默认配置文件中的统一社会信用代码
            e.setUnifSociCredCode(zszlCreditCode);
            e.setReportInstanceId(reportInstanceId);
            e.setReportInstancePeriod(period);
            e.setBatchNo(associationReport.getBatchNo());
            e.setWriteTime(LocalDateTime.now());
            e.setOp("insert");
        });
        // 移除老数据
//        this.serviceBean().remove(Wrappers.<T>lambdaQuery().eq(T::getReportInstanceId, reportInstanceId));
        DeleteDataSelector.getInstance(this.category()).deleteByReportInstanceId(reportInstanceId);
        // 写子表
        this.serviceBean().saveBatch(dataList);
    }
}
