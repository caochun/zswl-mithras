package cn.zswltech.mithras.service.controller.contract;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.contract.ContractBaseInfoApi;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.dto.contract.ContractOperationPrepareREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.*;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ProjEstablishVagueListRSP;
import cn.zswltech.mithras.service.annotation.ContractChangeOther;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseModifyMainAuthChecker;
import cn.zswltech.mithras.service.auth.checker.contract.ContractBaseRemoveMainAuthChecker;
import cn.zswltech.mithras.service.auth.checker.implnew.CommonAddMainAuthCheckerNew;
import cn.zswltech.mithras.common.constant.GlobalConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Listener.ContractPriceChangeEvent;
import cn.zswltech.mithras.service.service.Listener.client.ClientViewAuthorityEvent;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ProjClientRoleService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractLeaseItemService;
import cn.zswltech.mithras.service.service.contract.ContractService;
import cn.zswltech.mithras.service.service.contract.script.ContractExportService;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * 合同管理-首页，基本信息表
 *
 * @author vico
 * @date 2022-08-12
 */
@RestController
@Slf4j
public class ContractBaseInfoController implements ContractBaseInfoApi {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Autowired
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    @Resource
    private ContractService contractService;

    @Resource
    private ExecutionService executionService;
    @Resource
    private ProjClientRoleService projClientRoleService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ContractLeaseItemService contractLeaseItemService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private SysUserService sysUserService;

    @DataAuthCheck(keyFieldName = "contractId", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    @Override
    public R<Void> prepareContractOperation(@Valid ContractOperationPrepareREQ req) {
        contractService.prepareContractOperation(req.getContractId(), req.getOperation());
        return R.ok();
    }

    @Override
    public R<Void> saveAdjustRemark(@Valid ContractAdjustRemarkREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        contractBaseInfo.setAdjustRemark(req.getAdjustRemark());
        contractBaseInfoMapper.updateById(contractBaseInfo);
        return R.ok();
    }

    @ContractChangeOther
    @Override
    public R<Void> saveChangeRemark(@Valid ContractChangeRemarkREQ req) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        contractBaseInfo.setChangeRemark(req.getChangeRemark());
        contractBaseInfoMapper.updateAnnotationIncludeNullById(contractBaseInfo);
        return R.ok();
    }

    @Resource
    private ContractExportService contractExportService;
    @Resource
    private HttpServletResponse response;

    @Override
    public R<List<ProjEstablishVagueListRSP>> vague(@Valid ProjEstablishVagueListREQ req) {
        Map<String, ProjEstablishVagueListRSP> projReviewVagueMap = projReviewBaseInfoService.vagueQuery(req);
        if (ObjectUtil.isEmpty(projReviewVagueMap)) {
            return R.ok();
        }
        return R.ok(new ArrayList<>(projReviewVagueMap.values()));
    }

