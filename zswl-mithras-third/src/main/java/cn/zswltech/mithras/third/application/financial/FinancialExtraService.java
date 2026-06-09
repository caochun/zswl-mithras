package cn.zswltech.mithras.third.application.financial;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;

/**
 * @author dingqi
 * @date 2023/12/15
 * @description
 */
public interface FinancialExtraService {
    R<String> paymentRecodeExtra(ThirdPaymentDetailREQ req);
}
