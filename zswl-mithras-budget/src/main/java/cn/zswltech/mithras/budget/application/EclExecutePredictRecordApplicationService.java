package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordAddREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordImportREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordListREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordListRSP;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordModifyREQ;
import cn.zswltech.mithras.dto.budget.EclExecutePredictRecordRemoveREQ;
import cn.zswltech.mithras.budget.mapper.model.EclExecutePredictRecord;

import java.util.List;
import java.util.Set;

public interface EclExecutePredictRecordApplicationService {

    void add(EclExecutePredictRecordAddREQ req);

    String addCheck(EclExecutePredictRecordAddREQ req);

    void importFile(EclExecutePredictRecordImportREQ req);

    Set<String> importFileCheck(EclExecutePredictRecordImportREQ req);

    void modify(EclExecutePredictRecordModifyREQ req);

    PageR<EclExecutePredictRecordListRSP> pageList(EclExecutePredictRecordListREQ req);

    void remove(EclExecutePredictRecordRemoveREQ req);

    void saveRecords(List<EclExecutePredictRecord> records);

    List<EclExecutePredictRecord> listByExecutePredictId(Long executePredictId, List<Long> recordIds);

    void updateRecords(List<EclExecutePredictRecord> records);

    void removeByExecutePredictId(Long executePredictId);
}
