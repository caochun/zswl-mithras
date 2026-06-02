package cn.zswltech.mithras.service.overdue.application.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowProcessApiService;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.StartProcessReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.flow.ExecutionApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.overdue.application.assembler.DocPrintingAssembler;
import cn.zswltech.mithras.contract.overdue.application.command.PrintingAddCommand;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.application.query.PrintingPageQuery;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.Printing;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.PrintingRepository;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LongId;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/4 09:38
 */
@Service
public class PrintingApplicationService {
    @Resource
    private PrintingQueryService printingQueryService;
    @Resource
    private PrintingRepository printingRepository;
    @Resource
    private DocPrintingAssembler docPrintingAssembler;
    @Resource
    private FlowProcessApiService flowProcessApiService;
    @Resource
    private PrintingVersionService printingVersionService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private FlowTaskApiService taskApiService;

    public void remove(Long id) {
        Printing printing = printingRepository.find(new LongId(id));
        if (Objects.isNull(printing)) {
            throw new MithrasException("数据不存在");
        }
        // 仅未提交的允许删除
        if (StrUtil.isNotBlank(printing.getProcessStatus()) && !Objects.equals(printing.getProcessStatus(), ProcessStatus.UN_SUBMIT.name())) {
            throw new MithrasException("仅支持删除未提交的数据");
        }
        printingRepository.remove(new LongId(id));
    }

    public PageR<PrintingListDto> pageList(PrintingPageQuery query) {
        return printingQueryService.page(query);
    }
    public Long add(PrintingAddCommand command) {
        Printing printing = docPrintingAssembler.command2Entity(command);
        printingRepository.save(printing);
        return printing.getId().getId();
    }

    public PrintingDetailDto detail(SinglePkREQ req) {
        PrintingDetailDto printingDetailDto = null;
        if (ObjectUtil.isEmpty(req.getVersion())) {
            Printing printing = printingRepository.find(new LongId(req.getId()));
            printingDetailDto = docPrintingAssembler.entity2DetailDto(printing);
            setApply(printingDetailDto, printing.getProcessId());
            return printingDetailDto;
        } else {
            Printing printingLib = printingRepository.findLib(new LongId(req.getId()), req.getVersion());
            printingDetailDto = docPrintingAssembler.entity2DetailDto(printingLib);
            setApply(printingDetailDto, printingLib.getProcessId());
            return printingDetailDto;
        }
    }


    private void setApply(PrintingDetailDto printingDetailDto, String processId) {
        if (StringUtils.isNotBlank(processId)) {
            ProcessResp processResp = taskApiService.queryProcessById(processId);
            if (processResp != null) {
                String startUserId = processResp.getStartUserId();
                OrgDO rootOrg = sysUserService.getUserDept();
//                List<OrgDO> deptDos = sysUserService.getUserDeptList();
                List<OrgDO> deptDos = sysUserService.getSpecificUserDeptList(Long.valueOf(startUserId));
                if (ObjectUtil.isNull(rootOrg)) {
                    throw new MithrasException("无机构信息");
                }
                String deptName = "";
                if (ObjectUtil.isNotEmpty(deptDos)) {
                    deptName = deptDos.get(0).getName();
                    printingDetailDto.setApplyDeptName(deptName);
                }

                String userName = SpringUtil.getBean(Id2NameService.class).sysUserId2NameSingle(Long.valueOf(startUserId));
                printingDetailDto.setApplyName(userName);
            }
        }
    }

    public void save(PrintingDetailDto dto) {
        printingRepository.find(new LongId(dto.getId()));
        Printing printing = docPrintingAssembler.detailToEntity(dto);
        printingRepository.save(printing);
    }
    public void submit(Long id) {
        Printing printing = printingRepository.find(new LongId(id));
        StartProcessReq startProcessReq = new StartProcessReq();
        startProcessReq.setBusinessKey(String.valueOf(id));
        startProcessReq.setStartUserId(Optional.ofNullable(AccountUtil.getLoginInfo())
                .map(AccountVO::getId)
                .map(String::valueOf)
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN)));
//        startProcessReq.setStartUserDeptId(Optional.ofNullable(printing.getBizDept())
//                .map(String::valueOf).orElse(null));
        startProcessReq.setModelKey(ProcessModelTypeEnum.DocPrintingAuditFlow.name());
        startProcessReq.setProcessInstanceName("诉讼文书用印审批");
        String processInstanceId = flowProcessApiService.start(startProcessReq);
        printing.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        printing.setProcessId(processInstanceId);
        printingRepository.save(printing);
    }

    public void processEnd(Long id, Integer endType, Long startUserId, String processInstanceId, String modelKey) {
        Printing printing = printingRepository.find(new LongId(id));
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        boolean processCancel = ProcessBusinessStatusEnum.CANCEL.getType().equals(endType);
        if (processPass) {
            printing.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
            printingRepository.save(printing);
            printingVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            // 抄送给综合部经办岗用户
            ExecutionProcessBaseREQ req = new ExecutionProcessBaseREQ();
            List<Long> comprehensiveDeptUserIds = sysUserService.jobUsers(new HashSet<>(Collections.singletonList(JobEnum.comprehensiveDept.name())));
            req.setProcessInstanceId(processInstanceId);
            req.setCcUserIdList(comprehensiveDeptUserIds);
            getBean(ExecutionApi.class).cc(req);
        } else if (processCancel) {
            printing.setProcessStatus(ProcessStatus.CANCEL.name());
            printingRepository.save(printing);
            printingVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
        } else {
            printing.setProcessStatus(ProcessStatus.APPROVAL_REJECT.name());
            printingRepository.save(printing);
            printingVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.INVALID);
        }

    }
}
