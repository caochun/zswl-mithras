package cn.zswltech.mithras.assetclassify.mapper;

import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2023/1/3
 * @description
 */
public interface AssetClassifyClientMapper extends CustomBaseMapper<AssetClassifyClient> {

    List<AssetClassifyClient> listByClientIds(@Param("clientIds") List<Long> clientIds);

    List<AssetClassifyClient> listLatestByClassifyResult(@Param("classifyResult") String classifyResult,
                                                         @Param("clientIds") Set<Long> clientIds);

}
