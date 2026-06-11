package cn.zswltech.mithras.unittest.mapper;

import cn.zswltech.mithras.rating.model.RatingClient;
import cn.zswltech.mithras.rating.service.RatingClientService;
import cn.zswltech.mithras.report.mapper.model.CrModifyDataSnap;
import cn.zswltech.mithras.report.service.CrModifyDataSnapService;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@ActiveProfiles("dev")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FactoryMapperTest2 {

    @Resource
    private RatingClientService ratingClientService;
    @Resource
    private ClientService clientService;
    @Resource
    private CrModifyDataSnapService crModifyDataSnapService;

    @Test
    public void test(){
        List<RatingClient> list = ratingClientService.list();
        list.forEach(System.out::println);
        System.out.println("-------------------");
        Client byId = clientService.getById(8);
        System.out.println(byId);
        System.out.println("-------------------");
        List<CrModifyDataSnap> list1 = crModifyDataSnapService.list();
        list1.forEach(System.out::println);
    }


}
