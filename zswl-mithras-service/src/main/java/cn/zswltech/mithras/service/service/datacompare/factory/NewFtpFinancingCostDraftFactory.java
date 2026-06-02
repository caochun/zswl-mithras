package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpFinancingCostPricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.lib.NewFtpFinancingCostPricingLibMapper;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpFinancingCostPricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpFinancingCostPricingLib;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @create: 2022-08-04
 **/
@Service("financingCost")
public class NewFtpFinancingCostDraftFactory implements EditdataCompareFactory {

    @Resource
    private NewFtpFinancingCostPricingLibMapper libMapper;
    @Resource
    private NewFtpFinancingCostPricingLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<NewFtpFinancingCostPricingDraft, NewFtpFinancingCostPricingLib, NewFtpFinancingCostPricingListRSP>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.NEW_FTP_GUIDANCE.name(), version);
    }
}