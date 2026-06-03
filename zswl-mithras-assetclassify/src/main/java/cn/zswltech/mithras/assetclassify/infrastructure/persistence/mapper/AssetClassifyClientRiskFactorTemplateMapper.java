package cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClientRiskFactorTemplate;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
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
public interface AssetClassifyClientRiskFactorTemplateMapper extends CustomBaseMapper<AssetClassifyClientRiskFactorTemplate> {}
