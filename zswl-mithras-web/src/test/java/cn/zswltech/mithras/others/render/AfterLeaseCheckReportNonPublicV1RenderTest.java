package cn.zswltech.mithras.others.render;

import cn.hutool.core.io.FileUtil;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.application.orchestration.document.gendoc.render.afterlease.AfterLeaseCheckReportNonPublicV1Render;
import cn.zswltech.mithras.afterlease.model.NewAfterLeaseCheckPlanClient;
import cn.zswltech.mithras.application.orchestration.afterlease.AfterLeaseCheckPlanClientService;
import org.junit.Test;

import javax.annotation.Resource;
import java.io.OutputStream;

/**
 * @author dingqi
 * @date 2022/11/20
 * @description
 */
public class AfterLeaseCheckReportNonPublicV1RenderTest extends ApplicationTest {
    @Resource
    private AfterLeaseCheckPlanClientService checkPlanProjectService;
    @Resource
    private AfterLeaseCheckReportNonPublicV1Render projectReportNonPublicRender;

    @Test
    public void renderTest() throws Exception {
        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/test.docx");
        NewAfterLeaseCheckPlanClient checkPlanProject = checkPlanProjectService.getById(168L);
        projectReportNonPublicRender.render(outputStream, checkPlanProject);
    }
}
