package cn.zswltech.mithras.collection.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoREQ;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoListRSP;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoRSP;
import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestREQ;
import cn.zswltech.mithras.dto.collection.CollectionPenaltyInterestRSP;
import cn.zswltech.mithras.dto.collection.PenaltyInterestListRSP;

import javax.servlet.ServletOutputStream;

public interface CollectionBaseInfoApplicationService {

    PageR<CollectionBaseInfoListRSP> list(CollectionBaseInfoREQ req);

    void exportList(CollectionBaseInfoREQ req, ServletOutputStream outputStream);

    CollectionBaseInfoRSP detail(CollectionBaseInfoDetailREQ req);

    CollectionPenaltyInterestRSP penaltyInterestDetail(CollectionBaseInfoDetailREQ req);

    PageR<PenaltyInterestListRSP> penaltyInterestList(CollectionPenaltyInterestREQ req);
}
