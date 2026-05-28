package cn.zswltech.mithras.others.service.service;

import cn.zswltech.mithras.factory.model.RatingClientAreaIndicatorConfig;
import cn.zswltech.mithras.factory.service.RatingClientAreaIndicatorService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/3/18
 * @description
 */
public class RatingClientAreaIndicatorTest extends ApplicationTest {
    @Resource
    private RatingClientAreaIndicatorService ratingClientAreaIndicatorService;

    @Test
    public void createTest() {
        Long ratingClientId = 10086L;
        Long areaUniCode = 401201892L;
        ratingClientAreaIndicatorService.create(ratingClientId, areaUniCode, LocalDate.now().getYear());
    }
}
