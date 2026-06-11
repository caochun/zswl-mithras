package cn.zswltech.mithras.report.controller;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.report.CrAccountApi;
import cn.zswltech.mithras.dto.report.ReportChangeREQ;
import cn.zswltech.mithras.dto.report.account.AccountListREQ;
import cn.zswltech.mithras.dto.report.account.AccountListRSP;
import cn.zswltech.mithras.dto.report.account.AccountModifyREQ;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.report.auth.ReportAuthCheck;
import cn.zswltech.mithras.report.config.ReportConstants;
import cn.zswltech.mithras.report.enums.common.ApprovalStatus;
import cn.zswltech.mithras.report.enums.common.ReportPageEnum;
import cn.zswltech.mithras.report.enums.common.ReportState;
import cn.zswltech.mithras.report.flow.ICrProcessWorker;
import cn.zswltech.mithras.report.flow.ProcHelper;
import cn.zswltech.mithras.report.mapper.base.model.CrBaseModel;
import cn.zswltech.mithras.report.mapper.draft.model.CrAccountDraft;
import cn.zswltech.mithras.report.service.agg.CrAccountAggService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 征信报送-账户表接口
 *
 * @author wangchuanhao
 * @date 2023/1/11 3:55 PM
 */
@RestController
@Order(-1)
public class CrAccountController implements CrAccountApi {

    @Resource
    private CrAccountAggService crAccountAggService;
    @Autowired
    private List<ICrProcessWorker> workerList;
    @Resource
    private ProcHelper procHelper;

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.VIEW)
    public R<PageR<Map<String, DiffValue>>> list(AccountListREQ req) {
        return R.ok(crAccountAggService.list(req));
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    public R<Void> modify(AccountModifyREQ req) {
        crAccountAggService.modify(req);
        return R.ok();
    }

    @Override
    @ReportAuthCheck(type = ReportAuthCheck.AuthType.EDIT)
    @Transactional(rollbackFor = Throwable.class, transactionManager = ReportConstants.TRANSACTION_MANAGER)
    public R<Void> reportChange(ReportChangeREQ req) {
        ReportPageEnum reportPageEnum = ReportPageEnum.find(req.getModule());
        if (Objects.isNull(reportPageEnum) || !reportPageEnum.isCanEditReportFlag()) {
            throw new MithrasException(ResultMsg.UNSUPPORT_TYPE);
        }
        for (ICrProcessWorker worker : workerList) {
            if (worker.reportPageEnum().name().equals(reportPageEnum.name())) {
                IService serviceInstance = worker.getServiceInstance();
                Object o = serviceInstance.getById(req.getId());
                if (Objects.isNull(o)) {
                    throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
                }
                if (!ReportState.TO_BE_REPORT.name().equals(ReflectUtil.getFieldValue(o, "reportState"))) {
                    throw new MithrasException("非待报送状态的数据不允许修改");
                }
                if (ApprovalStatus.UNDER_APPROVAL.name().equals(ReflectUtil.getFieldValue(o, "approvalStatus")) && !procHelper.canEditData()) {
                    throw new MithrasException("该条数据处于审批中，请撤回后再修改！");
                }
                UpdateWrapper<Object> updateWrapper = Wrappers.update().set("report_flag", req.getReportFlag()).eq("id", req.getId());
                serviceInstance.update(updateWrapper);
            }
        }
        return R.ok();
    }

}
