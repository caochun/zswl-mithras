package cn.zswltech.mithras.application.orchestration.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.util.ApplicationContextUtil;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.common.ResultCode;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailListReq;
import cn.zswltech.mithras.dto.collection.*;
import cn.zswltech.mithras.dto.contract.receipt.ContractReceiptQueryActualTaxREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdMarginRecordREQ;
import cn.zswltech.mithras.dto.third.financial.ThirdPaymentDetailREQ;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.collection.enums.BillTypeEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.margin.enums.RecordTypeEnum;
import cn.zswltech.mithras.payment.enums.*;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.collection.excel.model.CollectionListExcelModel;
import cn.zswltech.mithras.foundation.excel.LinkStyleHandler;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.model.BillManagement;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.margin.persistence.model.MarginBaseInfo;
import cn.zswltech.mithras.margin.persistence.model.MarginRecordInfo;
import cn.zswltech.mithras.margin.persistence.model.WarrantyBaseInfo;
import cn.zswltech.mithras.margin.persistence.model.WarrantyRecordInfo;
import cn.zswltech.mithras.payment.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.model.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.collection.event.CollectionAddEvent;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.contract.core.ContractSettlePlanService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.margin.service.MarginRecordService;
import cn.zswltech.mithras.margin.service.WarrantyBaseInfoService;
import cn.zswltech.mithras.margin.service.WarrantyRecordService;
import cn.zswltech.mithras.ftp.newftp.service.FtpService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailUnconfirmedService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName CollectionFlowCenterService
 * @Description 流水中心
 * @Author jackerhe
 * @Date 2024/2/26 7:28 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class CollectionFlowCenterService {

    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private FinancialService financialService;
    @Resource
    private BillManagementService billManagementService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private MarginRecordService marginRecordService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private FtpService ftpService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractBaseInfoService contractInfoService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private CollectionFlowCenterService collectionFlowCenterService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Autowired
    private WarrantyRecordService warrantyRecordService;
    @Autowired
    private WarrantyBaseInfoService warrantyBaseInfoService;

    //付款业务流水列表
    public PageR<CollectionFlowCenterBusinessPaymentListRSP> paymentList(CollectionFlowCenterBusinessPaymentListREQ req) {
        if (ObjectUtil.isNotEmpty(req.getCashFlowItemList())) {
            if (req.getCashFlowItemList().size() == 1) {
                req.setCashFlowItem(req.getCashFlowItemList().get(0));
            } else {
                req.setCashFlowItem(null);
            }
        }
        Page<CollectionFlowCenterBusinessPaymentListRSP> page = paymentBaseInfoMapper.paymentFlowList(new Page<>(req.getPage(), req.getPageSize()), req);
        if (ObjectUtil.isNotEmpty(page) && ObjectUtil.isNotEmpty(page.getRecords())) {
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(page.getRecords().stream().map(CollectionFlowCenterBusinessPaymentListRSP::getBizDeptId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()));
            Map<Long, String> userId2Name = id2NameService.clientId2Name(page.getRecords().stream().map(CollectionFlowCenterBusinessPaymentListRSP::getClientId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()));
            page.getRecords().forEach(base -> {
                base.setBizDept(deptId2Name.get(base.getBizDeptId()));
                base.setClientName(userId2Name.get(base.getClientId()));
            });
        }
        return PageR.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    //付款手工核销
    public void paymentManualRecord(CollectionFlowCenterBusinessPaymentManualRecordREQ req) {
        PaymentFlowItemEnum of = Optional.ofNullable(PaymentFlowItemEnum.of(req.getCashFlowItem())).orElseThrow(() -> new MithrasException("暂不支持"));
        //这里只允许核销票据、信用证、保证金抵扣
        if (!CharSequenceUtil.equalsAny(req.getPaymentMethod(), PaymentMethod.PJ.display(), PaymentMethod.REFUND_MARGIN_DEDUCT.display, PaymentMethod.XYZ.display)) {
            throw new MithrasException("请前往【银行流水】页面进行银行流水认领。");
        }
        if (Objects.equals(of.name(), PaymentFlowItemEnum.CREDIT_PAYMENT.name())) {
            //付款核销
            //校验是否超过可核销记录
            PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(req.getPaymentId());
            if (ObjectUtil.isEmpty(paymentBaseInfo)) {
                throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
            }
            Long paidAmount = paymentActualDetailService.calculatePaidAmount(Collections.singletonList(req.getPaymentId()));
            if (LongUtil.null2zero(paymentBaseInfo.getApplyPaymentAmount()) < (LongUtil.null2zero(paidAmount) + LongUtil.null2zero(req.getPaidInAmount()))) {
                throw new MithrasException("本次核销将导致 累计核销⾦额⼤于计划⾦额，无法核销！");
            }
            //开始核销
            ThirdPaymentDetailREQ thirdPaymentDetailREQ = new ThirdPaymentDetailREQ();
            thirdPaymentDetailREQ.setCollectionCode(paymentBaseInfo.getPaymentCode());
            thirdPaymentDetailREQ.setPaymentMethod(req.getPaymentMethod());
            thirdPaymentDetailREQ.setPaidInAmount(LongUtil.tenThousand2Dollar(String.valueOf(req.getPaidInAmount())));
            thirdPaymentDetailREQ.setPaidInDate(req.getPaidInDate());
            thirdPaymentDetailREQ.setPknumber(getPkNumber());
            thirdPaymentDetailREQ.setReqFromCq(false);
            thirdPaymentDetailREQ.setInfoSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            req.setCashFlowItem(PaymentFlowItemEnum.CREDIT_PAYMENT.name());
            thirdPaymentDetailREQ.setUnConfirmedId(req.getPaymentActualDetailId());
            financialService.paymentRecode(thirdPaymentDetailREQ);
            //关联票据
            if (ObjectUtil.equals(req.getPaymentMethod(), PaymentMethod.PJ.display()) && ObjectUtil.isNotEmpty(req.getBillManagementAddREQ())) {
                BillManagementAddREQ billManagementAddREQ = req.getBillManagementAddREQ();
                if (ObjectUtil.isEmpty(billManagementAddREQ.getBillType())) {
                    billManagementAddREQ.setBillType(BillTypeEnum.PAYMENT.name());
                }
                billManagementAddREQ.setContractId(paymentBaseInfo.getContractId());
                billManagementAddREQ.setMainId(thirdPaymentDetailREQ.getRecordId());
                billManagementService.add(billManagementAddREQ);
            }
        } else {
            //核销保证金 --抵扣未做关联租金的操作，这里可能是核销只核销对应期项，防止管理任务多次操作,且保证金目前没有票据
            MarginBaseInfo marginBaseInfo = marginBaseInfoService.getById(req.getPaymentId());
            ThirdMarginRecordREQ thirdMarginRecordREQ = new ThirdMarginRecordREQ();
            PaymentMethod byName = Optional.ofNullable(PaymentMethod.findByDisplay(req.getPaymentMethod())).orElseThrow(() -> new MithrasException("暂不支持付款类型"));
            if (!StrUtil.equalsAny(byName.name(), PaymentMethod.REFUND_MARGIN.name(), PaymentMethod.REFUND_MARGIN_DEDUCT.name())) {
                throw new MithrasException("保证金付款类型只支持保证金退款，保证金抵扣");
            }
            thirdMarginRecordREQ.setCollectionType(byName.name());
            thirdMarginRecordREQ.setCollectionCode(marginBaseInfo.getMarginCode());
            thirdMarginRecordREQ.setCollectionDate(req.getPaidInDate());
            thirdMarginRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(req.getPaidInAmount())));
            thirdMarginRecordREQ.setPknumber(getPkNumber());
            thirdMarginRecordREQ.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            R<String> collectionRecode = financialService.backRecord(thirdMarginRecordREQ);
            if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
                log.error("核销失败，原因：{}", collectionRecode.getMsg());
                throw new MithrasException(collectionRecode.getMsg());
            }

            //保证金核销类型为保证金抵扣，关联最后一期租金
            if (PaymentMethod.REFUND_MARGIN_DEDUCT.name().equals(byName.name())) {
                //获取此合同下所有的租金收款项
                List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, marginBaseInfo.getContractId())
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .orderByDesc(CollectionBaseInfo::getPlanCollectionDate));
                if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
                    throw new MithrasException("暂无需要收款核销的租金，无法用使用保证金抵扣");
                }
                //收款明细id
                CollectionBaseInfo collectionBaseInfo;
                //查询借据数量
                int count = contractReceiptService.count(Wrappers.<ContractReceipt>lambdaQuery().eq(ContractReceipt::getContractId, marginBaseInfo.getContractId()));
                if (count == 1) {
                    //只有一个借据，关联最后一期租金
                    collectionBaseInfo = collectionBaseInfoList.get(0);
                } else {
                    //多个借据获取计划还款日期最靠后的那条,避免脏数据影响，排除第0期
                    collectionBaseInfo = collectionBaseInfoList.stream()
                            .filter(item -> item.getPhase() != 0)
                            .limit(count)
                            .max(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate)
                                    .thenComparing((item1, item2) -> {
                                        //日期相同时，获取借据编号最小的那条租金
                                        if (item1.getPlanCollectionDate().equals(item2.getPlanCollectionDate())) {
                                            boolean b1 = item1.getReceiptCode().endsWith("1");
                                            boolean b2 = item2.getReceiptCode().endsWith("1");
                                            return Boolean.compare(b1, b2);
                                        } else {
                                            return item1.getPlanCollectionDate().compareTo(item2.getPlanCollectionDate());
                                        }
                                    })).orElseThrow(() -> new MithrasException("未找到需要收款核销的租金"));
                }
                //实际收款
                long paidInAmount = req.getPaidInAmount();
                //本金
                long principal = 0;
                //利息
                long interest = 0;
                //罚息
                long penaltyInterest = 0;
                // 利息
                if (Objects.nonNull(collectionBaseInfo.getInterest()) && paidInAmount > 0 && collectionBaseInfo.getInterest() > 0) {
                    long toPayInterest = Math.min(paidInAmount, collectionBaseInfo.getInterest());
                    interest += toPayInterest;
                    paidInAmount -= toPayInterest;
                }
                // 本金
                if (Objects.nonNull(collectionBaseInfo.getPrincipal()) && paidInAmount > 0 && collectionBaseInfo.getPrincipal() > 0) {
                    long toPayPrincipal = Math.min(paidInAmount, collectionBaseInfo.getPrincipal());
                    principal += toPayPrincipal;
                    paidInAmount -= toPayPrincipal;
                }
                // 罚息
                if (Objects.nonNull(collectionBaseInfo.getPenaltyInterest()) && paidInAmount > 0 && collectionBaseInfo.getPenaltyInterest() > 0) {
                    long toPayPenaltyInterest = Math.min(paidInAmount, collectionBaseInfo.getPenaltyInterest());
                    penaltyInterest += toPayPenaltyInterest;
                }

                //核销保证金
                CollectionFlowCenterBusinessCollectionManualRecordREQ collectionManualRecordREQ = new CollectionFlowCenterBusinessCollectionManualRecordREQ();
                collectionManualRecordREQ.setCollectionId(collectionBaseInfo.getId().toString());
                collectionManualRecordREQ.setCollectionCode(collectionBaseInfo.getCode());
                collectionManualRecordREQ.setCashFlowItem(CashFlowItemEnum.RENT.name());
                collectionManualRecordREQ.setCollectionType(PaymentMethod.REFUND_MARGIN_DEDUCT.name());
                collectionManualRecordREQ.setCollectionDate(req.getPaidInDate());
                collectionManualRecordREQ.setCollectionAmount(req.getPaidInAmount());
                collectionManualRecordREQ.setPrincipal(principal);
                collectionManualRecordREQ.setInterest(interest);
                collectionManualRecordREQ.setPenaltyInterest(penaltyInterest);
                collectionManualRecordREQ.setBillManagementAddREQ(new BillManagementAddREQ());

                collectionFlowCenterService.collectionManualRecord(collectionManualRecordREQ, false);
            }
        }
    }

    //付款业务流水结算明细
    public List<CollectionFlowCenterBusinessPaymentSettleDetailRSP> paymentSettleDetail(CollectionFlowCenterBusinessPaymentSettleDetailREQ req) {
        PaymentFlowItemEnum of = Optional.ofNullable(PaymentFlowItemEnum.of(req.getCashFlowItem())).orElseThrow(() -> new MithrasException("暂不支持"));
        List<CollectionFlowCenterBusinessPaymentSettleDetailRSP> rsps = new ArrayList<>();
        if (Objects.equals(of.name(), PaymentFlowItemEnum.CREDIT_PAYMENT.name())) {
            //付款相关明细
            ActualDetailListReq actualDetailListReq = new ActualDetailListReq();
            actualDetailListReq.setPaymentId(req.getPaymentId());
            List<PaymentActualDetail> list = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getPaymentId, req.getPaymentId())
                    .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
            if (ObjectUtil.isNotEmpty(list)) {
                list.forEach(paymentActualDetail -> {
                    CollectionFlowCenterBusinessPaymentSettleDetailRSP rsp = new CollectionFlowCenterBusinessPaymentSettleDetailRSP();
                    rsp.setId(paymentActualDetail.getId());
                    rsp.setPaymentMethod(paymentActualDetail.getPaymentMethod());
                    rsp.setPaidInAmount(paymentActualDetail.getPaidInAmount());
                    rsp.setPaidInDate(paymentActualDetail.getPaidInDate());
                    rsp.setWriteOffType(paymentActualDetail.getWriteOffType());
                    rsp.setBankDetailNo(paymentActualDetail.getBankDetailNo());
                    rsp.setUpdateTime(paymentActualDetail.getUpdateTime());
                    rsp.setUpdateBy(paymentActualDetail.getUpdateBy());
                    rsp.setUpdateByName(id2NameService.sysUserId2NameSingle(paymentActualDetail.getUpdateBy()));
                    if (ObjectUtil.equals(paymentActualDetail.getPaymentMethod(), PaymentMethod.PJ.display())) {
                        List<BillManagement> billManagements = billManagementService.listByMainIdAndType(paymentActualDetail.getId(), BillTypeEnum.PAYMENT.name());
                        //此场景下最多一个票据
                        if (CollectionUtil.isNotEmpty(billManagements)) {
                            if (billManagements.size() == 1) {
                                BillManagement billManagement = billManagements.get(0);
                                rsp.setBillCode(billManagement.getBillCode());
                                rsp.setBillAmount(billManagement.getBillAmount());
                                rsp.setBillExpireDate(billManagement.getBillExpireDate());
                            } else {
                                billManagements.forEach(billManagement -> {
                                    CollectionFlowCenterBusinessPaymentSettleDetailRSP billRsp = BeanUtil.copyProperties(paymentActualDetail, CollectionFlowCenterBusinessPaymentSettleDetailRSP.class);
                                    billRsp.setPaidInAmount(billManagement.getBillAmount());
                                    billRsp.setBillCode(billManagement.getBillCode());
                                    billRsp.setBillAmount(billManagement.getBillAmount());
                                    billRsp.setBillExpireDate(billManagement.getBillExpireDate());
                                    rsps.add(billRsp);
                                });
                            }
                        }
                    }
                    rsps.add(rsp);
                });
            }
        } else if (Objects.equals(of.name(), PaymentFlowItemEnum.EARNEST_MONEY.name())){
            //保证金相关信息
            List<MarginRecordInfo> marginRecordInfos = marginRecordService.list(Wrappers.<MarginRecordInfo>lambdaQuery()
                    .eq(MarginRecordInfo::getMarginId, req.getPaymentId())
                    .in(MarginRecordInfo::getRecordType, ListUtil.toList(RecordTypeEnum.REFUND.name(), RecordTypeEnum.REFUND_MARGIN.name(), RecordTypeEnum.REFUND_MARGIN_DEDUCT.name())));
            if (CollectionUtil.isNotEmpty(marginRecordInfos)) {
                //现金流编号转合同编号
                Map<String, String> rentCode2ContractCode = new HashMap<>();
                Set<String> rentCodeSet = marginRecordInfos.stream().map(MarginRecordInfo::getRentCollectionCode).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
                if (ObjectUtil.isNotEmpty(rentCodeSet)) {
                    List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .in(CollectionBaseInfo::getCode, rentCodeSet));
                    if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
                        rentCode2ContractCode.putAll(collectionBaseInfos.stream().collect(Collectors.toMap(CollectionBaseInfo::getCode, CollectionBaseInfo::getContractCode)));
                    }
                }
                marginRecordInfos.forEach(marginRecordInfo -> {
                    CollectionFlowCenterBusinessPaymentSettleDetailRSP rsp = new CollectionFlowCenterBusinessPaymentSettleDetailRSP();
                    rsp.setId(marginRecordInfo.getId());
                    PaymentMethod byDisplay = PaymentMethod.findByName(marginRecordInfo.getCollectionType());
                    if (Objects.nonNull(byDisplay)) {
                        rsp.setPaymentMethod(byDisplay.getDisplay());
                    }
                    rsp.setPaidInAmount(marginRecordInfo.getCollectionAmount());
                    rsp.setPaidInDate(marginRecordInfo.getCollectionDate());
                    rsp.setWriteOffType(marginRecordInfo.getWriteOffStatus());
                    rsp.setUpdateTime(marginRecordInfo.getUpdateTime());
                    rsp.setUpdateBy(marginRecordInfo.getUpdateBy());
                    rsp.setUpdateByName(id2NameService.sysUserId2NameSingle(marginRecordInfo.getUpdateBy()));
                    rsp.setContractCode(rentCode2ContractCode.get(marginRecordInfo.getRentCollectionCode()));
                    rsps.add(rsp);
                });
            }
        } else{
            // 质保金
            List<WarrantyRecordInfo> warrantyRecordInfoList = warrantyRecordService.list(Wrappers.<WarrantyRecordInfo>lambdaQuery()
                    .eq(WarrantyRecordInfo::getWarrantyId, req.getPaymentId())
                    .in(WarrantyRecordInfo::getRecordType, ListUtil.toList(RecordTypeEnum.REFUND.name(), RecordTypeEnum.REFUND_WARRANTY.name())));
            if (CollectionUtil.isNotEmpty(warrantyRecordInfoList)) {
                WarrantyBaseInfo warrantyBaseInfo = warrantyBaseInfoService.getById(req.getPaymentId());

                warrantyRecordInfoList.forEach(warrantyRecordInfo -> {
                    CollectionFlowCenterBusinessPaymentSettleDetailRSP rsp = new CollectionFlowCenterBusinessPaymentSettleDetailRSP();
                    rsp.setId(warrantyRecordInfo.getId());
                    PaymentMethod byDisplay = PaymentMethod.findByName(warrantyRecordInfo.getCollectionType());
                    if (Objects.nonNull(byDisplay)) {
                        rsp.setPaymentMethod(byDisplay.getDisplay());
                    }
                    rsp.setPaidInAmount(warrantyRecordInfo.getCollectionAmount());
                    rsp.setPaidInDate(warrantyRecordInfo.getCollectionDate());
                    rsp.setWriteOffType(warrantyRecordInfo.getWriteOffStatus());
                    rsp.setUpdateTime(warrantyRecordInfo.getUpdateTime());
                    rsp.setUpdateBy(warrantyRecordInfo.getUpdateBy());
                    rsp.setUpdateByName(id2NameService.sysUserId2NameSingle(warrantyRecordInfo.getUpdateBy()));
                    rsp.setContractCode(warrantyBaseInfo.getContractCode());
                    rsps.add(rsp);
                });
            }
        }
        return rsps;
    }

    //收款业务流水列表
    public PageR<CollectionFlowCenterBusinessCollectionListRSP> collectionList(CollectionFlowCenterBusinessCollectionListREQ req) {
        Page<CollectionFlowCenterBusinessCollectionListRSP> rsps = collectionBaseInfoMapper.collectionList(new Page<>(req.getPage(), req.getPageSize()), req);
        if (ObjectUtil.isNotEmpty(rsps) && ObjectUtil.isNotEmpty(rsps.getRecords())) {
            Map<Long, String> deptId2Name = id2NameService.deptId2Name(rsps.getRecords().stream().map(CollectionFlowCenterBusinessCollectionListRSP::getBizDeptId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()));
            Map<Long, String> userId2Name = id2NameService.clientId2Name(rsps.getRecords().stream().map(CollectionFlowCenterBusinessCollectionListRSP::getClientId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toList()));
            rsps.getRecords().forEach(rsp -> {
                rsp.setBizDept(deptId2Name.get(rsp.getBizDeptId()));
                rsp.setClientName(userId2Name.get(rsp.getClientId()));
            });
        }
        return PageR.of(rsps.getRecords(), rsps.getTotal(), rsps.getCurrent(), rsps.getSize());
    }

    //收款手工核销
    public void collectionManualRecord(CollectionFlowCenterBusinessCollectionManualRecordREQ req, boolean flag) {
        //校验是否超额
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(req.getCollectionId());
        ThirdCollectionRecordREQ thirdCollectionRecordREQ = new ThirdCollectionRecordREQ();
        if (ObjectUtil.isEmpty(collectionBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //这里只允许核销票据、信用证、保证金抵扣
        if (!CharSequenceUtil.equalsAny(req.getCollectionType(), PaymentMethod.PJ.display(), PaymentMethod.REFUND_MARGIN_DEDUCT.display, PaymentMethod.XYZ.display)) {
            throw new MithrasException("请前往【银行流水】页面进行银行流水认领。");
        }
        if (LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) < (LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) + LongUtil.null2zero(req.getPrincipal()) + LongUtil.null2zero(req.getInterest()))) {
            throw new MithrasException("本次核销将导致 累计核销⾦额⼤于计划⾦额，操作失败！");
        }
        if (CashFlowItemEnum.RENT.name().equals(req.getCashFlowItem())) {
            req.setCollectionAmount(LongUtil.null2zero(req.getPrincipal()) + LongUtil.null2zero(req.getInterest()) + LongUtil.null2zero(req.getPenaltyInterest()));
        }
        //收款方式选择“保证金内扣”,flag 为true 自动核销保证金
        if (flag && PaymentMethod.REFUND_MARGIN_DEDUCT.getDisplay().equals(req.getCollectionType())) {
            //默认抵扣自己，传人可抵扣其他合同
            Long deductionContractId = collectionBaseInfo.getContractId();
            if (ObjectUtil.isNotEmpty(req.getDeductionContractId())) {
                deductionContractId = req.getDeductionContractId();
            }
            //检验保证金是否足够抵扣
            MarginBaseInfo marginBaseInfo1 = marginBaseInfoService.getOne(Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, deductionContractId));
            //保证今余额不足不能被核销
            if (Objects.isNull(marginBaseInfo1) || req.getCollectionAmount() > marginBaseInfo1.getCollectionAmount()) {
                throw new MithrasException("保证金余额不足，暂时无法核销！");
            }
            //核销保证金
            ThirdMarginRecordREQ thirdMarginRecordREQ = new ThirdMarginRecordREQ();
            thirdMarginRecordREQ.setCollectionType(PaymentMethod.REFUND_MARGIN_DEDUCT.name());
            thirdMarginRecordREQ.setCollectionCode(marginBaseInfo1.getMarginCode());
            thirdMarginRecordREQ.setCollectionDate(req.getCollectionDate());
            thirdMarginRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(req.getCollectionAmount())));
            thirdMarginRecordREQ.setPknumber(getPkNumber());
            thirdMarginRecordREQ.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
            thirdMarginRecordREQ.setCollectionId(req.getCollectionId());
            thirdMarginRecordREQ.setRentCollectionCode(collectionBaseInfo.getCode());
            R<String> collectionRecode = financialService.backRecord(thirdMarginRecordREQ);
            if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
                log.error("核销失败，原因：{}", collectionRecode.getMsg());
                throw new MithrasException(collectionRecode.getMsg());
            }
            //
            thirdCollectionRecordREQ.setDeductionMarginBaseId(marginBaseInfo1.getId());
            thirdCollectionRecordREQ.setDeductionMarginBaseCode(marginBaseInfo1.getMarginCode());
            //保证金回收
            if(ObjectUtil.isNotEmpty(req.getIsRecycleManager()) && ObjectUtil.equals(req.getIsRecycleManager(), YesOrNoNumberEnum.YES.getCode()) && ObjectUtil.isNotEmpty(req.getRecycleManagerAmount()) && ObjectUtil.isNotEmpty(req.getRecycleManagerDate())) {
                //创建保证金收款
                PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                        .eq(PaymentBaseInfo::getContractId, req.getDeductionContractId())
                        .in(PaymentBaseInfo::getPaymentStatus, ListUtil.toList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()))
                        .last(StringUtil.mysqlLimitOne()));
                if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
                    CollectionAddEvent collectionAddEvent = new CollectionAddEvent(paymentBaseInfo.getPaymentCode(), req.getDeductionContractId(), CashFlowItemEnum.EARNEST_MONEY, req.getRecycleManagerAmount(), req.getRecycleManagerDate(), "MARGIN_RECYCLE");
                    ApplicationContextUtil.getApplicationContext().publishEvent(collectionAddEvent);
                }
            }
        }

        //类型为“服务费/咨询费”，重新计算印花税
        if (Arrays.asList(CashFlowItemEnum.OTHERAMOUNT.name(), CashFlowItemEnum.COMMISSION.name()).contains(req.getCashFlowItem())) {
            ContractReceiptQueryActualTaxREQ receiptQueryActualTaxREQ = new ContractReceiptQueryActualTaxREQ();
            receiptQueryActualTaxREQ.setContractId(collectionBaseInfo.getContractId());
            try {
                contractReceiptService.computeFinancialCosts(receiptQueryActualTaxREQ, true);
            } catch (Exception e) {
                log.error("计算印花税失败{}", e.getMessage());
            }
        }
        thirdCollectionRecordREQ.setPknumber(getPkNumber());
        thirdCollectionRecordREQ.setDataSource(WriteOffTypeEnum.MANUAL_RECORD.display());
        thirdCollectionRecordREQ.setCollectionCode(collectionBaseInfo.getCode());
        thirdCollectionRecordREQ.setCollectionType(req.getCollectionType());
        thirdCollectionRecordREQ.setCollectionDate(req.getCollectionDate());
        thirdCollectionRecordREQ.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(req.getCollectionAmount())));
        thirdCollectionRecordREQ.setCashFlowItem(req.getCashFlowItem());
        thirdCollectionRecordREQ.setInvoiceFlag(YesOrNoNumberEnum.YES.getCode());
        thirdCollectionRecordREQ.setPrincipal(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(req.getPrincipal()))));
        thirdCollectionRecordREQ.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(req.getInterest()))));
        thirdCollectionRecordREQ.setPenaltyInterest(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(req.getPenaltyInterest()))));
        //核销流水
        R<String> collectionRecode = financialService.collectionRecode(thirdCollectionRecordREQ);
        if (ResultCode.FAILURE.getCode() == collectionRecode.getCode()) {
            log.error("核销失败，原因：{}", collectionRecode.getMsg());
            throw new MithrasException(collectionRecode.getMsg());
        }
        //添加票据
        if (ObjectUtil.equals(req.getCollectionType(), PaymentMethod.PJ.display()) && ObjectUtil.isNotEmpty(req.getBillManagementAddREQ())) {
            BillManagementAddREQ billManagementAddREQ = req.getBillManagementAddREQ();
            if (ObjectUtil.isEmpty(billManagementAddREQ.getBillType())) {
                billManagementAddREQ.setBillType(BillTypeEnum.COLLECTION.name());
            }
            billManagementAddREQ.setContractId(collectionBaseInfo.getContractId());
            billManagementAddREQ.setMainId(thirdCollectionRecordREQ.getRecordId());
            billManagementService.add(billManagementAddREQ);
        }
    }

    //收款业务流水结算明细
    public List<CollectionFlowCenterBusinessCollectionSettleDetailRSP> collectionSettleDetail(CollectionFlowCenterBusinessCollectionSettleDetailREQ req) {
        CollectionRecordListREQ recordListREQ = new CollectionRecordListREQ();
        recordListREQ.setId(req.getCollectionId());
        CollectionRecordListRSP recordListRSP = collectionRecordInfoService.list(recordListREQ);
        List<CollectionFlowCenterBusinessCollectionSettleDetailRSP> rsps = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(recordListRSP) && CollectionUtil.isNotEmpty(recordListRSP.getRecords())) {
            //保证金信息
            Set<Long> marginBaseIdSet = recordListRSP.getRecords().stream().map(CollectionRecordListRSP.Records::getDeductionMarginBaseId).filter(ObjectUtil::isNotNull).collect(Collectors.toSet());
            Map<Long, String> marginId2ContractCode = new HashMap<>();
            if(ObjectUtil.isNotEmpty(marginBaseIdSet)) {
                marginId2ContractCode.putAll(marginBaseInfoService.listByIds(marginBaseIdSet).stream().collect(Collectors.toMap(MarginBaseInfo::getId, MarginBaseInfo::getContractCode, (a, b) -> a)));
            }
            recordListRSP.getRecords().forEach(record -> {
                CollectionFlowCenterBusinessCollectionSettleDetailRSP rsp = BeanUtil.copyProperties(record, CollectionFlowCenterBusinessCollectionSettleDetailRSP.class);
                PaymentMethod paymentMethod = PaymentMethod.findByName(record.getCollectionType());
                if (Objects.nonNull(paymentMethod)) {
                    rsp.setCollectionType(paymentMethod.getDisplay());
                }
                rsp.setUpdateByName(id2NameService.sysUserId2NameSingle(rsp.getUpdateBy()));
                if (ObjectUtil.equals(record.getCollectionType(), PaymentMethod.PJ.display())) {
                    List<BillManagement> billManagements = billManagementService.listByMainIdAndType(record.getId(), BillTypeEnum.COLLECTION.name());
                    //此场景下最多一个票据
                    if (CollectionUtil.isNotEmpty(billManagements)) {
                        if (billManagements.size() == 1) {
                            BillManagement billManagement = billManagements.get(0);
                            rsp.setBillCode(billManagement.getBillCode());
                            rsp.setBillAmount(billManagement.getBillAmount());
                            rsp.setBillExpireDate(billManagement.getBillExpireDate());
                            rsp.setBillBuyRate(billManagement.getBillBuyRate());
                        } else {
                            billManagements.forEach(billManagement -> {
                                CollectionFlowCenterBusinessCollectionSettleDetailRSP billRsp = BeanUtil.copyProperties(rsp, CollectionFlowCenterBusinessCollectionSettleDetailRSP.class);
                                billRsp.setCollectionAmount(billManagement.getBillAmount());
                                billRsp.setBillCode(billManagement.getBillCode());
                                billRsp.setBillAmount(billManagement.getBillAmount());
                                billRsp.setBillExpireDate(billManagement.getBillExpireDate());
                                billRsp.setBillBuyRate(billManagement.getBillBuyRate());
                                rsps.add(billRsp);
                            });
                        }
                    }
                }
                rsp.setContractCode(marginId2ContractCode.get(record.getDeductionMarginBaseId()));
                rsps.add(rsp);
            });
        }
        return rsps;
    }

    private String getPkNumber() {
        return String.join("-", "ZSZL", UUIDUtil.genUuid());
    }


    public void exportBusinessCollection(CollectionFlowCenterBusinessCollectionExportListREQ collectionExportListREQ, HttpServletResponse response) throws IOException {
        if (collectionExportListREQ.getCollectionIds().isEmpty()) {
            throw new MithrasException("业务流水项目端收据选择为空");
        }
        List<Integer> collectionIds = collectionExportListREQ.getCollectionIds();
        String localDate = collectionExportListREQ.getPlanCollectionDate();
        YearMonth yearMonth = YearMonth.parse(localDate);
        String year = String.valueOf(yearMonth.getYear());
        String month = String.valueOf(yearMonth.getMonthValue());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        CollectionFlowCenterBusinessCollectionListREQ req = new CollectionFlowCenterBusinessCollectionListREQ();
        req.setPage(1);
        req.setPageSize(10000);
        req.setPlanCollectionDateFrom(startDate);
        req.setPlanCollectionDateTo(endDate);
        req.setCashFlowItemList(Arrays.asList(CashFlowItemEnum.EARNEST_MONEY.name(),
                CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name()));
        PageR<CollectionFlowCenterBusinessCollectionListRSP> rsp = collectionList(req);
        if (rsp.getList() == null || rsp.getList().isEmpty()) {
            throw new MithrasException("业务流水项目端收款不存在");
        }
        List<CollectionFlowCenterBusinessCollectionListRSP> res = rsp.getList();
        res = res.stream().sorted(Comparator.comparing(CollectionFlowCenterBusinessCollectionListRSP::getPlanCollectionDate)).collect(Collectors.toList());
        List<CollectionFlowCenterBusinessCollectionListRSP> collectionListRSPS = new ArrayList<>();
        for (CollectionFlowCenterBusinessCollectionListRSP collectionListRSP : res) {
            if (ProjectBizType.ZL.name().equalsIgnoreCase(collectionListRSP.getBizType())
                    && LeaseType.zhi_zu.name().equalsIgnoreCase(collectionListRSP.getLeaseType())) {
                continue;
            } else if (collectionListRSP.getPlanCollectionAmount() <= 0) {
                continue;
            } else if (collectionIds.contains(collectionListRSP.getCollectionId())) {
                collectionListRSPS.add(collectionListRSP);
            } else {
                continue;
            }
        }
        List<Long> ids = collectionListRSPS.stream().map(CollectionFlowCenterBusinessCollectionListRSP::getClientId).distinct().collect(Collectors.toList());
        Map<Long, String> clientMap = id2NameService.clientId2Name(ids);
        List<CollectionListExcelModel> excelModelList = collectionListRSPS.stream().map(o -> {
            CollectionListExcelModel excelModel = new CollectionListExcelModel();
            excelModel.setId(Long.valueOf(o.getCollectionId()));
            excelModel.setClientName(clientMap.get(o.getClientId()));
            excelModel.setCode(o.getCode());
            CashFlowItemEnum cashFlowItem = CashFlowItemEnum.valueOf(o.getCashFlowItem());
            excelModel.setCashFlowItem(cashFlowItem.getDisplay());
            excelModel.setContractCode(o.getContractCode());
            excelModel.setCollectionDate(Optional.ofNullable(o.getCollectionDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            excelModel.setCollectionAmount(Optional.ofNullable(o.getCollectionAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setPlanCollectionAmount(Optional.ofNullable(o.getPlanCollectionAmount()).map(Util::mithrasLong2BigDecimal).map(BigDecimal::toPlainString).orElse("0"));
            excelModel.setPlanCollectionDate(Optional.ofNullable(o.getPlanCollectionDate()).map(LocalDateTimeUtil::formatNormal).orElse(""));
            return excelModel;
        }).collect(Collectors.toList());
        List<String> linkNames = excelModelList.stream()
                .map(table -> table.getClientName().length() > 28 ? table.getClientName().substring(0, 28) : table.getClientName())
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        ClassPathResource classPathResource = new ClassPathResource("/doc/副本打印收据模板.xlsx");
        InputStream inputStream = classPathResource.getStream();
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("utf-8");
        String format = String.format("收据打印%s年%s月", year, month);
        String fileName = URLEncoder.encode(format, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        ExcelWriter excelWriter = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            //原模板只有一个sheet，通过poi复制出需要的sheet个数的模板
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            //设置模板的第一个sheet的名称
            workbook.setSheetName(0, "收据汇总表");
            workbook.setSheetName(1, linkNames.get(0));
            //循环表名
            Map<String, Integer> map = new HashMap<>();
            map.put(linkNames.get(0), 1);
            for (int i = 1; i < linkNames.size(); i++) {
                if (map.containsKey(linkNames.get(i))) {
                    map.put(linkNames.get(i), map.get(linkNames.get(i)) + 1);
                    workbook.cloneSheet(1, linkNames.get(i) + "_" + map.get(linkNames.get(i)));
                } else {
                    map.put(linkNames.get(i), 1);
                    workbook.cloneSheet(1, linkNames.get(i));
                }
            }

            //写到流里
            workbook.write(bos);
            byte[] bArray = bos.toByteArray();
            InputStream is = new ByteArrayInputStream(bArray);
            excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(is).build();
            Map<String, Integer> linkMap = new HashMap<>();
            WriteSheet writeSheet = EasyExcel.writerSheet("收据汇总表")
                    .registerWriteHandler(new LinkStyleHandler(linkMap))
                    .build();
            excelWriter.fill(excelModelList, writeSheet);
            //查询所有表
            List<CollectionListExcelModel> fieldList = excelModelList;
            Map<Long, List<CollectionListExcelModel>> collect = fieldList.stream().collect(Collectors.groupingBy(CollectionListExcelModel::getId));
            //设置表
            for (int i = 0; i < collectionListRSPS.size(); i++) {
                WriteSheet fieldSheet = EasyExcel.writerSheet(i + 1).build();
                List<CollectionListExcelModel> field = collect.get(Long.valueOf(collectionListRSPS.get(i).getCollectionId()));
                //自定义表头
                List<Map<String, String>> headList = new ArrayList<>();
                Map<String, String> headMap = new HashMap<>();
                LocalDate date = collectionListRSPS.get(i).getPlanCollectionDate();
                String dateYear = String.valueOf(date.getYear());
                String dateMonth = String.valueOf(date.getMonthValue());
                String dateDay = String.valueOf(date.getDayOfMonth());
                headMap.put("code", collectionListRSPS.get(i).getCode());
                //CashFlowItemEnum cashFlowItem = CashFlowItemEnum.valueOf(baseInfoList.get(i).getCashFlowItem());
                headMap.put("clientName", field.get(0).getClientName() + field.get(0).getCashFlowItem());
                headMap.put("contractCode", collectionListRSPS.get(i).getContractCode());
                String planCollectionAmount = Util.mithrasLong2BigDecimal(collectionListRSPS.get(i).getPlanCollectionAmount()).toString();
                headMap.put("amount", planCollectionAmount);
                headMap.put("cnAmount", toChinaUpper(planCollectionAmount));
                headMap.put("year", dateYear);
                headMap.put("month", dateMonth);
                headMap.put("day", dateDay);
                headList.add(headMap);

                excelWriter.fill(new FillWrapper("headlist", headList), fieldSheet);
                //excelWriter.fill(new FillWrapper("fieldList",field), fieldSheet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            if (excelWriter != null) {
                excelWriter.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static String toChinaUpper(String money) throws Exception {
        boolean lessZero = false;
        if (money.startsWith("-")) {
            money = money.substring(1);
            lessZero = true;
        }

        if (!money.matches("^[0-9]*$|^0+\\.[0-9]+$|^[1-9]+[0-9]*$|^[1-9]+[0-9]*.[0-9]+$")) {
            throw new Exception("钱数格式错误！");
        }
        String[] part = money.split("\\.");
        String integerData = part[0];
        String decimalData = part.length > 1 ? part[1] : "";
        //替换前置0
        if (integerData.matches("^0+$")) {
            integerData = "0";
        } else if (integerData.matches("^0+(\\d+)$")) {
            integerData = integerData.replaceAll("^0+(\\d+)$", "$1");
        }

        StringBuffer integer = new StringBuffer();
        for (int i = 0; i < integerData.length(); i++) {
            char perchar = integerData.charAt(i);
            integer.append(upperNumber(perchar));
            integer.append(upperNumber(integerData.length() - i - 1));
        }
        StringBuffer decimal = new StringBuffer();
        if (part.length > 1 && !"00".equals(decimalData)) {
            int length = decimalData.length() >= 2 ? 2 : decimalData.length();
            for (int i = 0; i < length; i++) {
                char perchar = decimalData.charAt(i);
                decimal.append(upperNumber(perchar));
                if (i == 0)
                    decimal.append('角');
                if (i == 1)
                    decimal.append('分');
            }
        }
        String result = integer.toString() + decimal.toString();
        result = dispose(result);
        if (lessZero && !"零圆整".equals(result)) {
            result = "负" + result;
        }
        return result;
    }

    private static char upperNumber(char number) {
        switch (number) {
            case '0':
                return '零';
            case '1':
                return '壹';
            case '2':
                return '贰';
            case '3':
                return '叁';
            case '4':
                return '肆';
            case '5':
                return '伍';
            case '6':
                return '陆';
            case '7':
                return '柒';
            case '8':
                return '捌';
            case '9':
                return '玖';
        }
        return '0';
    }

    private static char upperNumber(int index) {
        int realIndex = index % 9;
        if (index > 8) {//亿过后进入回归,之后是拾,佰...
            realIndex = (index - 9) % 8;
            realIndex = realIndex + 1;
        }
        switch (realIndex) {
            case 0:
                return '圆';
            case 1:
                return '拾';
            case 2:
                return '佰';
            case 3:
                return '仟';
            case 4:
                return '万';
            case 5:
                return '拾';
            case 6:
                return '佰';
            case 7:
                return '仟';
            case 8:
                return '亿';
        }
        return '0';
    }

    private static String dispose(String result) {
        result = result.replaceAll("0", "");//处理
        result = result.replaceAll("零仟零佰零拾|零仟零佰|零佰零拾|零仟|零佰|零拾", "零");
        result = result.replaceAll("零+", "零").replace("零亿", "亿");
        result = result.matches("^.*亿零万[^零]仟.*$") ? result.replace("零万", "零") : result.replace("零万", "万");
        result = result.replace("亿万", "亿");
        //处理小数
        result = result.replace("零角", "零").replace("零分", "");
        result = result.replaceAll("(^[零圆]*)(.+$)", "$2");
        result = result.replaceAll("(^.*)([零]+圆)(.+$)", "$1圆零$3");

        //处理整数单位
        result = result.replaceAll("圆零角零分|圆零角$|圆$|^零$|圆零$|零圆$", "圆整");
        result = result.replaceAll("^圆整$", "零圆整");
        result = result.replaceAll("零+", "零");
        result = result.replaceAll("(^.*)(圆[零]+$)", "$1圆整");

        return result;
    }

    public List<CollectionFlowCenterBusinessCollectionListRSP> businessCollectionExportList(CollectionFlowCenterBusinessCollectionExportREQ collectionExportREQ) {
        String localDate = collectionExportREQ.getPlanCollectionDate();
        YearMonth yearMonth = YearMonth.parse(localDate);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        CollectionFlowCenterBusinessCollectionListREQ req = new CollectionFlowCenterBusinessCollectionListREQ();
        req.setPage(1);
        req.setPageSize(10000);
        req.setPlanCollectionDateFrom(startDate);
        req.setPlanCollectionDateTo(endDate);
        req.setCashFlowItemList(Arrays.asList(CashFlowItemEnum.EARNEST_MONEY.name(),
                CashFlowItemEnum.RENT.name(), CashFlowItemEnum.FIRST_RENT.name()));
        PageR<CollectionFlowCenterBusinessCollectionListRSP> rsp = collectionList(req);
        if (rsp.getList() == null || rsp.getList().isEmpty()) {
            throw new MithrasException("业务流水项目端收款不存在");
        }
        List<CollectionFlowCenterBusinessCollectionListRSP> res = rsp.getList();
        res = res.stream().sorted(Comparator.comparing(CollectionFlowCenterBusinessCollectionListRSP::getPlanCollectionDate)).collect(Collectors.toList());
        List<CollectionFlowCenterBusinessCollectionListRSP> collectionListRSPS = new ArrayList<>();
        for (CollectionFlowCenterBusinessCollectionListRSP collectionListRSP : res) {
            if (ProjectBizType.ZL.name().equalsIgnoreCase(collectionListRSP.getBizType())
                    && LeaseType.zhi_zu.name().equalsIgnoreCase(collectionListRSP.getLeaseType())) {
                continue;
            } else if (collectionListRSP.getPlanCollectionAmount() <= 0) {
                continue;
            } else {
                if (CashFlowItemEnum.FIRST_RENT.name().equalsIgnoreCase(collectionListRSP.getCashFlowItem())) {
                    collectionListRSP.setCashFlowItem(CashFlowItemEnum.LEASE_RENT.name());
                    collectionListRSP.setPlanCollectionAmount(collectionListRSP.getPlanCollectionAmount());
                } else if (CashFlowItemEnum.RENT.name().equalsIgnoreCase(collectionListRSP.getCashFlowItem())) {
                    collectionListRSP.setCashFlowItem(CashFlowItemEnum.LEASE_RENT.name());
                    collectionListRSP.setPlanCollectionAmount(collectionListRSP.getPrincipal());
                } else if (CashFlowItemEnum.EARNEST_MONEY.name().equalsIgnoreCase(collectionListRSP.getCashFlowItem())) {
                    collectionListRSP.setCashFlowItem(CashFlowItemEnum.EARNEST_MONEY.name());
                    collectionListRSP.setPlanCollectionAmount(collectionListRSP.getPlanCollectionAmount());
                }
                collectionListRSPS.add(collectionListRSP);
            }
        }
        return collectionListRSPS;
    }

    public List<CollectionFlowCenterBusinessPaymentManualRecordRSP> paymentManualCashFlowList(CollectionFlowCenterBusinessPaymentManualCashFlowListREQ req) {
        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoService.getById(req.getPaymentId());
        Assert.notNull(paymentBaseInfo, "未找到付款申请信息");
        ContractBaseInfo contractBaseInfo = contractInfoService.getById(paymentBaseInfo.getContractId());
        Assert.notNull(contractBaseInfo, "未找到合同信息");
        // 找到付款申请下的所有的付款计划
        LambdaQueryWrapper<PaymentActualDetailUnconfirmed> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetailUnconfirmed::getPaymentId, req.getPaymentId());
        query.eq(Objects.nonNull(req.getPaymentActualDetailId()), PaymentActualDetailUnconfirmed::getId, req.getPaymentActualDetailId());
        query.in(PaymentActualDetailUnconfirmed::getWriteOffStatus, WriteOffStatus.COMMIT.name(), WriteOffStatus.TO_BE_WRITE_OFF.name(), WriteOffStatus.CONFIRM.name());
        if (Objects.equals(LeaseType.hui_zu.name(), contractBaseInfo.getLeaseType())) {
            query.eq(PaymentActualDetailUnconfirmed::getPaymentMethod, req.getPaymentMethod());
        }
        List<PaymentActualDetailUnconfirmed> actualDetailUnconfirmedList = paymentActualDetailUnconfirmedService.list(query);
        if (CollUtil.isEmpty(actualDetailUnconfirmedList)) {
            throw new MithrasException("未找到付款方式为" + req.getPaymentMethod() + "的付款计划");
        }

        Map<Long, List<PaymentActualDetail>> listMap = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getUnConfirmedId, actualDetailUnconfirmedList.stream().map(PaymentActualDetailUnconfirmed::getId).collect(Collectors.toList()))
        ).stream().collect(Collectors.groupingBy(PaymentActualDetail::getUnConfirmedId));
        return actualDetailUnconfirmedList.stream().map(item -> {
            CollectionFlowCenterBusinessPaymentManualRecordRSP rsp = new CollectionFlowCenterBusinessPaymentManualRecordRSP();
            rsp.setPaymentActualDetailId(item.getId());
            rsp.setCashFlowCode(paymentBaseInfo.getPaymentCode() + "-" + item.getSeqCode());
            rsp.setWriteOffedAmount(listMap.getOrDefault(item.getId(), Collections.emptyList()).stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
            rsp.setPayInAmount(item.getPaidInAmount());
            return rsp;
        }).collect(Collectors.toList());
    }

    //获取客户下合同保证金信息
    public List<CollectionFlowCenterClientContractMarginRSP> clientContractMargin(CollectionFlowCenterClientContractMarginREQ req) {
        List<MarginBaseInfo> marginBaseInfos = marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                .eq(MarginBaseInfo::getClientId, req.getClientId()));
        if (ObjectUtil.isEmpty(marginBaseInfos)) {
            return null;
        }
        //过滤保证金余额大约0
        marginBaseInfos.removeIf(e -> LongUtil.null2zero(e.getCollectionAmount()) <= 0);
        if (ObjectUtil.isEmpty(marginBaseInfos)) {
            return null;
        }
        List<CollectionFlowCenterClientContractMarginRSP> rsps = new ArrayList<>();
        marginBaseInfos.forEach(marginBaseInfo -> {
            CollectionFlowCenterClientContractMarginRSP rsp = BeanUtil.copyProperties(marginBaseInfo, CollectionFlowCenterClientContractMarginRSP.class);
            rsp.setEarnestMoneyBalance(LongUtil.null2zero(marginBaseInfo.getCollectionAmount()));
            rsps.add(rsp);
        });
        return rsps;
    }
}
