package cn.zswltech.mithras.service.gendoc.render;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryClientInfoListRsp;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckExternalQueryDetailRsp;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.afterlease.ClientRole;
import cn.zswltech.mithras.service.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.service.service.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.Includes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/19 14:59
 */
@Slf4j
@Component
public class AfterLeaseCheckExternalQueryRender extends AbstractBasicRender<AfterLeaseCheckExternalQueryDetailRsp> {
    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    @Resource
    private FileTemplateService fileTemplateService;

    @Override
    public String render(OutputStream outputStream,
                         AfterLeaseCheckExternalQueryDetailRsp detailRsp) throws Exception {
        Map<String, Object> renderModelMap = new HashMap<>(64);
//        String templatePath = "/doc/租后管理-外部信息查询报告主模板.docx";
        renderModelMap.put(RenderParameter.bizDeptName, detailRsp.getDeptName());
        renderModelMap.put(RenderParameter.clientName, detailRsp.getClientName());
        renderModelMap.put(RenderParameter.sponsorName, detailRsp.getSponsorUserName());
        renderModelMap.put(RenderParameter.contractTotalAmount, Optional.ofNullable(detailRsp.getContractTotalAmount()).map(this::toWan).orElse(null));
        renderModelMap.put(RenderParameter.riskExposure, Optional.ofNullable(detailRsp.getRiskExposure()).map(this::toWan).orElse(null));
        renderModelMap.put(RenderParameter.industryType, detailRsp.getIndustryType());
        renderModelMap.put(RenderParameter.inspectionDate, Optional.ofNullable(detailRsp.getInspectionDate()).map(item -> item.format(df)).orElse(null));
        renderModelMap.put(RenderParameter.deadline, Optional.ofNullable(detailRsp.getDeadline()).map(item -> item.format(df)).orElse(null));
        renderModelMap.put(RenderParameter.nextRepayDate, Optional.ofNullable(detailRsp.getNextRepayDate()).map(item -> item.format(df)).orElse(null));
        renderModelMap.put(RenderParameter.nextRepayAmount, Optional.ofNullable(detailRsp.getNextRepayAmount()).map(this::toWan).orElse(null));
        renderModelMap.put(RenderParameter.preventiveMeasures, detailRsp.getPreventiveMeasures());
        renderModelMap.put(RenderParameter.queryConclusion, detailRsp.getQueryConclusion());
        int idx = 1, jdx = 1;
        for (AfterLeaseCheckExternalQueryClientInfoListRsp clientInfo : detailRsp.getClientInfos()) {
            Map<String, Object> subRenderMap = new HashMap<>(32);
            subRenderMap.put(RenderParameter.clientRoleDisplay, ClientRole.valueOf(clientInfo.getClientRole()).display());
            subRenderMap.put(RenderParameter.clientName, clientInfo.getClientName());
            subRenderMap.put(RenderParameter.creditInfo, clientInfo.getCreditInfo());
            subRenderMap.put(RenderParameter.courtInfo, clientInfo.getCourtInfo());
            subRenderMap.put(RenderParameter.refereeNetworkInfo, clientInfo.getRefereeNetworkInfo());
            subRenderMap.put(RenderParameter.zhongdengInfo, clientInfo.getZhongdengInfo());
            subRenderMap.put(RenderParameter.creditReport, clientInfo.getCreditReport());
            subRenderMap.put(RenderParameter.other, clientInfo.getOther());
            subRenderMap.put(RenderParameter.queryDateFrom, clientInfo.getQueryTimeFrom().format(df));
            subRenderMap.put(RenderParameter.queryDateTo, clientInfo.getQueryTimeTo().format(df));
            DocxRenderData docxRenderData = Includes
                    .ofStream(fileTemplateService.getTemplate("租后-租后外部信息查询报告", "租后管理-外部信息查询承租人担保人子模板.docx"))
                    .setRenderModel(subRenderMap).create();
            if (ClientRole.GUARANTEE.name().equals(clientInfo.getClientRole())) {
                renderModelMap.put("singleGuaranteeInfo" + jdx, docxRenderData);
                jdx++;
            } else {
                renderModelMap.put("singleLesseeInfo" + idx, docxRenderData);
                idx++;
            }
        }
        XWPFTemplate template = XWPFTemplate
                .compile(fileTemplateService.getTemplate("租后-租后外部信息查询报告", "租后管理-外部信息查询报告主模板.docx"))
                .render(renderModelMap);
        template.writeAndClose(outputStream);
        return detailRsp.getClientName() + "-外部查询报告" + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    private static class RenderParameter {
        public static final String bizDeptName = "bizDeptName";
        public static final String clientName = "clientName";
        public static final String sponsorName = "sponsorName";
        public static final String contractTotalAmount = "contractTotalAmount";
        public static final String riskExposure = "riskExposure";
        public static final String industryType = "industryType";
        public static final String inspectionDate = "inspectionDate";
        public static final String deadline = "deadline";
        public static final String nextRepayDate = "nextRepayDate";
        public static final String nextRepayAmount = "nextRepayAmount";
        public static final String queryDateFrom = "queryDateFrom";
        public static final String queryDateTo = "queryDateTo";
        public static final String preventiveMeasures = "preventiveMeasures";
        public static final String queryConclusion = "queryConclusion";
        public static final String clientRoleDisplay = "clientRoleDisplay";
        public static final String creditInfo = "creditInfo";
        public static final String courtInfo = "courtInfo";
        public static final String refereeNetworkInfo = "refereeNetworkInfo";
        public static final String zhongdengInfo = "zhongdengInfo";
        public static final String creditReport = "creditReport";
        public static final String other = "other";
    }
}
