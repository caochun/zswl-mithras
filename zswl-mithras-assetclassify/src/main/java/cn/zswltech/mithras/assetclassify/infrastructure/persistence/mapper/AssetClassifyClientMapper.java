package cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper;

import cn.zswltech.mithras.assetclassify.infrastructure.persistence.mapper.model.AssetClassifyClient;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
public interface AssetClassifyClientMapper extends CustomBaseMapper<AssetClassifyClient> {

    List<AssetClassifyClient> listByClientIds(@Param("clientIds") List<Long> clientIds);

}
