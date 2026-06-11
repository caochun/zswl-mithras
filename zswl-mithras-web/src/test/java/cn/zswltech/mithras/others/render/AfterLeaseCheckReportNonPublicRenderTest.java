//package cn.zswltech.mithras.others.render;
//
//import cn.hutool.core.io.FileUtil;
//import cn.zswltech.mithras.others.service.ApplicationTest;
//import cn.zswltech.mithras.contract.gendoc.render.AfterLeaseCheckReportNonPublicRender;
//import cn.zswltech.mithras.afterlease.mapper.model.NewAfterLeaseCheckPlanClient;
//import cn.zswltech.mithras.application.orchestration.afterlease.AfterLeaseCheckPlanClientService;
//import org.junit.Test;
//
//import javax.annotation.Resource;
//import java.io.OutputStream;
//
///**
// * @author dingqi
// * @date 2022/11/20
// * @description
// */
//public class AfterLeaseCheckReportNonPublicRenderTest extends ApplicationTest {
//    @Resource
//    private AfterLeaseCheckPlanClientService checkPlanProjectService;
//    @Resource
//    private AfterLeaseCheckReportNonPublicRender projectReportNonPublicRender;
//
//    @Test
//    public void renderTest() throws Exception {
//        OutputStream outputStream = FileUtil.getOutputStream("/Users/mockorz/test.docx");
//        NewAfterLeaseCheckPlanClient checkPlanProject = checkPlanProjectService.getById(168L);
//        projectReportNonPublicRender.render(outputStream, checkPlanProject);
//    }
//}
