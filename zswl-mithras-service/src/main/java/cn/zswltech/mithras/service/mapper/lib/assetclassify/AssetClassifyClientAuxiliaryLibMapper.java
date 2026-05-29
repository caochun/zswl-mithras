package cn.zswltech.mithras.service.mapper.lib.assetclassify;

import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author dingqi
 * @date 2023/1/5
 * @description
 */
public interface AssetClassifyClientAuxiliaryLibMapper extends CustomBaseMapper<AssetClassifyClientAuxiliaryLib> {


    List<AssetClassifyClientAuxiliaryLib> listNewestClassifyLibByClientId(@Param("clientIds") Set<Long> clientIds);

    List<AssetClassifyClientAuxiliaryLib> listNewestClassifyLibByClientIdAndAssetClassifyId(@Param("clientIds") Set<Long> clientIds, @Param("assetClassifyId") Long assetClassifyId);


    /**
     * 查询最新的分类结果
     *
     * @return
     */
    List<AssetClassifyClientAuxiliaryLib> newestClassifyClientLib(@Param("mainId") Long mainId);
}
