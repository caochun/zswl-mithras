package cn.zswltech.mithras.ftp.newftp.convert;

import cn.zswltech.mithras.ftp.newftp.enums.AssetIndustryClassify;
import cn.zswltech.mithras.ftp.newftp.enums.RiskIndustryClassify;
import cn.zswltech.mithras.projectprocess.enums.newftp.RegionalClassify;

import java.util.Optional;

public class NewFtpCommonConvert {

    public static String appendFtpGuidanceKey(RiskIndustryClassify riskIndustryClassify,
                                              AssetIndustryClassify assetIndustryClassify,
                                              RegionalClassify regionalClassify) {
        return Optional.ofNullable(riskIndustryClassify).map(Enum::name).orElse(" ")
                + ":"
                + Optional.ofNullable(assetIndustryClassify).map(Enum::name).orElse(" ")
                + ":"
                + Optional.ofNullable(regionalClassify).map(Enum::name).orElse(" ");
    }
}
