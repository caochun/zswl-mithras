package cn.zswltech.mithras.service.service.contract.impl;
import cn.zswltech.mithras.contract.core.application.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.application.ContractReceiptService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowVariableApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptQueryActualTaxREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualExportREQ;
import cn.zswltech.mithras.dto.contract.rent.ContractRentActualImportREQ;
import cn.zswltech.mithras.dto.projreview.cashflowplan.IRRCalculateResultRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.contract.convert.contract.ContractRentConvert;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.payment.domain.enums.PaymentStatusEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.excel.ExcelExporterFactory;
import cn.zswltech.mithras.projectprocess.excel.exporter.IRRCalculateExcelExporter;
import cn.zswltech.mithras.projectprocess.excel.importer.CashFlowExcelImporter;
import cn.zswltech.mithras.projectprocess.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.contract.excel.model.ContractRentActualExcelModel;
import cn.zswltech.mithras.projectprocess.excel.model.IRRCalculateExcelModel;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractRentActualMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.service.bo.CashFlowIRRBO;
import cn.zswltech.mithras.contract.application.dto.ContractActualCashFlowExporterBO;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.flow.ProcessService;
import cn.zswltech.mithras.contract.versioning.application.ContractBaseInfoLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractReceiptLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractRentActualLibService;
import cn.zswltech.mithras.contract.versioning.handler.impl.ContractRentReceiptLibHandle;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.payment.interfaces.dto.FillReceiptInfoReq;
import cn.zswltech.mithras.service.util.ContractUtil;
import cn.zswltech.mithras.service.util.FinancialUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description
 */
