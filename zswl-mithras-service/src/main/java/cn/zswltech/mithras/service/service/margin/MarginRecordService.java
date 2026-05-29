package cn.zswltech.mithras.service.service.margin;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.gruul.common.util.UUIDUtil;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.dto.margin.*;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.service.constant.FinancialConstants;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.MarginWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.FinanceFlowDetailTableEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.margin.RecordTypeEnum;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.enums.notice.NoticeSourceENUM;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.service.enums.third.CQBusinessTypeENUM;
import cn.zswltech.mithras.service.enums.third.CQPaymentTypeENUM;
import cn.zswltech.mithras.service.enums.third.ExceptionSourceENUM;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.margin.MarginBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.margin.MarginRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.margin.MarginWriteOffRecordMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.margin.MarginBaseInfo;
import cn.zswltech.mithras.service.mapper.model.margin.MarginRecordInfo;
import cn.zswltech.mithras.service.mapper.model.margin.MarginWriteOffRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.Listener.collection.CollectionAddEventListener;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.message.MessageService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.service.service.third.financial.vo.CQ2PaymentVO;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.common.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;
import static com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotNull;

/**
 * @create: 2022-08-18
 **/
@Slf4j
@Service
public class MarginRecordService extends ServiceImpl<MarginRecordInfoMapper, MarginRecordInfo> {

    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private MarginRecordInfoMapper marginRecordInfoMapper;
    @Resource
    private MarginWriteOffRecordMapper marginWriteOffRecordMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionRecordInfoMapper collectionRecordInfoMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private FlowTaskApiService flowTaskApiService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private MessageService messageService;
    @Resource
    private MessageConver messageConvert;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;

    public long calculateBalanceBeforeTargetDate(Long contractId, LocalDate targetDate) {
        // 找到保证金主表
        MarginBaseInfo marginBaseInfo = SpringUtil.getBean(MarginBaseInfoService.class).getOne(
                Wrappers.<MarginBaseInfo>lambdaQuery().eq(MarginBaseInfo::getContractId, contractId).last(StringUtil.mysqlLimitOne())
        );
        if (Objects.isNull(marginBaseInfo)) {
            return 0L;
        }
        // 找到保证金余额
        List<MarginRecordInfo> marginRecordInfoList = this.list(
                Wrappers.<MarginRecordInfo>lambdaQuery()
                        .eq(MarginRecordInfo::getMarginId, marginBaseInfo.getId())
                        .lt(MarginRecordInfo::getCollectionDate, targetDate)
        );
        long collect = marginRecordInfoList.stream().filter(e -> Objects.equals(e.getRecordType(), RecordTypeEnum.COLLECTION.name())).mapToLong(MarginRecordInfo::getCollectionAmount).sum();
        long refund = marginRecordInfoList.stream().filter(e -> Objects.equals(e.getRecordType(), RecordTypeEnum.REFUND.name())).mapToLong(MarginRecordInfo::getCollectionAmount).sum();
        long balance = collect - refund;
        return balance < 0 ? 0 : balance;
    }

