package cn.zswltech.mithras.service.controller.dashboard.boss;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.boss.CurrentYearBusinessPayReceiptApi;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListRSP;
import cn.zswltech.mithras.service.service.dashboard.boss.CurrentYearBusinessPayReceiptService;
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
    private CurrentYearBusinessPayReceiptService currentYearBusinessPayReceiptService;

    @Override
    public R<CurrentYearBusinessPayReceiptRateListRSP> payReceiptRateList(CurrentYearBusinessPayReceiptRateListREQ req) {
        return R.ok(currentYearBusinessPayReceiptService.payReceiptRateList(req));
    }
}
