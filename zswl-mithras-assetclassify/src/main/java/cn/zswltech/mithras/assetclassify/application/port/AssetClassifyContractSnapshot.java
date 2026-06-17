package cn.zswltech.mithras.assetclassify.application.port;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetClassifyContractSnapshot {

    private Long id;

    private String bizType;

    private String contractCode;

    private String leaseType;
}
