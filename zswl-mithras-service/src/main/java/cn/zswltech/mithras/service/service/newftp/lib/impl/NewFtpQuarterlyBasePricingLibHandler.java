package cn.zswltech.mithras.service.service.newftp.lib.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.newftp.FtpValue;
import cn.zswltech.mithras.dto.newftp.NewFtpQuarterlyBasePricingDetailRsp;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.ftp.newftp.enums.NewFtpSubModule;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.ftp.newftp.lib.NewFtpLibAbstractHandler;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.service.newftp.service.drift.NewFtpQuarterlyBasePricingDraftService;
import cn.zswltech.mithras.service.service.newftp.service.lib.NewFtpQuarterlyBasePricingLibService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/24 10:32
 */
@Component
public class NewFtpQuarterlyBasePricingLibHandler extends
        NewFtpLibAbstractHandler<NewFtpQuarterlyBasePricingLib, NewFtpQuarterlyBasePricingDraft, NewFtpQuarterlyBasePricingDetailRsp> {
    @Resource
    private NewFtpBaseInfoService baseInfoService;

    @Override
    protected NewFtpQuarterlyBasePricingLib entity2Lib(NewFtpQuarterlyBasePricingDraft f) {
        return BeanUtil.copyProperties(f, NewFtpQuarterlyBasePricingLib.class);
    }

    @Override
    protected NewFtpQuarterlyBasePricingDraft lib2Entity(NewFtpQuarterlyBasePricingLib t) {
        return BeanUtil.copyProperties(t, NewFtpQuarterlyBasePricingDraft.class);
    }

    @Override
    protected NewFtpQuarterlyBasePricingDetailRsp lib2Rsp(NewFtpQuarterlyBasePricingLib f) {
        return BeanUtil.copyProperties(f, NewFtpQuarterlyBasePricingDetailRsp.class);
    }

    @Resource
    private NewFtpQuarterlyBasePricingLibService quarterlyBasePricingLibService;

    @Override
    public CommonVersionDiffBO libCompareLib(CommonVersion newVersion, CommonVersion oldVersion) {
        CommonVersionDiffBO diffBO = new CommonVersionDiffBO();
        List<NewFtpQuarterlyBasePricingLib> oldVersionList = quarterlyBasePricingLibService.list(Wrappers.<NewFtpQuarterlyBasePricingLib>lambdaQuery()
                .eq(NewFtpQuarterlyBasePricingLib::getFtpId, oldVersion.getMainId())
                .eq(NewFtpQuarterlyBasePricingLib::getVersion, oldVersion.getVersion()));
        Map<String, List<NewFtpQuarterlyBasePricingDraft>> oldRows = oldVersionList.stream()
                .collect(Collectors.groupingBy(pricing -> String.join(":",
                        pricing.getAssetIndustryClassify(), pricing.getRegionalClassify())));

        NewFtpQuarterlyBasePricingDetailRsp oldRsp = new NewFtpQuarterlyBasePricingDetailRsp();
        NewFtpQuarterlyBasePricingDraftService.ROW_KEYS.forEach((field, key) -> {
            List<FtpValue> rowData = oldRows.get(key).stream()
                    .sorted(Comparator.comparing(NewFtpQuarterlyBasePricingDraft::getTemplateId))
                    .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                    .collect(Collectors.toList());
            ReflectUtil.setFieldValue(oldRsp, field, rowData);
        });
        diffBO.setBeforeData(Arrays.asList(oldRsp));

        List<NewFtpQuarterlyBasePricingLib> newVersionList = quarterlyBasePricingLibService.list(Wrappers.<NewFtpQuarterlyBasePricingLib>lambdaQuery()
                .eq(NewFtpQuarterlyBasePricingDraft::getFtpId, newVersion.getMainId())
                .eq(NewFtpQuarterlyBasePricingLib::getVersion, newVersion.getVersion()));
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
        boolean changed = false;
        for (Field field : fields) {
            if (field.getName().equals("id")) {
                continue;
            }
            List<FtpValue> oldValue = (List<FtpValue>) ReflectUtil.getFieldValue(oldRsp, field);
            List<FtpValue> newValue = (List<FtpValue>) ReflectUtil.getFieldValue(newRsp, field);
            List<DiffValue> diffValues = new ArrayList<>();
            for (int i = 0; i < oldValue.size(); i++) {
                FtpValue oldV = oldValue.get(i);
                FtpValue newV = newValue.get(i);
                DiffValue diffValue = new DiffValue();
                diffValue.setBeforeValue(oldV.getValue());
                diffValue.setValue(newV.getValue());
                if (!oldV.getValue().equals(newV.getValue())) {
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
        return NewFtpSubModule.QUARTERLY_PRICING;
    }

    @Override
    public boolean needHandle(Long mainId) {
        // 非季度开始月无需处理
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        if (!DateUtil.isQuarterStart(baseInfo.getMonth())) {
            return false;
        }
        return true;
    }
}
