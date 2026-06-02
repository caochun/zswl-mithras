package cn.zswltech.mithras.ftp.newftp.convert;

import cn.zswltech.mithras.dto.newftp.NewFtpShiborInterestRateListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpShiborInterestRatePricingListRSP;
import cn.zswltech.mithras.ftp.newftp.excel.NewFtpShiborInterestRateExcelModel;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRateConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRatePricingConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author zhaozhengkang
 * @description 1年期SHIBOR利率
 * @date 2023-05-21
 */
@Mapper(componentModel = "spring", uses = NewFtpTypeConversionWorker.class)
public interface NewFtpShiborInterestRateConfigConverter {

    List<NewFtpShiborInterestRateConfig> excelModel2entity(List<NewFtpShiborInterestRateExcelModel> parse);

    @Mapping(target = "value", source = "value", qualifiedByName = "bigDecimalToInt")
    NewFtpShiborInterestRateConfig excelModel2entity(NewFtpShiborInterestRateExcelModel parse);

    List<NewFtpShiborInterestRateListRSP> entity2ListRSP(List<NewFtpShiborInterestRateConfig> records);


    List<NewFtpShiborInterestRatePricingListRSP> pricingEntity2ListRSP(List<NewFtpShiborInterestRatePricingConfig> records);

}
