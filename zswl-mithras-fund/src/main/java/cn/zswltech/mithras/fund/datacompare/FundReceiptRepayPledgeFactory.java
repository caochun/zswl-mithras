package cn.zswltech.mithras.fund.datacompare;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.fund.versioning.financing.FundFinancingPledgeInfoLibService;
import cn.zswltech.mithras.fund.versioning.financing.handler.impl.FundFinancingPledgeInfoLibHandler;
import cn.zswltech.mithras.fund.versioning.receiptrepay.FundReceiptRepayBaseInfoLibService;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfoLib;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.util.CompareUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wangchuanhao
 * @date 2023/2/22
 * @description
 */
@Component("fundReceiptRepayPledge")
public class FundReceiptRepayPledgeFactory implements EditdataCompareFactory {

    @Resource
    private CommonVersionMapper commonVersionMapper;
    @Resource
    private FundReceiptRepayBaseInfoLibService fundReceiptRepayBaseInfoLibService;
    @Resource
    private FundFinancingPledgeInfoLibService fundFinancingPledgeInfoLibService;
    @Resource
    private FundFinancingPledgeInfoLibHandler fundFinancingPledgeInfoLibHandler;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new FundReceiptRepayPledgeCompare(rsps, version);
    }

    @AllArgsConstructor
    public class FundReceiptRepayPledgeCompare extends AbstractDataCompare {

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
            List<FundFinancingPledgeListRSP> rspList = (List<FundFinancingPledgeListRSP>) rsps;
            // 找到上个版本
            CommonVersion lastVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                    .eq(CommonVersion::getMainId, mainId)
                    .eq(CommonVersion::getVersionType, versionType)
                    .eq(CommonVersion::getModule, "FUND_RECEIPT_REPAY")
                    .lt(StringUtils.isNotBlank(version), CommonVersion::getVersion, version)
                    .orderByDesc(CommonVersion::getVersion)
                    .last("LIMIT 1")
            );
            if (Objects.isNull(lastVersion)) {
                for (FundFinancingPledgeListRSP rsp : rspList) {
                    result.add(CompareUtil.compare(rsp, null));
                }
                result.forEach(o -> {
                    o.values().forEach(dif -> dif.setIsChange(false));
                });
                return result;
            }
            FundReceiptRepayBaseInfoLib baseInfoLib = fundReceiptRepayBaseInfoLibService.getOne(Wrappers.<FundReceiptRepayBaseInfoLib>lambdaQuery().eq(FundReceiptRepayBaseInfoLib::getOriginId, mainId).eq(FundReceiptRepayBaseInfoLib::getVersion, lastVersion.getVersion()));
            List<FundFinancingPledgeInfoLib> pledgeInfoLibs = fundFinancingPledgeInfoLibService.listByFinancingIdVersion(baseInfoLib.getFinancingId(), baseInfoLib.getFinancingVersion());
            Map<Long, FundFinancingPledgeListRSP> lastestPledgeMap = pledgeInfoLibs.stream()
                    .map(fundFinancingPledgeInfoLibHandler::actualLib2Entity)
                    .map(entity -> BeanUtil.copyProperties(entity, FundFinancingPledgeListRSP.class))
                    .collect(Collectors.toMap(FundFinancingPledgeListRSP::getId, c -> c));
            for (FundFinancingPledgeListRSP rsp : rspList) {
                result.add(CompareUtil.compare(rsp, lastestPledgeMap.get(rsp.getId())));
            }
            return result;
        }

    }

}
