package cn.zswltech.mithras.service.service.third.financial;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.third.financial.InnerCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdMarginRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;

public interface FinancialService {

    R<String> paymentRecode(ThirdPaymentDetailREQ req);

    R<String> collectionRecode(ThirdCollectionRecordREQ req);

    //保证金回退，内扣
    R<String> backRecord(ThirdMarginRecordREQ req);

    //质保金回退
    R<String> backRecordWarranty(ThirdMarginRecordREQ req);
    void innerRecord(InnerCollectionRecordREQ req);
}
