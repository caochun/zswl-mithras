package cn.zswltech.mithras.report;

import cn.zswltech.mithras.report.mapper.base.CrClientBaseMapper;
import cn.zswltech.mithras.report.mapper.base.model.CrClientBase;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * 切数据源
 *
 * @author wangchuanhao
 * @date 2022/10/8 11:46 AM
 */
public class DataSourceTest extends ApplicationTest {

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CrClientBaseMapper crClientBaseMapper;

    @Test
    public void test() {
        ApplicationTest.log.info("" + clientMapper.selectCount(Wrappers.<Client>lambdaQuery()));
        ApplicationTest.log.info("" + crClientBaseMapper.selectCount(Wrappers.<CrClientBase>lambdaQuery()));
    }

}
