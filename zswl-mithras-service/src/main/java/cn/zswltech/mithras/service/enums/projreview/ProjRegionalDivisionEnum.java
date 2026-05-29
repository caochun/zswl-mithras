package cn.zswltech.mithras.service.enums.projreview;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.common.enums.PullDown;
import cn.zswltech.mithras.service.enums.newftp.RegionalClassify;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;

public enum ProjRegionalDivisionEnum {
    // 以下3个用于FTP行业分类为国有产业类
    ZJ_AREA("浙江地区"),
    //    PEOPLE_CONSUME("民生消费类"),
    ONE_AREA("一类地区"),

    TWO_AREA("二类地区"),

    // 以下3个用于FTP行业分类为公用事业类和民生消费类
    PUBLIC_ZJ_AREA("浙江地区"),
    PUBLIC_ENCOURAGE("鼓励支持类地区"),
    PUBLIC_OTHER("其他地区")
    ;

    public String display;

    ProjRegionalDivisionEnum(String display) {
        this.display = display;
    }

    public static ProjRegionalDivisionEnum of(String code) {
        for (ProjRegionalDivisionEnum value : ProjRegionalDivisionEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static List<ProjRegionalDivisionEnum> projRegionalDivisionEnumList() {
        return ListUtil.of(
                ProjRegionalDivisionEnum.ZJ_AREA,
                ProjRegionalDivisionEnum.ONE_AREA,
                ProjRegionalDivisionEnum.TWO_AREA
        );
    }

    public static List<ProjRegionalDivisionEnum> projRegionalDivisionPublicAndCivilEnumList() {
        return ListUtil.of(
                ProjRegionalDivisionEnum.PUBLIC_ZJ_AREA,
                ProjRegionalDivisionEnum.PUBLIC_ENCOURAGE,
                ProjRegionalDivisionEnum.PUBLIC_OTHER
        );
    }

    @JsonValue
    public String getDisplay() {
        return display;
    }

    public static RegionalClassify getProjReviewEarningsRegionalClassify(ProjRegionalDivisionEnum projRegionalDivisionEnum){
        if(projRegionalDivisionEnum == null){
            return null;
        }
        switch (projRegionalDivisionEnum){
            case ZJ_AREA:
            case PUBLIC_ZJ_AREA:
                return RegionalClassify.ZHEJIANG;
            case ONE_AREA :
            case PUBLIC_ENCOURAGE:
                return RegionalClassify.ENCOURAGE;
            case TWO_AREA:
            case PUBLIC_OTHER:
                return RegionalClassify.OTHER;
        }
        return null;
    }
}
