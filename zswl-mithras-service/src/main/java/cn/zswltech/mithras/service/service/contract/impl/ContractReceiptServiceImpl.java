package cn.zswltech.mithras.service.service.contract.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptQueryActualTaxREQ;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptUpdateStartDateREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractReceiptComputeActualTaxRSP;
import cn.zswltech.mithras.dto.file.FileBatchRemoveREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.gendoc.render.ContractActualRentRender;
import cn.zswltech.mithras.contract.mapper.contract.ContractReceiptMapper;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.Listener.ContractReceiptDeleteEvent;
import cn.zswltech.mithras.contract.application.dto.FinancialCostsBO;
import cn.zswltech.mithras.contract.application.dto.StampDutyContextBO;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.contract.versioning.application.ContractReceiptLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.FlowUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@Slf4j
@Service
public class ContractReceiptServiceImpl extends ServiceImpl<ContractReceiptMapper, ContractReceipt> implements ContractReceiptService {
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractService contractService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractActualRentRender contractActualRentRender;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private FtpAssessmentInfoService ftpAssessmentInfoService;
    @Resource
    private FileService fileService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void removeReceiptById(Long receiptId) {
        ContractReceipt contractReceipt = this.getById(receiptId);
        if (Objects.isNull(contractReceipt)) {
            throw new MithrasException("没有找到对应借据");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractReceipt.getContractId());
        Assert.isTrue(!Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name()), () -> MithrasException.newException("直租业务不允许删除唯一的借据"));
        // 因为入参没有合同id，无法使用通用注解处理，手动调用
        //contractBaseOperationAuthChecker.check(BusinessModuleEnum.CONTRACT, ContractBaseInfoMapper.class, contractReceipt.getId(), null);
        // 查询对应借据下是否存在已核销现金流
        List<ContractRentActual> isVerifiedList = contractRentActualService.listVerifiedRentActual(contractReceipt.getContractId());
        for (ContractRentActual contractRentActual : isVerifiedList) {
            if (Objects.equals(receiptId, contractRentActual.getReceiptId())) {
                throw new MithrasException("该笔借据下存在已核销的现金流，借据删除失败");
            }
        }
        // 删除借据表
        this.removeById(receiptId);
        // 删除现金流表
        contractRentActualService.remove(contractReceipt.getContractId(), contractReceipt.getId());
        // 取消付款申请的关联关系
        paymentBaseInfoService.cancelJoinReceiptByReceiptId(receiptId);
        // 删除FTP价格考核信息表
        ftpAssessmentInfoService.removeByReceiptId(receiptId);
        // 发送通知消息给依赖方
        ContractReceiptDeleteEvent event = new ContractReceiptDeleteEvent(contractReceipt);
        ApplicationContextUtil.getApplicationContext().publishEvent(event);
    }

    @Override
    public Map<Long, ContractReceipt> getMapByIds(List<Long> ids, String version) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<ContractReceipt> list;
        if (ObjectUtil.isNull(version)) {
            list = this.listByIds(ids);
        } else {
            list = contractReceiptLibService.listByVersionIds(ids, version);
        }
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<Long, ContractReceipt> result = new HashMap<>();
        for (ContractReceipt contractReceipt : list) {
            result.put(contractReceipt.getId(), contractReceipt);
        }
        return result;
    }

    @Override
    public Map<Long, Boolean> checkCanRemove(Long contractId) {
        List<ContractReceipt> contractReceiptList = this.listByContractId(contractId);
        if (CollectionUtils.isEmpty(contractReceiptList)) {
            return Collections.emptyMap();
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        List<Long> receiptIds = contractReceiptList.stream().map(ContractReceipt::getId).collect(Collectors.toList());
        Map<Long, ContractReceiptLib> contractReceiptLibMap = contractReceiptLibService.getMapByOriginIds(receiptIds);
        Map<Long, Boolean> resultMap = new HashMap<>(contractReceiptList.size());
        for (ContractReceipt contractReceipt : contractReceiptList) {
            if (Objects.nonNull(contractReceiptLibMap.get(contractReceipt.getId()))) {
                resultMap.put(contractReceipt.getId(), Boolean.FALSE);
                continue;
            }
            // 如果没有历史版本则需要进一步判断相关状态来确定借据是否可以删除
            if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_RECEIPT_UNCOMMIT.name())) {
                resultMap.put(contractReceipt.getId(), Boolean.TRUE);
            } else {
                // 判断是否有流程，有的话流程在发起人状态也可以删除
                ProcessResp processResp = contractService.findRelatedProcess(contractId);
                if (Objects.nonNull(processResp) && ProcessModelTypeEnum.ContractAddNewReceiptFlow.name().equals(processResp.getModelKey())) {
                    boolean b = FlowUtil.isStartUserNode(processResp);
                    resultMap.put(contractReceipt.getId(), b);
                } else {
                    resultMap.put(contractReceipt.getId(), Boolean.FALSE);
                }
            }
        }
        return resultMap;
    }

    @Override
    public List<ContractReceipt> listByContractId(Long contractId) {
        LambdaQueryWrapper<ContractReceipt> query = Wrappers.lambdaQuery();
        query.eq(ContractReceipt::getContractId, contractId);
        return this.list(query);
    }

    @Override
    public Integer getNextSequenceByContractId(Long contractId) {
        LambdaQueryWrapper<ContractReceipt> query = Wrappers.lambdaQuery();
        query.eq(ContractReceipt::getContractId, contractId);
        query.isNotNull(ContractReceipt::getSequence);
        query.orderByDesc(ContractReceipt::getSequence);
        query.last(StringUtil.mysqlLimitOne());
        ContractReceipt last = this.getOne(query);
        if (Objects.isNull(last) || Objects.isNull(last.getSequence())) {
            return 1;
        }
        return last.getSequence() + 1;
    }

    @Override
    public ContractReceipt getOneByPaymentCode(String paymentCode) {
        LambdaQueryWrapper<ContractReceipt> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ContractReceipt::getPaymentApplyCode, paymentCode);
        queryWrapper.last(StringUtil.mysqlLimitOne());
        return this.getOne(queryWrapper);
    }

    @Override
    public void checkActualIrrByContractId(Long contractId) {
        List<ContractReceipt> contractReceiptList = this.listByContractId(contractId);
        if (CollectionUtil.isEmpty(contractReceiptList)) {
            return;
        }
        for (ContractReceipt contractReceipt : contractReceiptList) {
            if (Objects.isNull(contractReceipt.getActualIrr())) {
                throw new MithrasException(String.format("借据<%s>的实际IRR为空，请补充", contractReceipt.getReceiptCode()));
            }
            List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
            if (CollectionUtil.isNotEmpty(paymentBaseInfos)) {
                for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
                    if (LongUtil.null2zero(paymentBaseInfo.getLowestIrr()) > LongUtil.null2zero(contractReceipt.getActualIrr())) {
                        throw new MithrasException("实际irr低于最低irr");
                    }
                }
            }

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<List<ContractReceiptComputeActualTaxRSP>> computeFinancialCosts(ContractReceiptQueryActualTaxREQ req, boolean flag) {
        List<ContractReceiptComputeActualTaxRSP> rsp = new ArrayList<>();
        //1.校验参数
        //查询合同详细信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            throw new RuntimeException("合同不存在");
        }

        //如果存在版本条件,直接从版本表中取值并封装返回数据
        if (!ObjectUtil.isNull(req.getVersion())) {
            List<ContractReceiptLib> contractReceiptLibs = contractReceiptLibService.listByContractIdVersion(req.getContractId(), req.getVersion());
            if (CollectionUtils.isNotEmpty(contractReceiptLibs)) {
                for (ContractReceiptLib contractReceiptLib : contractReceiptLibs) {
                    //封装返回数据
                    ContractReceiptComputeActualTaxRSP contractReceiptQueryActualTax = getContractReceiptQueryActualTaxRSP(contractReceiptLib);
                    rsp.add(contractReceiptQueryActualTax);
                }
            }
            return R.ok(rsp);
        }

        //不存在版本条件
        //查找借据
        List<ContractReceipt> contractReceiptList = contractReceiptService.listByContractId(req.getContractId());
        if (CollectionUtils.isEmpty(contractReceiptList)) {
            return R.ok(rsp);
        }
        //2.税率：直租税率：13%    非直租税率：6%
        BigDecimal taxRate = LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType()) ? GlobalConstants.TAX_RATE_ZHI_ZU : GlobalConstants.TAX_RATE_FEI_ZHI_ZU;

        for (ContractReceipt contractReceipt : contractReceiptList) {
            //查询实际租金列表数据
            List<ContractRentActual> contractRentActualList = getContractRentActuals(contractReceipt.getId());
            if (CollectionUtils.isEmpty(contractRentActualList)) {
                continue;
            }

            //若主表数据为空或者flag为true，则计算
            if (Objects.isNull(contractReceipt.getRentExcludingTax())
                    || Objects.isNull(contractReceipt.getTax())
                    || Objects.isNull(contractReceipt.getExcludingInterestTax())
                    || Objects.isNull(contractReceipt.getTaxRate())
                    || (Objects.isNull(contractReceipt.getStampDuty()) && LeaseType.of(contractBaseInfo.getLeaseType()) != null)
                    || flag) {
                //计算财务成本信息
                FinancialCostsBO financialCosts = getFinancialCosts(contractBaseInfo, contractRentActualList, taxRate, contractReceipt.getId());

                contractReceipt.setTaxRate(Util.toMithrasUnit(taxRate.multiply(BigDecimal.valueOf(100))));
                contractReceipt.setTax(Util.mithrasLongDecimalTwo(financialCosts.getTax().longValue()));
                contractReceipt.setExcludingInterestTax(Util.mithrasLongDecimalTwo(financialCosts.getExcludingInterestTax().longValue()));
                contractReceipt.setRentExcludingTax(Util.mithrasLongDecimalTwo(financialCosts.getRentExcludingTax().longValue()));
//                contractReceipt.setStampDuty(Util.mithrasLongDecimalTwo(financialCosts.getStampDuty().longValue()));
                StampDutyContextBO stampDutyContextBO = financialCosts.getStampDutyContextBO();
                if (Objects.nonNull(stampDutyContextBO)) {
                    contractReceipt.setStampDutyTaxRateZL(stampDutyContextBO.getTaxRateZL());
                    contractReceipt.setStampDutyTaxRateMM(stampDutyContextBO.getTaxRateMM());
                    contractReceipt.setActualPayAmount(stampDutyContextBO.getActualPayAmount().longValue());
                    contractReceipt.setActualServiceFeeWithoutTax(stampDutyContextBO.getServiceFeeWithoutTax().longValue());
                    contractReceipt.setStampDutyZL(stampDutyContextBO.getTaxZL().longValue());
                    contractReceipt.setStampDutyMM(stampDutyContextBO.getTaxMM().longValue());
                    contractReceipt.setStampDuty(stampDutyContextBO.getTax().longValue());
                }
                contractReceiptService.updateById(contractReceipt);
            }

            //封装返回数据
            ContractReceiptComputeActualTaxRSP contractReceiptQueryActualTax = getContractReceiptQueryActualTaxRSP(contractReceipt);
            rsp.add(contractReceiptQueryActualTax);
        }

        return R.ok(rsp);
    }

    private ContractReceiptComputeActualTaxRSP getContractReceiptQueryActualTaxRSP(ContractReceipt contractReceipt) {
        return new ContractReceiptComputeActualTaxRSP()
                .setReceiptId(contractReceipt.getId())
                .setTaxRate(contractReceipt.getTaxRate())
                .setExcludingInterestTax(contractReceipt.getExcludingInterestTax())
                .setTax(contractReceipt.getTax())
                .setRentExcludingTax(contractReceipt.getRentExcludingTax())
                .setStampDuty(contractReceipt.getStampDuty());
    }

    private List<ContractRentActual> getContractRentActuals(Long contractReceiptId) {
        LambdaQueryWrapper<ContractRentActual> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentActual::getReceiptId, contractReceiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return contractRentActualService.list(query);

    }

    @Override
    public R<Void> generate(ContractReceiptQueryActualTaxREQ req) {
        //查询合同详细信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (ObjectUtil.isEmpty(contractBaseInfo)) {
            throw new MithrasException("合同不存在");
        }
        ByteArrayInputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            //删除实际租金表重新生成
            //获取已初始化的实际租金表
            List<MaterialsList> materialsLists = materialsListService.getListByFileName(contractBaseInfo.getId(),BusinessModuleEnum.CONTRACT.name(),ContractStatus.START_RENT.name(),null,"实际租金表" + GlobalConstants.OFFICE_WORD_SUFFIX);
            if(CollectionUtils.isNotEmpty(materialsLists)){
                List<Long> fileIds = materialsLists.stream().map(MaterialsList::getId).collect(Collectors.toList());
                //删除方法入参
                FileBatchRemoveREQ removeREQ = new FileBatchRemoveREQ();
                removeREQ.setMainId(contractBaseInfo.getId());
                removeREQ.setModuleType(BusinessModuleEnum.CONTRACT.name());
                removeREQ.setFileIds(fileIds);
                Map<String, Object> ext = new HashMap<>();
                ext.put("queryType",ContractStatus.START_RENT.name());
                removeREQ.setExt(ext);
                //批量删除
                fileService.batchRemove(removeREQ);
            }
            os = new ByteArrayOutputStream();
            String fileName = contractActualRentRender.render(os, contractBaseInfo);
            is = new ByteArrayInputStream(os.toByteArray());
            Long fileId = materialsListService.add(is, fileName, contractBaseInfo.getId(), ContractStatus.START_RENT.name(), null, BusinessModuleEnum.CONTRACT.name(), YesOrNoNumberEnum.YES);
            //SpringUtil.getBean(ContractSignInfoService.class).saveContractSignInfo(contractActualRentRender, contractBaseInfo, contractBaseInfo.getId(), fileId, contractActualRentRender.signatories(contractBaseInfo));
            return R.ok();
        } catch (MithrasException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成实际租金表发生未知异常[{}]", contractBaseInfo.getId(), e);
            throw new MithrasException("生成实际租金表发生未知异常");
        } finally {
            if (!Objects.isNull(is)) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("关闭输入流异常", e);
                }
            }
            if (!Objects.isNull(os)) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("关闭输出流异常", e);
                }
            }
        }
    }

    /**
     * 计算合同起租财务成本
     *
     * @param contractBaseInfo 合同详情
     * @param rentActualList   实际租金数据
     * @param taxRate          税率
     * @param receiptId        借据id
     * @return 财务成本信息
     */
    public FinancialCostsBO getFinancialCosts(ContractBaseInfo contractBaseInfo, List<ContractRentActual> rentActualList, BigDecimal taxRate, Long receiptId) {
        /*
            1. 不含税利息: (sum（租金表中的利息）+ 首期利息)/（1+税率），保留2位小数
            2. 税额：
                (1)直租：  sum（租金表中的利息）+ 首期利息 - 不含税利息 + ((sum（租金表中的本金) + 首期租金) - ((sum（租金表中的本金)+ 首期租金）/（1+税率))）
                (2)其他：  sum（租金表中的利息）+ 首期利息 - 不含税利息
            3. 不含税租金：sum（租金表中的租金）+ 首期利息 + 首期租金 - 税额
            4.印花税：
                （1）回租/转租赁：租金表的 (不含税租金+已核销的服务费、咨询费/(1+6%))*万0.5
                （2）直租：       租金表的 (不含税租金+已核销的服务费、咨询费/(1+6%))*万0.5 + 已核销的"投放款"*万3；
                （3）经营性租赁： 租金表的 (不含税租金+已核销的咨询费/(1+6%))*千1

            注：sum（租金表中的利息）不包含首期利息；sum（租金表中的租金）不包含首期租金
                税率：直租税率：13%    非直租税率：6%
         */

        //租金表中将第0期剔除掉
        rentActualList = rentActualList.stream().filter(rentActual -> rentActual.getCashFlowPhase() != 0).collect(Collectors.toList());

        //本金总额
        Long capitalSum = rentActualList.stream().map(ContractRentActual::getPrincipal).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
        //利息总额
        Long interestSum = rentActualList.stream().map(ContractRentActual::getInterest).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);
        //租金总额
        Long rentSum = rentActualList.stream().map(ContractRentActual::getRent).filter(Objects::nonNull).reduce(Long::sum).orElse(0L);

        BigDecimal tax; // 税额
        BigDecimal rentExcludingTax; //不含税租金
        BigDecimal stampDuty; //印花税
        Long firstRent = 0L; // 首期租金
        Long firstInstallmentInterest = 0L; // 首期利息

        //以借据为维度，将所有的已核销的费用查找出来
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery().eq(PaymentBaseInfo::getReceiptId, receiptId));
        List<Long> paymentBaseInfoIds = paymentBaseInfos.stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfos = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(paymentBaseInfoIds)) {
            collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getPaymentId, paymentBaseInfoIds)
                    .eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name()));
        }

        //合同状态为新建的合同，首期租金、利息取报价方案的首期租金、首期利息
        if (ContractStatus.NEW.name().equals(contractBaseInfo.getContractStatus())) {
            ContractLeasePrice leasePrice = contractLeasePriceService.getByContractId(contractBaseInfo.getId());
            if (Objects.nonNull(leasePrice)) {
                firstRent = leasePrice.getProjDownPayment();
                firstInstallmentInterest = leasePrice.getFirstInstallmentInterest();
            }
        } else {
            //其他状态取借据维度已核销的首期租金、首期利息实际付款金额
            if (CollectionUtils.isNotEmpty(collectionBaseInfos)) {
                firstRent = collectionBaseInfos.stream()
                        .filter(collectionBaseInfo -> CashFlowItemEnum.FIRST_RENT.name().equals(collectionBaseInfo.getCashFlowItem()))
                        .mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum();
                firstInstallmentInterest = collectionBaseInfos.stream()
                        .filter(collectionBaseInfo -> CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && collectionBaseInfo.getPhase() == 0)
                        .mapToLong(e -> Optional.ofNullable(e.getCollectionAmount()).orElse(0L)).sum();
            }
        }

        //1.不含税利息的计算公式：【sum（租金表中的利息）+ 首期利息】/（1+税率），保留2位小数。
        BigDecimal excludingInterestTax = NumberUtil.div(interestSum + firstInstallmentInterest, BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP);

        //2.税额计算公式：
        if (LeaseType.zhi_zu.name().equals(contractBaseInfo.getLeaseType())) {
            //直租：sum（租金表中的利息）+ 首期利息 - 不含税利息 + （sum（租金表中的 本金+ 首期租金）- sum（租金表中的 本金+ 首期租金）/（1+税率））
            tax = NumberUtil.sub(BigDecimal.valueOf(interestSum + firstInstallmentInterest), excludingInterestTax)
                    .add(BigDecimal.valueOf(capitalSum + firstRent).subtract(NumberUtil.div(capitalSum + firstRent, BigDecimal.ONE.add(taxRate), 2, RoundingMode.HALF_UP)));
        } else {
            //其他：sum（租金表中的利息）+ 首期利息 - 不含税利息
            tax = NumberUtil.sub(BigDecimal.valueOf(interestSum + firstInstallmentInterest), excludingInterestTax);
        }

        //3.不含税租金： sum（租金表中的租金） + 首期利息 + 首期租金 - 税额
        rentExcludingTax = NumberUtil.sub(BigDecimal.valueOf(rentSum + firstInstallmentInterest + firstRent), tax);

        //4.计算印花税 todo 借据先用预关联的进行计算
        StampDutyContextBO stampDutyContextBO = getStampDuty(contractBaseInfo.getLeaseType(), rentExcludingTax, paymentBaseInfoIds, collectionBaseInfos);

        return FinancialCostsBO.builder()
                .taxRate(taxRate)
                .capitalSum(capitalSum)
                .interestSum(interestSum)
                .rentSum(rentSum)
                .excludingInterestTax(excludingInterestTax)
                .tax(tax)
