package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListREQ;
import cn.zswltech.mithras.dto.fund.financing.pledge.FundFinancingPledgeListRSP;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfoLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.lib.fund.receiptrepay.service.FundReceiptRepayBaseInfoLibService;
import cn.zswltech.mithras.service.util.CompareUtil;
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
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;

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
                    .eq(CommonVersion::getModule, BusinessModuleEnum.FUND_RECEIPT_REPAY.name())
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
            FundFinancingPledgeListREQ pledgeListReq = new FundFinancingPledgeListREQ();
            pledgeListReq.setFinancingId(baseInfoLib.getFinancingId());
            pledgeListReq.setVersion(baseInfoLib.getFinancingVersion());
            Map<Long, FundFinancingPledgeListRSP> lastestPledgeMap = fundFinancingPledgeInfoService.list(pledgeListReq).stream().collect(Collectors.toMap(FundFinancingPledgeListRSP::getId, c -> c));
            for (FundFinancingPledgeListRSP rsp : rspList) {
                result.add(CompareUtil.compare(rsp, lastestPledgeMap.get(rsp.getId())));
            }
            return result;
        }

    }

}
