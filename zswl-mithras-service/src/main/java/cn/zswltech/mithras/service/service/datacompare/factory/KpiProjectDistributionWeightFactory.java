package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.kpi.KpiProjectDistributionWeightInfo;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.kpi.KpiProjectDistributionWeightLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionWeight;
import cn.zswltech.mithras.service.mapper.model.kpi.KpiProjectDistributionWeightLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.service.FundReceiptRepayBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.kpi.KpiProjectDistributionWeightLibService;
import cn.zswltech.mithras.service.service.lib.kpi.handler.impl.KpiProjectDistributionWeightLibHandler;
import cn.zswltech.mithras.service.util.CompareUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @create: 2022-08-26
 **/
@Service("kpiProjectDistributionWeight")
public class KpiProjectDistributionWeightFactory implements EditdataCompareFactory {
    @Resource
    private KpiProjectDistributionWeightLibMapper libMapper;
    @Resource
    private KpiProjectDistributionWeightLibHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private KpiProjectDistributionWeightLibService kpiProjectDistributionWeightLibService;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<KpiProjectDistributionWeight, KpiProjectDistributionWeightLib, KpiProjectDistributionWeightInfo>(rsps, libMapper, handler,commonVersionMapper, BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION.name(), version);
        //return new KpiProjectDistributionWeightFactory.KpiProjectDistributionWeightCompare(rsps, version);
    }


    @AllArgsConstructor
    public class KpiProjectDistributionWeightCompare extends AbstractDataCompare {

        private List rsps;
        private String version;

        @Override
        public Map<String, DiffValue> compareone(Long mainId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<String, DiffValue> compareone(Long mainId, Integer versionType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Map<String, DiffValue>> comparelist(Long mainId) {
            return comparelist(mainId, VersionTypeConstants.NORMAL);
        }

        @Override
        public List<Map<String, DiffValue>> comparelist(Long mainId, Integer versionType) {
            List<Map<String, DiffValue>> result = new LinkedList<>();
            if (CollectionUtils.isEmpty(rsps)) {
                return result;
            }
            // 这次的数据
            List<KpiProjectDistributionWeightInfo> rspList = (List<KpiProjectDistributionWeightInfo>) rsps;
            // 找到上个版本
            CommonVersion lastVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getMainId, mainId)
                    .eq(CommonVersion::getVersionType, versionType)
                    .eq(CommonVersion::getModule, BusinessModuleEnum.KPI_PROJECT_DISTRIBUTION.name())
                    .lt(StringUtils.isNotBlank(version), CommonVersion::getVersion, version)
                    .orderByDesc(CommonVersion::getVersion)
                    .last("LIMIT 1")
            );
            if (Objects.isNull(lastVersion)) {
                for (KpiProjectDistributionWeightInfo rsp : rspList) {
                    result.add(CompareUtil.compare(rsp, null));
                }
                result.forEach(o -> {
                    o.values().forEach(dif -> dif.setIsChange(false));
                });
                return result;
            }
            List<KpiProjectDistributionWeightLib> baseInfoLibList = libMapper.selectList(Wrappers.<KpiProjectDistributionWeightLib>lambdaQuery()
                    .eq(KpiProjectDistributionWeightLib::getProjectDistributionId, lastVersion.getMainId())
                    .eq(KpiProjectDistributionWeightLib::getVersion, lastVersion.getVersion())
            );
            List<KpiProjectDistributionWeightInfo> res = new ArrayList<>();
            for (KpiProjectDistributionWeightLib weightLib : baseInfoLibList) {
                KpiProjectDistributionWeightInfo weightInfo = handler.actualLib2Rsp(weightLib);
                res.add(weightInfo);
            }
            Set<String> weightNameSet = new HashSet<>();
            Set<String> prevWeightNameSet = new HashSet<>();
            Set<String> diffCur = new HashSet<>();
            Set<String> diffPre = new HashSet<>();
            for (KpiProjectDistributionWeightInfo weightInfo : rspList) {
                weightNameSet.add(weightInfo.getWeightType());
            }
            for (KpiProjectDistributionWeightInfo weightInfo : res) {
                prevWeightNameSet.add(weightInfo.getWeightType());
            }
            for (String cur : weightNameSet) {
                boolean found = false;
                for (String prev : prevWeightNameSet) {
                    if (cur.equalsIgnoreCase(prev)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    diffCur.add(cur);
                }
            }
            for (String prev : prevWeightNameSet) {
                boolean found = false;
                for (String cur : weightNameSet) {
                    if (cur.equalsIgnoreCase(prev)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    diffPre.add(prev);
                }
            }
            for (KpiProjectDistributionWeightInfo weightInfo : rspList) {
                for (KpiProjectDistributionWeightInfo prevWeightInfo : res) {
                    if (weightInfo.getWeightType().equalsIgnoreCase(prevWeightInfo.getWeightType())) {
                        result.add(CompareUtil.compare(weightInfo, prevWeightInfo));
                    }
                }
            }
            if (!diffCur.isEmpty()) {
                for (KpiProjectDistributionWeightInfo weightInfo : rspList) {
                    if (diffCur.contains(weightInfo.getWeightType())) {
                        result.add(CompareUtil.compare(weightInfo, null));
                    }
                }
            }
            if (!diffPre.isEmpty()) {
                for (KpiProjectDistributionWeightInfo weightInfo : res) {
                    if (diffPre.contains(weightInfo.getWeightType())) {
                        result.add(CompareUtil.compare(new KpiProjectDistributionWeightInfo(), weightInfo));
                    }
                }
            }
            return result;
        }

    }
}