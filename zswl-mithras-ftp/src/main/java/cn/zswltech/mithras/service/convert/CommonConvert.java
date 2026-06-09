package cn.zswltech.mithras.service.convert;

import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.TreeSelectRSP;
import cn.zswltech.mithras.ftp.newftp.enums.AssetIndustryClassify;
import cn.zswltech.mithras.projectprocess.enums.newftp.RegionalClassify;
import cn.zswltech.mithras.ftp.newftp.enums.RiskIndustryClassify;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.IndustryType;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

/**
 * @author dingqi
 * @date 2022/9/16
 * @description
 */
public class CommonConvert {
    public static <T> Page<T> toMybatisPlusPage(PageReq pageReq) {
        return new Page<>(pageReq.getPage(), pageReq.getPageSize());
    }

    public static TreeSelectRSP toTreeSelectRSP(IndustryType industryType) {
        TreeSelectRSP treeSelectRSP = new TreeSelectRSP();
        treeSelectRSP.setId(industryType.getId());
        treeSelectRSP.setParentId(industryType.getParentId());
        treeSelectRSP.setLabel(industryType.getDisplay());
        treeSelectRSP.setValue(industryType.getCode());
        treeSelectRSP.setChildren(new LinkedList<>());
        return treeSelectRSP;
    }

    public static FtpIndustryCategoryEnum toFtpIndustryCategory(String riskControlIndustryClassify) {
        // 风控行业分类转FTP行业分类
        if (Objects.equals(riskControlIndustryClassify, RiskControlIndustryClassify.PUBLIC_UTILITIES.name())) {
            return FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES;
        } else if (CharSequenceUtil.equalsAny(riskControlIndustryClassify, RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())) {
            return FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION;
        } else {
            return null;
        }
    }

    public static String appendFtpGuidanceKey(RiskIndustryClassify riskIndustryClassify, AssetIndustryClassify assetIndustryClassify, RegionalClassify regionalClassify) {
        return Optional.ofNullable(riskIndustryClassify).map(Enum::name).orElse(" ") + ":" + Optional.ofNullable(assetIndustryClassify).map(Enum::name).orElse(" ") + ":" + Optional.ofNullable(regionalClassify).map(Enum::name).orElse(" ");
    }
}
