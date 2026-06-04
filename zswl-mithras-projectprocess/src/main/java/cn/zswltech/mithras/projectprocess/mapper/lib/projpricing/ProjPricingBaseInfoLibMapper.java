package cn.zswltech.mithras.projectprocess.mapper.lib.projpricing;

import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfoLib;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 项目定价基本信息版本表 Mapper 接口
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
public interface ProjPricingBaseInfoLibMapper extends CustomBaseMapper<ProjPricingBaseInfoLib> {

    List<ProjPricingBaseInfoLib> listNewestPreviewByClientIds(@Param("clientIds") Set<Long> clientIds, @Param("region")List<String> region);
}
