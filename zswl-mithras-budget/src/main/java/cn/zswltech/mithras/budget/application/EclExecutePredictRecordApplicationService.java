package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordAddREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordImportREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordListREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordListRSP;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordModifyREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordRemoveREQ;

import java.util.Set;

public interface EclExecutePredictRecordApplicationService {

    void add(EclExecutePredictRecordAddREQ req);

    String addCheck(EclExecutePredictRecordAddREQ req);

    void importFile(EclExecutePredictRecordImportREQ req);

    Set<String> importFileCheck(EclExecutePredictRecordImportREQ req);

    void modify(EclExecutePredictRecordModifyREQ req);

    PageR<EclExecutePredictRecordListRSP> pageList(EclExecutePredictRecordListREQ req);

    void remove(EclExecutePredictRecordRemoveREQ req);
}
