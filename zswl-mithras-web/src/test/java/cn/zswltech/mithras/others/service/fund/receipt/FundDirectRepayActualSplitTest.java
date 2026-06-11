package cn.zswltech.mithras.others.service.fund.receipt;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.fund.directfinancing.FundDirectRepayActualSplitRSP;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualSplitService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/28
 * @description
 */
public class FundDirectRepayActualSplitTest extends ApplicationTest {
    @Resource
    private FundDirectFinancingRepayActualSplitService repayActualSplitService;

    @Test
    public void initHistoryData() {
        repayActualSplitService.initHistoryData();
    }

    @Test
    public void splitRepayActualTest() {
        repayActualSplitService.trySplit(251008L);
    }

    @Test
    public void listSplitRepayTest() {
        List<FundDirectRepayActualSplitRSP> result = repayActualSplitService.listSplitRspByFinancingId(251008L);
        System.out.println(JSONUtil.toJsonStr(result));
    }
}
