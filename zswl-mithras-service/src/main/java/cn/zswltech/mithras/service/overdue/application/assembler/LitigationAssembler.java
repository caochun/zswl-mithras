package cn.zswltech.mithras.service.overdue.application.assembler;

import cn.zswltech.mithras.service.overdue.application.command.DefendantAddCommand;
import cn.zswltech.mithras.service.overdue.application.command.ProgressAddCommand;
import cn.zswltech.mithras.service.overdue.application.dto.*;
import cn.zswltech.mithras.service.overdue.domain.litigation.CaseProgress;
import cn.zswltech.mithras.service.overdue.domain.litigation.Defendant;
import cn.zswltech.mithras.service.overdue.domain.litigation.Litigation;
import cn.zswltech.mithras.service.overdue.domain.litigation.TrialInfo;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationRegistration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/31 11:00
 */
@Mapper(componentModel = "spring")
public interface LitigationAssembler {

    LitigationListDto po2ListDto(LitigationRegistration record);
    List<LitigationListDto> po2ListDto(List<LitigationRegistration> records);

    @Mapping(target = "id", source = "id.id")
    LitigationDetailDto entity2DetailDto(Litigation litigation);

    @Mapping(target = "id", source = "id.id")
    LitigationDefendantDto defendantEntity2Dto(Defendant defendant);
    List<LitigationDefendantDto> defendantEntity2Dto(List<Defendant> defendants);

    @Mapping(target = "id", source = "id.id")
    LitigationCaseProgressDto progressEntity2Dto(CaseProgress caseProgress);
    List<LitigationCaseProgressDto> progressEntity2Dto(List<CaseProgress> caseProgresses);

    @Mapping(target = "id", source = "id.id")
    @Mapping(target = "lrId", source = "lrId.id")
    LitigationTrialInfoDto trialInfoEntity2Dto(TrialInfo trialInfo);

    @Mapping(target = "lrId.id", source = "lrId")
    Defendant command2Entity(DefendantAddCommand command);

    @Mapping(target = "lrId.id", source = "lrId")
    CaseProgress command2Entity(ProgressAddCommand command);


    @Mapping(source = "id", target = "id.id")
    @Mapping(source = "lrId", target = "lrId.id")
    TrialInfo trialInfoDto2Entity(LitigationTrialInfoDto trialInfo);
}
