package cn.zswltech.mithras.service.service.newftp.convert;

import cn.zswltech.mithras.dto.newftp.NewFtpTreasuryBondYieldListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpTreasuryBondYieldPricingListRSP;
import cn.zswltech.mithras.service.service.newftp.excel.NewFtpTreasuryBondYieldExcelModel;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpTreasuryBondYieldConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpTreasuryBondYieldPricingDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 10年期国债收益率
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring", uses = NewFtpTypeConversionWorker.class)
public interface NewFtpTreasuryBondYieldConfigConverter {

    List<NewFtpTreasuryBondYieldConfig> excelModel2entity(List<NewFtpTreasuryBondYieldExcelModel> parse);

    @Mapping(target = "value", source = "value", qualifiedByName = "bigDecimalToInt")
    NewFtpTreasuryBondYieldConfig excelModel2entity(NewFtpTreasuryBondYieldExcelModel model);

    NewFtpTreasuryBondYieldListRSP entity2rsp(NewFtpTreasuryBondYieldConfig entity);

    List<NewFtpTreasuryBondYieldListRSP> entity2rsp(List<NewFtpTreasuryBondYieldConfig> entity);

    List<NewFtpTreasuryBondYieldPricingListRSP> pricingEntity2rsp(List<NewFtpTreasuryBondYieldPricingConfig> entity);
}
