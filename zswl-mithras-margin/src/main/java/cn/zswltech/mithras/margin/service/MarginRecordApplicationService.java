package cn.zswltech.mithras.margin.service;

import cn.zswltech.mithras.dto.margin.MarginDeductDetailRSP;
import cn.zswltech.mithras.dto.margin.MarginRecordDetailREQ;
import cn.zswltech.mithras.dto.margin.MarginRecordDetailRSP;
import cn.zswltech.mithras.dto.margin.MarginRecordListREQ;
import cn.zswltech.mithras.dto.margin.MarginRecordListRSP;

import java.util.List;

public interface MarginRecordApplicationService {

    List<MarginRecordListRSP> list(MarginRecordListREQ req);

    MarginRecordDetailRSP collectionDetail(MarginRecordDetailREQ req);

    MarginRecordDetailRSP detail(MarginRecordDetailREQ req);

    MarginDeductDetailRSP deductDetail(MarginRecordDetailREQ req);
}
