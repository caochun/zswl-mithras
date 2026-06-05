package cn.zswltech.mithras.dashboard.interfaces.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.CurrentYearBusinessPayReceiptApi;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListRSP;
import cn.zswltech.mithras.dashboard.application.boss.CurrentYearBusinessPayReceiptApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/10/23 09:52
 * @description
 */
@Slf4j
@RestController
public class CurrentYearBusinessPayReceiptController implements CurrentYearBusinessPayReceiptApi {

    @Resource
    private CurrentYearBusinessPayReceiptApplicationService currentYearBusinessPayReceiptService;

    @Override
    public R<CurrentYearBusinessPayReceiptRateListRSP> payReceiptRateList(CurrentYearBusinessPayReceiptRateListREQ req) {
        return R.ok(currentYearBusinessPayReceiptService.payReceiptRateList(req));
    }
}
