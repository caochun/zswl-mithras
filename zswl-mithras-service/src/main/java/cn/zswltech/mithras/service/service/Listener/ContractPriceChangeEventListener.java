package cn.zswltech.mithras.service.service.Listener;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPrice;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewAocPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewFactoringPriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.service.util.LongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;


@Component
@Slf4j
public class ContractPriceChangeEventListener {

    @Autowired
    private ContractBaseInfoService baseInfoService;

    @Autowired
    private ContractPriceService priceService;

    @Autowired
    private ContractTenantryService tenantryService;

    @Resource
    private ProjReviewFactoringPriceService projReviewFactoringPriceService;
    @Resource
    private ProjReviewAocPriceService projReviewAocPriceService;
    @Resource
    private ProjReviewLeasePriceService projReviewLeasePriceService;

    @EventListener
    public void onPriceChange(ContractPriceChangeEvent event){
        log.info("ContractPriceChangeEvent : {}", event);
        if(ObjectUtil.isEmpty(event) || ObjectUtil.isEmpty(event.getContractId())){
            return;
        }
        ContractBaseInfo baseInfo = baseInfoService.getById(event.getContractId());
        if(ObjectUtil.isEmpty(baseInfo)){
            return;
        }
        ContractPriceDetailRSP detail = priceService.detail(new ContractPriceDetailREQ(baseInfo.getId()));
        if(ObjectUtil.isEmpty(detail)){
            return;
        }

        Long projCreditAmount = 0L;
        if(!ObjectUtil.isEmpty(detail.getLeasePriceModifyRSP())){
            ProjReviewLeasePrice leasePrice = projReviewLeasePriceService.getByProjectId(baseInfo.getProjReviewId());
            if (Objects.nonNull(leasePrice)) {
                projCreditAmount = leasePrice.getProjectApprovalAmount();
            }
        }
        if (Objects.nonNull(detail.getFactoringPriceRSP())) {
            ProjReviewFactoringPrice projReviewFactoringPrice = projReviewFactoringPriceService.getByProjectId(baseInfo.getProjReviewId());
            if (Objects.nonNull(projReviewFactoringPrice)) {
                projCreditAmount = projReviewFactoringPrice.getProjectApprovalAmount();
            }
        }
        if (Objects.nonNull(detail.getAocPriceRSP())) {
            ProjReviewAocPrice projReviewAocPrice = projReviewAocPriceService.getByProjectId(baseInfo.getProjReviewId());
            if (Objects.nonNull(projReviewAocPrice)) {
                projCreditAmount = projReviewAocPrice.getProjectApprovalAmount();
            }
        }

        List<ContractBaseInfo> contractBaseInfos = baseInfoService.selectListByProjId(baseInfo.getProjReviewId());
        if(ObjectUtil.isEmpty(contractBaseInfos) || contractBaseInfos.size() == 0){
            return;
        }
        Long finalProjCreditAmount = LongUtil.null2zero(projCreditAmount);
        contractBaseInfos.stream().forEach(info -> {
            //生效态不再计算
            if (ContractStatus.NEW.name().equals(info.getContractStatus())) {
                info.setRemainAvailableQuota(finalProjCreditAmount - baseInfoService.getRemainAvailableQuota(info.getId(), info.getCreditAmountLoop(), contractBaseInfos));
                //更新风险敞口
                tenantryService.updateStockRiskExposure(info.getId());
            }
            baseInfoService.updateById(info);
        });
//        baseInfoService.updateBatchById(contractBaseInfos);

    }

}
