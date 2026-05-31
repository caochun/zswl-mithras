package cn.zswltech.mithras.service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 天眼查 司法协助 类型 状态 枚举
 * 天眼查接口返回两个数据融合成一个字段了，产品又要求分开展示 只能尝试做解析拆分
 *
 *
 * 股权冻结|失效
 * 股权变更
 * 股权冻结|冻结
 * 一般冻结
 * 股权冻结|解除冻结
 * 股东变更
 * 冻结
 * 无效
 * 有效
 * 续冻
 * 失效
 * 完全冻结
 * 股权冻结
 * 解除冻结
 * 股权冻结|
 * 已冻结
 * 已解冻
 * 其他
 * 股权冻结|已冻结
 * 股权冻结|已解冻
 * @author wangchuanhao
 * @date 2022/6/24 2:07 PM
 */
@AllArgsConstructor
@Getter
public enum TycJudicialTypeStateEnum {

    /**
     * 已知枚举
     */
    DEFAULT(""),
    GQDJ_SX("股权冻结|失效"),
    GQBG("股权变更"),
    GQDJ_DJ("股权冻结|冻结"),
    YBDJ("一般冻结"),
    GQDJ_JCDJ("股权冻结|解除冻结"),
    GDBG("股东变更"),
    DJ("冻结"){
        @Override
        public String getType(String val) {
            return null;
        }
        @Override
        public String getState(String val) {
            return "冻结";
        }
    },
    WX("无效"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "无效";
        }
    },
    YX("有效"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "有效";
        }
    },
    XD("续冻"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "续冻";
        }
    },
    SX("失效"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "失效";
        }
    },
    WQDJ("完全冻结"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "完全冻结";
        }
    },
    GQDJ("股权冻结"),
    JCDJ("解除冻结"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "解除冻结";
        }
    },
    GQDJ_("股权冻结|"),
    YDJ("已冻结"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "已冻结";
        }
    },
    YJD("已解冻"){
        @Override
        public String getType(String val) {
            return "";
        }
        @Override
        public String getState(String val) {
            return "已解冻";
        }
    },
    QT("其他"),
    GQDJ_YDJ("股权冻结|已冻结"),
    GQDJ_YJD("股权冻结|已解冻"),
    ;

    private String val;

    private static Map<String, TycJudicialTypeStateEnum> map;

    static {
        map = Stream.of(TycJudicialTypeStateEnum.values()).collect(Collectors.toMap(TycJudicialTypeStateEnum::getVal, e -> e));
    }

    public static TycJudicialTypeStateEnum getByVal(String val) {
        // 去除空格并把中文｜转成英文|
        String tVal = isBlank(val) ? "" : val.trim().replaceAll("｜", "|");
        return map.getOrDefault(tVal, DEFAULT);
    }

    public String getType(String val) {
        if (isBlank(val)) {
            return "";
        }
        String[] ss = val.split("\\|");
        return ss.length > 0 ? ss[0] : "";
    }

    public String getState(String val) {
        if (isBlank(val)) {
            return "";
        }
        String[] ss = val.split("\\|");
        return ss.length > 1 ? ss[1] : "";
    }

    private static boolean isBlank(String val) {
        return val == null || val.trim().isEmpty();
    }

}
