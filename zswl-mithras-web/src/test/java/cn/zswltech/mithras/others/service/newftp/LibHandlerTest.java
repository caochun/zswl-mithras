package cn.zswltech.mithras.others.service.newftp;

import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpMonthlyGuidanceLibHandler;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/29 09:49
 */
public class LibHandlerTest extends ApplicationTest {

    @Resource
    private NewFtpMonthlyGuidanceLibHandler newFtpMonthlyGuidanceLibHandler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Test
    public void test() {
        CommonVersion oldVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, 198L)
                .eq(CommonVersion::getVersion, "000120230529"));
        CommonVersion newVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, 198L)
                .eq(CommonVersion::getVersion, "000220230529"));
        CommonVersionDiffBO commonVersionDiffBO = newFtpMonthlyGuidanceLibHandler.libCompareLib(newVersion, oldVersion);
        System.out.println(commonVersionDiffBO);
    }
}
