package cn.zswltech.mithras.others.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.FundsDailyCostJob;
import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCost;
import cn.zswltech.mithras.service.mapper.model.monthly.FundsDailyCostMain;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2025/11/6
 * @description
 */
public class FundsDailyCostJobTest extends ApplicationTest {
    @Resource
    private FundsDailyCostJob fundsDailyCostJob;
    @Resource
    private FundsDailyCostMainService fundsDailyCostMainService;
    @Resource
    private FundsDailyCostService fundsDailyCostService;

    @Test
    public void fundsDailyCostInitJob() {
        fundsDailyCostJob.fundsDailyCostInit();
    }

    @Test
    public void fundsDailyCostFinishJob() {
        fundsDailyCostJob.fundsDailyCostMainFinishJob();
    }

    @Test
    public void importExcelData() {
        List<Map<String, Object>> dataList = ExcelUtil.getReader("/Users/dingqi/Downloads/成本计提底稿202602.xlsx").read(0, 1, 250);
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }
        List<FundsDailyCostMain> all = fundsDailyCostMainService.list();
        if (CollectionUtil.isEmpty(all)) {
            return;
        }
        LocalDate targetDate = LocalDate.of(2026, 1, 31);
        List<FundsDailyCost> updateList = new LinkedList<>();
        Map<String, FundsDailyCostMain> map = all.stream().collect(Collectors.toMap(FundsDailyCostMain::getFinancingCode, e -> e));
        for (Map<String, Object> row : dataList) {
            String financingCode = Optional.ofNullable(row.get("融资编号")).map(Object::toString).orElse(null);
            Long productDetailId = Optional.ofNullable(row.get("产品明细ID")).map(Object::toString).map(e -> {
                if (StrUtil.isBlank(e)) {
                    return null;
                } else {
                    return Long.parseLong(e);
                }
            }).orElse(null);
            BigDecimal balance = Optional.ofNullable(row.get("1月底余额")).map(Object::toString).map(BigDecimal::new).orElse(BigDecimal.ZERO);
            if (StrUtil.isBlank(financingCode)) {
                log.info("融资编号为空，跳过不处理[{}]", JSONUtil.toJsonStr(row));
                continue;
            }
            FundsDailyCostMain main = map.get(financingCode);
            if (Objects.isNull(main)) {
                log.info("没有找到融资编号{}对应的主表数据，跳过不处理[{}]", financingCode, JSONUtil.toJsonStr(row));
                continue;
            }
            List<FundsDailyCost> list = fundsDailyCostService.list(
                    Wrappers.<FundsDailyCost>lambdaQuery()
                            .eq(FundsDailyCost::getMainId, main.getId())
                            .eq(FundsDailyCost::getInterestDate, targetDate)
            );
            if (CollectionUtil.isEmpty(list)) {
                log.info("融资编号{}没有找到需要更新的明细[{}]", financingCode, JSONUtil.toJsonStr(row));
                continue;
            }
            FundsDailyCost update = null;
            if (list.size() == 1) {
                update = list.get(0);
            } else {
                // 通过产品明细ID进一步过滤
                if (Objects.isNull(productDetailId)) {
                    log.info("融资编号{}存在多个明细但没有产品明细ID，跳过不处理[{}]", financingCode, JSONUtil.toJsonStr(row));
                    continue;
                }
                for (FundsDailyCost fundsDailyCost : list) {
                    if (Objects.equals(fundsDailyCost.getFinancingProductId(), productDetailId)) {
                        update = fundsDailyCost;
                        break;
                    }
                }
            }
            if (Objects.nonNull(update)) {
                update.setEndOfPeriodInterestBalance(balance.multiply(BigDecimal.valueOf(10000)).longValue());
                updateList.add(update);
            } else {
                log.info("融资编号{}没有找到需要更新的明细[{}]", financingCode, JSONUtil.toJsonStr(row));
            }
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            fundsDailyCostService.updateBatchById(updateList);
        }
    }
}
