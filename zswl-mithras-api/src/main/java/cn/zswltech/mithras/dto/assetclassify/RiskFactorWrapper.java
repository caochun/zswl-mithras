package cn.zswltech.mithras.dto.assetclassify;

import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 18:50
 */
@Data
public class RiskFactorWrapper {

    private Long templateId;

    private Boolean hasRisk;

    private String remark;

}
