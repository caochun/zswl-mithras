package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.projectprocess.versioning.projreview.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlProjectReviewFactPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RiskControlProjectReviewFactPortAdapter implements RiskControlProjectReviewFactPort {

    @Resource
    private ProjReviewBaseInfoLibMapper projReviewBaseInfoLibMapper;
    @Resource
    private ProjReviewLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ProjReviewFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ProjReviewAocPriceLibMapper aocPriceLibMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;

    @Override
    public Integer maxReviewPriceMonthCountByClientIds(Set<Long> clientIds, LocalDate snapshotDate) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return 0;
        }
        Set<Long> projReviewIds = projReviewBaseInfoLibMapper
                .listNewestPreviewByClientIds(clientIds, null)
                .stream()
                .filter(baseInfo -> baseInfo.getDataCreateTime().isBefore(snapshotDate.atTime(23, 59, 59)))
                .map(ProjReviewBaseInfoLib::getOriginId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return 0;
        }
        ProjReviewPriceDto priceDto = new ProjReviewPriceDto();
        priceDto.setProjReviewIds(projReviewIds);
        return Stream.of(
                        leasePriceLibMapper.listNewestPrice(priceDto)
                                .stream()
                                .map(ProjReviewLeasePrice::getLeaseMonthCount),
                        factoringPriceLibMapper.listNewestPrice(priceDto)
                                .stream()
                                .map(ProjReviewFactoringPriceLib::getFactoringCreditTerm),
                        aocPriceLibMapper.listNewestPrice(priceDto)
                                .stream()
                                .map(ProjReviewAocPriceLib::getCreditAmountLoop))
                .flatMap(stream -> stream)
                .filter(monthCount -> monthCount != null)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    public Set<Long> newestContractIdsByClientIdsAndRegionalClassifies(Set<Long> clientIds,
                                                                       List<String> regionalClassifies,
        LocalDate snapshotDate) {
        if (ObjectUtil.isEmpty(clientIds)) {
            return Collections.emptySet();
        }
        Set<Long> projReviewIds = projReviewBaseInfoLibMapper
                .listNewestPreviewByClientIds(clientIds, regionalClassifies)
                .stream()
                .filter(baseInfo -> baseInfo.getDataCreateTime().isBefore(snapshotDate.atTime(23, 59, 59)))
                .map(ProjReviewBaseInfoLib::getOriginId)
                .collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(projReviewIds)) {
            return Collections.emptySet();
        }
        return contractBaseInfoLibMapper
                .listNewestContractByPreviewIds(projReviewIds)
                .stream()
                .filter(contract -> contract.getDataCreateTime().isBefore(snapshotDate.atTime(23, 59, 59)))
                .map(ContractBaseInfoLib::getOriginId)
                .collect(Collectors.toSet());
    }
}
