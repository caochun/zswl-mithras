package cn.zswltech.mithras.service.overdue.domain.litigation;

import cn.zswltech.mithras.service.convert.TypeConversionWorker;
import cn.zswltech.mithras.service.overdue.application.dto.LitigationListDto;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationCaseProgress;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationDefendant;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationRegistration;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationTrialInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/11/5 09:45
 */
@Mapper(componentModel = "spring", uses = TypeConversionWorker.class)
public interface LitigationConverter {
    @Mapping(source = "code.code", target = "code")
    @Mapping(source = "id.id", target = "id")
    @Mapping(source = "contractIds", target = "contractIds", qualifiedByName = "toJsonString")
    @Mapping(source = "contractCodes", target = "contractCodes", qualifiedByName = "toJsonString")
    LitigationRegistration entity2Po(Litigation aggregate);

    @Mapping(source = "code", target = "code.code")
    @Mapping(source = "id", target = "id.id")
    @Mapping(source = "contractIds", target = "contractIds", qualifiedByName = "jsonStringToLongList")
    @Mapping(source = "contractCodes", target = "contractCodes", qualifiedByName = "jsonStringToStringList")
    Litigation po2Entity(LitigationRegistration po);

    @Mapping(source = "id", target = "id.id")
    @Mapping(source = "lrId", target = "lrId.id")
    Defendant defendantPo2Entity(LitigationDefendant one);
    List<Defendant> defendantPo2Entity(List<LitigationDefendant> list);

    @Mapping(source = "id", target = "id.id")
    @Mapping(source = "lrId", target = "lrId.id")
    CaseProgress progressPo2Entity(LitigationCaseProgress one);
    List<CaseProgress> progressPo2Entity(List<LitigationCaseProgress> list);

    @Mapping(source = "id", target = "id.id")
    @Mapping(source = "lrId", target = "lrId.id")
    TrialInfo trialInfo2Entity(LitigationTrialInfo one);

    @Mapping(source = "id.id", target = "id")
    @Mapping(source = "lrId.id", target = "lrId")
    LitigationDefendant defendantEntity2Po(Defendant defendant);
    @Mapping(source = "id.id", target = "id")
    @Mapping(source = "lrId.id", target = "lrId")
    LitigationCaseProgress progressEntity2Po(CaseProgress progress);
    @Mapping(source = "id.id", target = "id")
    @Mapping(source = "lrId.id", target = "lrId")
    LitigationTrialInfo trialInfoEntity2Po(TrialInfo trialInfo);
}
