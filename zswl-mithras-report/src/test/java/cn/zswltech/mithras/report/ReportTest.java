package cn.zswltech.mithras.report;

import cn.zswltech.mithras.report.handler.CrFacade;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 报送
 *
 * @author wangchuanhao
 * @date 2022/10/9 3:29 PM
 */
public class ReportTest extends ApplicationTest {

    @Resource
    private CrFacade crFacade;

    @Test
    public void test() {
        crFacade.handle(LocalDateTime.now());
    }

}
