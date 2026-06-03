package cn.zswltech.mithras.service.service.margin;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.enums.margin.RecordTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.third.enums.CQBusinessTypeENUM;
import cn.zswltech.mithras.third.enums.CQPaymentTypeENUM;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.service.mapper.margin.MarginWriteOffRecordMapper;
import cn.zswltech.mithras.service.mapper.margin.WarrantyBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.margin.WarrantyRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.margin.MarginWriteOffRecord;
import cn.zswltech.mithras.service.mapper.model.margin.WarrantyBaseInfo;
import cn.zswltech.mithras.service.mapper.model.margin.WarrantyRecordInfo;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.application.dto.CQ2PaymentVO;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @create: 2022-08-18
 **/
@Slf4j
@Service
public class WarrantyRecordService extends ServiceImpl<WarrantyRecordInfoMapper, WarrantyRecordInfo> {
    @Resource
    private WarrantyRecordInfoMapper warrantyRecordInfoMapper;
    @Resource
    private WarrantyBaseInfoMapper warrantyBaseInfoMapper;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private MarginWriteOffRecordMapper marginWriteOffRecordMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void financialAdd(WarrantyRecordInfo info) {
        int count = warrantyRecordInfoMapper.selectCount(Wrappers.<WarrantyRecordInfo>lambdaQuery()
                .eq(WarrantyRecordInfo::getWarrantyId, info.getWarrantyId()).eq(WarrantyRecordInfo::getRecordType, info.getRecordType()));
        info.setSortId(String.valueOf(count + 1));
        warrantyRecordInfoMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void financialWriteOff(WarrantyRecordInfo recordInfo, String collectionCode, Long paidInAmount, LocalDate paidInDate) {
        WarrantyBaseInfo warrantyBaseInfo = warrantyBaseInfoMapper.selectById(recordInfo.getWarrantyId());
        MarginWriteOffRecord writeOffRecord = new MarginWriteOffRecord();
        writeOffRecord.setRecordId(warrantyBaseInfo.getId());
        writeOffRecord.setRecordId(recordInfo.getId());

        long warrantyAmount = LongUtil.null2zero(warrantyBaseInfo.getCollectionAmount());
        writeOffRecord.setOperate(recordInfo.getWriteOffStatus());
        Long collectionAmount = LongUtil.null2zero(recordInfo.getCollectionAmount());
        if (RecordTypeEnum.COLLECTION.name().equals(recordInfo.getRecordType())) {
            warrantyAmount = warrantyAmount + collectionAmount;
            warrantyBaseInfo.setCollectionAmount(warrantyAmount);
            warrantyBaseInfo.setCollectionDate(recordInfo.getCollectionDate());
        } else if (RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_WARRANTY.name().equals(recordInfo.getCollectionType())) {
            warrantyAmount = warrantyAmount - collectionAmount;
            warrantyBaseInfo.setCollectionAmount(warrantyAmount);
            warrantyBaseInfo.setBackAmount(LongUtil.null2zero(warrantyBaseInfo.getBackAmount()) + collectionAmount);
        }
        warrantyBaseInfoMapper.updateById(warrantyBaseInfo);
        /*质保金核销完毕不推送苍穹，只有流水完成会推送*/
//        if (warrantyBaseInfo.getCollectionAmount() <= 0) {
//            //质保金核销完成，通知苍穹
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                @Override
//                public void afterCommit() {
//                    List<WarrantyRecordInfo> warrantyRecordInfos = warrantyRecordInfoMapper.selectList(Wrappers.<WarrantyRecordInfo>lambdaQuery()
//                            .eq(WarrantyRecordInfo::getWarrantyId, warrantyBaseInfo.getId())
//                            .orderByAsc(WarrantyRecordInfo::getCollectionDate));
//                    if (ObjectUtil.isNotEmpty(warrantyRecordInfos)) {
//                        List<CQ2PaymentVO> vos = new ArrayList<>();
//                        warrantyRecordInfos.stream().filter(e -> StrUtil.equalsAny(e.getCollectionType(), RecordTypeEnum.REFUND_WARRANTY.name(), RecordTypeEnum.REFUND.name())).forEach(recordInfo -> vos.add(buildPayment(warrantyBaseInfo, recordInfo)));
//                        financialManagerServiceImpl2.cq2PaymentExec(SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(warrantyBaseInfo.getContractId()), vos);
//                    }
//                }
//            });
//        }
        writeOffRecord.setOperateInfo(String.valueOf(warrantyBaseInfo.getWarrantyCode()));
        writeOffRecord.setMarginAmount(warrantyAmount);
        marginWriteOffRecordMapper.insert(writeOffRecord);
    }

    public CQ2PaymentVO buildPayment(WarrantyBaseInfo baseInfo, WarrantyRecordInfo detail) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            return null;
        }
        Client client = getBean(ClientService.class).getById(contractBaseInfo.getClientId());
        if (ObjectUtil.isEmpty(client)) {
            return null;
        }
        //20251217付款核销推送-设置业务类型
        String leaseTypeCode = null;
        if (ProjectBizType.ZL.name().equals(contractBaseInfo.getBizType())) {
            if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.hui_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.SALE_AND_LEASEBACK.getCode();//051售后回租
            } else if (Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                leaseTypeCode = CQBusinessTypeENUM.FINANCE_LEASING.getCode();//048融资租赁
            }
        }
        CQ2PaymentVO vo = new CQ2PaymentVO();
        vo.setCico_payzh_number(detail.getOurAccountNumber());
        OrgDO orgDO = SpringContextHolder.getBean(OrgDOMapper.class).selectByPrimaryKey(contractBaseInfo.getBizDeptId());
        vo.setCico_dept_number(ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId()));
        //vo.setPayzh_bank_name(detail.getOurAccountBank());
        vo.setApplydate(detail.getCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        vo.setCico_srcbillno(String.join("-", baseInfo.getWarrantyCode(), UUIDUtil.genUuid()));
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK05_006.getCode());
        entry.setE_asstacttype(FinancialConstants.BD_SUPPLIER);
        entry.setE_applyamount(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(detail.getCollectionAmount()))));
        entry.setE_asstact_name(client.getClientName());
        entry.setCico_pay_bank_number_number(detail.getOurAccountNumber());
        entry.setCico_pay_bank_name_name(detail.getOurAccountBank());
        entry.setE_settlementtype_number(detail.getCollectionType());
        entry.setE_asstact(client.getClientCode());
        entry.setCico_businesstype_number(leaseTypeCode);
        vo.setEntry(CollectionUtil.toList(entry));
        vo.setCico_paynum_rby(detail.getBankDetailNo());
        vo.setBilltype_number(FinancialConstants.AP_PAYAPPLY_BT_ZB);
        vo.setApplycause(String.format("%s:%s,%s", id2NameService.deptId2NameSingle(contractBaseInfo.getBizDeptId()), client.getClientName(),
                Optional.of(ProjectBizType.valueOf(contractBaseInfo.getBizType())).map(ProjectBizType::display).orElse(null)));
        //这里是项目端质保金退款
        vo.setSource(ExceptionSourceENUM.BUSINESS_WARRANTY.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(baseInfo.getWarrantyCode());
        return vo;
    }
}