//                .stampDuty(stampDuty)
                .stampDutyContextBO(stampDutyContextBO)
                .rentExcludingTax(rentExcludingTax).build();
    }

    private StampDutyContextBO getStampDuty(String leaseType, BigDecimal rentExcludingTax, List<Long> paymentBaseInfoIds, List<CollectionBaseInfo> collectionBaseInfos) {
        // 融资租赁合同印花税率
        BigDecimal taxRateZL = new BigDecimal("0.00005");
        // 买卖合同印花税率
        BigDecimal taxRateMM = new BigDecimal("0.0003");
        // 经营性租赁印花税税率
        BigDecimal taxRateJYX = new BigDecimal("0.001");
        long serviceFeeSum = 0L;
        if (CollectionUtils.isNotEmpty(collectionBaseInfos)) {
            serviceFeeSum = collectionBaseInfos.stream()
                    .filter(collectionBaseInfo -> Arrays.asList(CashFlowItemEnum.OTHERAMOUNT.name(), CashFlowItemEnum.COMMISSION.name()).contains(collectionBaseInfo.getCashFlowItem()))
                    .mapToLong(collectionBaseInfo -> Optional.ofNullable(collectionBaseInfo.getCollectionAmount()).orElse(0L)).sum();
        }
        //公用公式：(不含税租金+已核销的服务费、咨询费/(1+6%))
        BigDecimal serviceFeeWithoutTaxBD = BigDecimal.valueOf(serviceFeeSum).divide(BigDecimal.ONE.add(new BigDecimal("0.06")), 20, RoundingMode.HALF_UP);
        BigDecimal actualPayAmountBD = BigDecimal.ZERO;
        BigDecimal taxZLBD = BigDecimal.ZERO;
        BigDecimal taxMMBD = BigDecimal.ZERO;
        if (Objects.nonNull(leaseType)) {
            switch (LeaseType.of(leaseType)) {
                case zhi_zu:
                    //直租的印花税余额：租金表的 (不含税租金+已核销的服务费、咨询费/(1+6%))*万0.5 + 已核销的"投放款"*万3；
                    Long finishedSum = actualDetailService.calculatePaidAmount(paymentBaseInfoIds);
                    actualPayAmountBD = BigDecimal.valueOf(finishedSum);
                    taxMMBD = actualPayAmountBD.multiply(taxRateMM);
                    taxZLBD = rentExcludingTax.add(serviceFeeWithoutTaxBD).multiply(taxRateZL);
                    break;
                case hui_zu:
                    //回租/转租赁的印花税余额：租金表的 (不含税租金+已核销的服务费、咨询费/(1+6%))*万0.5
                    taxZLBD = rentExcludingTax.add(serviceFeeWithoutTaxBD).multiply(taxRateZL);
                    break;
                case jyx_zu:
                    //经营性租赁的印花税余额：租金表的 (不含税租金+已核销的咨询费/(1+6%))*千1
                    taxZLBD = rentExcludingTax.add(serviceFeeWithoutTaxBD).multiply(taxRateJYX);
                    break;
            }
        }
        StampDutyContextBO stampDutyContextBO = new StampDutyContextBO();
        stampDutyContextBO.setRentWithoutTax(rentExcludingTax);
        stampDutyContextBO.setServiceFeeWithoutTax(serviceFeeWithoutTaxBD);
        stampDutyContextBO.setActualPayAmount(actualPayAmountBD);
        stampDutyContextBO.setTaxRateZL(taxRateZL);
        stampDutyContextBO.setTaxRateMM(taxRateMM);
        stampDutyContextBO.setTaxZL(taxZLBD);
        stampDutyContextBO.setTaxMM(taxMMBD);
        stampDutyContextBO.setTax(taxZLBD.add(taxMMBD));
        return stampDutyContextBO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateActualStartDate(ContractReceiptUpdateStartDateREQ req) {
        log.info("updateActualStartDate req:{}", req);
        // 检查借据是否存在
        ContractReceipt contractReceipt = contractReceiptService.getById(req.getReceiptId());
        if (ObjectUtil.isEmpty(contractReceipt)) {
            throw new MithrasException(String.format("id为[%d]的借据不存在", req.getReceiptId()));
        }
        if (Objects.equals(contractReceipt.getIsFirstReceipt(), YesOrNoNumberEnum.YES.getCode())){
            throw MithrasException.newException("第一个借据不允许修改起租日期");
        }
        contractReceiptService.lambdaUpdate()
                .eq(ContractReceipt::getId, contractReceipt.getId())
                .set(ContractReceipt::getReceiptStartDate, LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN))
                .update();

        // 是同起租日也用到了实际起租日，所以需要同步更新
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getReceiptIdFinal, req.getReceiptId()));
        if (Objects.nonNull(paymentBaseInfo) && paymentBaseInfo.getSameStartDate()) {
            // 如果是默认起租日，则同步更新
            paymentBaseInfo.setDefaultCollectionDay(LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN).getDayOfMonth());
            paymentBaseInfoService.updateById(paymentBaseInfo);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateActualLeaseDate(ContractReceiptUpdateStartDateREQ req) {
        log.info("updateActualLeaseDate req:{}", req);
        if (CharSequenceUtil.isBlank(req.getProcessInstanceId())) {
            throw MithrasException.newException("流程实例id不能为空");
        }
        // 找到借据判断是否是非第一个借据
        ContractReceipt contractReceipt = contractReceiptService.getById(req.getReceiptId());
        if (Objects.isNull(contractReceipt)) {
            throw MithrasException.newException("借据不存在");
        }
        if (Objects.equals(contractReceipt.getIsFirstReceipt(), YesOrNoNumberEnum.YES.getCode())){
            throw MithrasException.newException("第一个借据不允许修改起租日期");
        }
        // 找到当前流程
        ProcessResp processResp = flowTaskApiService.queryProcessById(req.getProcessInstanceId());
        if (ObjectUtil.isEmpty(processResp)) {
            throw MithrasException.newException("流程不存在");
        }
        // 校验流程类型
        if (!CharSequenceUtil.equalsAny(processResp.getModelKey(), ProcessModelTypeEnum.ContractAddNewReceiptFlow.name(),
                ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.name())){
            throw MithrasException.newException("当前流程不允许修改借据起租日期");
        }
        // 校验当前审批节点
        if (!CharSequenceUtil.equalsAny(processResp.getCurTaskActivityIds(), "userTask_financeManager", "userTask_projectSponsor")) {
            throw MithrasException.newException("当前节点不允许修改借据起租日期");
        }
        // 校验结束，调用借据修改接口
        contractReceiptService.lambdaUpdate()
                .set(ContractReceipt::getReceiptStartDate, LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN))
                .eq(ContractReceipt::getId, contractReceipt.getId())
                .update();

        // 是同起租日也用到了实际起租日，所以需要同步更新
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getReceiptIdFinal, req.getReceiptId()));
        if (Objects.nonNull(paymentBaseInfo) && paymentBaseInfo.getSameStartDate()) {
            // 如果是默认起租日，则同步更新
            paymentBaseInfo.setDefaultCollectionDay(LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN).getDayOfMonth());
            paymentBaseInfoService.updateById(paymentBaseInfo);
        }
    }

    @Override
    public ContractReceipt getOntByReceiptCode(String receiptCode) {
        LambdaQueryWrapper<ContractReceipt> query = Wrappers.lambdaQuery();
        query.eq(ContractReceipt::getReceiptCode, receiptCode);
        return this.getOne(query);
    }

    /**
     * 计算综合IRR
     *
     */
    @Override
    public BigDecimal calculateCombinedIRR(Long contractId, String version) {
        // 主合同信息
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        //综合IRR只针对起租和结清合同
        if (!StrUtil.equals(ContractStatus.SETTLE.name(), contractBaseInfo.getContractStatus()) &&
                !StrUtil.equals(ContractStatus.START_RENT.name(), contractBaseInfo.getContractStatus())) {
            return null;
        }

        List<ContractReceipt> contractReceipts = contractReceiptService.listByContractId(contractId);
        //提取contractReceipts借据id
        List<Long> receiptIds = contractReceipts.stream().map(ContractReceipt::getId).collect(Collectors.toList());
        // 借据对应实际IRR  BigDecimal.valueOf(contractReceipt.getActualIrr()).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP)
        Map<Long, Integer> actualIrrMap = contractReceipts.stream().collect(Collectors.toMap(ContractReceipt::getId, ContractReceipt::getActualIrr));
        // 获取审核通过的付款申请
        List<PaymentBaseInfo> allPaymentBaseInfoList = paymentBaseInfoService.listEffectPaymentByContractId(contractId);
        // 借据对应申批通过的申请付款金额
        Map<Long, Long> applyAmountMap = allPaymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getReceiptId, PaymentBaseInfo::getApplyPaymentAmount));
        // 统计paymentBaseInfoList借据总金额ApplyPaymentAmount
        List<PaymentBaseInfo> paymentBaseInfoList = allPaymentBaseInfoList.stream().filter(x -> receiptIds.contains(x.getReceiptId())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return null;
        }
        long tmpApplyAmt = paymentBaseInfoList.stream().mapToLong(PaymentBaseInfo::getApplyPaymentAmount).sum();
        BigDecimal countApplyAmt = BigDecimal.valueOf(tmpApplyAmt).divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP);
        //计算综合IRR，公式：合同加权平均IRR=（（借据1的投放金额∗借据1的实际IRR）+（借据N的投放金额∗借据N）.../借据1的投放金额+借据N的投放金额...）∗100
        BigDecimal allPart = BigDecimal.ZERO;
        // 分子
        for (ContractReceipt contractReceipt : contractReceipts) {
            // 借据投放金额
            Long applyAmount = applyAmountMap.get(contractReceipt.getId());
            // 实际IRR
            Integer irr = actualIrrMap.get(contractReceipt.getId());
            // 判断receiptAmount和irr都不允许为空
            if (Objects.isNull(applyAmount) || Objects.isNull(irr)) {
                continue;
            }
            BigDecimal part = BigDecimal.valueOf(applyAmount)
                    .divide(BigDecimal.valueOf(10000)).setScale(2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(irr).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP));
            allPart = allPart.add(part);
        }
        // 计算综合IRR
        if (BigDecimal.ZERO.compareTo(allPart) == 0 || BigDecimal.ZERO.compareTo(countApplyAmt) == 0) {
            return null;
        }
        return allPart.divide(countApplyAmt, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }


}
