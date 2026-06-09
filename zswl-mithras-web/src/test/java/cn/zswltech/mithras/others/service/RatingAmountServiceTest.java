package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.rating.service.RatingAmountService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/12/20
 * @description
 */
public class RatingAmountServiceTest extends ApplicationTest {
    @Resource
    private RatingAmountService ratingAmountService;

    @Test
    public void ratingAbandon() {
        ratingAmountService.ratingAbandon();
    }
}
