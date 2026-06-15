package cn.zswltech.mithras.application.orchestration.adapter.assetclassify;

import cn.zswltech.mithras.assetclassify.application.port.AssetClassifyMarginAmountPort;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Component
public class AssetClassifyMarginAmountPortAdapter implements AssetClassifyMarginAmountPort {

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public Map<Long, Long> getAmountByReceiptIds(Collection<Long> receiptIds, LocalDate actualDate) {
        return marginBaseInfoService.getAmountByReceiptIds(receiptIds, actualDate);
    }
}
