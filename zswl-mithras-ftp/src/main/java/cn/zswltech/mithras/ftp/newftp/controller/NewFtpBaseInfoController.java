package cn.zswltech.mithras.ftp.newftp.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.newftp.NewFtpBaseInfoApi;
import cn.zswltech.mithras.dto.newftp.*;
import cn.zswltech.mithras.dto.projreview.ProjReviewVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpBusinessModule;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.exception.AuthCheckException;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.newftp.convert.NewFtpBaseInfoConverter;
import cn.zswltech.mithras.ftp.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpDescriptionTextDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceLib;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpVersionService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpDescriptionTextDraftService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpMonthlyGuidanceDraftService;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpQuarterlyBasePricingDraftService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpMonthlyGuidanceLibService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpQuarterlyBasePricingLibService;
import cn.zswltech.mithras.workflow.flow.util.FlowUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.api.common.R.ok;

/**
 * @author zhaozhengkang
 * @description ftp主表
 * @date 2023-05-21
 */
@RestController
public class NewFtpBaseInfoController implements NewFtpBaseInfoApi {

    @Resource
    private NewFtpBaseInfoService newFtpBaseInfoService;
    @Resource
    private NewFtpVersionService versionService;
    @Resource
    private NewFtpDescriptionTextDraftService descriptionTextService;
    @Resource
    private NewFtpBaseInfoConverter baseInfoConverter;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;
    @Resource
    private NewFtpMonthlyGuidanceLibService guidanceLibService;
    @Resource
    private NewFtpMonthlyGuidanceDraftService guidanceService;
    @Resource
    private NewFtpQuarterlyBasePricingLibService quarterlyBasePricingLibService;
    @Resource
    private NewFtpQuarterlyBasePricingDraftService quarterlyBasePricingService;
    @Resource
    private NewFtpQuarterlyBasePricingLibService newFtpQuarterlyBasePricingLibService;

    @Override
    public R<Long> add(NewFtpBaseInfoAddREQ req) {
        return ok(newFtpBaseInfoService.add(req));
    }

    @Override
    public R<NewFtpBaseInfoDetailRSP> detail(NewFtpDetailReq req) {
        NewFtpBaseInfo baseInfo = newFtpBaseInfoService.getById(req.getMainId());
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        NewFtpBaseInfoDetailRSP data = baseInfoConverter.entity2DetailRsp(baseInfo);
        data.setCalculateDeductionFlag(baseInfo.getCalculateDeductionFlag());
        data.setCalculateGuidanceFlag(baseInfo.getCalculateGuidanceFlag());
        return R.ok(data);
    }

    @Override
    public R<Void> calculate(NewFtpDetailReq req) {
        newFtpBaseInfoService.calculate(req.getMainId());
        return ok();
    }

    @Override
    public R<PageR<NewFtpBaseInfoListRSP>> list(NewFtpBaseInfoListREQ req) {
        return ok(newFtpBaseInfoService.list(req));
    }

