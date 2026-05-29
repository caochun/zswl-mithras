package cn.zswltech.mithras.service.enums.ftp;

import cn.zswltech.mithras.service.config.enumscan.IMaterialsTypeConvert;
import cn.zswltech.mithras.common.enums.PullDown;
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
public enum FtpMonthlyMaterialsEnum implements PullDown, IMaterialsTypeConvert {

    DEFAULT("资料清单", 1),
    SUPPLEMENT("补充资料", 2),
    MEETING_FILE("会议纪要", 3),
    ;

    private final String display;

    /**
     * 排序优先级
     */
    private final int sort;

    private static final Map<String, FtpMonthlyMaterialsEnum> map;

    static {
        map = Stream.of(FtpMonthlyMaterialsEnum.values()).collect(Collectors.toMap(FtpMonthlyMaterialsEnum::name, e -> e));
    }

    public static FtpMonthlyMaterialsEnum getByName(String name) {
        return map.get(name);
    }

    @Override
    public String businessModule() {
        return "FTP_MONTHLY_GUIDANCE";
    }

    @Override
    public String display() {
        return display;
    }
}
