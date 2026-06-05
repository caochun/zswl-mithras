package cn.zswltech.mithras.creditreport.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.creditreport.CreditReportBaseApi;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.creditreport.*;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.creditreport.enums.CreditApplyStatusEnum;
import cn.zswltech.mithras.creditreport.mapper.model.CreditReportBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.SysUserService;
import cn.zswltech.mithras.creditreport.service.CreditReportQueryService;
import cn.zswltech.mithras.creditreport.service.CreditReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Slf4j
@RestController
public class CreditReportController implements CreditReportBaseApi {

    @Resource
    private CreditReportService creditReportService;


    @Resource
    private FlowTaskApiService flowTaskApiService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private HttpServletResponse response;

    @Resource
    private CreditReportQueryService creditReportQueryService;

    @Override
    public R<List<ClientInfo>> getClientInfo(@Param("clientName") String clientName, @Param("creditReportId") Long creditReportId) {
        //return R.ok(creditReportService.getClientInfo(clientName, creditReportId));
        return R.ok(creditReportQueryService.getClientInfo(clientName, creditReportId));
    }

    @Override
    public R<CreditReportClientAddDTO> showCreditReportByClientId(Long clientId) {
        //return R.ok(creditReportService.showCreditReportByClientId(clientId));
        return R.ok(creditReportQueryService.showCreditReportByClientId(clientId));
    }

    @Override
    public R<Void> add(CreditReportAddCmd cmd) {
        //creditReportService.add(cmd);
        //新保存
        creditReportQueryService.add(cmd);
        return R.ok();
    }

    @Override
    public R<PageR<CreditReportListDTO>> list(CreditReportListREQ req) {
        //return R.ok(creditReportService.list(req));
        return R.ok(creditReportQueryService.list(req));
    }

    @Override
    public R<CreditReportDetailDTO> detail(Long id) {
        //return R.ok(creditReportService.detail(id));
        return R.ok(creditReportQueryService.detail(id));
    }

    @Override
    public R<Void> save(CreditReportSaveCmd cmd) {
        //creditReportService.saveCreditReport(cmd);
        creditReportQueryService.modify(cmd);
        return R.ok();
    }

    @Override
    public R<List<CreditReportSubmitDTO>> submit(CreditReportSubmitCmd cmd) {
        //creditReportService.submit(cmd)
        return R.ok(creditReportQueryService.submit(cmd));
    }

    @Override
    public R<Void> delete(Long id) {
        //creditReportService.delete(id);
        creditReportQueryService.remove(id);
        return R.ok();
    }

    @Override
    public R<List<CreditSearchCompareBusinessDTO>> compareBusiness(CreditSearchCompareBusinessCmd req) {
        //提交审批前需要调用天眼查比对
        CreditReportBaseInfo creditSearchInfo = creditReportQueryService.getById(req.getCreditSearchId());
        //CreditReportDO creditSearchInfo = creditReportService.getById(req.getCreditSearchId());
        if (ObjectUtil.isNull(creditSearchInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        boolean isHistory = true;
        //流程中只允许部分岗位调用天眼查比对
        if (ObjectUtil.isNotEmpty(req.getFlowId())) {
            ProcessResp processResp = flowTaskApiService.queryProcessById(req.getFlowId());
            if (ProcessBusinessStatusEnum.RUNNING.getType().equals(processResp.getProcessStatus())
                    && sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name(), JobEnum.leaderincharge.name(), JobEnum.projmanager.name())) {
                isHistory = false;
            }
        } else {
            Long userId = AccountUtil.getLoginInfo().getId();
            //项目经理且未提交过
            isHistory = CreditApplyStatusEnum.UN_SUBMIT.name().equals(creditSearchInfo.getApplyStatus())
                    && (userId.equals(creditSearchInfo.getCreateBy()));
        }
        return R.ok(creditReportService.compareBusiness(req.getCreditSearchId(), isHistory));
    }

    @Override
    public void export(CreditReportListREQ req) {
        try {
            response.addHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("征信查询" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            creditReportQueryService.export(response.getOutputStream(), req);
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出征信查询发生未知异常", e);
            throw new MithrasException("导出征信查询发生未知异常");
        }
    }
}
