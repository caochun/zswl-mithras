package cn.zswltech.mithras.collection.application.facade;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.collection.CollectionRecordDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionRecordDetailRSP;
import cn.zswltech.mithras.dto.collection.CollectionRecordListREQ;
import cn.zswltech.mithras.dto.collection.CollectionRecordListRSP;

public interface CollectionRecordInfoApplicationService {

    CollectionRecordListRSP list(CollectionRecordListREQ req);

    CollectionRecordDetailRSP detail(CollectionRecordDetailREQ req);

    R<String> addFinancialRecord();
}