    @Override
    @DataAuthCheck(checkerClass = CommonAddMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<ContractBaseInfoAddRSP> add(ContractBaseInfoAddREQ req) {
        ContractBaseInfoAddRSP rsp = contractBaseInfoService.add(req);
        return R.ok(rsp);
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> modify(ContractBaseInfoModifyREQ req) {
        contractBaseInfoService.modify(req);
        return R.ok();
    }

    @Override
    public R<Void> modifyLeaseItem(@Valid ContractBaseInfoModifyREQ req) {
        contractBaseInfoService.modifyLeaseItem(req);
        return R.ok();
    }

    @Override
    public R<PageR<ContractBaseInfoListRSP>> list(ContractBaseInfoListREQ req) {
        // 预处理
        if (StrUtil.isNotBlank(req.getContractCode())) {
            // 中文括号替换为英文括号
            String s = req.getContractCode().replaceAll("（", "(").replaceAll("）", ")");
            // 去掉首尾空格
            req.setContractCode(s.trim());
        }
        Page<ContractBaseInfoListRSP> data = contractBaseInfoService.list(req);
        return R.ok(PageR.of(data.getRecords(), data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseRemoveMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> remove(ContractBaseInfoRemoveREQ req) {
        contractBaseInfoService.remove(req);
        return R.ok();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public R<Void> cancel(@Valid ContractBaseInfoRemoveREQ req) {
        contractBaseInfoService.updateContractStatus(ContractStatus.INVALID, ContractProcessStatusEnum.NEW_CANCEL, req.getId());
        // 解绑租赁物
        contractLeaseItemService.unbindLeaseItem(req.getId());
        //通知变更
        ApplicationContextUtil.getApplicationContext().publishEvent(new ContractPriceChangeEvent(this, req.getId()));
        // 自动取消审批流(如果有的话)
        ProcessResp processResp = contractService.findRelatedProcess(req.getId());
        if (Objects.nonNull(processResp)) {
            ExecutionProcessBaseREQ cancelProcessReq = new ExecutionProcessBaseREQ();
            cancelProcessReq.setProcessInstanceId(processResp.getProcessInstanceId());
            cancelProcessReq.setMessage("因合同被作废，审批自动取消");
            executionService.cancelProcess(cancelProcessReq);
        }
        projClientRoleService.projContractFinish(Collections.singletonList(req.getId()));
        // 通知客户权限变更
        ApplicationContextUtil.getApplicationContext().publishEvent(
                new ClientViewAuthorityEvent(new ClientViewAuthorityEvent.ClientViewAuthorityInfo(
                        BusinessModuleEnum.CONTRACT,
                        req.getId().toString(),
                        "合同管理-作废")
                )
        );
        return R.ok();
    }

    @Override
    public R<ContractBaseInfoDetailRSP> detail(@Valid ContractBaseInfoDetailREQ req) {
        return R.ok(contractBaseInfoService.detail(req));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
//    @DataAuthCheck(keyFieldName = "id", checkerClass = ContractBaseModifyMainAuthChecker.class, businessModule = BusinessModuleEnum.CONTRACT)
    public R<Void> updateActualLeaseDate(@Valid ContractActualLeaseDateREQ req) {
        LocalDate localDate = LocalDateTimeUtil.parse(req.getActualLeaseDate(), DatePattern.NORM_DATE_PATTERN).toLocalDate();
        contractBaseInfoService.updateActualLeaseDate(localDate, req.getId());
        return R.ok();
    }

    @Override
    public void exportContract() {
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("合同统计表" + GlobalConstants.OFFICE_EXCEL_SUFFIX, StandardCharsets.UTF_8.name()));
            contractExportService.exportContracts(response.getOutputStream());
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("导出合同统计列表发生未知异常", e);
            throw new MithrasException("导出合同统计列表发生未知异常");
        }
    }

    @Override
    public R<List<ContractCompareBusinessRSP>> compareBusiness(@Valid ContractCompareBusinessREQ req) {
        //提交审批前需要调用天眼查比对
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (ObjectUtil.isNull(contractBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        boolean isHistory = true;
        //流程中只允许部分岗位调用天眼查比对
        if (ObjectUtil.isNotEmpty(req.getFlowId())) {
            ProcessResp processResp = flowTaskApiService.queryProcessById(req.getFlowId());
            if (ProcessBusinessStatusEnum.RUNNING.getType().equals(processResp.getProcessStatus()) && sysUserService.currentUserIsSpecificJob(JobEnum.yunYingGuanLi.name(), JobEnum.loanreviewpost.name(), JobEnum.projmanager.name())) {
                isHistory = false;
            }
        } else {
            Long userId = AccountUtil.getLoginInfo().getId();
            //项目经理且未提交过
            isHistory = !(ContractStatus.NEW.name().equals(contractBaseInfo.getContractStatus()) && ContractProcessStatusEnum.NEW_UNCOMMIT.name().equals(contractBaseInfo.getContractProcessStatus()) &&
                    (userId.equals(contractBaseInfo.getCreateBy()) || userId.equals(contractBaseInfo.getProjSponsorUserId())));
        }
        return R.ok(contractBaseInfoService.compareBusiness(req.getContractId(), isHistory));
    }

    @Override
    public R<Boolean> checkCombinedIrr(@Valid ContractOperationPrepareREQ req) {
        Boolean result = contractService.checkCombinedIrr(req.getContractId(), req.getOperation(), req.getCommitPrepareId());
        return R.ok(result);
    }
}
