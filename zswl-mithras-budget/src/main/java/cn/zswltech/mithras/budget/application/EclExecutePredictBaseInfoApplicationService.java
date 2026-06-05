package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoAddREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoListREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoListRSP;
import cn.zswltech.mithras.dto.budget.EclExecutePredictBaseInfoRemoveREQ;

public interface EclExecutePredictBaseInfoApplicationService {

    Long createFromManual(EclExecutePredictBaseInfoAddREQ req);

    PageR<EclExecutePredictBaseInfoListRSP> pageList(EclExecutePredictBaseInfoListREQ req);

    void remove(EclExecutePredictBaseInfoRemoveREQ req);

    void calculationAsync(Long id);
}
