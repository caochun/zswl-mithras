package cn.zswltech.mithras.others.service.util;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.util.DateUtil;
import org.junit.Test;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/9/26
 * @description
 */
public class DateUtilTest extends ApplicationTest {
    @Test
    public void isWorkdayTest() {
        LocalDate date = LocalDate.of(2022, 10, 8);
        System.out.println(DateUtil.isWorkday(date));
    }
}