@Service
@Slf4j
public class ContractRentActualServiceImpl extends ServiceImpl<ContractRentActualMapper, ContractRentActual> implements ContractRentActualService {
    @Resource
    private CashFlowExcelImporter cashFlowExcelImporter;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractReceiptLibService contractReceiptLibService;
    @Resource
    private ContractRentReceiptLibHandle contractRentReceiptLibHandle;
    @Resource
    private ContractRentActualLibService contractRentActualLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibService contractBaseInfoLibService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private IRRCalculateExcelExporter irrCalculateExcelExporter;
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ProcessService processService;
    @Resource
    private FlowVariableApiService flowVariableApiService;
    @Resource
    private ContractLeasePriceService contractLeasePriceService;
    @Resource
    private ContractRentActualService thisService;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void importExcel(ContractRentActualImportREQ req) throws Exception {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同数据不存在"));
        if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name()) && Objects.nonNull(req.getReceiptId())) {
            Assert.notBlank(req.getChangeDate(), () -> MithrasException.newException("变更日期不能为空"));
        }
        // 解析Excel
        List<CashFlowExcelModel> excelModelList = cashFlowExcelImporter.parse(req.getFile().getInputStream());
        Assert.notEmpty(excelModelList, () -> MithrasException.newException("没有从导入文件中解析出需要导入的数据"));
        thisService.importExcelBySystem(req, excelModelList, contractBaseInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void importExcelBySystem(ContractRentActualImportREQ req, List<CashFlowExcelModel> actualRentList, ContractBaseInfo contractBaseInfo) {
        //计算xirr
        double xirr = this.calculateXIRR(actualRentList);
        // 过滤非租金现金流
        actualRentList = actualRentList.stream().filter(item -> Objects.nonNull(item.getCashFlowPhase()) && item.getCashFlowPhase() > 0).collect(Collectors.toList());
        // 处理借据
        ContractReceipt contractReceipt = thisService.doContractReceipt(req, contractBaseInfo);
        //更新回借据表
        contractReceipt.setXirr(xirr);
        contractReceiptService.updateById(contractReceipt);

        // 数据校验 直租无需校验
        if (!Objects.equals(LeaseType.zhi_zu.name(), contractBaseInfo.getLeaseType())) {
            PaymentBaseInfo paymentBaseInfo = null;
            if (Objects.isNull(req.getPaymentId())) {
                // 查询付款记录
                List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
                if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
                    Assert.isTrue(paymentBaseInfoList.size() <= 1, () -> MithrasException.newException("数据异常，找到多个本借据关联的付款申请"));
                    paymentBaseInfo = paymentBaseInfoList.get(0);
                }
            } else {
                paymentBaseInfo = paymentBaseInfoService.getById(req.getPaymentId());
            }
            if (Objects.isNull(paymentBaseInfo)) {
                this.checkWithoutPayment(actualRentList);
            } else {
                this.checkWithPayment(contractReceipt, actualRentList, paymentBaseInfo);
            }
        } else {
            for (CashFlowExcelModel cashFlowExcelModel : actualRentList) {
                cashFlowExcelModel.checkBasicRent();
            }
        }
        // 处理租金表
        this.doContractRentList(actualRentList, contractReceipt, contractBaseInfo);
        // 重新查一下最新的数据来确定合同实际结束日
        ContractBaseInfo toUpdate = new ContractBaseInfo();
        toUpdate.setId(contractBaseInfo.getId());
        LocalDate actualFinishDate = null;
        List<ContractRentActual> list = this.listByContract(contractBaseInfo.getId());
        if (!CollectionUtils.isEmpty(list)) {
            actualFinishDate = list.get(list.size() - 1).getCashFlowDate();
        }
        // 调整主合同实际结束日
        if (Objects.nonNull(actualFinishDate)) {
            contractBaseInfoService.updateActualFinishDate(contractBaseInfo.getId(), actualFinishDate);
        }
        // 重新计算税额
        ContractReceiptQueryActualTaxREQ contractReceiptQueryActualTaxREQ = new ContractReceiptQueryActualTaxREQ();
        contractReceiptQueryActualTaxREQ.setContractId(contractBaseInfo.getId());
        contractReceiptService.computeFinancialCosts(contractReceiptQueryActualTaxREQ, true);
    }

    @Override
    public Map<Long, List<ContractRentActual>> getMapGroupByReceipt(Long contractId, String version) {
        LambdaQueryWrapper<ContractRentActual> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentActual::getContractId, contractId);
        query.orderByAsc(ContractRentActual::getReceiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        List<ContractRentActual> contractRentActualList;
        if (ObjectUtil.isNull(version)) {
            contractRentActualList = this.list(query);
        } else {
            contractRentActualList = contractRentActualLibService.listByContractVersion(contractId, version);
        }
        if (CollectionUtils.isEmpty(contractRentActualList)) {
            return Collections.emptyMap();
        }
        //  增加零期现金流
        List<ContractRentActual> contractRentActuals = new ArrayList<>();
        ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, contractId).one();
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.lambdaQuery().eq(CollectionBaseInfo::getContractId, contractId).list();
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        if (!ObjectUtils.isEmpty(contractRentActualList) && !ObjectUtils.isEmpty(contractLeasePrice)
                && !ObjectUtils.isEmpty(contractLeasePrice.getFirstInstallmentInterest()) && contractLeasePrice.getFirstInstallmentInterest() > 0) {
            Long firstInstallmentInterest = contractLeasePrice.getFirstInstallmentInterest();
            LocalDate collectionDate = null;
            Long collectionAmount = null;
            if (!ObjectUtils.isEmpty(collectionBaseInfoList)) {
                Optional<CollectionBaseInfo> collectionBaseInfoOptional = collectionBaseInfoList.stream().filter(collectionBaseInfo ->
                        CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && !Objects.isNull(collectionBaseInfo.getPhase()) && collectionBaseInfo.getPhase() == 0
                ).findFirst();

                if (collectionBaseInfoOptional.isPresent()) {
                    collectionDate = collectionBaseInfoOptional.get().getCollectionDate();
                    collectionAmount = collectionBaseInfoOptional.get().getCollectionAmount();
                }
            }
            String cashFlowCode = contractRentActualList.get(0).getCashFlowCode();
            ContractRentActual rentActual = contractRentActualList.get(0);
            ContractRentActual contractRentActual = new ContractRentActual();
            contractRentActual.setCashFlowCode(!Objects.isNull(cashFlowCode) && !cashFlowCode.isEmpty() ? cashFlowCode.substring(0, cashFlowCode.length() - 1) + "0" : null);
            contractRentActual.setContractId(contractId);
            contractRentActual.setReceiptId(rentActual.getReceiptId());
            contractRentActual.setCashFlowDate(ObjectUtil.isNotEmpty(contractBaseInfo.getActualLeaseDate()) ? contractBaseInfo.getActualLeaseDate() : contractBaseInfo.getEstimatedLeaseDate());
            contractRentActual.setCashFlowPhase(0);
            contractRentActual.setRent(firstInstallmentInterest);
            contractRentActual.setPrincipal(0L);
            contractRentActual.setRemainingPrincipal(0L);
            contractRentActual.setInterest(firstInstallmentInterest);
            contractRentActual.setCollectionDate(collectionDate);
            contractRentActual.setCollectionAmount(collectionAmount);
            contractRentActuals.add(contractRentActual);
            contractRentActuals.addAll(contractRentActualList);
            contractRentActualList = contractRentActuals;
        }
        // 使用LinkedHashMap保证数据有序
        Map<Long, List<ContractRentActual>> map = new LinkedHashMap<>();
        for (ContractRentActual contractRentActual : contractRentActualList) {
            List<ContractRentActual> list = map.get(contractRentActual.getReceiptId());
            if (Objects.isNull(list)) {
                list = new LinkedList<>();
                map.put(contractRentActual.getReceiptId(), list);
            }
            list.add(contractRentActual);
        }
        return map;
    }

    @Override
    public List<ContractRentActual> listByContract(Long contractId) {
        LambdaQueryWrapper<ContractRentActual> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActual::getContractId, contractId);
        query.orderByAsc(ContractRentActual::getCashFlowDate);
        return this.list(query);
    }

    @Override
    public List<ContractRentActual> firstRentByContract(Long contractId) {
        ContractRentActual firstReceipt = baseMapper.selectOne(Wrappers.<ContractRentActual>lambdaQuery()
                .select(ContractRentActual::getReceiptId)
                .eq(ContractRentActual::getContractId, contractId)
                .groupBy(ContractRentActual::getReceiptId)
                .orderByAsc(ContractRentActual::getId)
                .last(StringUtil.mysqlLimitOne())
        );
        if (ObjectUtil.isNotEmpty(firstReceipt)) {
            return baseMapper.selectList(Wrappers.<ContractRentActual>lambdaQuery()
                    .eq(ContractRentActual::getContractId, contractId)
                    .eq(ContractRentActual::getReceiptId, firstReceipt.getReceiptId())
                    .isNotNull(ContractRentActual::getCashFlowCode)
                    .orderByAsc(ContractRentActual::getCashFlowPhase));
        }
        return null;
    }

    @Override
    public List<ContractRentActual> listByReceipt(Long receiptId) {
        LambdaQueryWrapper<ContractRentActual> query = Wrappers.lambdaQuery();
        query.eq(ContractRentActual::getReceiptId, receiptId);
        query.orderByAsc(ContractRentActual::getCashFlowPhase);
        return this.list(query);
    }

    @Override
    public List<ContractRentActual> listByReceipts(Collection<Long> receiptIds) {
        LambdaQueryWrapper<ContractRentActual> query = Wrappers.lambdaQuery();
        query.in(ContractRentActual::getReceiptId, receiptIds);
        return this.list(query);
    }

    @Override
    public List<ContractRentActual> listVerifiedRentActual(Long contractId) {
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(CollectionBaseInfo::getContractId, contractId);
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        query.in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name(), CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name()));
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(query);
        if (CollectionUtils.isEmpty(collectionBaseInfoList)) {
            return Collections.emptyList();
        }
        List<Long> ids = collectionBaseInfoList.stream().map(CollectionBaseInfo::getRentActualId).collect(Collectors.toList());
        return this.listByIds(ids);
    }

    @Override
    public void remove(Long contractId, Long receipt) {
        LambdaQueryWrapper<ContractRentActual> query = new LambdaQueryWrapper<>();
        query.eq(ContractRentActual::getContractId, contractId);
        query.eq(ContractRentActual::getReceiptId, receipt);
        this.remove(query);
    }

    @Override
    public void exportExcel(boolean isHistory, ContractRentActualExportREQ req, OutputStream outputStream) throws Exception {
        ContractReceipt contractReceipt;
        List<ContractRentActual> contractRentActualList;
        if (isHistory) {
            // 数据比对时候的历史数据导出
            // 传过来的借据id是版本表id
            ContractReceiptLib contractReceiptLib = contractReceiptLibService.getById(req.getReceiptId());
            Assert.notNull(contractReceiptLib, () -> MithrasException.newException("没有找到对应历史版本的借据信息"));
            contractReceipt = contractRentReceiptLibHandle.actualLib2Entity(contractReceiptLib);
            contractRentActualList = contractRentActualLibService.listByReceiptVersion(contractReceiptLib.getOriginId(), contractReceiptLib.getVersion());
        } else {
            // 传过来的借据id是编辑区id
            if (StrUtil.isBlank(req.getVersion())) {
                contractReceipt = Assert.notNull(contractReceiptService.getById(req.getReceiptId()), () -> MithrasException.newException("借据不存在"));
                contractRentActualList = this.listByReceipt(req.getReceiptId());
            } else {
                ContractReceiptLib contractReceiptLib = contractReceiptLibService.getByOriginIdAndVersion(req.getReceiptId(), req.getVersion());
                contractReceipt = contractRentReceiptLibHandle.actualLib2Entity(contractReceiptLib);
                contractRentActualList = contractRentActualLibService.listByReceiptVersion(contractReceiptLib.getOriginId(), req.getVersion());
            }
        }
        ContractBaseInfo contractBaseInfo = Assert.notNull(contractBaseInfoService.getById(contractReceipt.getContractId()), () -> MithrasException.newException("合同不存在"));
        if (CollectionUtils.isEmpty(contractRentActualList)) {
            throw new MithrasException("暂无可导出的数据");
        } else {
            //  增加零期现金流
            List<ContractRentActual> contractRentActuals = new ArrayList<>();
            ContractRentActual contractRentActual = new ContractRentActual();
            ContractLeasePrice contractLeasePrice = contractLeasePriceService.lambdaQuery().eq(ContractLeasePrice::getContractId, contractReceipt.getContractId()).one();
            Long firstInstallmentInterest = Optional.ofNullable(contractLeasePrice).map(ContractLeasePrice::getFirstInstallmentInterest).orElse(0L);
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.lambdaQuery().eq(CollectionBaseInfo::getContractId, contractReceipt.getContractId()).list();
            if (!ObjectUtils.isEmpty(contractRentActualList) && !ObjectUtils.isEmpty(contractLeasePrice)
                    && !ObjectUtils.isEmpty(firstInstallmentInterest) && firstInstallmentInterest > 0) {
                LocalDate collectionDate = null;
                Long collectionAmount = null;
                if (!ObjectUtils.isEmpty(collectionBaseInfoList)) {
                    Optional<CollectionBaseInfo> collectionBaseInfoOptional = collectionBaseInfoList.stream().filter(collectionBaseInfo ->
                            CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && !Objects.isNull(collectionBaseInfo.getPhase()) && collectionBaseInfo.getPhase() == 0
                    ).findFirst();

                    if (collectionBaseInfoOptional.isPresent()) {
                        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoOptional.get();
                        collectionDate = collectionBaseInfo.getCollectionDate();
                        collectionAmount = collectionBaseInfo.getCollectionAmount();
                        contractRentActual.setId(collectionBaseInfo.getId());
                    }
                }
                String cashFlowCode = contractRentActualList.get(0).getCashFlowCode();
                ContractRentActual rentActual = contractRentActualList.get(0);
                contractRentActual.setCashFlowCode(!Objects.isNull(cashFlowCode) && !cashFlowCode.isEmpty() ? cashFlowCode.substring(0, cashFlowCode.length() - 1) + "0" : null);
                contractRentActual.setContractId(contractReceipt.getContractId());
                contractRentActual.setReceiptId(rentActual.getReceiptId());
                contractRentActual.setCashFlowDate(contractBaseInfo.getEstimatedLeaseDate());
                contractRentActual.setCashFlowPhase(0);
                contractRentActual.setRemainingPrincipal(0L);
                contractRentActual.setRent(firstInstallmentInterest);
                contractRentActual.setPrincipal(0L);
                contractRentActual.setInterest(firstInstallmentInterest);
                contractRentActual.setCollectionDate(collectionDate);
                contractRentActual.setCollectionAmount(collectionAmount);
                contractRentActuals.add(contractRentActual);
                contractRentActuals.addAll(contractRentActualList);
                contractRentActualList = contractRentActuals;
            }
        }
        ProjectBizType projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
        if (Objects.isNull(projectBizType)) {
            throw new MithrasException("未定义的业务类型");
        }
        ExcelExporterFactory.getContractActualRentExcelExporter(projectBizType).exportExcel(this.toExcelDataList(contractRentActualList), outputStream);
    }

    @Override
    public void exportRichExcel(boolean isHistory, ContractRentActualExportREQ req, OutputStream outputStream) {
        ContractActualCashFlowExporterBO contractActualCashFlowExporterBO = new ContractActualCashFlowExporterBO();
        contractActualCashFlowExporterBO.setHistory(isHistory);
        ProjectBizType projectBizType;
        if (isHistory) {
            // 比对数据版本
            ContractReceiptLib contractReceiptLib = Assert.notNull(contractReceiptLibService.getById(req.getReceiptId()), () -> MithrasException.newException("对应历史版本借据不存在"));
            ContractBaseInfoLib contractBaseInfoLib = Assert.notNull(contractBaseInfoLibService.getByOriginIdVersion(contractReceiptLib.getContractId(), contractReceiptLib.getVersion()), () -> MithrasException.newException("对应历史版本合同信息不存在"));
            contractActualCashFlowExporterBO.setContractBaseInfoLib(contractBaseInfoLib);
            contractActualCashFlowExporterBO.setContractReceiptLib(contractReceiptLib);
            contractActualCashFlowExporterBO.setVersion(contractBaseInfoLib.getVersion());
            projectBizType = ProjectBizType.of(contractBaseInfoLib.getBizType());
        } else {
            if (StrUtil.isBlank(req.getVersion())) {
                // 编辑区
                ContractReceipt contractReceipt = Assert.notNull(contractReceiptService.getById(req.getReceiptId()), () -> MithrasException.newException("对应借据不存在"));
                ContractBaseInfo contractBaseInfo = Assert.notNull(contractBaseInfoService.getById(contractReceipt.getContractId()), () -> MithrasException.newException("合同不存在"));
                contractActualCashFlowExporterBO.setContractBaseInfo(contractBaseInfo);
                contractActualCashFlowExporterBO.setContractReceipt(contractReceipt);
                contractActualCashFlowExporterBO.setContractBaseInfo(contractBaseInfo);
                projectBizType = ProjectBizType.of(contractBaseInfo.getBizType());
            } else {
                // 指定版本号历史版本
                ContractReceiptLib contractReceiptLib = Assert.notNull(contractReceiptLibService.getByOriginIdAndVersion(req.getReceiptId(), req.getVersion()), () -> MithrasException.newException("对应历史版本借据不存在"));
                ContractBaseInfoLib contractBaseInfoLib = Assert.notNull(contractBaseInfoLibService.getByOriginIdVersion(contractReceiptLib.getContractId(), contractReceiptLib.getVersion()), () -> MithrasException.newException("对应历史版本合同信息不存在"));
                contractActualCashFlowExporterBO.setContractBaseInfoLib(contractBaseInfoLib);
                contractActualCashFlowExporterBO.setContractReceiptLib(contractReceiptLib);
                contractActualCashFlowExporterBO.setVersion(contractBaseInfoLib.getVersion());
                contractActualCashFlowExporterBO.setHistory(true);
                projectBizType = ProjectBizType.of(contractBaseInfoLib.getBizType());
            }
        }
        if (Objects.isNull(projectBizType)) {
            throw new MithrasException("未定义的业务类型");
        }
        ExcelExporterFactory.getContractActualCashFlowExcelExporter(projectBizType).export(outputStream, contractActualCashFlowExporterBO);
    }

    @Override
    public List<ContractRentActual> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<ContractRentActual> query = Wrappers.lambdaQuery();
        query.in(ContractRentActual::getContractId, contractIds);
        return this.list(query);
    }

    @Override
    public LocalDate getContractRentExpirationDate(Long contractId) {
        if (ObjectUtil.isEmpty(contractId)) {
            return null;
        }
        ContractRentActual contractRentActual = baseMapper.selectOne(Wrappers.<ContractRentActual>lambdaQuery()
                .in(ContractRentActual::getContractId, contractId)
                .orderByDesc(ContractRentActual::getCashFlowDate)
                .last(StringUtil.mysqlLimitOne()));

        return ObjectUtil.isNull(contractRentActual) ? null : contractRentActual.getCashFlowDate();
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public IRRCalculateResultRSP calculateIRR(Long receiptId, boolean showDetailExcel) {
        ContractReceipt contractReceipt = contractReceiptService.getById(receiptId);
        if (Objects.isNull(contractReceipt)) {
            throw new MithrasException("借据不存在");
        }
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractReceipt.getContractId());
        if (Objects.isNull(contractBaseInfo)) {
            throw new MithrasException("合同不存在");
        }
        // 找到实际租金表
        List<ContractRentActual> contractRentActualList = this.listByReceipt(contractReceipt.getId());
        if (CollectionUtil.isEmpty(contractRentActualList)) {
            throw new MithrasException("实际租金表不存在");
        }
        // 找到借据下关联的付款申请
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            throw new MithrasException("请先导入有效的实际租金表");
        }
        // 找到付款实际核销记录
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = paymentBaseInfoList.stream().collect(Collectors.toMap(PaymentBaseInfo::getId, e -> e));
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentIds(paymentBaseInfoMap.keySet());
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            throw new MithrasException("不存在实际付款核销记录");
        }
        Map<Long, List<PaymentActualDetail>> paymentActualDetailMap = paymentActualDetailList.stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
        // 排序找到最早的，后续作为第0期
        paymentActualDetailList.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
        Long firstPaymentId = paymentActualDetailList.get(0).getPaymentId();
        LocalDate firstPayDate = paymentActualDetailList.get(0).getPaidInDate();

        // 报价方案
        ContractPriceDetailREQ contractPriceDetailREQ = new ContractPriceDetailREQ();
        contractPriceDetailREQ.setContractId(contractBaseInfo.getId());
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(contractPriceDetailREQ);
        RepayRateEnum repayRateEnum = RepayRateEnum.of(contractPriceDetailRSP.getRepayRate());
