package cn.zswltech.mithras.others.render;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.gendoc.render.afterlease.AfterLeaseCheckReportPublicV2Render;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.service.service.afterlese.AfterLeaseCheckPlanClientService;
import org.junit.Test;

import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
public class AfterLeaseCheckReportPublicV2RenderTest extends ApplicationTest {
    @Test
    public void testRender() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/publicv2.xlsx");
        NewAfterLeaseCheckPlanClient checkPlanClient = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).getById(8280L);
        SpringUtil.getBean(AfterLeaseCheckReportPublicV2Render.class).render(outputStream, checkPlanClient);
    }
}
