package cn.zswltech.mithras.blackgray.enums;

import cn.zswltech.decision.engine.api.common.dto.DictionaryDTO;
import cn.zswltech.mithras.common.enums.PullDown;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BlackgrayBusinessENUM
 * @Description
 * @Date 2023/11/29 4:56 下午
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum BlackGrayTypeEnum implements PullDown {
    GRAY_LIST(6, "灰名单", 1, "六个月"),
    BLACK_LIST(12,  "黑名单", 2, "十二个月");

    private final int month;
    private final String desc;
    private final int num;
    private final String monthName;

    public static BlackGrayTypeEnum of(String code) {
        for (BlackGrayTypeEnum value : BlackGrayTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static BlackGrayTypeEnum jkOf(String jkCode) {
        if ("GRAY_LIST".equals(jkCode)) {
            return GRAY_LIST;
        }
        if ("BLACK_LIST".equals(jkCode)) {
            return BLACK_LIST;
        }
        if ("CONCERN_LIST".equals(jkCode)) {
            return null;
        }
        return null;
    }

    public static BlackGrayTypeEnum ofName(String code) {
        for (BlackGrayTypeEnum value : BlackGrayTypeEnum.values()) {
            if (value.getDesc().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public static Integer name2Num(String code) {
        for (BlackGrayTypeEnum value : BlackGrayTypeEnum.values()) {
            if (value.name().equals(code)) {
                return value.num;
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

    public static Date getPlanOutboundTime(Date warehouseTime, BlackGrayTypeEnum blackGrayType){
        if (warehouseTime == null){
            return null;
        }
        if(blackGrayType == null ){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(warehouseTime);
            calendar.add(Calendar.MONTH, 36);
            return calendar.getTime();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(warehouseTime);
        calendar.add(Calendar.MONTH, blackGrayType.getMonth());
        return calendar.getTime();
    }

    @Override
    public String display() {
        return this.desc;
    }
}
