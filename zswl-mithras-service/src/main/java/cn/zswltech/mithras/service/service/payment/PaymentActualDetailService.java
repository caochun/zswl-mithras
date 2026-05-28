package cn.zswltech.mithras.service.service.payment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.api.payment.dto.PaymentMaterialsListRsp;
import cn.zswltech.mithras.api.payment.dto.PlanedDetailDto;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailDto;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailListReq;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailListRsp;
import cn.zswltech.mithras.api.payment.writeoff.ActualDetailPostReq;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.convert.payment.PaymentActualDetailConverter;
import cn.zswltech.mithras.service.convert.payment.PaymentConvert;
import cn.zswltech.mithras.service.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.SpecialFileBusinessType;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.enums.payment.PaymentMethod;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetailUnconfirmed;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentWriteOffHistory;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 实际付款记录表
 * @date 2022-08-16
 */
@Service
public class PaymentActualDetailService extends ServiceImpl<PaymentActualDetailMapper, PaymentActualDetail> {
    @Resource
    private PaymentActualDetailConverter converter;
    @Resource
    private PaymentMaterialsService materialsService;
    @Resource
    private PaymentBaseInfoService baseInfoService;
    @Resource
    private PaymentPlanedDetailService paymentPlanedDetailService;
    @Resource
    private PaymentConvert paymentConvert;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private PaymentWriteOffHistoryService historyService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private PaymentActualDetailUnconfirmedService paymentActualDetailUnconfirmedService;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private PaymentWriteOffService paymentWriteOffService;

