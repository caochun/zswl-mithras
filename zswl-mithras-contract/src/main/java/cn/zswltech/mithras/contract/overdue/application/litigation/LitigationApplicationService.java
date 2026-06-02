package cn.zswltech.mithras.contract.overdue.application.litigation;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.contract.enums.overdue.LitigationStatus;
import cn.zswltech.mithras.contract.overdue.application.assembler.LitigationAssembler;
import cn.zswltech.mithras.contract.overdue.application.command.*;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationDetailDto;
import cn.zswltech.mithras.contract.overdue.application.dto.LitigationListDto;
import cn.zswltech.mithras.contract.overdue.application.query.LitigationPageQuery;
import cn.zswltech.mithras.contract.overdue.domain.litigation.Defendant;
import cn.zswltech.mithras.contract.overdue.domain.litigation.Litigation;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LitigationRepository;
import cn.zswltech.mithras.contract.overdue.domain.litigation.LongId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 10:03
 */
@Service
public class LitigationApplicationService {

    @Resource
    private LitigationQueryService litigationQueryService;

    @Resource
    private LitigationRepository litigationRepository;

    @Resource
    private LitigationAssembler litigationAssembler;

    public PageR<LitigationListDto> page(LitigationPageQuery query) {
        return litigationQueryService.page(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long create(LitigationCreateCommand command) {
        Litigation litigation = new Litigation();
        litigation.setClientId(command.getClientId());
        litigation.setClientName(command.getClientName());
        litigationRepository.save(litigation);
        return litigation.getId().getId();
    }

    public LitigationDetailDto detail(Long id) {
        Litigation litigation = litigationRepository.find(new LongId(id));
        return litigationAssembler.entity2DetailDto(litigation);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void defendantRemove(DefendantRemoveCommand command) {
        LongId aggId = new LongId(command.getLrId());
        Litigation litigation = litigationRepository.find(aggId);
        litigation.getDefendants().removeIf(defendant -> command.getIds().contains(defendant.getId().getId()));
        litigationRepository.save(litigation);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void progressAdd(ProgressAddCommand command) {
        LongId aggId = new LongId(command.getLrId());
        Litigation litigation = litigationRepository.find(aggId);
        litigation.getCaseProgresses().add(litigationAssembler.command2Entity(command));
        litigation.setStatus(LitigationStatus.valueOf(command.getStatus()));
        litigationRepository.save(litigation);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void defendantAdd(DefendantAddCommand command) {
        LongId aggId = new LongId(command.getLrId());
        Litigation litigation = litigationRepository.find(aggId);
        Defendant defendant = litigationAssembler.command2Entity(command);
        litigation.getDefendants().add(defendant);
        litigationRepository.save(litigation);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void save(LitigationSaveCommand command) {
        LongId aggId = new LongId(command.getId());
        Litigation litigation = litigationRepository.find(aggId);
        litigation.setTrialInfo(litigationAssembler.trialInfoDto2Entity(command.getTrialInfo()));
        litigation.setContractIds(command.getContractIds());
        litigation.setContractCodes(command.getContractCodes());
        litigationRepository.save(litigation);
    }
}
