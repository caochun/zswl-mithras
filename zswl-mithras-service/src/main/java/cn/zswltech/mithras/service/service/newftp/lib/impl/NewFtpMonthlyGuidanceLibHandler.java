package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.newftp.FtpValue;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceDetaiRsp;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.enums.newftp.NewFtpSubModule;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.service.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.service.service.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.service.service.newftp.model.lib.NewFtpMonthlyGuidanceLib;
import cn.zswltech.mithras.service.service.newftp.service.lib.NewFtpMonthlyGuidanceLibService;
import cn.zswltech.mithras.service.service.newftp.service.drift.NewFtpMonthlyGuidanceDraftService;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 10:28
 */
@Slf4j
@Component
public class NewFtpMonthlyGuidanceLibHandler extends NewFtpLibAbstractHandler<NewFtpMonthlyGuidanceLib, NewFtpMonthlyGuidanceDraft, NewFtpMonthlyGuidanceDetaiRsp> {
    @Resource
    private NewFtpMonthlyGuidanceLibService guidanceLibService;
    @Resource
    private NewFtpMonthlyGuidanceDraftService newFtpMonthlyGuidanceDraftService;

    @Override
    protected NewFtpMonthlyGuidanceLib entity2Lib(NewFtpMonthlyGuidanceDraft f) {
        return BeanUtil.copyProperties(f, NewFtpMonthlyGuidanceLib.class);
    }

    @Override
    protected NewFtpMonthlyGuidanceDraft lib2Entity(NewFtpMonthlyGuidanceLib t) {
        return BeanUtil.copyProperties(t, NewFtpMonthlyGuidanceDraft.class);
    }

    @Override
    protected NewFtpMonthlyGuidanceDetaiRsp lib2Rsp(NewFtpMonthlyGuidanceLib f) {
        return BeanUtil.copyProperties(f, NewFtpMonthlyGuidanceDetaiRsp.class);
    }

    @Override
    public CommonVersionDiffBO libCompareLib(CommonVersion newVersion, CommonVersion oldVersion) {
        CommonVersionDiffBO diffBO = new CommonVersionDiffBO();

        List<NewFtpMonthlyGuidanceLib> oldVersionList = guidanceLibService.list(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceDraft::getFtpId, oldVersion.getMainId())
                .eq(NewFtpMonthlyGuidanceLib::getVersion, oldVersion.getVersion()));
        Map<String, List<NewFtpMonthlyGuidanceDraft>> oldRows = oldVersionList.stream()
                .collect(Collectors.groupingBy(guidance -> String.join(":",
                        guidance.getRiskIndustryClassify(),
                        StringUtil.null2Space(guidance.getAssetIndustryClassify()),
                        StringUtil.null2Space(guidance.getRegionalClassify()))));

        NewFtpMonthlyGuidanceDetaiRsp oldRsp = new NewFtpMonthlyGuidanceDetaiRsp();
        newFtpMonthlyGuidanceDraftService.getRowKeysMap(oldVersion.getMainId()).forEach((field, key) -> {
            List<FtpValue> rowData = oldRows.get(key).stream()
                    .sorted(Comparator.comparing(NewFtpMonthlyGuidanceDraft::getTemplateId))
                    .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                    .collect(Collectors.toList());
            ReflectUtil.setFieldValue(oldRsp, field, rowData);
        });
        diffBO.setBeforeData(Arrays.asList(oldRsp));

        List<NewFtpMonthlyGuidanceLib> newVersionList = guidanceLibService.list(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceDraft::getFtpId, newVersion.getMainId())
                .eq(NewFtpMonthlyGuidanceLib::getVersion, newVersion.getVersion()));
        Map<String, List<NewFtpMonthlyGuidanceDraft>> newRows = newVersionList.stream()
                .collect(Collectors.groupingBy(guidance -> String.join(":",
                        guidance.getRiskIndustryClassify(),
                        StringUtil.null2Space(guidance.getAssetIndustryClassify()),
                        StringUtil.null2Space(guidance.getRegionalClassify()))));
        NewFtpMonthlyGuidanceDetaiRsp newRsp = new NewFtpMonthlyGuidanceDetaiRsp();
        newFtpMonthlyGuidanceDraftService.getRowKeysMap(newVersion.getMainId()).forEach((field, key) -> {
            List<FtpValue> rowData = newRows.get(key).stream()
                    .sorted(Comparator.comparing(NewFtpMonthlyGuidanceDraft::getTemplateId))
                    .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                    .collect(Collectors.toList());
            ReflectUtil.setFieldValue(newRsp, field, rowData);
        });

        Field[] fields = ReflectUtil.getFields(NewFtpMonthlyGuidanceDetaiRsp.class);
        Map<String, DiffValue> diff = new HashMap<>();
        boolean changed = false;
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                continue;
            }
            List<FtpValue> oldValue = (List<FtpValue>) ReflectUtil.getFieldValue(oldRsp, field);
            List<FtpValue> newValue = (List<FtpValue>) ReflectUtil.getFieldValue(newRsp, field);
            if (Objects.isNull(newValue) || Objects.isNull(oldValue)) {
                continue;
            }
            List<DiffValue> diffValues = new ArrayList<>();
            for (int i = 0; i < oldValue.size(); i++) {
                FtpValue oldV = oldValue.get(i);
                FtpValue newV = newValue.get(i);
                DiffValue diffValue = new DiffValue();
                diffValue.setBeforeValue(oldV.getValue());
                diffValue.setValue(newV.getValue());
                if (!Objects.equals(oldV.getValue(), newV.getValue())) {
                    changed = true;
                    diffValue.setIsChange(true);
                } else {
                    diffValue.setIsChange(false);
                }
                diffValues.add(diffValue);
            }
            DiffValue upDiff = new DiffValue();
            upDiff.setDiffValueList(diffValues);
            diff.put(field.getName(), upDiff);
        }
        diffBO.setAfterData(Arrays.asList(diff));
        diffBO.setModuleChanged(changed);
        return diffBO;
    }

    @Override
    public NewFtpSubModule getSubModule() {
        return NewFtpSubModule.MONTHLY_GUIDANCE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
