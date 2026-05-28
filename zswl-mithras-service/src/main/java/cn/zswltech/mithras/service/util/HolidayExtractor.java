package cn.zswltech.mithras.service.util;

/**
 * @author luyi
 */


import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * @author junke
 * https://www.gov.cn/
 * 根据中央人民政府网通知解析节假日
 */
public class HolidayExtractor {

    private static final Pattern C1 = Pattern.compile("：.*?放假");
    private static final Pattern C2 = Pattern.compile("。.*?上班");

    public static void main(String[] args) {
        String s = "一、元旦：1月1日放假，与周末连休。\n" +
                "\n" +
                "二、春节：2月10日至17日放假调休，共8天。2月4日（星期日）、2月18日（星期日）上班。鼓励各单位结合带薪年休假等制度落实，安排职工在除夕（2月9日）休息。\n" +
                "\n" +
                "三、清明节：4月4日至6日放假调休，共3天。4月7日（星期日）上班。\n" +
                "\n" +
                "四、劳动节：5月1日至5日放假调休，共5天。4月28日（星期日）、5月11日（星期六）上班。\n" +
                "\n" +
                "五、端午节：6月10日放假，与周末连休。\n" +
                "\n" +
                "六、中秋节：9月15日至17日放假调休，共3天。9月14日（星期六）上班。\n" +
                "\n" +
                "七、国庆节：10月1日至7日放假调休，共7天。9月29日（星期日）、10月12日（星期六）上班。";
        HolidayWork extract = extract(s, 2024);
        System.out.println(JSONUtil.toJsonStr(extract));
    }

    public static HolidayWork extract(String content, Integer year) {
        Matcher matcher = C1.matcher(content);
        List<String> matches = new ArrayList<>(8);
        while (matcher.find()) {
            matches.add(matcher.group(0).substring(1).trim());
        }
        List<LocalDate> holidays = holidays(matches, year);
        //
        Matcher matcher2 = C2.matcher(content);
        List<String> matches2 = new ArrayList<>(8);
        while (matcher2.find()) {
            matches2.add(matcher2.group(0).substring(1).trim());
        }
        List<LocalDate> workdays = workdays(matches2, year);
        return new HolidayWork(holidays, workdays);
    }

    private static List<LocalDate> workdays(List<String> matches, Integer yyyy) {
        List<LocalDate> result = new ArrayList<>(matches.size() * 2);
        for (String m : matches) {
            String[] parts = m.split("、");
            for (String part : parts) {
                String[] ymd = part.split("年|月|日");
                List<Integer> list = Arrays.stream(ymd).filter(NumberUtil::isNumber).map(Integer::parseInt).collect(Collectors.toList());
                if (list.size() == 3) {
                    result.add(LocalDate.of(yyyy, list.get(1), list.get(2)));
                }
                if (list.size() == 2) {
                    result.add(LocalDate.of(yyyy, list.get(0), list.get(1)));
                }
            }
        }
        return result;
    }

    private static List<LocalDate> holidays(List<String> matches, Integer yyyy) {
        List<LocalDate> result = new ArrayList<>();
        List<Pair<LocalDate, LocalDate>> pairList = new ArrayList<>();
        for (String m : matches) {
            m = m.substring(0, m.indexOf("放假"));
            LocalDate startDate = null;
            LocalDate endDate = null;
            String[] parts = m.split("至");
            String[] startYmd = parts[0].split("年|月|日");
            if (startYmd.length == 3) {
                startDate = LocalDate.of(yyyy, parseInt(startYmd[1]), parseInt(startYmd[2]));
            }
            if (startYmd.length == 2) {
                startDate = LocalDate.of(yyyy, parseInt(startYmd[0]), parseInt(startYmd[1]));
            }
            if (parts.length > 1) {
                String[] endYmd = parts[1].split("年|月|日");
                if (endYmd.length == 3) {
                    endDate = LocalDate.of(yyyy, parseInt(endYmd[1]), parseInt(endYmd[2]));
                }
                if (endYmd.length == 2) {
                    endDate = LocalDate.of(yyyy, parseInt(endYmd[0]), parseInt(endYmd[1]));
                }
                if (endYmd.length == 1) {
                    endDate = LocalDate.of(yyyy, startDate.getMonth(), parseInt(endYmd[0]));
                }
            }
            if (endDate == null) {
                endDate = startDate;
            }
            //
            pairList.add(Pair.of(startDate, endDate));
        }
        for (Pair<LocalDate, LocalDate> pair : pairList) {
            LocalDate start = pair.getKey();
            while (!start.isAfter(pair.getValue())) {
                if (start.getDayOfWeek().getValue() < 6) {
                    //正常是周六、周日的不认为是特殊节假日
                    result.add(start);
                }
                start = start.plusDays(1);
            }
        }
        return result;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HolidayWork {
        private List<LocalDate> holidays;
        private List<LocalDate> workdays;
    }

    @Data
    public static class HwInitBody implements Serializable {
        private static final long serialVersionUID = 8555793269307691116L;
        private Integer year;
        private String str;
    }

}
