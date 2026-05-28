package cn.zswltech.mithras.others.service.capital;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FinanceFlowAutoWriteOffTest
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/26 18:04
 * @Version 1.0
 **/
public class FinanceFlowAutoWriteOffTest extends ApplicationTest {

    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;

    @Test
    public void writeOffNotice() {
        List<Long> financeFlowRecordIds = new ArrayList<>();
        financeFlowRecordIds.add(19785L);
        financeFlowAutoWriteOffService.writeOffNotice(financeFlowRecordIds);
    }

    //31677

    @Test
    public void financialWriteOff() {
        collectionRecordInfoService.financialWriteOff(27835L, 13957L, null, "RENT", "2023A0063-01-014", 630768300L, LocalDate.of(2024,7,15));
    }

}