    @SneakyThrows
    @Transactional(rollbackFor = Throwable.class)
    public String add(MarginRecordInfo info) {
        info.setWriteOff(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
        info.setReview(MarginWriteOffStatusEnum.TO_BE_REVIEW.name());
        info.setWriteOffStatus(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
        Integer count = marginRecordInfoMapper.selectCount(Wrappers.<MarginRecordInfo>lambdaQuery()
                .eq(MarginRecordInfo::getMarginId, info.getMarginId()).eq(MarginRecordInfo::getRecordType, info.getRecordType()));
        info.setSortId(String.valueOf(count + 1));
        marginRecordInfoMapper.insert(info);
        return null;
    }

    //    @Transactional(rollbackFor = Throwable.class)
//    public R<String> update(MarginRecordUpdateREQ req){
//        MarginRecordInfo recordInfo = marginRecordInfoMapper.selectById(req.getId());
//        if (MarginWriteOffStatusEnum.IGNORE.name().equals(recordInfo.getWriteOffStatus()) || MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(recordInfo.getWriteOffStatus() )){
//            return R.fail("核销完毕或忽略状态，不允许操作！");
//        }
////        MarginWriteOffRecord writeOffRecord = new MarginWriteOffRecord();
//        AccountVO loginInfo = AccountUtil.getLoginInfo();
//        if (Strings.isNotEmpty(req.getWriteOff())){
//            List<UserDO> user = userDOMapper.queryLikeJobs(JobEnum.financialmanager.name(), Collections.singletonList(loginInfo.getId()));
//            if (CollectionUtil.isEmpty(user)){
//                return R.fail("财务经理才能进行核销操作！");
//            }
//            if (req.getWriteOff().equals(recordInfo.getWriteOff())){
//                return R.ok();
//            }
//            recordInfo.setWriteOff(req.getWriteOff());
////            writeOffRecord.setOperate(Optional.ofNullable(req.getWriteOff()).map(MarginWriteOffStatusEnum::of).map(MarginWriteOffStatusEnum::display).orElse(null));
//            if (req.getWriteOff().equals(MarginWriteOffStatusEnum.WRITTEN_OFF.name()) || req.getWriteOff().equals(MarginWriteOffStatusEnum.IGNORE.name())) {
//                recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.TO_BE_REVIEW.name());
//                recordInfo.setWriteOffUser(loginInfo.getId());
//            }else if(req.getWriteOff().equals(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name()) ){
//                recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.TO_BE_WRITE_OFF.name());
//                recordInfo.setWriteOffUser(null);
//            }
//        }
//        MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectById(recordInfo.getMarginId());
////        writeOffRecord.setMarginId(marginBaseInfo.getId());
////        writeOffRecord.setRecordId(req.getId());
//        long marginAmount = marginBaseInfo.getCollectionAmount();
//        if (Strings.isNotEmpty(req.getReview())){
//            if (recordInfo.getWriteOffUser() == null){
//                return R.fail("请先核销！");
//            }
//            List<UserDO> user = userDOMapper.queryLikeJobs(JobEnum.cashier.name(), Collections.singletonList(loginInfo.getId()));
//            if (CollectionUtil.isEmpty(user)){
//                return R.fail("出纳才能进行复核操作！");
//            }
//            if (req.getReview().equals(recordInfo.getReview())){
//                return R.ok();
//            }
//            recordInfo.setReview(req.getReview());
////            writeOffRecord.setOperate(Optional.ofNullable(req.getReview()).map(MarginWriteOffStatusEnum::of).map(MarginWriteOffStatusEnum::display).orElse(null));
//            if (req.getReview().equals(MarginWriteOffStatusEnum.REVIEWED.name())) {
//                if (recordInfo.getWriteOff().equals(MarginWriteOffStatusEnum.WRITTEN_OFF.name())) {
//                    recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
//                    recordInfo.setCollectionDate(LocalDate.now());
//                    recordInfo.setReviewUser(loginInfo.getId());
//                    Long collectionAmount = LongUtil.null2zero(recordInfo.getCollectionAmount());
//                    if (RecordTypeEnum.COLLECTION.name().equals(recordInfo.getRecordType())){
//                        marginAmount = marginAmount + collectionAmount;
//                        marginBaseInfo.setCollectionAmount(marginAmount);
//                        marginBaseInfo.setCollectionDate(recordInfo.getCollectionDate());
//                    }else if(RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN.name().equals(recordInfo.getCollectionType())){
//                        marginAmount = marginAmount - collectionAmount;
//                        marginBaseInfo.setCollectionAmount(marginAmount);
//                        marginBaseInfo.setBackAmount(LongUtil.null2zero(marginBaseInfo.getBackAmount())+collectionAmount);
//                    }else if(RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN_DEDUCT.name().equals(recordInfo.getCollectionType())){
//                        marginAmount = marginAmount - collectionAmount;
//                        marginBaseInfo.setCollectionAmount(marginAmount);
//                        marginBaseInfo.setDeductAmount(LongUtil.null2zero(marginBaseInfo.getDeductAmount())+collectionAmount);
//                    }
//                    if (marginBaseInfo.getCollectionAmount() < 0){
//                        R.fail("退款或抵扣金额不可大于保证金余额！");
//                    }
//                    if (recordInfo.getCollectionType().equals(RecordTypeEnum.REFUND_MARGIN_DEDUCT.name())) {
//                        CollectionRecordInfo info = new CollectionRecordInfo();
//                        info.setCollectionId(recordInfo.getCollectionId());
//                        info.setDataSource(String.valueOf(marginBaseInfo.getId()));
//                        info.setCollectionDate(recordInfo.getCollectionDate());
//                        info.setCollectionType(RecordTypeEnum.REFUND_MARGIN_DEDUCT.name());
//                        info.setPostscript(recordInfo.getPostscript());
//                        info.setCollectionAmount(recordInfo.getCollectionAmount());
//                        info.setWriteOffStatus(CollectionRecordWriteOffStatus.WRITTEN_OFF.name());
//                        info.setPrincipal(recordInfo.getDeductPrincipal());
//                        info.setInterest(recordInfo.getDeductInterest());
//                        info.setPenaltyInterest(recordInfo.getDeductPenaltyInterest());
//                        Integer count = collectionRecordInfoMapper.selectCount(Wrappers.<CollectionRecordInfo>lambdaQuery()
//                                .eq(CollectionRecordInfo::getCollectionId, info.getCollectionId()));
//                        info.setSortId(count + 1);
//                        collectionRecordInfoMapper.insert(info);
//                        CollectionBaseInfo baseInfo = collectionBaseInfoMapper.selectById(recordInfo.getCollectionId());
//                        collectionRecordInfoService.writeOffRecord(info,baseInfo);
//                        collectionBaseInfoMapper.updateById(baseInfo);
//                    }
//                }else {
//                    return R.fail("请先联系财务经理核销！");
//                }
//            }else if (req.getReview().equals(MarginWriteOffStatusEnum.IGNORE.name())) {
//                if (recordInfo.getWriteOff().equals(MarginWriteOffStatusEnum.IGNORE.name())){
//                    recordInfo.setWriteOffStatus(MarginWriteOffStatusEnum.IGNORE.name());
//                    recordInfo.setReviewUser(loginInfo.getId());
//                }else {
//                    return R.fail("请先联系财务经理忽略核销！");
//                }
//            }
//        }
//        marginRecordInfoMapper.updateById(recordInfo);
//        if (MarginWriteOffStatusEnum.REVIEWED.name().equals(req.getReview())){
//            marginBaseInfoMapper.updateById(marginBaseInfo);
//            ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(marginBaseInfo.getContractId());
//            if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
//                Integer count = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, marginBaseInfo.getContractId()).ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED));
//                if (count == 0 && marginBaseInfo.getCollectionAmount() <= 0) {
//                    log.info("保证金核销完毕，通知合同执行结清操作[contractId: {}]", marginBaseInfo.getContractId());
//                    contractBaseInfoService.contractSettle(marginBaseInfo.getContractId());
//                }
//            }
////            writeOffRecord.setOperateInfo(String.valueOf(marginBaseInfo.getMarginCode()));
//            }
////        else {
////            writeOffRecord.setOperateInfo(String.valueOf(recordInfo.getSortId()));
////        }
////        writeOffRecord.setMarginAmount(marginAmount);
////        marginWriteOffRecordMapper.insert(writeOffRecord);
//        return R.ok();
//    }
    @Transactional(rollbackFor = Throwable.class)
    public void financialAdd(MarginRecordInfo info) {
        int count = marginRecordInfoMapper.selectCount(Wrappers.<MarginRecordInfo>lambdaQuery()
                .eq(MarginRecordInfo::getMarginId, info.getMarginId()).eq(MarginRecordInfo::getRecordType, info.getRecordType()));
        info.setSortId(String.valueOf(count + 1));
        marginRecordInfoMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void financialWriteOff(MarginRecordInfo recordInfo, String collectionCode, Long paidInAmount, LocalDate paidInDate) {
        MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectById(recordInfo.getMarginId());
        MarginWriteOffRecord writeOffRecord = new MarginWriteOffRecord();
        writeOffRecord.setMarginId(marginBaseInfo.getId());
        writeOffRecord.setRecordId(recordInfo.getId());
        //todo 这里怎么会空
        long marginAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount());
        writeOffRecord.setOperate(recordInfo.getWriteOffStatus());
        Long collectionAmount = LongUtil.null2zero(recordInfo.getCollectionAmount());
        if (RecordTypeEnum.COLLECTION.name().equals(recordInfo.getRecordType())) {
            marginAmount = marginAmount + collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setCollectionDate(recordInfo.getCollectionDate());
        } else if (RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN.name().equals(recordInfo.getCollectionType())) {
            marginAmount = marginAmount - collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setBackAmount(LongUtil.null2zero(marginBaseInfo.getBackAmount()) + collectionAmount);
        } else if (RecordTypeEnum.REFUND.name().equals(recordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN_DEDUCT.name().equals(recordInfo.getCollectionType())) {
            marginAmount = marginAmount - collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setDeductAmount(LongUtil.null2zero(marginBaseInfo.getDeductAmount()) + collectionAmount);
        }
        marginBaseInfoMapper.updateById(marginBaseInfo);
        //ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(marginBaseInfo.getContractId());
        //提前结清不是起租状态了
        //if (Objects.equals(contractBaseInfo.getContractStatus(), ContractStatus.START_RENT.name())) {
        Integer count = collectionBaseInfoMapper.selectCount(Wrappers.<CollectionBaseInfo>lambdaQuery().eq(CollectionBaseInfo::getContractId, marginBaseInfo.getContractId())
                .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED));
        if (marginBaseInfo.getCollectionAmount() <= 0) {
            //保证金核销完成，通知苍穹
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    List<MarginRecordInfo> marginRecordInfos = marginRecordInfoMapper.selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                            .eq(MarginRecordInfo::getMarginId, marginBaseInfo.getId())
                            .orderByAsc(MarginRecordInfo::getCollectionDate));
                    if (ObjectUtil.isNotEmpty(marginRecordInfos)) {
                        List<CQ2PaymentVO> vos = new ArrayList<>();
                        marginRecordInfos.stream().filter(e -> StrUtil.equalsAny(e.getCollectionType(), RecordTypeEnum.REFUND_MARGIN.name(), RecordTypeEnum.REFUND.name())).forEach(recordInfo -> vos.add(buildPayment(marginBaseInfo, recordInfo)));
                        financialManagerServiceImpl2.cq2PaymentExec(SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(marginBaseInfo.getContractId()), vos);
                    }
                }
            });
        }
        if (count == 0 && marginBaseInfo.getCollectionAmount() <= 0) {
            log.info("保证金核销完毕，通知合同执行结清操作[contractId: {}]", marginBaseInfo.getContractId());
            //这里修改为正常结清通过可结清，提前结清的需在结清确认流程中
            ProcessPageReq req = new ProcessPageReq();
            req.setBusinessKey(String.valueOf(marginBaseInfo.getContractId()));
            req.setPageIndex(1);
            req.setPageSize(1);
            req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleFlow.name());
            req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
            ProcessResp processResp = flowTaskApiService.queryProcess(req).getContents()
                    .stream().findFirst().orElse(null);
            if (ObjectUtil.isEmpty(processResp)) {
                contractBaseInfoService.contractSettle(marginBaseInfo.getContractId());
            } else {
                req.setModelKey(ProcessModelTypeEnum.ContractEarlySettleConfirmFlow.name());
                req.setProcessStatusList(ListUtil.toList(ProcessBusinessStatusEnum.RUNNING.getType(), ProcessBusinessStatusEnum.PASS.getType(), ProcessBusinessStatusEnum.PASS_ALL.getType()));
                if (ObjectUtil.isNotEmpty(flowTaskApiService.queryProcess(req).getContents()
                        .stream().findFirst().orElse(null))) {
                    contractBaseInfoService.contractSettle(marginBaseInfo.getContractId());
                }
            }
        }
        //}
        writeOffRecord.setOperateInfo(String.valueOf(marginBaseInfo.getMarginCode()));
        writeOffRecord.setMarginAmount(marginAmount);
        marginWriteOffRecordMapper.insert(writeOffRecord);

        if (isNotNull(collectionCode)){
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .eq(CollectionBaseInfo::getCode, collectionCode)
                    .gt(CollectionBaseInfo::getPhase, 0)
                    .in(CollectionBaseInfo::getWriteOffStatus, Arrays.asList(CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name(),
                            CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())));
            if (!collectionBaseInfoList.isEmpty()) {
                Set<Long> userIds = new HashSet<>();
                CollectionBaseInfo info = collectionBaseInfoList.get(0);
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(info.getContractId());
                //法律合规部
                Set<Long> flhgbSet = sysUserService.getUserByDeptCode("FLHGB_ZCBQ").stream().map(UserDO::getId).collect(Collectors.toSet());
                //财务部
                Set<Long> jhcwbSet = sysUserService.getUserByDeptCode("JHCWB").stream().map(UserDO::getId).collect(Collectors.toSet());
                //资金部
                Set<Long> zjglbSet = sysUserService.getUserByDeptCode("ZJGLB").stream().map(UserDO::getId).collect(Collectors.toSet());
                userIds.addAll(flhgbSet);
                userIds.addAll(jhcwbSet);
                userIds.addAll(zjglbSet);
                if (isNotNull(contractBaseInfo.getProjSponsorUserId())) {
                    userIds.add(contractBaseInfo.getProjSponsorUserId());
                }
                if (isNotNull(contractBaseInfo.getProjCosponsorUserIds())) {
                    List<Long> cosponsorUserIds = JSONUtil.toList(contractBaseInfo.getProjCosponsorUserIds(), Long.class);
                    userIds.addAll(cosponsorUserIds);
                }
                if (isNotNull(contractBaseInfo.getBizDeptLeaderId())) {
                    userIds.add(contractBaseInfo.getBizDeptLeaderId());
                }
                MessageAddREQ messageAddREQ = new MessageAddREQ();
                messageAddREQ.setFrom("系统通知");
                messageAddREQ.setMessageType(MessageTypeEnum.RENT_RECEIVED.name());
                String clientName = id2NameService.clientId2Name(Collections.singleton(info.getClientId())).get(info.getClientId());
                BigDecimal cashFlowAmountB = LongUtil.tenThousand2Dollar(String.valueOf(info.getCashFlowAmount())).setScale(2, RoundingMode.HALF_UP);
                BigDecimal paidInAmountB = LongUtil.tenThousand2Dollar(String.valueOf(paidInAmount)).setScale(2, RoundingMode.HALF_UP);
                messageAddREQ.setRelation(String.format("%s-%s-第%s期租金%s元于%s到账%s元，请知悉",
                        clientName, contractBaseInfo.getContractCode(), info.getPhase(), cashFlowAmountB, paidInDate, paidInAmountB));
                messageAddREQ.setContent(info.getCode());
                messageAddREQ.setPcurl(StringUtils.format("/cpm/collectionWriteOff/detail/%s", info.getId()));
                messageAddREQ.setTo(new ArrayList<>(userIds));
                messageAddREQ.setNeedOa(false);
                messageAddREQ.setNoticeSource(NoticeSourceENUM.RENT_RECEIVED.name());
                messageService.sendMessage(messageConvert.reqToMessage(messageAddREQ));
            }
        }
    }

    //反核销付款数据
    @Transactional(rollbackFor = Throwable.class)
    public void withdraw(Long id) {
        MarginRecordInfo marginRecordInfo = baseMapper.selectById(id);
        if(ObjectUtil.isEmpty(marginRecordInfo)) {
            return;
        }
        LambdaUpdateWrapper<MarginRecordInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(MarginRecordInfo::getDeleted, YesOrNoNumberEnum.YES.getCode());
        updateWrapper.eq(MarginRecordInfo::getId, marginRecordInfo.getId());
        baseMapper.update(null, updateWrapper);
        MarginBaseInfo marginBaseInfo = marginBaseInfoMapper.selectById(marginRecordInfo.getMarginId());
        //todo 这里怎么会空
        long marginAmount = LongUtil.null2zero(marginBaseInfo.getCollectionAmount());
        Long collectionAmount = LongUtil.null2zero(marginRecordInfo.getCollectionAmount());
        if (RecordTypeEnum.COLLECTION.name().equals(marginRecordInfo.getRecordType())) {
            marginAmount = marginAmount - collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setCollectionDate(marginRecordInfo.getCollectionDate());
        } else if (RecordTypeEnum.REFUND.name().equals(marginRecordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN.name().equals(marginRecordInfo.getCollectionType())) {
            marginAmount = marginAmount + collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setBackAmount(LongUtil.null2zero(marginBaseInfo.getBackAmount()) + collectionAmount);
        } else if (RecordTypeEnum.REFUND.name().equals(marginRecordInfo.getRecordType()) && RecordTypeEnum.REFUND_MARGIN_DEDUCT.name().equals(marginRecordInfo.getCollectionType())) {
            marginAmount = marginAmount + collectionAmount;
            marginBaseInfo.setCollectionAmount(marginAmount);
            marginBaseInfo.setDeductAmount(LongUtil.null2zero(marginBaseInfo.getDeductAmount()) + collectionAmount);
        }
        marginBaseInfoMapper.updateById(marginBaseInfo);
        financeFlowRecordService.withdrawBankFlow(marginRecordInfo.getId(), FinanceFlowDetailTableEnum.MARGIN_RECORD_INFO.name(), marginRecordInfo.getCollectionAmount());
    }

    private CQ2PaymentVO buildPayment(MarginBaseInfo baseInfo, MarginRecordInfo detail){
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(baseInfo.getContractId());
        if(ObjectUtil.isEmpty(contractBaseInfo)){
            return null;
        }
        Client client = getBean(ClientService.class).getById(contractBaseInfo.getClientId());
        if(ObjectUtil.isEmpty(client)){
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
        //vo.setPayzh_bank_name(detail.getOurAccountBank());
        vo.setApplydate(detail.getCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        //vo.setExchangerate(BigDecimal.valueOf(1));
       /* vo.setPaycurrency_number(FinancialConstants.RMB);
        vo.setSettlecurrency_number(FinancialConstants.RMB);*/
        vo.setCico_srcbillno(String.join("-", baseInfo.getMarginCode(), UUIDUtil.genUuid()));
        CQ2PaymentVO.CQ2PaymentVOEntry entry = vo.new CQ2PaymentVOEntry();
        entry.setE_paymenttype_number(CQPaymentTypeENUM.FK05_004.getCode());
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
        //这里是项目端保证金推款或者抵扣
        vo.setSource(ExceptionSourceENUM.BUSINESS_MARGIN.name());
        vo.setBusinessKey(String.valueOf(baseInfo.getId()));
        vo.setBusinessTitle(baseInfo.getMarginCode());
        return vo;
    }


    public List<MarginRecordListRSP> list(MarginRecordListREQ req) {
        List<MarginRecordInfo> infoList;
        if (RecordTypeEnum.COLLECTION.name().equals(req.getRecordType())) {
            MarginBaseInfo info = marginBaseInfoMapper.selectById(req.getId());
            List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getContractId, info.getContractId())
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name())
                    .orderByAsc(CollectionBaseInfo::getCode));
            infoList = new LinkedList<>();
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfos) {
                MarginRecordInfo recordInfo = new MarginRecordInfo();
                collectionBaseInfo2marginRecordInfo(collectionBaseInfo, recordInfo);
                infoList.add(recordInfo);
            }
        } else {
            infoList = marginRecordInfoMapper.selectList(Wrappers.<MarginRecordInfo>lambdaQuery()
                    .eq(MarginRecordInfo::getMarginId, req.getId())
                    .eq(MarginRecordInfo::getRecordType, req.getRecordType())
                    .orderByAsc(MarginRecordInfo::getSortId));
        }
        List<MarginRecordListRSP> rsps = new LinkedList<>();
        //现金流编号转合同编号
        Map<String, String> rentCode2ContractCode = new HashMap<>();
        Set<String> rentCodeSet = infoList.stream().map(MarginRecordInfo::getRentCollectionCode).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        Set<Long> collectIdSet = infoList.stream().map(MarginRecordInfo::getCollectionId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet());
        List<CollectionBaseInfo> collectionBaseInfos = null;
        if (ObjectUtil.isNotEmpty(collectIdSet)) {
            collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getId, collectIdSet));
            if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
                rentCode2ContractCode.putAll(collectionBaseInfos.stream().collect(Collectors.toMap(CollectionBaseInfo::getCode, CollectionBaseInfo::getContractCode)));
            }
        }
        //查询应收日期
        Map<Long, CollectionBaseInfo> collectionId2Bean = new HashMap<>();
        if (ObjectUtil.isNotEmpty(collectionBaseInfos)) {
            collectionId2Bean = collectionBaseInfos.stream().collect(Collectors.toMap(CollectionBaseInfo::getId, e -> e, (a, b) -> a));
        }
        for (MarginRecordInfo o : infoList) {
            MarginRecordListRSP rsp = new MarginRecordListRSP();
            BeanUtil.copyProperties(o, rsp);
            if (RecordTypeEnum.COLLECTION.name().equals(o.getRecordType())) {
                rsp.setWriteOffStatus(Optional.ofNullable(o.getWriteOffStatus()).map(CollectionWriteOffStatusEnum::of).map(CollectionWriteOffStatusEnum::display).orElse(null));
            } else {
                rsp.setWriteOffStatus(Optional.ofNullable(o.getWriteOffStatus()).map(MarginWriteOffStatusEnum::of).map(MarginWriteOffStatusEnum::display).orElse(null));
            }
            rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
            rsp.setDataSource("财务系统");
            rsp.setContractCode(rentCode2ContractCode.get(o.getRentCollectionCode()));
            CollectionBaseInfo collectionBaseInfo = collectionId2Bean.get(o.getCollectionId());
            if (ObjectUtil.isNotEmpty(collectionBaseInfo)) {
                rsp.setPlanCollectionDate(collectionBaseInfo.getPlanCollectionDate());
                rsp.setPlanCollectionAmount(collectionBaseInfo.getPlanCollectionAmount());
            }
            rsps.add(rsp);
        }
        return rsps;
    }

    private void collectionBaseInfo2marginRecordInfo(CollectionBaseInfo collectionBaseInfo, MarginRecordInfo recordInfo) {
        recordInfo.setCollectionId(collectionBaseInfo.getId());
        recordInfo.setId(collectionBaseInfo.getId());
        recordInfo.setSortId(collectionBaseInfo.getCode());
        recordInfo.setCollectionDate(collectionBaseInfo.getCollectionDate());
        recordInfo.setCollectionAmount(collectionBaseInfo.getCollectionAmount());
        recordInfo.setWriteOffStatus(collectionBaseInfo.getWriteOffStatus());
        recordInfo.setRecordType(RecordTypeEnum.COLLECTION.name());
        recordInfo.setCollectionType(RecordTypeEnum.WIRE_TRANSFER.name());
    }

    public MarginRecordDetailRSP collectionDetail(MarginRecordDetailREQ req) {
        CollectionBaseInfo collectionBaseInfo = collectionBaseInfoMapper.selectById(req.getId());
        MarginRecordInfo marginRecordInfo = new MarginRecordInfo();
        collectionBaseInfo2marginRecordInfo(collectionBaseInfo, marginRecordInfo);
        CollectionRecordInfo recordInfo = collectionRecordInfoMapper.selectOne(Wrappers.<CollectionRecordInfo>lambdaQuery().eq(CollectionRecordInfo::getCollectionId, req.getId()).orderByDesc(CollectionRecordInfo::getSortId).last("LIMIT 1"));
        MarginRecordDetailRSP rsp = new MarginRecordDetailRSP();
        if (recordInfo != null) {
            MarginRecordDetailRSP.BankInfo bankInfo = new MarginRecordDetailRSP.BankInfo();
            bankInfo.setAccountId(recordInfo.getOurAccountId());
            bankInfo.setAccountName(recordInfo.getOurAccountName());
            bankInfo.setAccountNumber(recordInfo.getOurAccountNumber());
            bankInfo.setAccountBank(recordInfo.getOurAccountBank());
            rsp.setOurBankInfo(bankInfo);
        }
        BeanUtil.copyProperties(marginRecordInfo, rsp);
        rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
//        if (marginRecordInfo.getSourceFlag() == 1) {
//            Map<Long, String> id2Name = id2NameService.sysUserId2Name(Collections.singleton(Long.parseLong(marginRecordInfo.getDataSource())));
//            rsp.setDataSource(id2Name.get(Long.parseLong(marginRecordInfo.getDataSource())));
//        }
        return rsp;
    }

    public MarginRecordDetailRSP detail(MarginRecordDetailREQ req) {
        MarginRecordInfo marginRecordInfo = marginRecordInfoMapper.selectById(req.getId());
        if (ObjectUtil.isNull(marginRecordInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        MarginRecordDetailRSP rsp = new MarginRecordDetailRSP();
        if (StrUtil.isNotBlank(marginRecordInfo.getOurAccountNumber())) {
            rsp.setOurBankInfo(getBankInfo(marginRecordInfo, false));
        }
        if (StrUtil.isNotEmpty(marginRecordInfo.getOtherAccountNumber())) {
            rsp.setOtherBankInfo(getBankInfo(marginRecordInfo, true));
        }
        BeanUtil.copyProperties(marginRecordInfo, rsp);
        rsp.setCollectionType(Optional.ofNullable(rsp.getCollectionType()).map(RecordTypeEnum::of).map(RecordTypeEnum::display).orElse(null));
//        if (marginRecordInfo.getSourceFlag() == 1) {
//            Map<Long, String> id2Name = id2NameService.sysUserId2Name(Collections.singleton(Long.parseLong(marginRecordInfo.getDataSource())));
//            rsp.setDataSource(id2Name.get(Long.parseLong(marginRecordInfo.getDataSource())));
//        }
        rsp.setDataSource("财务系统");
        return rsp;
    }

    public MarginDeductDetailRSP deductDetail(MarginRecordDetailREQ req) {
        MarginRecordInfo marginRecordInfo = marginRecordInfoMapper.selectById(req.getId());
        MarginDeductDetailRSP rsp = new MarginDeductDetailRSP();
        BeanUtil.copyProperties(marginRecordInfo, rsp);
        MarginCashInfoRSP cashInfo = new MarginCashInfoRSP();
        CollectionBaseInfo info = collectionBaseInfoMapper.selectById(marginRecordInfo.getCollectionId());
        rsp.setPhase(info.getPhase());
        rsp.setPaymentCode(info.getPaymentCode());
        cashInfo.setCode(info.getCode());
        cashInfo.setCashFlowAmount(info.getCashFlowAmount());
        cashInfo.setCashFlowItem(Optional.ofNullable(info.getCashFlowItem()).map(CashFlowItemEnum::of).map(CashFlowItemEnum::display).orElse(null));
        cashInfo.setInterest(info.getInterest());
        cashInfo.setPenaltyInterest(info.getPenaltyInterest());
        cashInfo.setPrincipal(info.getPrincipal());
        cashInfo.setPlanCollectionDate(info.getPlanCollectionDate());
        cashInfo.setPlanCollectionAmount(info.getPlanCollectionAmount());
        cashInfo.setLastDeductPrincipal(LongUtil.null2zero(info.getPrincipal()) - LongUtil.null2zero(info.getCollectionPrincipal()));
        cashInfo.setLastDeductInterest(LongUtil.null2zero(info.getInterest()) - LongUtil.null2zero(info.getCollectionInterest()));
        cashInfo.setLastDeductPenaltyInterest(LongUtil.null2zero(info.getPenaltyInterest()) - LongUtil.null2zero(info.getCollectionPenaltyInterest()));
        cashInfo.setLastDeductAmount(cashInfo.getLastDeductInterest() + cashInfo.getLastDeductPenaltyInterest() + cashInfo.getLastDeductPrincipal());
        rsp.setCashInfo(cashInfo);
        return rsp;
    }

    private MarginRecordDetailRSP.BankInfo getBankInfo(MarginRecordInfo marginRecordInfo, boolean isOther) {
        MarginRecordDetailRSP.BankInfo bankInfo = new MarginRecordDetailRSP.BankInfo();
        if (isOther) {
            bankInfo.setAccountName(marginRecordInfo.getOtherAccountName());
            bankInfo.setAccountNumber(marginRecordInfo.getOtherAccountNumber());
            bankInfo.setAccountBank(marginRecordInfo.getOtherAccountBank());
        } else {
            bankInfo.setAccountId(marginRecordInfo.getOurAccountId());
            bankInfo.setAccountName(marginRecordInfo.getOurAccountName());
            bankInfo.setAccountNumber(marginRecordInfo.getOurAccountNumber());
            bankInfo.setAccountBank(marginRecordInfo.getOurAccountBank());
        }

        return bankInfo;
    }
}
