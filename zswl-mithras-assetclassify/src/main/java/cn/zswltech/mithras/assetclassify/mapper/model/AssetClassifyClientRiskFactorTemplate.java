package cn.zswltech.mithras.assetclassify.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/9/5 11:33
 */
@Data
@TableName("asset_classify_client_risk_factor_template")
public class AssetClassifyClientRiskFactorTemplate extends BaseModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 风险因素清单类别
    @TableField("type")
    private String type;

    // 风险因素
    @TableField("risk_factor")
    private String riskFactor;

    // 是否有风险 0-否 1-是
    @TableField("has_risk")
    private Boolean hasRisk;

    // 资产五级分类优化：风险因素项目类别 operation_lease：经营租赁项目  non_shipping：非航运项目  history：历史数据
    @TableField("risk_factor_type")
    private String riskFactorType;
}
