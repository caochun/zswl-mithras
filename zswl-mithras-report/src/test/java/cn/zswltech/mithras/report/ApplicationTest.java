package cn.zswltech.mithras.report;

import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.contract.gendoc.BusinessDataRepository;
import cn.zswltech.mithras.customer.mapper.model.client.CorpCommerceInfoLib;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
public class ApplicationTest {

    protected static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

    /**
     * 测试执行开始时间
     */
    private long executeStart = 0;

    @Resource
    private BusinessDataRepository businessDataRepository;

    @Before
    public void before() {
        System.out.println("测试执行开始");
        executeStart = System.currentTimeMillis();
    }


    @Test
    public void test() {
        System.out.println("单测测试");
    }

    @After
    public void after() {
        System.out.println("测试执行完成，执行时间：" + (System.currentTimeMillis() - executeStart) / 1000.0 + "s");
    }


}
