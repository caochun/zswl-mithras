package cn.zswltech.mithras.riskcontrol.common;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordAreaType;
import cn.zswltech.mithras.dto.riskcontrol.scorecard.RiskControlScoreCordOptionGrade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/8 11:07
 */
@Component
public class RiskControlTypeConversionWorker {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    public String formatDate(String date) {
        if (date.length() < 10) {
            return null;
        } else if (date.length() == 10) {
            return date;
        } else {
            return date.substring(0, 10);
        }
    }

    public BigDecimal double2BigDecimal(double d) {
        return BigDecimal.valueOf(d);
    }

    public String toJsonString(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        return JSONUtil.toJsonStr(obj);
    }

    public List<RiskControlScoreCordAreaType> jsonStringToRiskControlScoreCordAreaList(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSONUtil.toList(jsonStr, RiskControlScoreCordAreaType.class);
    }

    public RiskControlScoreCordOptionGrade jsonStringToRiskControlScoreCordOptionGradeType(String jsonStr) {
        if (StrUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JSONUtil.toBean(jsonStr, RiskControlScoreCordOptionGrade.class);
    }

    public Integer importantReasonInt2EnumName(String r) {
        ImportantReason of = ImportantReason.of(r);
        if (of != null) {
            return of.code;
        } else {
            return null;
        }
    }

    public Integer levelInt2EnumName(String level) {
        if ("一般关联交易".equals(level)) {
            return 1;
        }
        if ("重大关联交易".equals(level)) {
            return 2;
        }
        return null;
    }

    enum ImportantReason {
        /**
         * 单笔交易金额达到五亿元以上
         */
        one("单笔交易金额达到五亿元以上", 21),
        two("单笔交易金额占集团金融板块上一年度末经审计的净资产的1%以上", 22),
        three("该笔交易发生后，与一个关联方的年度累计交易金额占集团金融板块上一年度末经审计的净资产5%以上", 23),
        ;

        private final String display;
        private final Integer code;

        private static final Map<String, ImportantReason> MAP = new HashMap<>();

        static {
            MAP.put("单笔交易金额达到五亿元以上", one);
            MAP.put("单笔交易金额占集团金融板块上一年度末经审计的净资产的1%以上", two);
            MAP.put("该笔交易发生后，与一个关联方的年度累计交易金额占集团金融板块上一年度末经审计的净资产5%以上", three);
        }

        ImportantReason(String display, Integer code) {
            this.display = display;
            this.code = code;
        }

        public String display() {
            return display;
        }

        public Integer code() {
            return this.code;
        }

        public static ImportantReason of(String display) {
            return MAP.get(display);
        }
    }
}
