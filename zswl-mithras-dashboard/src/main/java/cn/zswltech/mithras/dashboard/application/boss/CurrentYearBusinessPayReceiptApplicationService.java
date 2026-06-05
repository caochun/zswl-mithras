package cn.zswltech.mithras.dashboard.application.boss;

import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListREQ;
import cn.zswltech.mithras.dto.dashboard.boss.CurrentYearBusinessPayReceiptRateListRSP;

public interface CurrentYearBusinessPayReceiptApplicationService {

    CurrentYearBusinessPayReceiptRateListRSP payReceiptRateList(CurrentYearBusinessPayReceiptRateListREQ req);
}
