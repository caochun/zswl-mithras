package cn.zswltech.mithras.third.repository.tyc;

import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * mock数据类型枚举
 * 通过api类型进行转换
 *
 * @author wangchuanhao
 * @date 2022/11/19 3:45 PM
 */
@AllArgsConstructor
@Getter
public enum TycMockDataType {

    MORTGAGE_INFO(PlatformApiEnum.TYC_MORTGAGE_INFO),
    EQUITY_INFO(PlatformApiEnum.TYC_EQUITY_INFO),
    PUNISHMENT_INFO(PlatformApiEnum.TYC_PUNISHMENT_INFO),
    ABNORMAL(PlatformApiEnum.TYC_ABNORMAL),
    JUDICIAL(PlatformApiEnum.TYC_JUDICIAL),
    LAW_SUIT(PlatformApiEnum.TYC_LAW_SUIT),
    CONSUM_RESTRIC(PlatformApiEnum.TYC_CONSUMPTION_RESTRICTION),
    ZHIXING_INFO(PlatformApiEnum.TYC_ZHIXING_INFO),
    DISHONEST(PlatformApiEnum.TYC_DISHONEST),
    LAW_SUIT_DETAIL(PlatformApiEnum.TYC_LAW_SUIT_DETAIL),
    ;

    private PlatformApiEnum platformApiEnum;

    private static Map<PlatformApiEnum, TycMockDataType> map;

    static {
        map = Stream.of(TycMockDataType.values()).collect(Collectors.toMap(TycMockDataType::getPlatformApiEnum, e -> e));
    }

    public static String convert(PlatformApiEnum platformApiEnum) {
        return Optional.ofNullable(map.get(platformApiEnum)).map(TycMockDataType::name).orElse(null);
    }

}
