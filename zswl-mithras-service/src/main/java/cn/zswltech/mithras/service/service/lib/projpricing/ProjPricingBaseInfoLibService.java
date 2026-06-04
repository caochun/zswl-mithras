package cn.zswltech.mithras.projectprocess.service.lib.projpricing;

import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @author: jackerhe
 * @date: 2022/8/2 10:39 上午
 **/
public interface ProjPricingBaseInfoLibService extends IService<ProjPricingBaseInfoLib> {

    ProjPricingBaseInfo getOldEdition(Long projId);

    ProjPricingBaseInfoDetailRSP detail(Long id, String version);

    ProjPricingBaseInfoLib getByOriginIdAndVersion(Long originId, String version);

    List<ProjPricingBaseInfoLib> listNewestReviews();

    ProjPricingBaseInfoLib getEffectLatestOne(Long originId);
}