    public Map<Long, Long> calculatePayAmountByProjReviewId(Collection<Long> projReviewIds) {
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByProjReviewIds(new LinkedList<>(projReviewIds));
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyMap();
        }
        Map<Long, List<ContractBaseInfo>> projReviewId2ContractIdMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        List<PaymentActualDetail> paymentActualDetailList = this.listByContractIds(contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));
        Map<Long, Long> contractId2TotalPayMap = paymentActualDetailList.stream().filter(e -> Objects.nonNull(e.getPaidInAmount())).collect(Collectors.groupingBy(PaymentActualDetail::getContractId, Collectors.summingLong(PaymentActualDetail::getPaidInAmount)));
        // 按照项目进一步聚合
        Map<Long, Long> result = new HashMap<>();
        for (Long projReviewId : projReviewIds) {
            long total = 0;
            List<ContractBaseInfo> cbiList = projReviewId2ContractIdMap.get(projReviewId);
            if (CollectionUtil.isNotEmpty(cbiList)) {
                for (ContractBaseInfo contractBaseInfo : cbiList) {
                    total += Optional.ofNullable(contractId2TotalPayMap.get(contractBaseInfo.getId())).orElse(0L);
                }
            }
            result.put(projReviewId, total);
        }
        return result;
    }

    public List<PaymentActualDetail> listByContractIds(Collection<Long> contractIds) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.in(PaymentActualDetail::getContractId, contractIds);
        return this.list(query);
    }

    public List<PaymentActualDetail> listByPaymentDate(LocalDate paidInDateFrom, LocalDate paidInDateTo, Collection<Long> contractIds) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.ge(ObjectUtil.isNotEmpty(paidInDateFrom), PaymentActualDetail::getPaidInDate, paidInDateFrom);
        query.in(ObjectUtil.isNotEmpty(contractIds), PaymentActualDetail::getContractId, contractIds);
        query.le(ObjectUtil.isNotEmpty(paidInDateTo), PaymentActualDetail::getPaidInDate, paidInDateTo);
        return this.list(query);
    }

    public PaymentActualDetail getEarliestPayment(Long contractId) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetail::getContractId, contractId);
        query.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        query.isNotNull(PaymentActualDetail::getPaidInDate);
        query.orderByAsc(PaymentActualDetail::getPaidInDate);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }

    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public Long add(MultipartFile[] enclosures, ActualDetailPostReq req) {
        PaymentBaseInfo baseInfo = baseInfoService.getById(req.getPaymentId());
        if (baseInfo.getWriteOffUserIds() != null && JSON.parseArray(baseInfo.getWriteOffUserIds()).size() >= 1) {
            throw new MithrasException("付款申请已核销，禁止操作付款记录");
        }
        PaymentActualDetail info = converter.postReqToEntity(req);
        Integer actualDetailCount = baseMapper.selectCount(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, req.getPaymentId()));
        if (actualDetailCount == null) {
            actualDetailCount = 0;
        }
        info.setSeqCode(String.format("%02d", actualDetailCount + 1));
        info.setWriteOffStatus(PaymentWriteOffStatus.TO_BE_WRITE_OFF.name());
        info.setInfoSource(id2NameService.sysUserId2NameSingle(AccountUtil.getLoginInfo().getId()));
        info.setClientId(baseInfo.getClientId());
        baseMapper.insert(info);
        for (MultipartFile multipartFile : enclosures) {
            materialsService.uploadPaidProf(multipartFile, info.getId(), "PAID_PROOF", SpecialFileBusinessType.PAID_RECORD.name());
        }
        if (PaymentWriteOffStatus.NO_PAID.name().equals(baseInfo.getWriteOffStatus())) {
            baseInfo.setWriteOffStatus(PaymentWriteOffStatus.TO_BE_WRITE_OFF.name());
        }
        baseInfoService.updateById(baseInfo);
        return info.getId();
    }

    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void modify(MultipartFile[] enclosures, ActualDetailPostReq req) {
        PaymentActualDetail actual = baseMapper.selectById(req.getId());
        // 不允许更新状态
        req.setWriteOffStatus(actual.getWriteOffStatus());
        if (ObjectUtil.isNull(actual)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        PaymentBaseInfo baseInfo = baseInfoService.getById(actual.getPaymentId());
        if (baseInfo.getWriteOffUserIds() != null && JSON.parseArray(baseInfo.getWriteOffUserIds()).size() >= 1) {
            throw new MithrasException("付款申请已核销，禁止操作付款记录");
        }
        if (PaymentWriteOffStatus.WRITTEN_OFF.name().equals(baseInfo.getWriteOffStatus())) {
            throw new MithrasException("付款申请已核销，禁止操作付款记录");
        }
        if (WriteOffStatus.WRITTEN_OFF.name().equals(actual.getWriteOffStatus())) {
            throw new MithrasException("付款记录已核销，禁止修改");
        }
        PaymentActualDetail info = converter.postReqToEntity(req);
        baseMapper.updateAnnotationIncludeNullById(info);
        // enclosureId 和 enclosureName 不为空，说明文件没有变动
        if (ObjectUtil.isNotEmpty(req.getEnclosureId()) && ObjectUtil.isNotEmpty(req.getEnclosureName())) {
            return;
        } else {
            // 删除所有
            materialsService.removeByBelongId(info.getId());
            if (enclosures != null) {
                for (MultipartFile multipartFile : enclosures) {
                    materialsService.uploadPaidProf(multipartFile, info.getId(), "PAID_PROOF", SpecialFileBusinessType.PAID_RECORD.name());
                }
            }

        }
    }

    //反核销付款数据
    @Transactional(rollbackFor = Throwable.class)
    public void withdraw(Long id) {
        PaymentActualDetail paymentActualDetail = baseMapper.selectById(id);
        if (ObjectUtil.isEmpty(paymentActualDetail)) {
            return;
        }
        LambdaUpdateWrapper<PaymentActualDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(PaymentActualDetail::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(PaymentActualDetail::getId, paymentActualDetail.getId());
        baseMapper.update(null, updateWrapper);
        paymentWriteOffService.modifyPaymentStatus(paymentActualDetail.getPaymentId());
        financeFlowRecordService.withdrawBankFlow(paymentActualDetail.getId(), FinanceFlowDetailTableEnum.PAYMENT_ACTUAL_DETAIL.name(), paymentActualDetail.getPaidInAmount());
    }

    public ActualDetailListRsp list(ActualDetailListReq req) {
        PaymentBaseInfo payment = baseInfoService.getById(req.getPaymentId());
//        if(!RecordStatus.TAKE_EFFECT.equals(payment.getPaymentStatus())){
//            throw new MithrasException("付款申请未生效");
//        }
        if (null == payment.getApplyPaymentAmount()) {
            throw new MithrasException("请先填写付款申请的相关信息");
        }
        ActualDetailListRsp rsp = paymentConvert.entityToActualDetailListRsp(payment);
        // 填充计划付款中填写的对方账户信息作为默认账户
        List<PlanedDetailDto> planedDetailDtoList = paymentPlanedDetailService.list(payment.getId());
        if (CollectionUtil.isNotEmpty(planedDetailDtoList)) {
            PlanedDetailDto planedDetailDto = planedDetailDtoList.get(0);
            rsp.setDefaultOppositeAccountName(planedDetailDto.getOppositeAccountName());
            rsp.setDefaultOppositeAccount(planedDetailDto.getOppositeAccount());
            rsp.setDefaultOppositeAccountBank(planedDetailDto.getOppositeAccountBank());
        }
        if (ObjectUtil.isNotEmpty(rsp.getWriteOffUserIds())) {
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(rsp.getWriteOffUserIds());
            rsp.setWriteOffUserName(rsp.getWriteOffUserIds().stream().map(userId2Name::get).collect(Collectors.toList()));
        }
        LambdaQueryWrapper<PaymentActualDetail> actualDetailQuery = Wrappers.lambdaQuery();
        actualDetailQuery.eq(PaymentActualDetail::getPaymentId, req.getPaymentId());
        if (CollectionUtil.isNotEmpty(req.getWriteOffStatusList())) {
            actualDetailQuery.in(PaymentActualDetail::getWriteOffStatus, req.getWriteOffStatusList());
        }
        List<PaymentActualDetail> actualDetails = baseMapper.selectList(actualDetailQuery);

        Long paidAmount = actualDetails.stream()
                .filter(e -> WriteOffStatus.WRITTEN_OFF.name().equals(e.getWriteOffStatus()))
                .mapToLong(PaymentActualDetail::getPaidInAmount)
                .sum();
        rsp.setPaidAmount(paidAmount);
        rsp.setObligation(rsp.getPayableAmount() - rsp.getPaidAmount());
        rsp.setActualDetails(converter.entitiesToDtos(actualDetails));
        if (ObjectUtil.isNotEmpty(rsp.getActualDetails())) {
            rsp.getActualDetails().forEach(base -> base.setPaymentWay(GlobalConstants.CQ_PAYMENT_METHOD_PJ.equals(base.getPaymentMethod()) ? 1 : 0));
        }
        // 补充未确认列表和已确认列表
        List<PaymentActualDetailUnconfirmed> list = paymentActualDetailUnconfirmedService.listByPaymentId(req.getPaymentId());
        List<PaymentActualDetailUnconfirmed> unconfirmedList = list.stream().filter(e -> !Objects.equals(e.getWriteOffStatus(), WriteOffStatus.CONFIRM.name())).collect(Collectors.toList());
        List<PaymentActualDetailUnconfirmed> confirmedList = list.stream().filter(e -> Objects.equals(e.getWriteOffStatus(), WriteOffStatus.CONFIRM.name())).collect(Collectors.toList());
        List<ActualDetailDto> unconfirmedActualDetails = converter.unconfirmedEntitiesToDtos(unconfirmedList);
        List<ActualDetailDto> actualDetailDtos = converter.unconfirmedEntitiesToDtos(confirmedList);
        List<Long> contractIds = new LinkedList<>();
        List<Long> clientIds = new LinkedList<>();
        if (CollUtil.isNotEmpty(unconfirmedActualDetails)) {
            contractIds.addAll(unconfirmedActualDetails.stream().map(ActualDetailDto::getContractId).collect(Collectors.toList()));
            clientIds.addAll(unconfirmedActualDetails.stream().map(ActualDetailDto::getClientId).collect(Collectors.toList()));
        }
        if (CollUtil.isNotEmpty(actualDetailDtos)) {
            contractIds.addAll(actualDetailDtos.stream().map(ActualDetailDto::getContractId).collect(Collectors.toList()));
            clientIds.addAll(actualDetailDtos.stream().map(ActualDetailDto::getClientId).collect(Collectors.toList()));
        }
        Map<Long, String> contractIdCodeMap = new HashMap<>();
        if (CollUtil.isNotEmpty(contractIds)) {
            contractIdCodeMap = SpringUtil.getBean(ContractBaseInfoService.class).listByIds(contractIds)
                    .stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getContractCode));
        }
        Map<Long, String> clientId2NameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(clientIds)) {
            clientId2NameMap = SpringUtil.getBean(Id2NameService.class).clientId2Name(clientIds);
        }
        // 找到实际核销的钱
        Map<Long, List<PaymentActualDetail>> actualDetailListMap = new HashMap<>();
        if (CollUtil.isNotEmpty(list)) {
            actualDetailListMap = SpringUtil.getBean(PaymentActualDetailService.class).list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .in(PaymentActualDetail::getUnConfirmedId, list.stream().map(PaymentActualDetailUnconfirmed::getId).collect(Collectors.toList()))
            ).stream().collect(Collectors.groupingBy(PaymentActualDetail::getUnConfirmedId));
        }
        for (ActualDetailDto e : unconfirmedActualDetails) {
            fillOtherInfo(e, contractIdCodeMap, clientId2NameMap, actualDetailListMap);
        }
        for (ActualDetailDto e : actualDetailDtos) {
            fillOtherInfo(e, contractIdCodeMap, clientId2NameMap, actualDetailListMap);
        }
        rsp.setUnconfirmedActualDetails(unconfirmedActualDetails);
        rsp.setConfirmedActualDetails(actualDetailDtos);
        //【已确认】+【未确认】的 ∑实付金额是否等于申请付款金额
        long canWriteOffAmount = baseInfoService.getCanWriteOffMaxAmount(payment);
        long unconfirmedAmount = unconfirmedList.stream().filter(f -> WriteOffStatus.TO_BE_WRITE_OFF.name().equals(f.getWriteOffStatus()))
                .mapToLong(PaymentActualDetailUnconfirmed::getPaidInAmount).sum();
        long confirmedAmount = confirmedList.stream().mapToLong(PaymentActualDetailUnconfirmed::getPaidInAmount).sum();
        rsp.setAmountIsSame(canWriteOffAmount == unconfirmedAmount + confirmedAmount);
        return rsp;
    }

    private static void fillOtherInfo(ActualDetailDto e, Map<Long, String> contractIdCodeMap, Map<Long, String> clientId2NameMap, Map<Long, List<PaymentActualDetail>> actualDetailListMap) {
        e.setContractCode(contractIdCodeMap.get(e.getContractId()));
        e.setClientName(clientId2NameMap.get(e.getClientId()));
        e.setPaymentMethodEnum(Optional.ofNullable(PaymentMethod.findByDisplay(e.getPaymentMethod())).map(Enum::name).orElse(null));
        if (ObjectUtil.isNotEmpty(actualDetailListMap.get(e.getId()))) {
            e.setPaidAmount(actualDetailListMap.get(e.getId()).stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum());
        }
    }

    public ActualDetailDto detail(Long actualId) {
        ActualDetailDto rsp = converter.entityToDto(baseMapper.selectById(actualId));
        rsp.setMaterialsList(materialsService.listProofOfPaid(actualId));
        // 填充合同编号和客户名称
        rsp.setContractCode(Optional.ofNullable(SpringUtil.getBean(ContractBaseInfoService.class).getById(rsp.getContractId())).map(ContractBaseInfo::getContractCode).orElse(null));
        rsp.setClientName(SpringUtil.getBean(Id2NameService.class).clientId2NameSingle(rsp.getClientId()));
        rsp.setPaymentMethodEnum(Optional.ofNullable(PaymentMethod.findByDisplay(rsp.getPaymentMethod())).map(Enum::name).orElse(null));
        if (ObjectUtil.isNotEmpty(rsp.getMaterialsList())) {
            PaymentMaterialsListRsp paymentMaterialsListRsp = rsp.getMaterialsList().get(0);
            rsp.setEnclosureId(paymentMaterialsListRsp.getId());
            rsp.setEnclosureName(paymentMaterialsListRsp.getFileName());
        }
        return rsp;
    }

    @Transactional(rollbackFor = Exception.class)
    public void writeOff(Long actualDetailId, String statusName) {
        PaymentWriteOffHistory history = new PaymentWriteOffHistory();
        history.setOperation(WriteOffStatus.valueOf(statusName).display);
        PaymentActualDetail entity = getById(actualDetailId);
        PaymentBaseInfo baseInfo = baseInfoService.getById(entity.getPaymentId());
        if (baseInfo.getWriteOffUserIds() != null && JSON.parseArray(baseInfo.getWriteOffUserIds()).size() >= 1) {
            throw new MithrasException("付款申请已核销，禁止操作付款记录");
        }
        entity.setId(actualDetailId);
        entity.setWriteOffStatus(statusName);
        baseMapper.updateById(entity);

        // 如果主表记录的最近支付日期 在当前核销记录的日期之前，更新主表记录的最忌支付日期
        if (baseInfo.getPaidInDate() == null ||
                (entity.getPaidInDate() != null && baseInfo.getPaidInDate().isBefore(entity.getPaidInDate()))) {
            baseInfo.setPaidInDate(entity.getPaidInDate());
        }
        // 检查付款申请下的所有付款记录
        List<PaymentActualDetail> all = baseMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, entity.getPaymentId()));
        int w = 0;
        for (PaymentActualDetail actual : all) {
            if (WriteOffStatus.WRITTEN_OFF.name().equals(actual.getWriteOffStatus())) {
                w++;
            }
        }
        // 当已核销记录数量为0的时候，更新主表记录为 "待核销" 状态
        if (w == 0) {
            baseInfo.setWriteOffStatus(PaymentWriteOffStatus.TO_BE_WRITE_OFF.name());
        } else {
            // 首次核销要更新首次核销日期
            //baseInfo.setFirstWriteOffDate(Optional.ofNullable(baseInfo.getFirstWriteOffDate()).orElse(LocalDate.now()));
            baseInfo.setWriteOffStatus(PaymentWriteOffStatus.PART_WRITTEN_OFF.name());
        }
        baseInfoService.updateById(baseInfo);
        // 记录日志
        history.setDataStatus(baseInfo.getWriteOffStatus());
        history.setPaymentid(entity.getPaymentId());
        history.setOperateDataCode(entity.getSeqCode());
        history.setOperatePersonId(AccountUtil.getLoginInfo().getId());
        history.setOperateTime(LocalDateTime.now());
        history.setOperatePersonName(id2NameService.sysUserId2NameSingle(history.getOperatePersonId()));
        history.setOperateDataType("付款记录");
        historyService.save(history);
    }

    /**
     * 计算生效的付款申请已支付的金额
     * 主要用以统一单份合同
     *
     * @param paymentIds
     * @return
     */
    public Long calculatePaidAmount(List<Long> paymentIds) {
        if (ObjectUtil.isEmpty(paymentIds)) {
            return 0L;
        }
        List<PaymentActualDetail> writeOff = baseMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getPaymentId, paymentIds)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        long amount = 0L;
        for (PaymentActualDetail paymentActualDetail : writeOff) {
            amount += paymentActualDetail.getPaidInAmount();
        }
        return amount;
    }

    /**
     * 计算生效的付款申请已支付的金额
     *
     * @param paymentIds
     * @return map(contractId, PaidAmount)
     */
    public Map<Long, Long> calculatePaidAmountBatch(List<Long> paymentIds) {
        if (ObjectUtil.isEmpty(paymentIds)) {
            return Collections.EMPTY_MAP;
        }
        List<PaymentActualDetail> writeOff = baseMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getPaymentId, paymentIds)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        Map<Long, Long> amounts = new HashMap<>();
        for (PaymentActualDetail paymentActualDetail : writeOff) {
            Long key = paymentActualDetail.getContractId();
            amounts.put(key, amounts.getOrDefault(key, 0L) + paymentActualDetail.getPaidInAmount());
        }
        return amounts;
    }

    /**
     * 计算生效的付款申请已支付的金额
     *
     * @param contractIds
     * @return Long 已付金额
     */
    public Long calculatePaidAmountByContractIds(List<Long> contractIds) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return 0L;
        }
        List<PaymentActualDetail> writeOff = baseMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getContractId, contractIds)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()));
        Long amounts = 0L;
        for (PaymentActualDetail paymentActualDetail : writeOff) {
            amounts += LongUtil.null2zero(paymentActualDetail.getPaidInAmount());
        }
        return amounts;
    }

    public List<PaymentActualDetail> listByPaymentId(Long paymentId) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.eq(PaymentActualDetail::getPaymentId, paymentId);
        return this.list(query);
    }

    public List<PaymentActualDetail> listByPaymentIds(Collection<Long> paymentIds) {
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.in(PaymentActualDetail::getPaymentId, paymentIds);
        return this.list(query);
    }

    public Map<Long, List<PaymentActualDetail>> getMapByPaymentIds(Collection<Long> paymentIds) {
        if (CollectionUtil.isEmpty(paymentIds)) {
            return Collections.emptyMap();
        }
        List<PaymentActualDetail> paymentActualDetailList = this.listByPaymentIds(paymentIds);
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return Collections.emptyMap();
        }
        return paymentActualDetailList.stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
    }

    /**
     * @param clientIds      目标客户id  为NUll表示查询所有客户， 为空集合表示查询不到任何客户
     * @param paidInDateFrom 支付日期起
     * @param paidInDateTo   支付日期止
     * @return 已核销的支付记录
     */
    public List<PaymentActualDetail> writtenOffDetailsByClientIds(@Nullable Set<Long> clientIds,
                                                                  LocalDate paidInDateFrom,
                                                                  LocalDate paidInDateTo) {
        if (clientIds == null) {
            return list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                    .ge(paidInDateFrom != null, PaymentActualDetail::getPaidInDate, paidInDateFrom)
                    .le(paidInDateTo != null, PaymentActualDetail::getPaidInDate, paidInDateTo)
            );
        }
        if (clientIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.in(PaymentActualDetail::getClientId, clientIds);
        query.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        query.ge(paidInDateFrom != null, PaymentActualDetail::getPaidInDate, paidInDateFrom);
        query.le(paidInDateTo != null, PaymentActualDetail::getPaidInDate, paidInDateTo);
        return this.list(query);
    }

    /**
     * @param contractIds    合同ID  为NUll表示查询所有合同， 为空集合表示查询不到任何合同
     * @param paidInDateFrom 支付日期起
     * @param paidInDateTo   支付日期止
     * @return 已核销的支付记录
     */
    public List<PaymentActualDetail> writtenOffDetailsByContractIds(@Nullable Set<Long> contractIds,
                                                                    LocalDate paidInDateFrom,
                                                                    LocalDate paidInDateTo) {
        if (contractIds == null) {
            return list(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                    .ge(paidInDateFrom != null, PaymentActualDetail::getPaidInDate, paidInDateFrom)
                    .le(paidInDateTo != null, PaymentActualDetail::getPaidInDate, paidInDateTo)
            );
        }
        if (contractIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.in(PaymentActualDetail::getContractId, contractIds);
        query.eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name());
        query.ge(paidInDateFrom != null, PaymentActualDetail::getPaidInDate, paidInDateFrom);
        query.le(paidInDateTo != null, PaymentActualDetail::getPaidInDate, paidInDateTo);
        return this.list(query);
    }

    /**
     * 计算合同的剩余未投放金额
     *
     * @param contractIds 合同id集合
     * @return map(contractId, remainingAmount)
     */
    public Map<Long, Long> remainingAmount(Set<Long> contractIds) {
        Map<Long, Long> contractAmounts = contractPriceService.queryNewestContractAmount(contractIds);

        Map<Long, List<PaymentActualDetail>> groupByContract = list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getContractId, contractIds)
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()))
                .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));

        Map<Long, Long> remainingAmounts = new HashMap<>();
        for (Long contractId : contractIds) {
            Long paidAmount = groupByContract.getOrDefault(contractId, Collections.emptyList())
                    .stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            if (contractAmounts.containsKey(contractId)) {
                remainingAmounts.put(contractId, contractAmounts.get(contractId) - paidAmount);
            } else {
                remainingAmounts.put(contractId, 0L);
            }
        }
        return remainingAmounts;
    }

    public List<PaymentActualDetail> queryFirstLaunchClient(LocalDateTime startTime) {
        return baseMapper.queryFirstLaunchClient(startTime);
    }

    //这里单条核销打标
    @Transactional(rollbackFor = Throwable.class)
    public void cancelWriteRecord(PaymentActualDetail info) {
        if (LongUtil.null2zero(info.getPaidInAmount()) >= 0) {
            //收款大于0不需要查找对应反核销期限
            return;
        }
        //查找对应反核销记录
        PaymentActualDetail paymentActualDetail = baseMapper.selectOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getPaymentId, info.getPaymentId())
                .eq(PaymentActualDetail::getCancelWriteOffFlag, YesOrNoNumberEnum.NO.getCode())
                .eq(PaymentActualDetail::getPaidInDate, info.getPaidInDate())
                .eq(PaymentActualDetail::getPaidInAmount, Math.abs(LongUtil.null2zero(info.getPaidInAmount())))
                .orderByAsc(PaymentActualDetail::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(paymentActualDetail)) {
            paymentActualDetail.setCancelWriteOffFlag(YesOrNoNumberEnum.YES.getCode());
            info.setCancelWriteOffFlag(YesOrNoNumberEnum.YES.getCode());
            info.setCancelWriteOffId(paymentActualDetail.getId());
            paymentActualDetail.setCancelWriteOffId(info.getId());
            baseMapper.updateById(info);
            baseMapper.updateById(paymentActualDetail);

            SysUserService userService = SpringContextHolder.getBean(SysUserService.class);
            Set<Long> userIds = userService.getUserIdsByRole("ZXBSGL");
            PaymentBaseInfo paymentBaseInfo = SpringContextHolder.getBean(PaymentBaseInfoService.class).getById(paymentActualDetail.getPaymentId());
            Map<Long, String> longStringMap = SpringContextHolder.getBean(Id2NameService.class).clientId2Name(Collections.singleton(paymentBaseInfo.getClientId()));
            String name = longStringMap.getOrDefault(paymentBaseInfo.getClientId(), "");
            if (CollUtil.isNotEmpty(userIds)) {
                for (Long userId : userIds) {
                    MessageAddREQ messageAddREQ = new MessageAddREQ();
                    messageAddREQ.setFrom("系统通知");
                    messageAddREQ.setMessageType(MessageTypeEnum.FINANCIAL_ANTI_SETTLEMENT_NOTICE.name());
                    messageAddREQ.setRelation(String.format("%s-【%s】-付款进行了反核销处理，请及时关注！", name, paymentBaseInfo.getContractCode()));
                    messageAddREQ.setContent(paymentBaseInfo.getPaymentCode());
                    messageAddREQ.setPcurl(StringUtils.format(MessageUrlEnum.PAYMENT_COMPLETE.pcUrl, paymentBaseInfo.getId()));
                    messageAddREQ.setTo(Collections.singletonList(userId));
                    messageAddREQ.setNeedOa(false);
                    messageAddREQ.setNoticeSource(NoticeSourceENUM.CREDIT_REPORT.name());
                    SpringContextHolder.getBean(MessageService.class).sendMessage(SpringContextHolder.getBean(MessageConver.class).reqToMessage(messageAddREQ));
                }
            }
        }
    }
}