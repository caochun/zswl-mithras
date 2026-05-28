package cn.zswltech.mithras.service.controller.finance;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceOverdueVersionApi;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationPushREQ;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueIntegrationPushRSP;
import cn.zswltech.mithras.dto.finance.overdue.FinanceOverdueVersionSubmitREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.financeoverdue.OverduePlanStatueEnum;
import cn.zswltech.mithras.service.mapper.model.finance.FinanceOverdueReportBase;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueIntegrationService;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueReportBaseService;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueSettlementService;
import cn.zswltech.mithras.service.service.finance.FinanceOverdueVersionService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName FinanceOverdueVersionController
 * @Description TODO
 * @Author jackerhe
 * @Date 2025/9/19 10:06
 * @Version 1.0
 **/
@RestController
public class FinanceOverdueVersionController implements FinanceOverdueVersionApi {

    @Resource
    private FinanceOverdueVersionService financeOverdueVersionService;
    @Resource
    private FinanceOverdueIntegrationService financeOverdueIntegrationService;
    @Resource
    private FinanceOverdueSettlementService financeOverdueSettlementService;
    @Resource
    private FinanceOverdueReportBaseService financeOverdueReportBaseService;

    @Override
    public R<Void> submit(@Valid FinanceOverdueVersionSubmitREQ req) {
        financeOverdueVersionService.submit(req);
        return R.ok();
    }

    @Override
    public R<FinanceOverdueIntegrationPushRSP> push(@Valid FinanceOverdueVersionSubmitREQ req) {
        FinanceOverdueReportBase reportBase = financeOverdueReportBaseService.getById(req.getReportId());
        if (ObjectUtil.isEmpty(reportBase)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (OverduePlanStatueEnum.CLOSE.name().equals(reportBase.getReportStatus())){
            throw new MithrasException("已关闭的计划不能推送");
        }
        FinanceOverdueIntegrationPushREQ integrationPushREQ = new FinanceOverdueIntegrationPushREQ();
        integrationPushREQ.setReportBaseId(req.getReportId());
        integrationPushREQ.setIds(req.getIntegrationRecordIds());

        FinanceOverdueIntegrationPushREQ overdueREQ = new FinanceOverdueIntegrationPushREQ();
        overdueREQ.setReportBaseId(req.getReportId());
        overdueREQ.setIds(req.getSettlementRecordIds());
        FinanceOverdueIntegrationPushRSP rsp = new FinanceOverdueIntegrationPushRSP();
        FinanceOverdueIntegrationPushRSP financeOverdueIntegrationRsp = null;
        FinanceOverdueIntegrationPushRSP financeOverdueSettlementRsp = null;
        if (CollectionUtil.isEmpty(req.getIntegrationRecordIds()) && ObjectUtil.isEmpty(req.getSettlementRecordIds())) {
            financeOverdueIntegrationRsp = financeOverdueIntegrationService.push(integrationPushREQ);
            financeOverdueSettlementRsp = financeOverdueSettlementService.push(overdueREQ);
        } else if (CollectionUtil.isNotEmpty(req.getIntegrationRecordIds()) && ObjectUtil.isEmpty(req.getSettlementRecordIds())){
            financeOverdueIntegrationRsp = financeOverdueIntegrationService.push(integrationPushREQ);
        } else if (CollectionUtil.isEmpty(req.getIntegrationRecordIds()) && ObjectUtil.isNotEmpty(req.getSettlementRecordIds())) {
            financeOverdueSettlementRsp = financeOverdueSettlementService.push(overdueREQ);
        } else {
            financeOverdueIntegrationRsp = financeOverdueIntegrationService.push(integrationPushREQ);
            financeOverdueSettlementRsp = financeOverdueSettlementService.push(overdueREQ);
        }
        financeOverdueReportBaseService.modifyReportStatus(req.getReportId());
        List<String> messages = new ArrayList<>();

        if (ObjectUtil.isNotEmpty(financeOverdueIntegrationRsp)){
            rsp.setCount(financeOverdueIntegrationRsp.getCount());
            rsp.setSuccess(financeOverdueIntegrationRsp.getSuccess());
            rsp.setFailure(financeOverdueIntegrationRsp.getFailure());
            if (CollectionUtil.isNotEmpty(financeOverdueIntegrationRsp.getMessage())) {
                messages.addAll(financeOverdueIntegrationRsp.getMessage());
            }
        }
        if (ObjectUtil.isNotEmpty(financeOverdueSettlementRsp)){
            rsp.setCount(rsp.getCount() + financeOverdueSettlementRsp.getCount());
            rsp.setSuccess(rsp.getSuccess() + financeOverdueSettlementRsp.getSuccess());
            rsp.setFailure(rsp.getFailure() + financeOverdueSettlementRsp.getFailure());
            if (CollectionUtil.isNotEmpty(financeOverdueSettlementRsp.getMessage())) {
                messages.addAll(financeOverdueSettlementRsp.getMessage());
            }
        }
        rsp.setMessage(messages);
        if (rsp.getCount() < 1) {
            throw new MithrasException("无可推送数据");
        }
        return R.ok(rsp);
    }

}
