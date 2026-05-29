package cn.zswltech.mithras.service.mapper.assetclassify;

import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
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
