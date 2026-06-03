package cn.zswltech.mithras.service.convert.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionHistoryRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightInfo;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightInfoWithTag;
import cn.zswltech.mithras.kpi.enums.KpiProjectWeightTypeEnum;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionWeightLib;
import cn.zswltech.mithras.service.others.MithrasException;

import java.util.*;

/**
 * @author dingqi
 * @date 2023/6/15
 * @description
 */
public class KpiProjectDistributionConvert {
    public static KpiProjectDistributionWeightInfo toKpiProjectDistributionWeightInfo(KpiProjectDistributionWeight dbModel, Map<Long, String> userNameMap, Map<Long, String> deptNameMap) {
        KpiProjectDistributionWeightInfo info = new KpiProjectDistributionWeightInfo();
        info.setId(dbModel.getId());
        info.setWeightType(dbModel.getWeightType());
        KpiProjectWeightTypeEnum weightTypeEnum = KpiProjectWeightTypeEnum.find(dbModel.getWeightType());
        if (Objects.isNull(weightTypeEnum)) {
            throw new MithrasException("未知的分配比重类型[" + dbModel.getWeightType() + "]");
        }
        info.setWeightTypeName(weightTypeEnum.getDisplay());
        if (StrUtil.isNotBlank(dbModel.getWeightTarget())) {
            info.setWeightTarget(Long.valueOf(dbModel.getWeightTarget()));
            switch (weightTypeEnum) {
                case PROJECT_SPONSOR:
                case PROJECT_COSPONSOR:
                case OTHER_DEPT_RECOMMEND: {
                    info.setWeightTargetName(userNameMap.get(info.getWeightTarget()));
                    break;
                }
                case BUSINESS_DEPT: {
                    info.setWeightTargetName(deptNameMap.get(info.getWeightTarget()));
                    break;
                }
            }
        }
        info.setWeightValue(dbModel.getWeightValue());
        info.setProjectDistributionId(dbModel.getProjectDistributionId());
        return info;
    }

    public static KpiProjectDistributionWeight toKpiProjectDistributionWeight(KpiProjectDistributionWeightInfo info) {
        KpiProjectDistributionWeight dbModel = new KpiProjectDistributionWeight();
        dbModel.setId(info.getId());
        dbModel.setWeightType(info.getWeightType());
        if (Objects.nonNull(info.getWeightTarget())) {
            dbModel.setWeightTarget(String.valueOf(info.getWeightTarget()));
        }
        dbModel.setWeightValue(info.getWeightValue());
        return dbModel;
    }

    public static KpiProjectDistributionHistoryRSP toKpiProjectDistributionHistoryRSP(KpiProjectDistributionBaseInfoLib baseInfoLib, List<KpiProjectDistributionWeightLib> weightLibList, Map<Long, String> userNameMap, Map<Long, String> deptNameMap) {
        KpiProjectDistributionHistoryRSP rsp = new KpiProjectDistributionHistoryRSP();
        rsp.setVersion(baseInfoLib.getVersion());
        rsp.setOperateDate(LocalDateTimeUtil.format(baseInfoLib.getCreateTime().toLocalDate(), DatePattern.NORM_DATE_PATTERN));
        rsp.setEffectYear(baseInfoLib.getEffectYear());
        rsp.setEffectMonth(baseInfoLib.getEffectMonth());
        rsp.setChangeReason(baseInfoLib.getChangeReason());
        if (CollectionUtil.isEmpty(weightLibList)) {
            rsp.setWeightInfoWithTagList(Collections.emptyList());
            return rsp;
        }
        List<KpiProjectDistributionWeightInfoWithTag> weightInfoWithTagList = new ArrayList<>(weightLibList.size());
        for (KpiProjectDistributionWeightLib weightLib : weightLibList) {
            KpiProjectDistributionWeightInfo weightInfo = toKpiProjectDistributionWeightInfo(weightLib, userNameMap, deptNameMap);
            // 使用originId进行填充
            weightInfo.setId(weightLib.getOriginId());
            weightInfoWithTagList.add(BeanUtil.copyProperties(weightInfo, KpiProjectDistributionWeightInfoWithTag.class));
        }
        rsp.setWeightInfoWithTagList(weightInfoWithTagList);
        return rsp;
    }
}
