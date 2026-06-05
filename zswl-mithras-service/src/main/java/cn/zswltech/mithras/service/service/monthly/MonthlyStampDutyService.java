package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.fund.domain.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.monthly.enums.StampDutyTypeEnum;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.monthly.mapper.model.MonthlyStampDuty;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.monthly.mapper.MonthlyStampDutyMapper;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * @author chenyifei
 * @since 2024-05-20
 */
@Service
public class MonthlyStampDutyService extends ServiceImpl<MonthlyStampDutyMapper, MonthlyStampDuty>{

    @Resource
    private MonthlyStampDutyService stampDutyService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundOrganizationService organizationService;

    /**
     * 项目端-印花税入库
     * @param contractId
     */
    @Transactional(rollbackFor = Throwable.class)
    public void saveRecordProj(Long contractId) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if(contractBaseInfo == null){
            return;
        }
        PaymentActualDetail actualDetail = paymentActualDetailService.getOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getContractId,contractId).last("limit 1"));
        if(actualDetail == null){
            return;
        }
        List<MonthlyStampDuty> existStampDutyList = stampDutyService.list(Wrappers.<MonthlyStampDuty>lambdaQuery().eq(MonthlyStampDuty::getContractId, contractId));
        List<Long> existReceiptIdList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(existStampDutyList)){
            existReceiptIdList = existStampDutyList.stream().map(MonthlyStampDuty::getReceiptId).collect(Collectors.toList());
        }

        Map<Long, String> clientId2Name = id2NameService.clientId2Name(Collections.singleton(contractBaseInfo.getClientId()));
        List<ContractReceipt> contractReceiptList = contractReceiptService.list(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getContractId, contractId)
                .notIn(CollectionUtil.isNotEmpty(existReceiptIdList),ContractReceipt::getId,existReceiptIdList));

        for (ContractReceipt contractReceipt : contractReceiptList) {
            // 正常情况下只会循环一次
            MonthlyStampDuty monthlyStampDuty = new MonthlyStampDuty();
            monthlyStampDuty.setType(StampDutyTypeEnum.PROJ.name())
                    .setDate(actualDetail.getPaidInDate())
                    .setContractId(contractId)
                    .setReceiptId(contractReceipt.getId())
                    .setContractCode(contractBaseInfo.getContractCode())
                    .setClientName(clientId2Name.get(contractBaseInfo.getClientId()))
                    .setClientId(contractBaseInfo.getClientId())
                    .setStampDuty(contractReceipt.getStampDuty());
            stampDutyService.save(monthlyStampDuty);
        }
    }


    /**
     * 资金端-融资-印花税入库
     * @param baseInfo
     */
    @Transactional(rollbackFor = Throwable.class)
    public void saveRecordFin(FundFinancingBaseInfo baseInfo) {
        // 融资：业务类型=项目贷款/流动资金贷款
        if(!Arrays.asList(FundFinancingBizTypeEnum.PROJECT_LOAN.name(), FundFinancingBizTypeEnum.WORKING_CAPITAL_LOAN.name()).contains(baseInfo.getBusinessType())){
            return;
        }
        List<FundOrganization> organizationList = organizationService.getByFinancingId(baseInfo.getId());
        BigDecimal financingAmount = BigDecimal.valueOf(Optional.ofNullable(baseInfo.getFinancingAmount()).orElse(0L));
        MonthlyStampDuty monthlyStampDuty = new MonthlyStampDuty();
        monthlyStampDuty.setType(StampDutyTypeEnum.FIN_FIN.name())
                .setDate(baseInfo.getActualLoanDate())
                .setFinancingId(baseInfo.getId())
                .setFinancingCode(baseInfo.getFinancingCode())
                .setOrganizationName(Optional.ofNullable(organizationList.get(0)).map(FundOrganization::getOrganizationName).orElse(null))
                .setStampDuty(new BigDecimal("0.00005").multiply(financingAmount).longValue());
        stampDutyService.save(monthlyStampDuty);
    }

    /**
     * 资金端-直融-印花税入库
     * @param baseInfo
     */
    @Transactional(rollbackFor = Throwable.class)
    public void saveRecordDirectFin(FundDirectFinancingBaseInfo baseInfo) {
        // 直融：项目类型=ABS/ABN
        if (baseInfo != null && Arrays.asList(DirectFinancingType.ABS.name(), DirectFinancingType.ABN.name()).contains(baseInfo.getDirectFinancingType())) {
            MonthlyStampDuty one = this.getOne(Wrappers.<MonthlyStampDuty>lambdaQuery().eq(MonthlyStampDuty::getFinancingId, baseInfo.getId())
                    .eq(MonthlyStampDuty::getType, StampDutyTypeEnum.FIN_DIRECT_FIN.name()));
            BigDecimal financingAmount = BigDecimal.valueOf(Optional.ofNullable(baseInfo.getFinancingAmount()).orElse(0L));
            MonthlyStampDuty monthlyStampDuty = new MonthlyStampDuty();
            monthlyStampDuty.setType(StampDutyTypeEnum.FIN_DIRECT_FIN.name())
                    .setDate(baseInfo.getDurationFrom())
                    .setFinancingId(baseInfo.getId())
                    .setFinancingCode(baseInfo.getFinancingCode())
                    .setOrganizationName(baseInfo.getProductName())
                    .setStampDuty(new BigDecimal("0.00005").multiply(financingAmount).longValue());
            if(one == null) {
                this.save(monthlyStampDuty);
            }else{
                this.update(monthlyStampDuty,Wrappers.<MonthlyStampDuty>lambdaUpdate().eq(MonthlyStampDuty::getFinancingId,baseInfo.getId()));
            }

        }
    }

    public void removeStampDuty(Long id,StampDutyTypeEnum typeEnum) {
        if(typeEnum == null){
            return;
        }
        LambdaUpdateWrapper<MonthlyStampDuty> wrapper = Wrappers.lambdaUpdate();
        switch (typeEnum){
            case PROJ:
                wrapper.eq(MonthlyStampDuty::getType,StampDutyTypeEnum.PROJ.name())
                        .eq(MonthlyStampDuty::getContractId,id)
                        .eq(MonthlyStampDuty::getDeleted,false)
                        .eq(MonthlyStampDuty::getIsConfirmed,false);
                break;
            case FIN_FIN:
                wrapper.eq(MonthlyStampDuty::getType,StampDutyTypeEnum.FIN_FIN.name())
                        .eq(MonthlyStampDuty::getFinancingId,id)
                        .eq(MonthlyStampDuty::getDeleted,false)
                        .eq(MonthlyStampDuty::getIsConfirmed,false);
                break;
            case FIN_DIRECT_FIN:
                wrapper.eq(MonthlyStampDuty::getType,StampDutyTypeEnum.FIN_DIRECT_FIN.name())
                        .eq(MonthlyStampDuty::getFinancingId,id)
                        .eq(MonthlyStampDuty::getDeleted,false)
                        .eq(MonthlyStampDuty::getIsConfirmed,false);
                break;
        }
        wrapper.set(MonthlyStampDuty::getDeleted,true);
        this.update(wrapper);
    }
}
