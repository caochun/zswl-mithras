package cn.zswltech.mithras.unittest.mapper;

import cn.zswltech.mithras.rating.model.RatingClient;
import cn.zswltech.mithras.rating.service.RatingClientService;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@MybatisPlusTest
@ContextConfiguration(classes = FactoryTestMybatisConfig.class)
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class FactoryMapperTest {

    @Resource
    private RatingClientService ratingClientService;

    @Test
    public void test(){
        RatingClient byId = ratingClientService.getById(1);
        System.out.println(byId);
    }


}