    @Override
    public R<List<NewFtpDescriptionTextListRsp>> listDesc(NewFtpDetailReq req) {
        List<NewFtpDescriptionTextDraft> entities = descriptionTextService.list(Wrappers.<NewFtpDescriptionTextDraft>lambdaQuery().eq(NewFtpDescriptionTextDraft::getFtpId, req.getMainId()));
        return ok(baseInfoConverter.descEntity2ListRsp(entities));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public R<Void> modifDesc(NewFtpDescriptionTextModifyReq req) {

        NewFtpDescriptionTextDraft byId = descriptionTextService.getById(req.getId());
        if (Objects.isNull(byId)) {
            throw new MithrasException("描述不存在");
        }
        ProcessResp relatedProcess = newFtpBaseInfoService.findRelatedProcess(byId.getFtpId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(relatedProcess);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
        NewFtpDescriptionTextDraft updateEntity = new NewFtpDescriptionTextDraft();
        updateEntity.setId(req.getId());
        updateEntity.setDescContent(req.getDescContent());
        descriptionTextService.updateById(updateEntity);

        NewFtpBaseInfo baseInfo = newFtpBaseInfoService.getById(byId.getFtpId());
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
        return R.ok();
    }

    @Override
    public R<Void> submit(NewFtpDetailReq req) {
        versionService.submit(req.getMainId());
        return ok();
    }

    @Override
    public R<PageR<CommonVersionListRSP>> versions(CommonVersionListREQ req) {
        return ok(versionService.selectPage(req));
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(ProjReviewVersionDiffREQ req) {
        CommonVersion newVersion = commonVersionMapper.selectById(req.getId());
        if (Objects.isNull(newVersion)) {
            throw new MithrasException("版本不存在");
        }
        CommonVersion oldVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, newVersion.getMainId())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, newVersion.getModule())
                .lt(CommonVersion::getVersion, newVersion.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(oldVersion)) {
            throw new MithrasException("版本不存在");
        }
        return R.ok(versionService.doCompare(newVersion, oldVersion));
    }

    @Override
    public R<Map<String, DiffValue>> compareMonthly(NewFtpDetailReq req) {
        List<NewFtpMonthlyGuidanceDraft> newVersionList = ObjectUtil.isEmpty(req.getVersion()) ? guidanceService.list(Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceDraft::getFtpId, req.getMainId())) : guidanceLibService.list(req);
        if (ObjectUtil.isEmpty(newVersionList)) {
            return R.ok();
        }
        Map<String, List<NewFtpMonthlyGuidanceDraft>> newRows = newVersionList.stream()
                .collect(Collectors.groupingBy(guidance -> String.join(":",
                        guidance.getRiskIndustryClassify(),
                        StringUtil.null2Space(guidance.getAssetIndustryClassify()),
                        StringUtil.null2Space(guidance.getRegionalClassify()))));
        NewFtpMonthlyGuidanceDetaiRsp newRsp = new NewFtpMonthlyGuidanceDetaiRsp();
        guidanceService.getRowKeysMap(req.getMainId()).forEach((field, key) -> {
            List<FtpValue> rowData = newRows.get(key).stream()
                    .sorted(Comparator.comparing(NewFtpMonthlyGuidanceDraft::getTemplateId))
                    .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                    .collect(Collectors.toList());
            ReflectUtil.setFieldValue(newRsp, field, rowData);
        });

        CommonVersion oldVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getMainId, req.getMainId())
                .eq(CommonVersion::getModule, NewFtpBusinessModule.NEW_FTP_GUIDANCE.name())
                .lt(ObjectUtil.isNotEmpty(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        NewFtpMonthlyGuidanceDetaiRsp oldRsp = new NewFtpMonthlyGuidanceDetaiRsp();
        if (oldVersion != null) {
            List<NewFtpMonthlyGuidanceLib> oldVersionList = guidanceLibService.list(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                    .eq(NewFtpMonthlyGuidanceDraft::getFtpId, oldVersion.getMainId())
                            .eq(NewFtpMonthlyGuidanceDraft::getFtpId, req.getMainId())
                    .eq(NewFtpMonthlyGuidanceLib::getVersion, oldVersion.getVersion()));
            Map<String, List<NewFtpMonthlyGuidanceDraft>> oldRows = oldVersionList.stream()
                    .collect(Collectors.groupingBy(guidance -> String.join(":",
                            guidance.getRiskIndustryClassify(),
                            StringUtil.null2Space(guidance.getAssetIndustryClassify()),
                            StringUtil.null2Space(guidance.getRegionalClassify()))));

            guidanceService.getRowKeysMap(req.getMainId()).forEach((field, key) -> {
                List<FtpValue> rowData = oldRows.get(key).stream()
                        .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                        .collect(Collectors.toList());
                ReflectUtil.setFieldValue(oldRsp, field, rowData);
            });
        }
        Field[] fields = ReflectUtil.getFields(NewFtpMonthlyGuidanceDetaiRsp.class);
        Map<String, DiffValue> diff = new HashMap<>();
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                continue;
            }
            List<FtpValue> oldValue = (List<FtpValue>) ReflectUtil.getFieldValue(oldRsp, field);
            List<FtpValue> newValue = (List<FtpValue>) ReflectUtil.getFieldValue(newRsp, field);
            if (Objects.isNull(newValue)) {
                continue;
            }
            List<DiffValue> diffValues = new ArrayList<>();
            for (int i = 0; i < newValue.size(); i++) {
                DiffValue diffValue = new DiffValue();
                FtpValue newV = newValue.get(i);
                if (ObjectUtil.isEmpty(oldValue)) {
                    diffValue.setBeforeValue(null);
                    diffValue.setIsChange(false);
                } else {
                    FtpValue oldV = oldValue.get(i);
                    diffValue.setBeforeValue(oldV.getValue());
                    diffValue.setIsChange(!Objects.equals(oldV.getValue(), newV.getValue()));
                }
                diffValue.setValue(newV.getValue());
                diffValues.add(diffValue);
            }
            DiffValue upDiff = new DiffValue();
            upDiff.setDiffValueList(diffValues);
            diff.put(field.getName(), upDiff);
        }
        return R.ok(diff);

    }

    @Override
    public R<Map<String, DiffValue>> compareQuarterly(NewFtpDetailReq req) {
        CommonVersion oldVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getMainId, req.getMainId())
                .eq(CommonVersion::getModule, NewFtpBusinessModule.NEW_FTP_GUIDANCE.name())
                .lt(ObjectUtil.isNotEmpty(req.getVersion()), CommonVersion::getVersion, req.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        NewFtpQuarterlyBasePricingDetailRsp oldRsp = new NewFtpQuarterlyBasePricingDetailRsp();
        if (oldVersion != null) {
            List<NewFtpQuarterlyBasePricingLib> oldVersionList = quarterlyBasePricingLibService.list(Wrappers.<NewFtpQuarterlyBasePricingLib>lambdaQuery()
                    .eq(NewFtpQuarterlyBasePricingLib::getFtpId, oldVersion.getMainId())
                    .eq(NewFtpQuarterlyBasePricingLib::getVersion, oldVersion.getVersion()));
            Map<String, List<NewFtpQuarterlyBasePricingDraft>> oldRows = oldVersionList.stream()
                    .collect(Collectors.groupingBy(pricing -> String.join(":",
                            pricing.getAssetIndustryClassify(), pricing.getRegionalClassify())));
            NewFtpQuarterlyBasePricingDraftService.ROW_KEYS.forEach((field, key) -> {
                List<FtpValue> rowData = oldRows.get(key).stream()
                        .sorted(Comparator.comparing(NewFtpQuarterlyBasePricingDraft::getTemplateId))
                        .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                        .collect(Collectors.toList());
                ReflectUtil.setFieldValue(oldRsp, field, rowData);
            });
        }
        List<NewFtpQuarterlyBasePricingDraft> newVersionList = ObjectUtil.isEmpty(req.getVersion()) ? quarterlyBasePricingService.list(
                Wrappers.<NewFtpQuarterlyBasePricingDraft>lambdaQuery()
                        .eq(NewFtpQuarterlyBasePricingDraft::getFtpId, req.getMainId())) : newFtpQuarterlyBasePricingLibService.list(req);
        Map<String, List<NewFtpQuarterlyBasePricingDraft>> newRows = newVersionList.stream()
                .collect(Collectors.groupingBy(pricing -> String.join(":",
                        pricing.getAssetIndustryClassify(), pricing.getRegionalClassify())));
        NewFtpQuarterlyBasePricingDetailRsp newRsp = new NewFtpQuarterlyBasePricingDetailRsp();
        NewFtpQuarterlyBasePricingDraftService.ROW_KEYS.forEach((field, key) -> {
            List<FtpValue> rowData = newRows.get(key).stream()
                    .sorted(Comparator.comparing(NewFtpQuarterlyBasePricingDraft::getTemplateId))
                    .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                    .collect(Collectors.toList());
            ReflectUtil.setFieldValue(newRsp, field, rowData);
        });

        Field[] fields = ReflectUtil.getFields(NewFtpQuarterlyBasePricingDetailRsp.class);
        Map<String, DiffValue> diff = new HashMap<>();
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                continue;
            }
            List<FtpValue> oldValue = (List<FtpValue>) ReflectUtil.getFieldValue(oldRsp, field);
            List<FtpValue> newValue = (List<FtpValue>) ReflectUtil.getFieldValue(newRsp, field);
            List<DiffValue> diffValues = new ArrayList<>();
            for (int i = 0; i < newValue.size(); i++) {
                DiffValue diffValue = new DiffValue();
                FtpValue newV = newValue.get(i);
                if (ObjectUtil.isEmpty(oldValue)) {
                    diffValue.setBeforeValue(null);
                    diffValue.setIsChange(false);
                } else {
                    FtpValue oldV = oldValue.get(i);
                    diffValue.setBeforeValue(oldV.getValue());
                    diffValue.setIsChange(!Objects.equals(oldV.getValue(), newV.getValue()));
                }
                diffValue.setValue(newV.getValue());
                diffValues.add(diffValue);
            }
            DiffValue upDiff = new DiffValue();
            upDiff.setDiffValueList(diffValues);
            diff.put(field.getName(), upDiff);
        }
        return R.ok(diff);

    }


}