//        //第0期标准日期 Year and month= 第1期的 年月- 还款频率所对应的月份数量,day= 遍历租金表的第1期至n-1期，获取数量最多的 day。
//        Integer standardDay = getStandardDay(contractBaseInfo.getId());
//        ContractRentActual contractRentActual = contractRentActualList.get(0);
//        LocalDate zeroDate;
//        if (ObjectUtil.equals(repayRateEnum, RepayRateEnum.LRREGULAR)) {
//            zeroDate = contractBaseInfo.getActualLeaseDate();
//        } else {
//            zeroDate = contractRentActual.getCashFlowDate().minusMonths((long) RepayRateEnum.getRuleMonth(contractPriceDetailRSP.getRepayRate()) * contractRentActual.getCashFlowPhase()).plusDays(standardDay - contractRentActual.getCashFlowDate().getDayOfMonth());
//        }
        // 计算第0期标准日期
        LocalDate zeroStandardDate;
        PaymentBaseInfo firstPayment = paymentBaseInfoMap.get(firstPaymentId);
        if (Objects.isNull(firstPayment.getDefaultCollectionDay())) {
            zeroStandardDate = contractBaseInfo.getActualLeaseDate();
        } else {
            zeroStandardDate = FinancialUtil.ensureStandardZeroPhaseDate(firstPayment.getDefaultCollectionDay(), firstPayDate, true);
        }
        // 付款对应的收款项
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.listByPaymentIds(paymentBaseInfoMap.keySet());
        Map<Long, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getPaymentId));

        // 付款数据插入到实际租金表中获取新的租金表
        long totalEarnest = 0L;
        List<CashFlowBO> cashFlowBOList = new LinkedList<>();
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : paymentActualDetailMap.entrySet()) {
            List<PaymentActualDetail> details = entry.getValue();
            details.sort(Comparator.comparing(PaymentActualDetail::getPaidInDate));
            long sum = entry.getValue().stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            // 处理保证金、首期租金、咨询费/服务费/手续费
            List<CollectionBaseInfo> collectionList = collectionBaseInfoMap.get(entry.getKey());
            long earnest = 0L;
            long downPayment = 0L;
            long consultingFee = 0L;
            long commission = 0L;
            long firstInterest = 0L;
            if (CollectionUtil.isNotEmpty(collectionList)) {
                earnest = collectionList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name())).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                totalEarnest += earnest;
                downPayment = collectionList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name())).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                consultingFee = collectionList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.OTHERAMOUNT.name())).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                commission = collectionList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.COMMISSION.name())).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                firstInterest = collectionList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()) && Objects.nonNull(e.getPhase()) && e.getPhase() == 0).filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
            }
            sum = sum - earnest - downPayment - consultingFee - commission - firstInterest;
            CashFlowBO cashFlowBO = new CashFlowBO();
            if (Objects.equals(firstPaymentId, entry.getKey())) {
                cashFlowBO.setCashFlowPhase(0);
                cashFlowBO.setCashFlowDate(firstPayDate);
                cashFlowBO.setStandardCashFlowDate(zeroStandardDate);
            } else {
                cashFlowBO.setCashFlowDate(details.get(0).getPaidInDate());
            }
            cashFlowBO.setCashFlowAmount(sum * -1);
            cashFlowBOList.add(cashFlowBO);
        }
        for (int i = 0; i < contractRentActualList.size(); i++) {
            CashFlowBO cashFlowBO = ContractRentConvert.toCashFlowBO(contractRentActualList.get(i));
            //已经填入第0期，这里不再处理
            if (ObjectUtil.equals(0, cashFlowBO.getCashFlowPhase())) {
                continue;
            }
            cashFlowBOList.add(cashFlowBO);
            if (i == contractRentActualList.size() - 1) {
                // 计算保证金退款(名义价款影响很小，且无法拆分到借据上，忽略不计算)
                cashFlowBO.setCashFlowAmount(cashFlowBO.getCashFlowAmount() - totalEarnest);
            }
        }
        cashFlowBOList.sort(Comparator.comparing(CashFlowBO::getCashFlowDate));
        CashFlowIRRBO cashFlowIRRBO = FinancialUtil.calculateIRR(contractPriceDetailRSP.getMonthCount(), repayRateEnum, cashFlowBOList);
        if (!showDetailExcel) {
            IRRCalculateResultRSP rsp = new IRRCalculateResultRSP();
            rsp.setIrr(cashFlowIRRBO.getIrr().setScale(4, RoundingMode.HALF_UP).toPlainString());
            return rsp;
        }
        // 生成计算详情文件
        // TODO 概算表测算IRR代码高度重复，待优化
        List<IRRCalculateExcelModel.CashFlowAdjustExcelModel> excelDataList = cashFlowIRRBO.getCashFlowAdjustList().stream().map(item -> {
            IRRCalculateExcelModel.CashFlowAdjustExcelModel excelModel = new IRRCalculateExcelModel.CashFlowAdjustExcelModel();
            excelModel.setCashFlowDate(item.getCashFlowDate());
            excelModel.setAdjustCashFlowDate(item.getAdjustCashFlowDate());
            excelModel.setCashFlowPhase(item.getCashFlowPhase());
            excelModel.setAdjustCashFlowPhase(item.getAdjustCashFlowPhase());
            excelModel.setCashFlowAmount(BigDecimal.valueOf(item.getCashFlowAmount()));
            excelModel.setAdjustCashFlowAmount(item.getAdjustCashFlowAmount());
            return excelModel;
        }).collect(Collectors.toList());
        IRRCalculateExcelModel excelModel = new IRRCalculateExcelModel();
        excelModel.setIrrPerPhase(cashFlowIRRBO.getIrrPerPhase());
        excelModel.setIrr(cashFlowIRRBO.getIrr());
        excelModel.setCashFlowAdjustExcelModelList(excelDataList);
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        Long fileId;
        try {
            baos = new ByteArrayOutputStream();
            irrCalculateExcelExporter.export(baos, excelModel);
            bais = IoUtil.toStream(baos);
            // 上传临时文件
            fileId = materialsListService.add(bais, UUID.randomUUID().toString() + GlobalConstants.OFFICE_EXCEL_SUFFIX, contractBaseInfo.getId(), "CONTRACT_ACTUAL_IRR_CALCULATE", "TMP");
        } finally {
            if (Objects.nonNull(baos)) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error("关闭输出流异常", e);
                }
            }
            if (Objects.nonNull(bais)) {
                try {
                    bais.close();
                } catch (IOException e) {
                    log.error("关闭输入流异常", e);
                }
            }
        }
        // 拼装返回结果
        IRRCalculateResultRSP rsp = new IRRCalculateResultRSP();
        rsp.setFileId(fileId);
        rsp.setIrr(excelModel.getIrr().setScale(4, RoundingMode.HALF_UP).toPlainString());
        return rsp;
    }

    private Integer getStandardDay(Long contractId) {
        List<ContractRentActual> contractRentActuals = listByContract(contractId);
        Map<Integer, Integer> occurrenceMap = new HashMap<>();

        // 统计每个数字出现的次数
        for (ContractRentActual rentActual : contractRentActuals) {
            LocalDate cashFlowDate = rentActual.getCashFlowDate();
            if (ObjectUtil.isNotEmpty(cashFlowDate)) {
                int dayOfMonth = cashFlowDate.getDayOfMonth();
                occurrenceMap.put(dayOfMonth, occurrenceMap.getOrDefault(dayOfMonth, 0) + 1);
            }
        }
        int maxOccurrenceCount = 0;
        Integer maxOccurrenceNumber = null;
        Set<Map.Entry<Integer, Integer>> entries = occurrenceMap.entrySet();
        for (Map.Entry<Integer, Integer> entry : entries) {
            if (entry.getValue() > maxOccurrenceCount) {
                maxOccurrenceCount = entry.getValue();
                maxOccurrenceNumber = entry.getKey();
            }
        }
        return maxOccurrenceNumber;
    }

    public static String getCashFlowCode(String receiptCode, Integer cashFlowPhase) {
        return receiptCode + "-" + NumberUtil.decimalFormat("000", cashFlowPhase);
    }

    private List<ContractRentActualExcelModel> toExcelDataList(List<ContractRentActual> contractRentActualList) {
        if (CollectionUtils.isEmpty(contractRentActualList)) {
            return Collections.emptyList();
        }
        List<Long> actualRentIds = contractRentActualList.stream().map(ContractRentActual::getId).collect(Collectors.toList());
        List<ContractRentActualExcelModel> excelModels = contractRentActualList.stream().map(ContractRentConvert::toContractRentActualExcelModel).collect(Collectors.toList());
        Map<Long, CollectionBaseInfo> collectionBaseInfoMap = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery().in(CollectionBaseInfo::getRentActualId, actualRentIds)).stream().collect(Collectors.toMap(CollectionBaseInfo::getRentActualId, v -> v));
        for (ContractRentActualExcelModel excelModel : excelModels) {
            //  第零期处理
            if (!Objects.isNull(excelModel.getCashFlowPhase()) && excelModel.getCashFlowPhase() == 0) {
                CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(excelModel.getId());
                if (!Objects.isNull(collectionBaseInfo)) {
                    excelModel.setReceived(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus()) ? "是" : "否");
                    excelModel.setReceivedDate(collectionBaseInfo.getCollectionDate());
                    if (Objects.nonNull(collectionBaseInfo.getCollectionAmount())) {
                        BigDecimal divide = new BigDecimal(collectionBaseInfo.getCollectionAmount()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
                        excelModel.setReceivedAmount(divide.toString());
                    }
                }
                continue;
            }

            CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMap.get(excelModel.getId());
            if (Objects.isNull(collectionBaseInfo)) {
                excelModel.setReceived("否");
                continue;
            }
            if (Objects.isNull(collectionBaseInfo.getCollectionAmount())) {
                excelModel.setReceived("否");
                continue;
            }
            excelModel.setReceived(CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(collectionBaseInfo.getWriteOffStatus()) ? "是" : "否");
            excelModel.setReceivedDate(collectionBaseInfo.getCollectionDate());
            BigDecimal divide = new BigDecimal(collectionBaseInfo.getCollectionAmount()).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
            excelModel.setReceivedAmount(divide.toString());
        }
        return excelModels;
    }

    private void checkWithPayment(ContractReceipt contractReceipt, List<CashFlowExcelModel> cashFlowExcelModelList, PaymentBaseInfo paymentBaseInfo) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractReceipt.getContractId());
        BigDecimal contractAmount;
        List<PaymentActualDetail> paymentActualDetailList = paymentActualDetailService.listByPaymentId(paymentBaseInfo.getId());
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            contractAmount = NumberUtil.div(paymentBaseInfo.getApplyPaymentAmount().toString(), GlobalConstants.MONEY_MULTIPLE, 2);
        } else {
            contractAmount = NumberUtil.div(String.valueOf(paymentActualDetailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum()), GlobalConstants.MONEY_MULTIPLE, 2);
        }
        BigDecimal downPayment;
        if (Objects.nonNull(paymentBaseInfo.getDownPayment())) {
            downPayment = NumberUtil.div(paymentBaseInfo.getDownPayment().toString(), GlobalConstants.MONEY_MULTIPLE, 2);
        } else {
            downPayment = new BigDecimal(BigInteger.valueOf(0L), 2);
        }
        // 整体校验
        Set<Integer> phaseSet = new HashSet<>();
        BigDecimal principalSum = new BigDecimal(BigInteger.valueOf(0L), 2);
        for (CashFlowExcelModel cashFlowExcelModel : cashFlowExcelModelList) {
            cashFlowExcelModel.checkActualRentWithPaymentCode();
            // 校验期项
            Assert.isTrue(!phaseSet.contains(cashFlowExcelModel.getCashFlowPhase()), () -> MithrasException.newException("期项不能重复"));
            phaseSet.add(cashFlowExcelModel.getCashFlowPhase());
            // 校验剩余本金
            principalSum = principalSum.add(Optional.ofNullable(cashFlowExcelModel.getPrincipal()).orElse(BigDecimal.ZERO));
            if (cashFlowExcelModel.getCashFlowPhase() == 1 && Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name())) {
                // 如果直租类型则不校验第1期剩余本金
                continue;
            }
            BigDecimal r = contractAmount.subtract(downPayment).subtract(principalSum);
            Assert.isTrue(r.equals(cashFlowExcelModel.getRemainingPrincipal()), () -> MithrasException.newException("期项为" + cashFlowExcelModel.getCashFlowPhase() + "的一行剩余本金计算结果不正确"));
        }
        // 最后一期剩余本金需要等于0
        CashFlowExcelModel last = cashFlowExcelModelList.get(cashFlowExcelModelList.size() - 1);
        Assert.isTrue(last.getRemainingPrincipal().compareTo(BigDecimal.valueOf(0L)) == 0, () -> MithrasException.newException("最后一期剩余本金应等于0"));
    }

    private void checkWithoutPayment(List<CashFlowExcelModel> cashFlowExcelModelList) {
        Assert.notEmpty(cashFlowExcelModelList, () -> MithrasException.newException("导入数据不能为空"));
        for (CashFlowExcelModel cashFlowExcelModel : cashFlowExcelModelList) {
            cashFlowExcelModel.checkActualRentWithoutPaymentCode();
        }
        // 最后一期剩余本金需要等于0
        CashFlowExcelModel last = cashFlowExcelModelList.get(cashFlowExcelModelList.size() - 1);
        if (Objects.nonNull(last.getRemainingPrincipal())) {
            Assert.isTrue(last.getRemainingPrincipal().compareTo(BigDecimal.valueOf(0L)) == 0, () -> MithrasException.newException("最后一期剩余本金应等于0"));
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public ContractReceipt doContractReceipt(ContractRentActualImportREQ req, ContractBaseInfo contractBaseInfo) {
        boolean isZhiZu = Objects.equals(contractBaseInfo.getLeaseType(), LeaseType.zhi_zu.name());
        ContractReceipt contractReceipt;
        boolean joinPayment = false;
        if (Objects.isNull(req.getReceiptId())) {
            // 直租场景校验一下是否已有借据，保证直租场景的借据数量
            List<ContractReceipt> existList = contractReceiptService.listByContractId(req.getContractId());
            if (isZhiZu) {
                if (CollectionUtil.isNotEmpty(existList)) {
                    throw new MithrasException("直租类型合同已存在借据，无法再新增借据");
                }
            } else {
                joinPayment = true;
            }
            // 新增借据，第一次新增借据是需要指定标识为1并且初始化计划起租日期
            contractReceipt = new ContractReceipt();
            contractReceipt.setContractId(req.getContractId());
            if (CollUtil.isEmpty(existList)) {
                contractReceipt.setIsFirstReceipt(YesOrNoNumberEnum.YES.getCode());
                contractReceipt.setReceiptStartDate(LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN));
            } else {
                contractReceipt.setReceiptStartDate(LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN));
            }
            // 生成借据编号
            Integer sequence = contractReceiptService.getNextSequenceByContractId(contractBaseInfo.getId());
            contractReceipt.setSequence(sequence);
            contractReceipt.setReceiptCode(ContractUtil.generateReceiptCode(contractBaseInfo.getContractCode(), sequence, isZhiZu));
            //这里补充企业规模
            List<CorpCommerceInfo> corpCommerceInfo = getBean(CorpCommerceInfoService.class).findByClientId(contractBaseInfo.getClientId());
            if (CollectionUtil.isNotEmpty(corpCommerceInfo)) {
                contractReceipt.setOrgScale(corpCommerceInfo.get(0).getOrgScale());
            }
            contractReceiptService.save(contractReceipt);
            // 在FTP考核价格信息表中占个位置
            SpringUtil.getBean(FtpAssessmentInfoService.class).initByReceiptId(contractReceipt.getId());
        } else {
            contractReceipt = contractReceiptService.getById(req.getReceiptId());
            if (CharSequenceUtil.isNotBlank(req.getChangeDate())) {
                contractReceipt.setChangeDate(LocalDateTimeUtil.parseDate(req.getChangeDate(), DatePattern.NORM_DATE_PATTERN));
            }
            if (CharSequenceUtil.isNotBlank(req.getReceiptStartDate())) {
                contractReceipt.setReceiptStartDate(LocalDateTimeUtil.parseDate(req.getReceiptStartDate(), DatePattern.NORM_DATE_PATTERN));
            }
            contractReceipt.setIncomeSharingFlag(YesOrNoNumberEnum.NO.getCode());
            contractReceiptService.updateById(contractReceipt);
        }
        if (!isZhiZu && CharSequenceUtil.equalsAny(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_UNCOMMIT.name(), ContractProcessStatusEnum.START_RENT_COMMIT.name())) {
            List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listByReceiptId(contractReceipt.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                // 区分是否自动发起流程来确定付款申请
                PaymentBaseInfo paymentBaseInfo;
                ProcessResp processResp = processService.findRelatedProcess(contractBaseInfo.getId().toString(), Collections.singletonList(ProcessModelTypeEnum.ContractStartRentAutoFlow.name()));
                if (Objects.nonNull(processResp)) {
                    Map<String, Object> varMap = flowVariableApiService.getVariables(processResp.getProcessInstanceId(), Collections.singletonList(PaymentBaseInfoService.CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY));
                    Object paymentId = varMap.get(PaymentBaseInfoService.CONTRACT_AUTO_FLOW_TARGET_PAYMENT_KEY);
                    if (Objects.isNull(paymentId)) {
                        throw new MithrasException("数据异常，没有找到流程对应的付款申请");
                    }
                    paymentBaseInfo = paymentBaseInfoService.getById(Long.parseLong(paymentId.toString()));
                } else {
                    // 默认使用第一个付款申请作为起租时候关联借据的付款
                    paymentBaseInfoList = paymentBaseInfoService.detailByContractIds(Collections.singletonList(contractBaseInfo.getId()));
                    Assert.notEmpty(paymentBaseInfoList, () -> MithrasException.newException("合同下没有任何付款申请"));
                    List<PaymentBaseInfo> filterList = paymentBaseInfoList.stream()
                            .filter(e -> {
                                boolean effect = CharSequenceUtil.equalsAny(e.getPaymentStatus(), PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name());
                                boolean unlink = Objects.isNull(e.getReceiptId());
                                return effect && unlink;
                            })
                            .sorted(Comparator.comparingLong(PaymentBaseInfo::getId))
                            .collect(Collectors.toList());
                    Assert.notEmpty(filterList, () -> MithrasException.newException("不存在生效且未关联借据的付款申请"));
                    // 自动起租需要填入实际起租日
                    getBean(ContractReceiptService.class).lambdaUpdate()
                            .set(ContractReceipt::getReceiptStartDate, contractBaseInfo.getActualLeaseDate())
                            .eq(ContractReceipt::getId, contractReceipt.getId())
                            .update();
                    paymentBaseInfo = filterList.get(0);
                }
                req.setPaymentId(paymentBaseInfo.getId());
                joinPayment = true;
            } else {
                req.setPaymentId(paymentBaseInfoList.get(0).getId());
            }
        }
        // 如果是选择了付款申请则把借据挂到付款申请下
        if (joinPayment && Objects.nonNull(req.getPaymentId())) {
            FillReceiptInfoReq fillReceiptInfoReq = new FillReceiptInfoReq();
            fillReceiptInfoReq.setPaymentId(req.getPaymentId());
            fillReceiptInfoReq.setReceiptId(contractReceipt.getId());
            fillReceiptInfoReq.setReceiptCode(contractReceipt.getReceiptCode());
            paymentBaseInfoService.fillReceiptInfo(fillReceiptInfoReq);
        }
        return contractReceipt;
    }

    private void doContractRentList(List<CashFlowExcelModel> excelModelList, ContractReceipt contractReceipt, ContractBaseInfo contractBaseInfo) {
        List<ContractRentActual> dbList = this.listByReceipt(contractReceipt.getId());
        List<ContractRentActual> importList = excelModelList.stream().map(item -> {
            ContractRentActual cra = ContractRentConvert.toContractRentActual(item);
            cra.setContractId(contractBaseInfo.getId());
            cra.setReceiptId(contractReceipt.getId());
            // 生效（起租动作或者新增投放动作）或者起租状态需要生成现金流编号
            if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
                cra.setCashFlowCode(getCashFlowCode(contractReceipt.getReceiptCode(), cra.getCashFlowPhase()));
            } else if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.TAKE_EFFECT.name())) {
                if (Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_UNCOMMIT.name())
                        || Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.START_RENT_COMMIT.name())
                        || Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_RECEIPT_UNCOMMIT.name())
                        || Objects.equals(contractBaseInfo.getContractProcessStatus(), ContractProcessStatusEnum.NEW_RECEIPT_COMMIT.name())) {
                    cra.setCashFlowCode(getCashFlowCode(contractReceipt.getReceiptCode(), cra.getCashFlowPhase()));
                }
            }
            return cra;
        }).collect(Collectors.toList());
        // 直接保存
        if (CollectionUtil.isEmpty(dbList)) {
            this.saveBatch(importList);
            return;
        }
        // 查询借据下已有的数据
        List<ContractRentActual> existDbList = this.listByReceipt(contractReceipt.getId());
        // 已核销记录
        List<ContractRentActual> isVerifiedList = this.listVerifiedRentActual(contractBaseInfo.getId());
        // 已核销的期项集合
        Set<Integer> isVerifiedPhaseSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(isVerifiedList)) {
            for (ContractRentActual contractRentActual : isVerifiedList) {
                if (Objects.equals(contractRentActual.getReceiptId(), contractReceipt.getId())) {
                    isVerifiedPhaseSet.add(contractRentActual.getCashFlowPhase());
                }
            }
        }
        // 去掉已核销记录获得待处理的租金记录并转化为map
        Map<Integer, ContractRentActual> dbMap = existDbList.stream()
                .filter(contractRentActual -> !isVerifiedPhaseSet.contains(contractRentActual.getCashFlowPhase()))
                .collect(Collectors.toMap(ContractRentActual::getCashFlowPhase, item -> item));
