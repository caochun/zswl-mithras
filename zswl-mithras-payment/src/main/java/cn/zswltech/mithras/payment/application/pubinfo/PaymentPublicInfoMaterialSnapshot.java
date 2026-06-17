package cn.zswltech.mithras.payment.application.pubinfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentPublicInfoMaterialSnapshot {

    private Long belongId;

    private String ossFilename;

    private String filename;

    private String materialsType;

    private String materialSubType;
}
