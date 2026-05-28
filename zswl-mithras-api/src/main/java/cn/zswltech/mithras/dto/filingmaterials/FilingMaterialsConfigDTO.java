package cn.zswltech.mithras.dto.filingmaterials;

import lombok.Data;

/**
 * @author lllin
 * @date 2025-12-03
 */
@Data
public class FilingMaterialsConfigDTO{
    private String businessType;
    private String dirCode;

    private String dirName;

    private String conditionKey;
    private Long sortCode;
}
