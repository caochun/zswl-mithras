package cn.zswltech.mithras.ftp.oldftp.enums;

import cn.zswltech.mithras.foundation.metadata.IMaterialsTypeConvert;
import cn.zswltech.mithras.foundation.metadata.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ftp模块资料清单
 *
 * @author wangchuanhao
 * @date 2022/8/9 12:48 AM
 */
@AllArgsConstructor
@Getter
public enum FtpQuarterlyMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    DEFAULT("资料清单", 1),
    SUPPLEMENT("补充资料", 2),
    MEETING_FILE("会议纪要", 3),
    ;

    private final String display;

    /**
     * 排序优先级
     */
    private final int sort;

    private static final Map<String, FtpQuarterlyMaterialsEnum> map;

    static {
        map = Stream.of(FtpQuarterlyMaterialsEnum.values()).collect(Collectors.toMap(FtpQuarterlyMaterialsEnum::name, e -> e));
    }

    public static FtpQuarterlyMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "FTP_QUARTERLY_GUIDANCE";
    }

    @Override
    public String display() {
        return display;
    }
}
