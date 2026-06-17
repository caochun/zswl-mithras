package cn.zswltech.mithras.assetclassify.application.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetClassifyPaymentBaseSnapshot {

    private Long id;

    private Long contractId;

    private Long receiptIdFinal;
}
