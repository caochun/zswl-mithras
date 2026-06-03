package cn.zswltech.mithras.unittest.service;

import cn.zswltech.mithras.basedata.enums.BaseDataSpecialDateTypeEnum;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/1/10
 * @description
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BaseDataSpecialDateService.class)
public class BaseDataSpecialDateServiceTest {
    @Spy
    @InjectMocks
    BaseDataSpecialDateService baseDataSpecialDateService = new BaseDataSpecialDateService();

    @Test
    public void calculateWorkDaysTest() {
        // mock data
        BaseDataSpecialDate baseDataSpecialDate = new BaseDataSpecialDate();
        baseDataSpecialDate.setSpecialDate(LocalDate.of(2024, 1, 1));
        baseDataSpecialDate.setSpecialType(BaseDataSpecialDateTypeEnum.HOLIDAY.name());
        List<BaseDataSpecialDate> mockList = Collections.singletonList(baseDataSpecialDate);
        Mockito.doReturn(mockList).when(baseDataSpecialDateService.between(Mockito.any(), Mockito.any()));
        // test
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 5);
        long days = baseDataSpecialDateService.calculateWorkDays(from, to);
        Assertions.assertEquals(4, days);
    }
}