//        if (CollectionUtil.isEmpty(dbMap)) {
//            throw new MithrasException("数据无变化");
//        }
        // 去掉已核销记录的excel文件数据并转化为map
        Map<Integer, ContractRentActual> importMap = importList.stream()
                .filter(contractRentActual -> !isVerifiedPhaseSet.contains(contractRentActual.getCashFlowPhase()))
                .collect(Collectors.toMap(ContractRentActual::getCashFlowPhase, item -> item));
        if (CollectionUtil.isEmpty(importMap)) {
            throw new MithrasException("数据无变化");
        }
        // dbMap和importMap同时存在的数据，用importMap的数据更新
        // dbMap有但是importMap没有的，删除dbMap的数据
        // dbMap没有但是importMap有的，则新增
        List<ContractRentActual> toSaveOrUpdateList = new LinkedList<>();
        List<Long> toRemoveIdList = new LinkedList<>();
        List<Integer> dbMapKeyList = new ArrayList<>(dbMap.keySet());
        dbMapKeyList.sort(Comparator.comparingInt(Integer::intValue));
        int dbPhaseMax = dbMapKeyList.size() == 0 ? 0 : dbMapKeyList.get(dbMapKeyList.size() - 1);
        List<Integer> importMapKeyList = new ArrayList<>(importMap.keySet());
        importMapKeyList.sort(Comparator.comparingInt(Integer::intValue));
        int importPhaseMax = importMapKeyList.get(importMapKeyList.size() - 1);
        int loopEnd = Math.max(dbPhaseMax, importPhaseMax);
        int i = 0;
        while (i <= loopEnd) {
            i++;
            ContractRentActual dbData = dbMap.get(i);
            ContractRentActual importData = importMap.get(i);
            if (Objects.isNull(dbData) && Objects.isNull(importData)) {
                continue;
            }
            if (Objects.nonNull(dbData) && Objects.nonNull(importData)) {
                // 使用import数据更新db数据
                importData.setId(dbData.getId());
                importData.setCreateBy(dbData.getCreateBy());
                importData.setCreateTime(dbData.getCreateTime());
                importData.setUpdateBy(Optional.ofNullable(AccountUtil.getLoginInfo()).map(AccountVO::getId).orElse(null));
                importData.setUpdateTime(LocalDateTime.now());
                toSaveOrUpdateList.add(importData);
            } else if (Objects.nonNull(dbData)) {
                // 删除db数据
                toRemoveIdList.add(dbData.getId());
            } else {
                // 新增import数据
                toSaveOrUpdateList.add(importData);
            }
        }
        if (!CollectionUtils.isEmpty(toSaveOrUpdateList)) {
            this.saveOrUpdateBatch(toSaveOrUpdateList);
        }
        if (!CollectionUtils.isEmpty(toRemoveIdList)) {
            this.removeByIds(toRemoveIdList);
        }
    }

    public double calculateXIRR(List<CashFlowExcelModel> actualRentList) {
        try {
            if (CollectionUtil.isEmpty(actualRentList)) {
                return 0.0;
            }
            // 参数校验
            List<Double> amounts = new ArrayList<>();
            List<LocalDate> dates = new ArrayList<>();
            for (CashFlowExcelModel cashFlowExcelModel : actualRentList) {
                if (ObjectUtil.isNotEmpty(cashFlowExcelModel) && ObjectUtil.isNotEmpty(cashFlowExcelModel.getCashFlowAmount()) && ObjectUtil.isNotEmpty(cashFlowExcelModel.getCashFlowDate())) {
                    amounts.add(cashFlowExcelModel.getCashFlowAmount().doubleValue());
                    dates.add(cashFlowExcelModel.getCashFlowDate());
                }
            }
            // 检查正负现金流
            boolean hasPositive = false;
            boolean hasNegative = false;
            for (Double amount : amounts) {
                if (amount > 0) hasPositive = true;
                if (amount < 0) hasNegative = true;
            }
            if (!(hasPositive && hasNegative)) {
                log.warn("calculateXIRR error 现金流必须包含正负值");
                return 0.0;
            }
            // 获取最早日期并计算时间差
            LocalDate firstDate = Collections.min(dates);
            double[] years = new double[dates.size()];
            for (int i = 0; i < dates.size(); i++) {
                long daysBetween = ChronoUnit.DAYS.between(firstDate, dates.get(i));
                years[i] = daysBetween / 365.0;
            }

            // 牛顿迭代参数
            double guess = 0.1;  // 初始猜测10%
            int maxIteration = 1000;
            double precision = 1e-6;

            // 开始迭代计算
            for (int i = 0; i < maxIteration; i++) {
                double npv = 0.0;
                double derivative = 0.0;

                for (int j = 0; j < amounts.size(); j++) {
                    double amount = amounts.get(j);
                    double time = years[j];

                    if (guess <= -1.0) {
                        guess = -0.99999;  // 防止无效计算
                    }

                    double denominator = Math.pow(1 + guess, time);
                    npv += amount / denominator;
                    derivative += -amount * time / (denominator * (1 + guess));
                }

                // 检查收敛
                if (Math.abs(npv) < precision) {
                    return guess;
                }

                // 防止除零错误
                if (Math.abs(derivative) < precision) {
                    log.warn("calculateXIRR error 无法收敛（导数过小）");
                    return 0.0;
                }

                // 更新猜测值
                double newGuess = guess - npv / derivative;

                // 限制有效范围
                if (newGuess <= -1.0) {
                    newGuess = -0.9999;
                }

                guess = newGuess;
            }

            log.warn("calculateXIRR error 经过 " + maxIteration + " 次迭代未收敛");
        } catch (Exception e) {
            log.warn("xirr计算错误，不影响正常业务", e);
        }
        return 0.0;
    }
}
