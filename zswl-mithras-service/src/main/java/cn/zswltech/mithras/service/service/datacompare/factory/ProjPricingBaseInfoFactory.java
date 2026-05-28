package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.projpricing.baseinfo.ProjPricingBaseInfoDetailRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.projpricing.ProjPricingBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.projpricing.handler.impl.ProjPricingBaseInfoLibHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @create: 2022-08-03
 **/
@Service("projPricingBaseInfo")
public class ProjPricingBaseInfoFactory implements EditdataCompareFactory {

    @Resource
    private ProjPricingBaseInfoLibMapper libMapper;
    @Resource
    private ProjPricingBaseInfoLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        /**
         * 为了不影响其他代码而特殊处理。以下字段在detail接口null->null，在(ProjPricingBaseInfoDetailRSP lib2Rsp(o))时null -> Collections.emptyList()
         * 导致对比结果出错，就此特殊处理
         */
        ((List<ProjPricingBaseInfoDetailRSP>) rsps).forEach(o ->{
            o.setDebtorInfo(Optional.ofNullable(o.getDebtorInfo())
                    .orElse(Collections.emptyList()));
            o.setGuaranteeInfo(Optional.ofNullable(o.getGuaranteeInfo())
                    .orElse(Collections.emptyList()));
            o.setLesseeInfo(Optional.ofNullable(o.getLesseeInfo())
                    .orElse(Collections.emptyList()));
            o.setMortgagorInfo(Optional.ofNullable(o.getMortgagorInfo())
                    .orElse(Collections.emptyList()));
            o.setPledgorInfo(Optional.ofNullable(o.getPledgorInfo())
                    .orElse(Collections.emptyList()));
            o.setLeaseTypes(Optional.ofNullable(o.getLeaseTypes())
                    .orElse(Collections.emptyList()));
            o.setFactoringTypes(Optional.ofNullable(o.getFactoringTypes())
                    .orElse(Collections.emptyList()));
            o.setProjCosponsorUserIds(Optional.ofNullable(o.getProjCosponsorUserIds())
                    .orElse(Collections.emptyList()));
        });
        return new DefaultDataCompare<ProjPricingBaseInfo, ProjPricingBaseInfoLib, ProjPricingBaseInfoDetailRSP>(rsps, libMapper, handler, commonVersionMapper,BusinessModuleEnum.PROJ_PRICING.name(), version);
    }
}
