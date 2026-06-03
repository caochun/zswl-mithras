package cn.zswltech.mithras.kpi.service.lib;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.kpi.mapper.lib.KpiProjectDistributionBaseInfoLibMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionBaseInfoLib;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dingqi
 * @date 2023/6/14
 * @description
 */
@Slf4j
@Service
public class KpiProjectDistributionBaseInfoLibService extends ServiceImpl<KpiProjectDistributionBaseInfoLibMapper, KpiProjectDistributionBaseInfoLib> {
    public List<KpiProjectDistributionBaseInfoLib> listEffectByProjectDistributionId(Long projectDistributionId) {
        LambdaQueryWrapper<KpiProjectDistributionBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionBaseInfoLib::getProjectDistributionId, projectDistributionId);
        query.eq(KpiProjectDistributionBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        return this.list(query);
    }

    public KpiProjectDistributionBaseInfoLib getSpecificByMainIdAndVersion(Long projectDistributionId, String version) {
        LambdaQueryWrapper<KpiProjectDistributionBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionId);
        query.eq(KpiProjectDistributionBaseInfoLib::getVersion, version);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    public KpiProjectDistributionBaseInfoLib getNearSpecificDate(Long projectDistributionId, Integer year, Integer month) {
        LambdaQueryWrapper<KpiProjectDistributionBaseInfoLib> query = Wrappers.lambdaQuery();
        query.eq(KpiProjectDistributionBaseInfo::getProjectDistributionId, projectDistributionId);
        query.le(KpiProjectDistributionBaseInfo::getEffectYear, year);
        query.eq(KpiProjectDistributionBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        query.orderByDesc(KpiProjectDistributionBaseInfo::getEffectYear);
        query.orderByDesc(KpiProjectDistributionBaseInfo::getEffectMonth);
        query.orderByDesc(KpiProjectDistributionBaseInfo::getId);
        List<KpiProjectDistributionBaseInfoLib> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        for (KpiProjectDistributionBaseInfoLib baseInfoLib : list) {
            if (baseInfoLib.getEffectYear().equals(year)) {
                if (baseInfoLib.getEffectMonth() <= month) {
                    return baseInfoLib;
                }
            } else {
                return baseInfoLib;
            }
        }
        return null;
    }

    public List<KpiProjectDistributionBaseInfoLib> listEffectByContractCodes(LocalDate date, List<String> contractCodes) {
        LambdaQueryWrapper<KpiProjectDistributionBaseInfoLib> query = Wrappers.lambdaQuery();
        query.in(ObjectUtil.isNotEmpty(contractCodes), KpiProjectDistributionBaseInfoLib::getContractCode, contractCodes);
        query.eq(KpiProjectDistributionBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL);
        if (ObjectUtil.isNotEmpty(date)) {
            query.and(wrapper -> wrapper.lt(KpiProjectDistributionBaseInfoLib::getEffectYear, date.getYear())
            .or(wrapper1 -> wrapper1.eq(KpiProjectDistributionBaseInfoLib::getEffectYear, date.getYear()).le(KpiProjectDistributionBaseInfoLib::getEffectMonth, date.getMonthValue())));
        }
        query.orderByDesc(KpiProjectDistributionBaseInfoLib::getEffectYear);
        query.orderByDesc(KpiProjectDistributionBaseInfoLib::getEffectMonth);
        query.orderByDesc(KpiProjectDistributionBaseInfoLib::getCreateTime);
        Map<String, KpiProjectDistributionBaseInfoLib> map = new HashMap<>();
        List<KpiProjectDistributionBaseInfoLib> list = this.list(query);
        //取最新数据
        list.forEach(e -> {
            KpiProjectDistributionBaseInfoLib lib = map.get(e.getContractCode());
            if(ObjectUtil.isEmpty(lib) || e.getEffectYear() > lib.getEffectYear() || (ObjectUtil.equals(e.getEffectYear(), lib.getEffectYear()) && e.getEffectMonth() > lib.getEffectMonth())) {
                map.put(e.getContractCode(), e);
            }
        });
        return new ArrayList<>(map.values());
    }
}
