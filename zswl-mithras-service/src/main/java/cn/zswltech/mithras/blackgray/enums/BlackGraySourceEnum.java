package cn.zswltech.mithras.blackgray.enums;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.decision.engine.api.common.dto.DictionaryDTO;
import cn.zswltech.mithras.service.config.enumscan.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName 黑名单来源
 * @Description
 * @Date 2023/11/29 4:56 下午
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum BlackGraySourceEnum implements PullDown {
    EXTERNAL_APPROVAL("外部名单手工申请"),
    EXTERNAL_UPLOAD("外部名单批量上传"),
    INTERNAL_APPROVAL( "内部名单手工申请"),
    INTERNAL_UPLOAD( "内部名单批量上传"),
    GROUP_RADIATION("集团关联"),
    ;
    /**
     * EXTERNAL_APPROVAL("外部名单入库申请"),
     *     EXTERNAL_UPLOAD("外部名单上传"),
     *     INTERNAL_APPROVAL( "内部名单入库申请"),
     *     INTERNAL_UPLOAD( "内部名单上传"),
     *     GROUP_RADIATION("集团纳入"),
     **/

    private final String desc;

    public static BlackGraySourceEnum of(String code) {
        for (BlackGraySourceEnum value : BlackGraySourceEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static List<DictionaryDTO> toDictionaryDTO(){
        return Arrays.stream(values()).map(t ->
                DictionaryDTO.builder()
                        .value(t.name())
                        .label(t.getDesc())
                        .build()
        ).collect(Collectors.toList());
    }

    //判断是否是外部来源数据 true 外部，false 内部
    public static boolean isExternal(String content){
       return StrUtil.equalsAny(content, EXTERNAL_APPROVAL.name(), EXTERNAL_UPLOAD.name());
    }


    @Override
    public String display() {
        return this.desc;
    }
}
