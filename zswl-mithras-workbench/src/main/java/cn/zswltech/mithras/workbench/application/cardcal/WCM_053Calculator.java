package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchCollectionAmount;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 本月已收/剩余
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_053Calculator implements CardCalculator {
    @Resource
    private WorkbenchCardCollectionPort workbenchCardCollectionPort;

    @Override
    public String metricCode() {
        return "WCM_053";
    }

    @Override
    public String calculate() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<WorkbenchCollectionAmount> collections = workbenchCardCollectionPort.listPlanCollections(start, end);
        BigDecimal received = BigDecimal.ZERO;
        BigDecimal remain = BigDecimal.ZERO;
        for (WorkbenchCollectionAmount collection : collections) {
            if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collection.getWriteOffStatus())) {
                received = received.add(new BigDecimal(LongUtil.null2zero(collection.getCollectionAmount())));
            } else {
                remain = remain.add(new BigDecimal(LongUtil.null2zero(collection.getPlanCollectionAmount()) - LongUtil.null2zero(collection.getCollectionAmount())));
            }
        }
        List<PieRes> res = new ArrayList<>();
        res.add(new PieRes("已收", received.divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString()));
        res.add(new PieRes("剩余", remain.divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString()));
        return JSON.toJSONString(res);
    }
}
