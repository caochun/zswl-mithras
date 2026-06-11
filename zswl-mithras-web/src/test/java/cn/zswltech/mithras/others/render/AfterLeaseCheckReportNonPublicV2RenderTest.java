package cn.zswltech.mithras.others.render;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease.AfterLeaseCheckReportNonPublicV2Render;
import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.application.orchestration.afterlease.AfterLeaseCheckPlanClientService;
import org.junit.Test;

import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2023/11/16
 * @description
 */
public class AfterLeaseCheckReportNonPublicV2RenderTest extends ApplicationTest {
    @Test
    public void testRender() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/nonpublicv2.docx");
        NewAfterLeaseCheckPlanClient checkPlanClient = SpringUtil.getBean(AfterLeaseCheckPlanClientService.class).getById(8280);
        SpringUtil.getBean(AfterLeaseCheckReportNonPublicV2Render.class).render(outputStream, checkPlanClient);
    }
}
