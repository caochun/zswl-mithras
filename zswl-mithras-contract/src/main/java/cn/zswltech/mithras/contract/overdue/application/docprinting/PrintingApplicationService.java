package cn.zswltech.mithras.contract.overdue.application.docprinting;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.contract.overdue.application.assembler.DocPrintingAssembler;
import cn.zswltech.mithras.contract.overdue.application.command.PrintingAddCommand;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.PrintingListDto;
import cn.zswltech.mithras.contract.overdue.application.query.PrintingPageQuery;
import cn.zswltech.mithras.contract.overdue.application.service.PrintingQueryService;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.Printing;
import cn.zswltech.mithras.contract.overdue.domain.docprinting.PrintingRepository;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LongId;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.enums.common.ProcessStatus;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.CurrentUserResolver;
import cn.zswltech.mithras.service.service.JobUserResolver;
import cn.zswltech.mithras.service.service.ProcessCcNotifier;
import cn.zswltech.mithras.service.service.ProcessStartUserResolver;
import cn.zswltech.mithras.service.service.ProcessStarter;
import cn.zswltech.mithras.service.service.UserBizDeptResolver;
import cn.zswltech.mithras.service.service.UserNameResolver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    private PrintingVersionService printingVersionService;
    @Resource
    private CurrentUserResolver currentUserResolver;
    @Resource
    private ProcessStarter processStarter;
    @Resource
    private ProcessStartUserResolver processStartUserResolver;
    @Resource
    private UserBizDeptResolver userBizDeptResolver;
    @Resource
    private UserNameResolver userNameResolver;
    @Resource
    private JobUserResolver jobUserResolver;
    @Resource
    private ProcessCcNotifier processCcNotifier;

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
            Long startUserId = processStartUserResolver.processStartUserId(processId);
            if (Objects.nonNull(startUserId)) {
                printingDetailDto.setApplyDeptName(userBizDeptResolver.getBizDeptNameByUserId(startUserId));
                printingDetailDto.setApplyName(userNameResolver.sysUserId2NameSingle(startUserId));
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
        Long startUserId = Optional.ofNullable(currentUserResolver.currentUserId())
                .orElseThrow(() -> new MithrasException(ResultMsg.USER_NOT_LOGIN));
//        startProcessReq.setStartUserDeptId(Optional.ofNullable(printing.getBizDept())
//                .map(String::valueOf).orElse(null));
        String processInstanceId = processStarter.start(String.valueOf(id), startUserId,
                ProcessModelTypeEnum.DocPrintingAuditFlow.name(), "诉讼文书用印审批");
        printing.setProcessStatus(ProcessStatus.UNDER_APPROVAL.name());
        printing.setProcessId(processInstanceId);
        printingRepository.save(printing);
    }

    public void processEnd(Long id, boolean processPass, boolean processCancel, Long startUserId, String processInstanceId) {
        Printing printing = printingRepository.find(new LongId(id));
        if (processPass) {
            printing.setProcessStatus(ProcessStatus.APPROVAL_PASS.name());
            printingRepository.save(printing);
            printingVersionService.recordVersion(id, VersionTypeEnum.APPROVAL, startUserId, processInstanceId, VersionTypeConstants.NORMAL);
            // 抄送给综合部经办岗用户
            List<Long> comprehensiveDeptUserIds = jobUserResolver.jobUsers(new HashSet<>(Collections.singletonList(JobEnum.comprehensiveDept.name())));
            processCcNotifier.cc(processInstanceId, comprehensiveDeptUserIds);
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
