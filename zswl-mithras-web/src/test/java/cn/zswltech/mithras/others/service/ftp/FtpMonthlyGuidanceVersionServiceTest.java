package cn.zswltech.mithras.others.service.ftp;

import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.VersionTypeEnum;
import cn.zswltech.mithras.ftp.oldftp.service.FtpMonthlyGuidanceVersionService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/11 16:05
 */
public class FtpMonthlyGuidanceVersionServiceTest extends ApplicationTest {

    @Resource
    private FtpMonthlyGuidanceVersionService versionService;

    @Test
    public void testRecordVersion(){
        versionService.recordVersion(1L, VersionTypeEnum.APPROVAL, 3L, "test", VersionTypeConstants.NORMAL);
    }

    @Test
    public void testCpmpare(){
        CommonVersionDiffRSP commonVersionDiffRSP = versionService.comparePreVersion(9318L);
        System.out.println();
    }
}
