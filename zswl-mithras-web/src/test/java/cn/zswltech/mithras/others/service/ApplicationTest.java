package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("uat")
public class ApplicationTest {

    protected static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

    /**
     * 测试执行开始时间
     */
    private long executeStart = 0;

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
