package cn.zswltech.mithras.service.overdue.infrastructure.repository;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.contract.overdue.domain.litigation.*;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.Diff;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.DiffType;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.EntityDiff;
import cn.zswltech.mithras.contract.overdue.domain.share.diff.ListDiff;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.LitigationCaseProgressDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.LitigationDefendantDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.LitigationRegistrationDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.LitigationTrialInfoDao;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.LitigationCaseProgress;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.LitigationDefendant;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.LitigationRegistration;
import cn.zswltech.mithras.contract.overdue.infrastructure.dao.model.LitigationTrialInfo;
import cn.zswltech.mithras.service.service.Id2NameService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/21 17:10
 */
@Component
public class LitigationRepositoryImpl extends LitigationRepository {

    @Resource
    private LitigationRegistrationDao litigationRegistrationDao;
    @Resource
    private LitigationDefendantDao litigationDefendantDao;
    @Resource
    private LitigationCaseProgressDao litigationCaseProgressDao;
    @Resource
    private LitigationTrialInfoDao litigationTrialInfoDao;
    @Resource
    private LitigationConverter litigationConverter;
    @Resource
    private Id2NameService id2NameService;


    public LitigationRepositoryImpl() {
        super(Litigation.class);
    }

    @Override
    protected LongId onInsert(Litigation aggregate) {
        LitigationRegistration latestOne = litigationRegistrationDao.getOne(
                Wrappers.<LitigationRegistration>lambdaQuery()
                        .ge(LitigationRegistration::getCreateTime, LocalDate.now())
                        .orderByDesc(BaseModel::getCreateTime).last("limit 1"));
        LitigationCode code = latestOne == null ? new LitigationCode() : new LitigationCode(latestOne.getCode()).nextCode();
        aggregate.setCode(code);
        LitigationRegistration po = litigationConverter.entity2Po(aggregate);
        litigationRegistrationDao.save(po);
        return new LongId(po.getId());
    }

    @Override
    protected Litigation onSelect(LongId aggregateId) {
        List<Defendant> defendant = litigationConverter.defendantPo2Entity(
                litigationDefendantDao.list(Wrappers.<LitigationDefendant>lambdaQuery()
                        .eq(LitigationDefendant::getLrId, aggregateId.getId())));
        List<CaseProgress> progress = litigationConverter.progressPo2Entity(
                litigationCaseProgressDao.list(Wrappers.<LitigationCaseProgress>lambdaQuery()
                        .eq(LitigationCaseProgress::getLrId, aggregateId.getId())
                        .orderByDesc(BaseModel::getCreateTime)));

        Map<Long, String> userNames = id2NameService.sysUserId2Name(progress.stream().map(CaseProgress::getCreateBy).collect(Collectors.toSet()));
        progress.forEach(item -> item.setProcessPerson(userNames.get(item.getCreateBy())));
        TrialInfo trialInfo = litigationConverter.trialInfo2Entity(
                litigationTrialInfoDao.getOne(Wrappers.<LitigationTrialInfo>lambdaQuery()
                        .eq(LitigationTrialInfo::getLrId, aggregateId.getId()).last("limit 1")));
        Litigation res = litigationConverter.po2Entity(litigationRegistrationDao.getById(aggregateId.getId()));
        res.setTrialInfo(trialInfo);
        res.setDefendants(defendant);
        res.setCaseProgresses(progress);
        return res;
    }

    @Override
    protected void onUpdate(Litigation aggregate, EntityDiff diff) {
        if (diff.isSelfModified()) {
            LitigationRegistration po = litigationConverter.entity2Po(aggregate);
            litigationRegistrationDao.updateById(po);
        }
        Diff defendantsDiff = diff.getDiff("defendants");
        if (defendantsDiff instanceof ListDiff) {
            ListDiff diffList = (ListDiff) defendantsDiff;
            for (Diff itemDiff : diffList) {
                if (itemDiff.getType() == DiffType.Removed) {
                    Defendant defendant = (Defendant) itemDiff.getOldValue();
                    litigationDefendantDao.removeById(defendant.getBizId().getId());
                }
                if (itemDiff.getType() == DiffType.Added) {
                    Defendant defendant = (Defendant) itemDiff.getNewValue();
                    LitigationDefendant defendantPo = litigationConverter.defendantEntity2Po(defendant);
                    defendantPo.setLrId(aggregate.getBizId().getId());
                    litigationDefendantDao.save(defendantPo);
                }
                if (itemDiff.getType() == DiffType.Modified) {
                    Defendant defendant = (Defendant) itemDiff.getNewValue();
                    LitigationDefendant defendantPo = litigationConverter.defendantEntity2Po(defendant);
                    litigationDefendantDao.updateById(defendantPo);
                }
            }
        }
        Diff trialInfoDiff = diff.getDiff("trialInfo");
        if (trialInfoDiff != null) {
            if (trialInfoDiff.getType() == DiffType.Removed) {
                TrialInfo trialInfo = (TrialInfo) trialInfoDiff.getOldValue();
                litigationTrialInfoDao.removeById(trialInfo.getBizId().getId());
            }
            if (trialInfoDiff.getType() == DiffType.Added) {
                TrialInfo trialInfo = (TrialInfo) trialInfoDiff.getNewValue();
                LitigationTrialInfo trialInfoPo = litigationConverter.trialInfoEntity2Po(trialInfo);
                trialInfoPo.setLrId(aggregate.getBizId().getId());
                litigationTrialInfoDao.save(trialInfoPo);
            }
            if (trialInfoDiff.getType() == DiffType.Modified) {
                TrialInfo trialInfo = (TrialInfo) trialInfoDiff.getNewValue();
                LitigationTrialInfo trialInfoPo = litigationConverter.trialInfoEntity2Po(trialInfo);
                litigationTrialInfoDao.updateById(trialInfoPo);
            }
        }
        Diff progressesDiff = diff.getDiff("caseProgresses");
        if (progressesDiff instanceof ListDiff) {
            ListDiff diffList = (ListDiff) progressesDiff;
            for (Diff itemDiff : diffList) {
                if (itemDiff.getType() == DiffType.Removed) {
                    CaseProgress progress = (CaseProgress) itemDiff.getOldValue();
                    litigationCaseProgressDao.removeById(progress.getBizId().getId());
                }
                if (itemDiff.getType() == DiffType.Added) {
                    CaseProgress progress = (CaseProgress) itemDiff.getNewValue();
                    LitigationCaseProgress progressPo = litigationConverter.progressEntity2Po(progress);
                    progressPo.setLrId(aggregate.getBizId().getId());
                    litigationCaseProgressDao.save(progressPo);
                }
                if (itemDiff.getType() == DiffType.Modified) {
                    CaseProgress progress = (CaseProgress) itemDiff.getNewValue();
                    LitigationCaseProgress progressPo = litigationConverter.progressEntity2Po(progress);
                    litigationCaseProgressDao.updateById(progressPo);
                }
            }
        }
    }

    @Override
    protected void onDelete(LongId aggregateId) {
        litigationDefendantDao.remove(Wrappers.<LitigationDefendant>lambdaQuery().eq(LitigationDefendant::getLrId, aggregateId.getId()));
        litigationCaseProgressDao.remove(Wrappers.<LitigationCaseProgress>lambdaQuery().eq(LitigationCaseProgress::getLrId, aggregateId.getId()));
        litigationTrialInfoDao.remove(Wrappers.<LitigationTrialInfo>lambdaQuery().eq(LitigationTrialInfo::getLrId, aggregateId.getId()));
        litigationRegistrationDao.removeById(aggregateId.getId());
    }

}
