package cn.zswltech.mithras.service.service.Listener.collection;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.collection.CollectionBaseInfoAddREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.contract.baseinfo.ContractBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.margin.MarginBaseInfoAddREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.enums.projestablish.LeaseType;
import cn.zswltech.mithras.third.enums.*;
import cn.zswltech.mithras.service.mapper.finance.ContractAssessDeptDetailMapper;
import cn.zswltech.mithras.service.mapper.margin.MarginBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.margin.WarrantyBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.margin.MarginBaseInfo;
import cn.zswltech.mithras.service.mapper.model.margin.WarrantyBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentCollectionInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentCollectionInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionService;
import cn.zswltech.mithras.service.service.contract.*;
import cn.zswltech.mithras.service.service.contract.impl.ContractReceiptServiceImpl;
import cn.zswltech.mithras.contract.archive.application.ContractRentActualLibService;
import cn.zswltech.mithras.contract.archive.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.margin.MarginBaseInfoService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.third.financial.FinancialManagerService;
import cn.zswltech.mithras.service.service.third.financial.impl.FinancialManagerServiceImpl2;
import cn.zswltech.mithras.third.financialshare.infrastructure.client.req.CQReceiveRentREQ;
import cn.zswltech.mithras.third.financialshare.application.dto.*;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static cn.hutool.core.text.CharSequenceUtil.*;
import static cn.hutool.core.util.ObjectUtil.*;
import static cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum.*;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;


/**
 * @create: 2022-08-29
 **/
@Component
@Slf4j
public class CollectionAddEventListener implements ApplicationListener<CollectionAddEvent> {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private MarginBaseInfoMapper marginBaseInfoMapper;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private ContractBaseInfoLibHandler baseInfoLibHandler;
    @Resource
    private FinancialManagerService financialManagerService;
    @Resource
    private ContractSettlePlanService contractSettlePlanService;
    @Resource
    private CollectionService collectionService;
    @Resource
    private FinancialManagerServiceImpl2 financialManagerServiceImpl2;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private UserService userService;
    @Resource
    private ContractAssessDeptDetailMapper contractAssessDeptDetailMapper;
    @Resource
    private WarrantyBaseInfoMapper warrantyBaseInfoMapper;
    @Resource
    private PaymentCollectionInfoMapper paymentCollectionInfoMapper;


    public void earnestChange() {
        //保证金金额，转负数
    }

    /**
     * @param contractId 合同id
     *                   实际租金表发生变化
     *                   对比实际租金表和收款明细、将变化的地方保存并同步苍穹
     */
    @Transactional(rollbackFor = Throwable.class)
    public void rentChange(Long contractId, ProcessModelTypeEnum processModelTypeEnum) {
        //借据缓存，加快速度
        Map<Long, ContractReceipt> receiptCache = new HashMap<>(4);
        //1、查出合同的实际租金表
        ContractBaseInfoLib contractBaseInfoLib = getBean(ContractBaseInfoLibHandler.class).queryLatestDataByOriginId(contractId);/*获取主表最新版本*/
        List<ContractRentActual> rentActualList = getBean(ContractRentActualLibService.class).listByContractVersion(contractId, contractBaseInfoLib.getVersion());
        //过滤出现金流编号不为空的记录；（为空代表着并未关联付款）
        rentActualList = rentActualList.stream().filter(e -> isNotBlank(e.getCashFlowCode())).collect(Collectors.toList());
        //转换为map，key为【借据id+期限】，保证唯一
        Map<String, ContractRentActual> contractRentActualMap = rentActualList.stream()
                .collect(Collectors.toMap(e -> join("#", e.getReceiptId(), e.getCashFlowPhase()), e -> e));
        //借据分组实际租金表
        Map<Long, List<ContractRentActual>> receiptIdRentActualMap = rentActualList.stream().collect(Collectors.groupingBy(ContractRentActual::getReceiptId));
        //2、查找出该合同租金收款明细记录
        List<CollectionBaseInfo> collectionList = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                .orderByAsc(CollectionBaseInfo::getReceiptCode));
        //查找合同利率
        ContractPriceDetailRSP detail = contractPriceService.detail(new ContractPriceDetailREQ(contractId));
        BigDecimal rate = NumberUtil.div(LongUtil.tenThousand2Dollar(NumberUtil.add(detail.getLprPercent(), detail.getLprAddPercent()).toString()).toString(), "100",
                5, RoundingMode.HALF_UP);
        //转换为map，key为【借据id+期项】，保证唯一
        Map<String, CollectionBaseInfo> collectionMap = collectionList.stream()
                //借据为空，未关联
                .filter(e -> ObjectUtil.isNotEmpty(e.getReceiptId()))
                .collect(Collectors.toMap(e -> join("#", e.getReceiptId(), e.getPhase()), e -> e));
        //3、根据【借据id+期项】编号进行比对，
        Set<String> allKeySet = new HashSet<>(contractRentActualMap.keySet());
        allKeySet.addAll(collectionMap.keySet());
        //对比后的变化类型列表
        List<CollectionBaseInfo> toUpdateList = new ArrayList<>();
        List<CollectionBaseInfo> toAddList = new ArrayList<>();
        List<CollectionBaseInfo> toDeleteList = new ArrayList<>();
        //查询最新合同结清方案
        ContractSettlePlan contractSettlePlan = contractSettlePlanService.getLatestContractSettlePlan(contractId);
        //根据流程判断是否修改期次等
        FinancialChangeStateENUM financialChangeStateENUM = FinancialChangeStateENUM.changeCqStatus(processModelTypeEnum, isNull(contractSettlePlan) ? null :
                contractSettlePlan.getIsEarnestDeduction());
        for (String key : allKeySet) {
            //实际租金表
            ContractRentActual rentActual = contractRentActualMap.get(key);
            //合同收款明细
            CollectionBaseInfo baseInfo = collectionMap.get(key);
            //已核销(核销完毕、部分核销)的记录不做处理
            if (isNotNull(baseInfo) && equalsAny(baseInfo.getWriteOffStatus(), WRITE_OFF_COMPLETED.name(), PORTION_WRITTEN_OFF.name())) {
                continue;
            }
            //如果收款明细和实际租金表中存在编号匹配的记录，且收款明细还未核销
            if (isNotNull(rentActual) && isNotNull(baseInfo)) {
                //是否发生变更
                if (hasChanged(rentActual, baseInfo)) {
                    toUpdateList.add(extractChangedInfo(rentActual, baseInfo));
                }
            }
            //如果实际租金表中存在记录，收款明细中不存在；代表着实际租金表可能新增了期数
            if (isNotNull(rentActual) && isNull(baseInfo)) {
                //借据放入缓存
                receiptCache.putIfAbsent(rentActual.getReceiptId(), getBean(ContractReceiptServiceImpl.class).getById(rentActual.getReceiptId()));
                toAddList.add(newCollectionFromRentActual(contractBaseInfoLib, receiptCache.get(rentActual.getReceiptId()), rentActual, CashFlowItemEnum.RENT));
            }
            //如果实际租金表没有记录，收款明细中有记录；代表着实际租金表可能减少了期数
            if (isNull(rentActual) && isNotNull(baseInfo)) {
                toDeleteList.add(baseInfo);
            }
        }
        //4、针对变化的点，进行数据库变更及苍穹同步；
        //分【借据】进行
        Map<Long/*借据id*/, FinancialCollectionRentVO/*变更详细*/> receiptRentMap = new HashMap<>(8);
        if (isNotEmpty(toUpdateList)) {
            //修改数据库收款明细
            getBean(CollectionBaseInfoService.class).updateBatchById(toUpdateList);
            for (CollectionBaseInfo cbf : toUpdateList) {
                receiptRentMap.putIfAbsent(cbf.getReceiptId(),
                        new FinancialCollectionRentVO().setContractId(cbf.getContractId()).setReceiptId(cbf.getReceiptId()).setChangeState(financialChangeStateENUM).
                                setReceiptCode(cbf.getReceiptCode()).setContractCode(cbf.getContractCode()).setLeaseRate(rate));
                receiptRentMap.get(cbf.getReceiptId()).getUpdateRentActual().add(
                        buildSyncCqReqBody(cbf)
                );
            }
        }
        if (isNotEmpty(toAddList)) {
            getBean(CollectionBaseInfoService.class).saveBatch(toAddList);
            for (CollectionBaseInfo cbf : toAddList) {
                receiptRentMap.putIfAbsent(cbf.getReceiptId(),
                        new FinancialCollectionRentVO().setContractId(cbf.getContractId()).setReceiptId(cbf.getReceiptId()).setReceiptId(cbf.getReceiptId()).
                                setChangeState(financialChangeStateENUM).setReceiptCode(cbf.getReceiptCode()).setContractCode(cbf.getContractCode()).setLeaseRate(rate));
                receiptRentMap.get(cbf.getReceiptId()).getAddRentActual().add(
                        buildSyncCqReqBody(cbf)
                );
            }
        }
        if (isNotEmpty(toDeleteList)) {
            List<Long> idList = toDeleteList.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
            getBean(CollectionBaseInfoService.class).removeByIds(idList);
            for (CollectionBaseInfo cbf : toDeleteList) {
                receiptRentMap.putIfAbsent(cbf.getReceiptId(),
                        new FinancialCollectionRentVO().setContractId(cbf.getContractId()).setReceiptId(cbf.getReceiptId()).setReceiptId(cbf.getReceiptId()).
                                setChangeState(financialChangeStateENUM).setReceiptCode(cbf.getReceiptCode()).setContractCode(cbf.getContractCode()).setLeaseRate(rate));
                receiptRentMap.get(cbf.getReceiptId()).getRemoveRentActual().add(
                        buildSyncCqReqBody(cbf)
                );
                //删除补0添加期限
                receiptRentMap.get(cbf.getReceiptId()).getAddRentActual().add(
                        buildSyncCqReqBody(cbf).setRent(0L).setInterest(0L).setPrincipal(0L).setRemainingPrincipal(0L)
                );
            }
        }
        //正常结清时需要特殊处理 最后一期取消重新推送，并填充保证金变更结果
        if (FinancialChangeStateENUM.ContractNormalSettle.equals(financialChangeStateENUM)) {
            CollectionBaseInfo baseInfo = collectionList.get(collectionList.size() - 1);
            receiptRentMap.putIfAbsent(baseInfo.getReceiptId(),
                    new FinancialCollectionRentVO().setContractId(baseInfo.getContractId()).setReceiptId(baseInfo.getReceiptId()).
                            setChangeState(financialChangeStateENUM).setReceiptCode(baseInfo.getReceiptCode()).setContractCode(baseInfo.getContractCode()).setLeaseRate(rate));
            SyncCqReqBody syncCqReqBody = buildSyncCqReqBody(baseInfo);
            receiptRentMap.get(baseInfo.getReceiptId()).getUpdateRentActual().add(syncCqReqBody);
        }
        //补充第0期租金 -- 唯一ID，合同起租日期，合同剩余本金，当新增借据时传输，用于苍穹技术计提利息
        receiptRentMap.values().forEach(receiptRent -> {
            if (ProcessModelTypeEnum.ContractStartRentFlow.equals(processModelTypeEnum) || ProcessModelTypeEnum.ContractAddNewReceiptFlow.equals(processModelTypeEnum)
                    || ProcessModelTypeEnum.ContractStartRentAutoFlow.equals(processModelTypeEnum)
                    || ProcessModelTypeEnum.ContractAddNewReceiptAutoFlow.equals(processModelTypeEnum)) {
                receiptRent.getAddRentActual().add(addZeroRent(receiptRent.getReceiptId(), receiptRent.getReceiptCode(),
                        receiptIdRentActualMap.get(receiptRent.getReceiptId()), contractBaseInfoLib.getActualLeaseDate()));
            }
        });
        //
        SyncCqReqBizInfo bizInfo = getBizInfo(contractId);
        //一期推送  实际仅取了数据处理对象
        List<CQReceiveRentREQ> cqReceiveRentREQS = getBean(FinancialManagerServiceImpl2.class).adjustReceipts(bizInfo, receiptRentMap);
        if(ObjectUtil.isEmpty(cqReceiveRentREQS)) {
            return;
        }
        //名义价款修正
        changeNomnalPrice(contractId,rentActualList);
        //租金表变更，记账
        accountApplication(contractBaseInfoLib, getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate)
                .orderByAsc(CollectionBaseInfo::getReceiptCode)), processModelTypeEnum, cqReceiveRentREQS);
    }
    /**
     * 租金调整后 修改名义价款
     */
    private void changeNomnalPrice (Long contractId , List<ContractRentActual> rentActualList){
        log.info("changeNomnalPrice start!! contratId:{}",contractId);
        if (CollectionUtils.isEmpty(rentActualList)){
            return;
        }
        //取名义价款
        List<CollectionBaseInfo> collectionList = getBean(CollectionBaseInfoService.class).list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractId)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.NOMINAL_PRICE.name())
                .orderByAsc(CollectionBaseInfo::getPlanCollectionDate));
        if (CollectionUtils.isEmpty(collectionList)){
            log.info("changeNomnalPrice 名义价款为空 contratId:{}",contractId);
            return;
        }
        //倒序去第一条
        Optional<ContractRentActual> contractRentActual = rentActualList.stream()
                .sorted(Comparator.comparingInt(ContractRentActual::getCashFlowPhase).reversed())
                .findFirst();
        collectionList.stream().forEach(x->{
            x.setPlanCollectionDate(contractRentActual.get().getCashFlowDate());
            getBean(CollectionBaseInfoService.class).updateById(x);
        });
    }

    private void accountApplication(ContractBaseInfoLib contractBaseInfoLib, List<CollectionBaseInfo> collectionList, ProcessModelTypeEnum processModelTypeEnum, List<CQReceiveRentREQ> cqReceiveRentREQS) {
        SyncCqReqBizInfo bizInfo = SpringContextHolder.getBean(CollectionAddEventListener.class).getBizInfo(contractBaseInfoLib.getOriginId());
        //提供客户（租金往来方）名称
        ContractTenantry contractTenantry = contractTenantryService.getOne(Wrappers.<ContractTenantry>lambdaQuery()
                .eq(ContractTenantry::getContractId, contractBaseInfoLib.getOriginId())
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(contractTenantry) && ObjectUtil.isNotEmpty(contractTenantry.getRentConcatAccountId())) {
            Client client = getBean(ClientService.class).getById(Long.valueOf(contractTenantry.getRentConcatAccountId()));
            if (ObjectUtil.isNotEmpty(client)) {
                bizInfo.setCustomer(client.getClientCode());
                bizInfo.setCustomerName(client.getClientName());
            }
        }
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        CQ2AccountApplicationVO vo = new CQ2AccountApplicationVO();
        vo.setDescription(Optional.ofNullable(CQAccountApplicationTypeENUM.getCqBusinessType(processModelTypeEnum)).map(CQAccountApplicationTypeENUM::getDisplay).orElse(null));
        vo.setBizdate(nowFormat);
        vo.setTallydate(vo.getBizdate());
        vo.setCico_customer(bizInfo.getCustomerName());
        vo.setCico_taxcategory("normal");//不确定
        vo.setMainbiztype_number("JTZB001");//报账业务类型.编码
        vo.setDept_number(bizInfo.getOrgCode());
        vo.setCico_sourcebillno(String.join("-", contractBaseInfoLib.getContractCode(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_MS_PATTERN))));//
        //todo

        BigDecimal taxrate;
        //税率
        if (ProjectBizType.ZL.name().equals(bizInfo.getBizType()) && LeaseType.zhi_zu.name().equals(bizInfo.getLeaseType())) {
            taxrate = BigDecimal.valueOf(0.13);
        } else {
            taxrate = BigDecimal.valueOf(0.06);
        }
        List<CQ2AccountApplicationVO.CQ2AccountApplicationVOBody> bodys = new ArrayList<>();
        //是否合同起租
        boolean isStartRent = ProcessModelTypeEnum.ContractStartRentFlow.equals(processModelTypeEnum) || ProcessModelTypeEnum.ContractStartRentAutoFlow.equals(processModelTypeEnum);
        bodys.add(getBody(vo, contractBaseInfoLib, collectionList, cqReceiveRentREQS, taxrate, bizInfo, true,isStartRent));
        bodys.add(getBody(vo, contractBaseInfoLib, collectionList, cqReceiveRentREQS, taxrate, bizInfo, false,isStartRent));
        vo.setTallyentryentity(bodys);
        //项目端付款申请
        vo.setSource(ExceptionSourceENUM.ASSET_SIDE_CONTRACT.name());
        vo.setBusinessKey(String.valueOf(contractBaseInfoLib.getOriginId()));
        vo.setBusinessTitle(contractBaseInfoLib.getContractCode());
        getBean(FinancialManagerServiceImpl2.class).cq2AccountApplicationExec(vo);
    }

    /**
     * 参数凭借
     * @param isPrincipal 是否本金
     * @param isStartRent  是否合同起租
     */
    private CQ2AccountApplicationVO.CQ2AccountApplicationVOBody getBody(CQ2AccountApplicationVO vo,
                                                                        ContractBaseInfoLib contractBaseInfoLib,
                                                                        List<CollectionBaseInfo> collectionList,
                                                                        List<CQReceiveRentREQ> cqReceiveRentREQS,
                                                                        BigDecimal taxrate,
                                                                        SyncCqReqBizInfo bizInfo,
                                                                        boolean isPrincipal,
                                                                        boolean isStartRent) {
        CQ2AccountApplicationVO.CQ2AccountApplicationVOBody body = vo.new CQ2AccountApplicationVOBody();
        LocalDate now = LocalDate.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        body.setBusinessdate(nowFormat);
        if (CQAccountApplicationTypeENUM.PROJECT_LEASE_START.getDisplay().equals(vo.getDescription())) {
            List<CQReceiveRentREQ.ReceiveRentBody> rentList = cqReceiveRentREQS.stream().map(CQReceiveRentREQ::getEntry).flatMap(Collection::stream).collect(Collectors.toList());
            //记账期间 起租日的年月
            LocalDate actualLeaseDate = contractBaseInfoLib.getActualLeaseDate();
            if (ObjectUtil.isNotEmpty(actualLeaseDate)) {
                vo.setCico_period_number(actualLeaseDate.format(DateTimeFormatter.ofPattern(DatePattern.SIMPLE_MONTH_PATTERN)));
            }
            //项目起租：租金总额
            if (isPrincipal) {
                body.setTallyexplanation("本金");
                body.setCico_amount(rentList.stream().map(CQReceiveRentREQ.ReceiveRentBody::getPrincipal).reduce(BigDecimal.ZERO, BigDecimal::add));
                //不含税金额 本金+ 利息/（1+税率）
                body.setCico_bhsje(body.getCico_amount());
                body.setCico_se(BigDecimal.ZERO);//不含税金额*税率
            } else {
                body.setTallyexplanation("利息");
                BigDecimal newAmount = BigDecimal.ZERO;
                BigDecimal newBhsje = BigDecimal.ZERO;
                        //不含税金额 本金+ 利息/（1+税率）
                BigDecimal oldAmount = rentList.stream().map(CQReceiveRentREQ.ReceiveRentBody::getInterest).reduce(BigDecimal.ZERO, BigDecimal::add);
                newAmount = newAmount.add(oldAmount);
                //原不含税
                BigDecimal oldBhsje =rentList.stream().map(e -> {
                    BigDecimal interest = e.getInterest();
                    return interest.divide(BigDecimal.valueOf(1).add(taxrate), 10, RoundingMode.HALF_UP);
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
                newBhsje = newBhsje.add(oldBhsje);
                //XMX-70 合同起租增加首期利息推送
                if(isStartRent){
                    //首期利息 本金为0 所以直接用利息算就可以了
                    BigDecimal firstInterest = collectionList.stream()
                            .filter(Objects::nonNull)
                            .filter(col -> Objects.equals(0, col.getPhase()))
                            .filter(col -> Objects.equals(CashFlowItemEnum.RENT.name(), col.getCashFlowItem()))
                            .map(col -> {
                                BigDecimal interest = Objects.isNull(col.getInterest()) ? BigDecimal.ZERO : BigDecimal.valueOf(col.getInterest());
                                return interest.divide(new BigDecimal(GlobalConstants.MONEY_MULTIPLE), 10, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add);
                    newAmount = newAmount.add(firstInterest);
                    BigDecimal firstBhsje = firstInterest.divide(BigDecimal.valueOf(1).add(taxrate), 10, RoundingMode.HALF_UP);
                    newBhsje = newBhsje.add(firstBhsje);
                }
                body.setCico_amount(newAmount);
                body.setCico_bhsje(newBhsje);
                body.setCico_se(body.getCico_bhsje().multiply(taxrate).setScale(10, RoundingMode.HALF_UP));//不含税金额*税率
            }
        } else {
            vo.setCico_period_number(now.format(DateTimeFormatter.ofPattern(DatePattern.SIMPLE_MONTH_PATTERN)));
            //项目变更：租金表的每次变更，给租金变更的差额，允许负数
            if (ObjectUtil.isNotEmpty(cqReceiveRentREQS)) {
                BigDecimal cicoAmount = BigDecimal.ZERO;
                BigDecimal bhsje = BigDecimal.ZERO;
                if (isPrincipal) {
                    for (CQReceiveRentREQ request : cqReceiveRentREQS) {
                        List<CQReceiveRentREQ.ReceiveRentBody> entry = request.getEntry();
                        if (ObjectUtil.isNotEmpty(entry)) {
                            for (CQReceiveRentREQ.ReceiveRentBody rentBody : entry) {
                                if (ObjectUtil.isNotEmpty(rentBody) && ObjectUtil.equals(YesOrNoNumberEnum.NO.getCode(), rentBody.getBilling()) && rentBody.getPrincipaldifference() != null) {
                                    cicoAmount = cicoAmount.add(rentBody.getPrincipaldifference());
                                    if (ObjectUtil.isNotEmpty(rentBody.getPrincipaldifference())) {
                                        bhsje = bhsje.add(rentBody.getPrincipaldifference());
                                    }
                                }
                            }
                        }
                    }
                    body.setTallyexplanation("本金");
                    body.setCico_se(BigDecimal.ZERO);
                    body.setCico_bhsje(bhsje);
                } else {
                    body.setTallyexplanation("利息");
                    for (CQReceiveRentREQ request : cqReceiveRentREQS) {
                        List<CQReceiveRentREQ.ReceiveRentBody> entry = request.getEntry();
                        if (ObjectUtil.isNotEmpty(entry)) {
                            for (CQReceiveRentREQ.ReceiveRentBody rentBody : entry) {
                                if (ObjectUtil.equals(YesOrNoNumberEnum.NO.getCode(), rentBody.getBilling()) && rentBody.getInterestdifference() != null) {
                                    cicoAmount = cicoAmount.add(rentBody.getInterestdifference());
                                    if (ObjectUtil.isNotEmpty(rentBody.getInterestdifference())) {
                                        bhsje = bhsje.add(rentBody.getInterestdifference().divide(BigDecimal.valueOf(1).add(taxrate), 10, RoundingMode.HALF_UP));
                                    }
                                }
                            }
                        }
                    }
                    //不含税金额 本金+ 利息/（1+税率）
                    body.setCico_bhsje(bhsje);
                    body.setCico_se(body.getCico_bhsje().multiply(taxrate).setScale(10, RoundingMode.HALF_UP));//不含税金额*税率
                }
                body.setCico_amount(cicoAmount);
            }
        }
        body.setCico_contract_num(contractBaseInfoLib.getContractCode());
        body.setCico_custname(bizInfo.getCustomer());
        body.setCico_hsje(body.getCico_amount());
        body.setTallyamount(body.getCico_amount());
        body.setCico_financialins(bizInfo.getCustomer());
        body.setTallydeptid_number(bizInfo.getOrgCode());
        body.setCustomer_number(bizInfo.getCustomer());
        //body.setCico_project2_number(contractBaseInfoLib.getContractCode());
        body.setCico_project2_name(contractBaseInfoLib.getContractCode());
        body.setCico_sl_number(CQTaxRateENUM.getCqBusinessType(taxrate).getCode());
        //body.setCico_entryproject(contractBaseInfoLib.getContractCode());
        //业务类型
        body.setCico_ywlxtyoe_number(Optional.ofNullable(CQBusinessTypeENUM.getCqBusinessType(ProjectBizType.of(bizInfo.getBizType()), LeaseType.of(bizInfo.getLeaseType()))).map(CQBusinessTypeENUM::getCode).orElse(null));
        return body;
    }

    private SyncCqReqBody addZeroRent(Long receiptId, String receiptCode, List<ContractRentActual> rentActualIds, LocalDate startRentDate) {
        if (ObjectUtil.isEmpty(rentActualIds)) {
            return null;
        }
        List<Long> paymentIds = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .eq(PaymentBaseInfo::getReceiptIdFinal, receiptId)).stream().map(PaymentBaseInfo::getId).collect(Collectors.toList());
        /*PaymentActualDetail paymentActualDetail = paymentActualDetailService.getOne(Wrappers.<PaymentActualDetail>lambdaQuery()
                .in(PaymentActualDetail::getPaymentId, paymentIds)
                .orderByAsc(PaymentActualDetail::getPaidInDate)
                .last(StringUtil.mysqlLimitOne()));*/
        Map<String, Long> longLongMap = collectionService.listContractAmountByRentActualIds(rentActualIds.stream().map(ContractRentActual::getId).collect(Collectors.toList()), false, false);
        SyncCqReqBody syncCqReqBody = new SyncCqReqBody();
        //syncCqReqBody.setDate(ObjectUtil.isNotEmpty(paymentActualDetail) ? paymentActualDetail.getPaidInDate() : LocalDate.now());
        //先使用实际起租日
        syncCqReqBody.setDate(ObjectUtil.isNotEmpty(startRentDate) ? startRentDate : LocalDate.now());
        syncCqReqBody.setPhase(0);
        syncCqReqBody.setRent(0L);
        syncCqReqBody.setCode(String.format("%s-%s-000", receiptCode, receiptId));
        syncCqReqBody.setPrincipal(0L);
        syncCqReqBody.setInterest(0L);
        syncCqReqBody.setRemainingPrincipal(LongUtil.null2zero(longLongMap.get(receiptCode)));
        syncCqReqBody.setInterest(0L);
        return syncCqReqBody;
    }

    private SyncCqReqBody buildSyncCqReqBody(CollectionBaseInfo collectionBaseInfo) {
        return new SyncCqReqBody()
                .setDate(collectionBaseInfo.getPlanCollectionDate())
                .setPhase(collectionBaseInfo.getPhase())
                .setRent(collectionBaseInfo.getPlanCollectionAmount())
                .setPrincipal(collectionBaseInfo.getPrincipal())
                .setInterest(collectionBaseInfo.getInterest())
                .setRemainingPrincipal(collectionBaseInfo.getReceiptRemainingPrincipal())
                .setCode(collectionBaseInfo.getCode());
    }

    public SyncCqReqBizInfo getBizInfo(Long contractId) {
        SyncCqReqBizInfo bizInfo = new SyncCqReqBizInfo();
        ContractBaseInfoDetailRSP contractBaseInfo = getBean(ContractBaseInfoService.class).detail(new ContractBaseInfoDetailREQ(contractId));
        //报价方案
        ContractPriceDetailRSP priceDetailRSP = getBean(ContractPriceService.class).detail(new ContractPriceDetailREQ(contractBaseInfo.getId()));
        OrgDO orgDO = getBean(OrgDOMapper.class).selectByPrimaryKey(contractBaseInfo.getBizDeptId());
        bizInfo.setOrgCode(ObjectUtil.isNull(orgDO) ? null : String.valueOf(orgDO.getMainOrgId()));
        ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getId());
        //客户查询客户编号
        Client client = getBean(ClientService.class).getById(main.getLesseeId());
        if (ObjectUtil.isNull(client)) {
            log.error("{} 未查询到主办信息, client {}", contractId, main);
        }
        bizInfo.setCustomer(ObjectUtil.isNull(client) ? null : client.getClientCode());
        bizInfo.setProjSponsorUserPhone(userService.getRealPhone(contractBaseInfo.getProjSponsorUserId()));
        bizInfo.setContractCode(contractBaseInfo.getContractCode());
        bizInfo.setRate(NumberUtil.div(LongUtil.tenThousand2Dollar(NumberUtil.add(priceDetailRSP.getLprPercent(),
                priceDetailRSP.getLprAddPercent()).toString()).toString(), "100", 10, RoundingMode.HALF_UP));
        bizInfo.setBizType(contractBaseInfo.getBizType());
        bizInfo.setLeaseType(contractBaseInfo.getLeaseType());
        bizInfo.setInterestWay(contractBaseInfo.getIncomeConfirmType());
        bizInfo.setCustomerName(client.getClientName());
        return bizInfo;
    }


    /**
     * 新增收款明细
     * @param contractBaseInfoLib 合同
     * @param receipt             借据
     * @param rentActual          租金表记录
     * @param cashFlowItem        现金流类型
     * @return CollectionBaseInfo
     */
    public CollectionBaseInfo newCollectionFromRentActual(ContractBaseInfoLib contractBaseInfoLib,
                                                          ContractReceipt receipt,
                                                          ContractRentActual rentActual,
                                                          CashFlowItemEnum cashFlowItem) {
        CollectionBaseInfo collectionBaseInfo = new CollectionBaseInfo();
        collectionBaseInfo.setRentActualId(rentActual.getId());
        collectionBaseInfo.setClientId(contractBaseInfoLib.getClientId());
        collectionBaseInfo.setContractId(contractBaseInfoLib.getOriginId());
        collectionBaseInfo.setPhase(rentActual.getCashFlowPhase());
        collectionBaseInfo.setContractCode(contractBaseInfoLib.getContractCode());
        collectionBaseInfo.setPrincipal(rentActual.getPrincipal());
        collectionBaseInfo.setInterest(rentActual.getInterest());
        collectionBaseInfo.setPlanCollectionAmount(rentActual.getRent());
        collectionBaseInfo.setPlanCollectionDate(rentActual.getCashFlowDate());
        collectionBaseInfo.setCashFlowItem(cashFlowItem.name());
        collectionBaseInfo.setCashFlowAmount(rentActual.getRent());
        collectionBaseInfo.setReceiptId(rentActual.getReceiptId());
        collectionBaseInfo.setReceiptCode(receipt.getReceiptCode());
        collectionBaseInfo.setReceiptId(receipt.getId());
        collectionBaseInfo.setReceiptRemainingPrincipal(rentActual.getRemainingPrincipal());

        //set default
        collectionBaseInfo.setWriteOffStatus(UNCOLLECTION.name());
        collectionBaseInfo.setAllRecordSort(0);
        collectionBaseInfo.setPenaltyInterestUpdate(0);
        //收款编号设置
        collectionBaseInfo.setCode(
                getBean(CollectionBaseInfoService.class)
                        .getCode(cashFlowItem.name(), collectionBaseInfo.getReceiptCode(), rentActual.getCashFlowPhase(), contractBaseInfoLib.getContractCode())
        );
        return collectionBaseInfo;

    }

    //将合同实际收款表中copy到 收款明细表中
    public CollectionBaseInfo extractChangedInfo(ContractRentActual rentActual, CollectionBaseInfo baseInfo) {
        CollectionBaseInfo toUpdate = BeanUtil.copyProperties(baseInfo, CollectionBaseInfo.class);
        toUpdate.setPlanCollectionDate(rentActual.getCashFlowDate());
        toUpdate.setPrincipal(rentActual.getPrincipal());
        toUpdate.setInterest(rentActual.getInterest());
        toUpdate.setContractId(rentActual.getContractId());
        toUpdate.setContractCode(baseInfo.getContractCode());
        toUpdate.setPlanCollectionAmount(rentActual.getRent());
        toUpdate.setCashFlowAmount(rentActual.getRent());
        toUpdate.setReceiptRemainingPrincipal(rentActual.getRemainingPrincipal());
        toUpdate.setId(baseInfo.getId());
        toUpdate.setPhase(rentActual.getCashFlowPhase());
        toUpdate.setReceiptId(baseInfo.getReceiptId());
        toUpdate.setReceiptCode(baseInfo.getReceiptCode());
        return toUpdate;
    }

    /**
     * 判断实际租金表+收款明细表 还款日期、期限、租金、利息、本金 是否不一致
     *
     */
    public boolean hasChanged(ContractRentActual rentActual, CollectionBaseInfo baseInfo) {
        return notEqual(rentActual.getCashFlowDate(), baseInfo.getPlanCollectionDate()) ||
                notEqual(rentActual.getCashFlowPhase(), baseInfo.getPhase()) ||
                notEqual(rentActual.getRent(), baseInfo.getCashFlowAmount()) ||
                notEqual(rentActual.getPrincipal(), baseInfo.getPrincipal()) ||
                notEqual(rentActual.getInterest(), baseInfo.getInterest()) ||
                notEqual(rentActual.getRent(), baseInfo.getPlanCollectionAmount());
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void onApplicationEvent(CollectionAddEvent event) {
        log.info("CollectionAddEventListener.onApplicationEvent---Start! event:{}", JSONObject.toJSON(event));
        ContractBaseInfoLib contractBaseInfo = baseInfoLibHandler.queryLatestDataByOriginId(event.getContractId());
        List<CollectionBaseInfoAddREQ> addReqs = new LinkedList<>();
        switch (event.getCashFlowItem()) {
            case RENT: {
                //获取代理对象调用
                getBean(CollectionAddEventListener.class).rentChange(event.getContractId(), event.getProcessModelTypeEnum());
/*
                List<ContractRentActual> rentActuals = contractRentActualMapper.selectList(Wrappers.<ContractRentActual>lambdaQuery()
                        .eq(ContractRentActual::getContractId, event.getContractId())
                        .isNotNull(ContractRentActual::getCashFlowCode));
                if (CollectionUtil.isEmpty(rentActuals)){
                    return;
                }
                List<CollectionBaseInfo> needCQs = new ArrayList<>();
                Map<Long, List<ContractRentActual>> idRentActualMap = rentActuals.stream().collect(Collectors.groupingBy(ContractRentActual::getId));
                List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, event.getContractId())
                        .eq(CollectionBaseInfo::getCashFlowItem,CashFlowItemEnum.RENT));
                Set<Long> notAddids = new HashSet<>();
                if (CollectionUtil.isNotEmpty(collectionBaseInfos)){
                    List<Long> ids = new ArrayList<>();
                    List<CollectionBaseInfo> needUpdate = new ArrayList<>();
                    for (CollectionBaseInfo info :collectionBaseInfos) {
                        if (CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name().equals(info.getWriteOffStatus()) || CollectionWriteOffStatusEnum.PORTION_WRITTEN_OFF.name().equals(info.getWriteOffStatus())){
                            notAddids.add(info.getRentActualId());
                            continue;
                        }
                        List<ContractRentActual> contractRentActuals = idRentActualMap.get(info.getRentActualId());
                        if(CollectionUtil.isNotEmpty(contractRentActuals) && (CollectionWriteOffStatusEnum.UNCOLLECTION.name().equals(info.getWriteOffStatus()) || CollectionWriteOffStatusEnum.TO_BE_WRITE_OFF.name().equals(info.getWriteOffStatus()))){
                            ContractRentActual contractRentActual = contractRentActuals.get(0);
                            if (info.getPlanCollectionDate().equals(contractRentActual.getCashFlowDate()) || info.getPlanCollectionDate().isAfter(contractRentActual.getCashFlowDate())){
                                info.setPlanCollectionDate(contractRentActual.getCashFlowDate());
                                info.setPrincipal(contractRentActual.getPrincipal());
                                info.setInterest(contractRentActual.getInterest());
                                info.setPlanCollectionAmount(contractRentActual.getRent());
                                info.setCashFlowAmount(contractRentActual.getRent());
                                needUpdate.add(info);
                                notAddids.add(info.getRentActualId());
                                continue;
                            }
                        }
                        ids.add(info.getId());
                    }
                    if (CollectionUtil.isNotEmpty(ids)) {
                        collectionBaseInfoMapper.deleteBatchIds(ids);
                    }
                    if (CollectionUtil.isNotEmpty(needUpdate)) {
                        collectionBaseInfoService.updateBatchById(needUpdate);
                        needCQs.addAll(needUpdate);
                    }
                }
                ContractReceipt receipt = null;
                rentActuals.sort(Comparator.comparing(ContractRentActual::getReceiptId));
                ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getOriginId());
                for (ContractRentActual o : rentActuals) {
                    if (!notAddids.contains(o.getId())) {
                        if (receipt == null || !receipt.getId().equals(o.getId())) {
                            receipt = contractReceiptMapper.selectById(o.getReceiptId());
                            if (StrUtil.isEmpty(receipt.getPaymentApplyCode())){
                                continue;
                            }
                        }
                        CollectionBaseInfoAddREQ req = new CollectionBaseInfoAddREQ();
                        req.setClientId(main.getLesseeId());
                        req.setContractId(contractBaseInfo.getOriginId());
                        if (paymentBaseInfo != null) {
                            req.setPaymentId(paymentBaseInfo.getId());
                            req.setPaymentCode(paymentBaseInfo.getPaymentCode());
                        }
                        req.setReceiptId(receipt.getId());
                        req.setReceiptCode(receipt.getReceiptCode());
                        req.setPhase(o.getCashFlowPhase());
                        req.setContractCode(contractBaseInfo.getContractCode());
                        req.setPrincipal(o.getPrincipal());
                        req.setInterest(o.getInterest());
                        req.setPlanCollectionAmount(o.getRent());
                        req.setPlanCollectionDate(o.getCashFlowDate());
                        req.setCashFlowItem(event.getCashFlowItem().name());
                        req.setCashFlowAmount(o.getRent());
                       // req.setRentActualId(o.getId());
                        addReqs.add(req);
                    }
                }
                if (CollectionUtil.isNotEmpty(addReqs)) {
                    //租金带入具体变更类型
                    collectionBaseInfoService.add(addReqs, event.getProcessModelTypeEnum());
                }else {
                    if(ObjectUtil.isNotEmpty(needCQs)){
                        //同步苍穹应收单
                        financialManagerService.cqReceiveExec(needCQs, event.getProcessModelTypeEnum());
                    }
                }*/
                break;
            }
            case OTHERAMOUNT:
            case NOMINAL_PRICE:
            case EARLY_STOP_COMPENSATION:
            case EARNEST_MONEY:
            case RETENTION_MONEY:
            case FIRST_RENT: {
                if (event.getSource().equals(ContractProcessStatusEnum.SETTLE_PASS.name())) {
                    marginBaseInfoMapper.updateStatus(event.getContractId());
                }
                if (event.getAmount() > 0) {
                    CollectionBaseInfoAddREQ req = new CollectionBaseInfoAddREQ();
                    ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getOriginId());
                    req.setClientId(main.getLesseeId());
                    req.setContractId(contractBaseInfo.getOriginId());
                    req.setPhase(event.getPhase());
                    req.setContractCode(contractBaseInfo.getContractCode());
                    req.setPlanCollectionAmount(event.getAmount());
                    req.setPlanCollectionDate(event.getPlanCollectionDate());
                    req.setCashFlowItem(event.getCashFlowItem().name());
                    if (event.getCashFlowItem() != CashFlowItemEnum.NOMINAL_PRICE && event.getCashFlowItem() != CashFlowItemEnum.EARLY_STOP_COMPENSATION) {
                        req.setPaymentCode((String) event.getSource());
                        PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                                .eq(PaymentBaseInfo::getContractId, event.getContractId())
                                .eq(PaymentBaseInfo::getPaymentCode, req.getPaymentCode())
                                .last(StringUtil.mysqlLimitOne()));
                        if (ObjectUtil.isNotEmpty(paymentBaseInfo)) {
                            req.setPaymentId(paymentBaseInfo.getId());
                        }
                    }
                    req.setCashFlowAmount(event.getAmount());
                    addReqs.add(req);
                    collectionBaseInfoService.add(addReqs);
                    //同步苍穹
                    financialManagerServiceImpl2.receiveExec(getBizInfo(event.getContractId()),
                            addReqs.stream().map(this::collection2FinancialVo).collect(Collectors.toList()));
                    if (event.getCashFlowItem() == CashFlowItemEnum.EARNEST_MONEY) {
                        MarginBaseInfo info = marginBaseInfoMapper.selectOne(Wrappers.<MarginBaseInfo>lambdaQuery()
                                .eq(MarginBaseInfo::getContractId, event.getContractId()));
                        //应收金额之和
                        List<CollectionBaseInfo> marginCollectionList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                                .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getOriginId())
                                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name()));
                        Long totalReceivableAmount = 0L;
                        if (ObjectUtil.isNotEmpty(marginCollectionList)) {
                            totalReceivableAmount = marginCollectionList.stream().map(CollectionBaseInfo::getPlanCollectionAmount).reduce(Long::sum).orElse(0L);
                        }
                        if (info == null) {
                            MarginBaseInfoAddREQ marginBaseInfoAddREQ = new MarginBaseInfoAddREQ();
                            marginBaseInfoAddREQ.setContractId(contractBaseInfo.getOriginId());
                            marginBaseInfoAddREQ.setContractCode(contractBaseInfo.getContractCode());
                            marginBaseInfoAddREQ.setClientId(main.getLesseeId());
                            //区分计划和实际
                            if(ObjectUtil.isEmpty(event.getHandleType()) || !"MARGIN_RECYCLE".equals(event.getHandleType())) {
                                marginBaseInfoAddREQ.setPlanMarginAmount(event.getAmount());
                            }
                            marginBaseInfoAddREQ.setTotalReceivableAmount(totalReceivableAmount);
                            marginBaseInfoAddREQ.setPlanMarginDate(contractBaseInfo.getPaymentPlanDate());
                            marginBaseInfoService.add(marginBaseInfoAddREQ);
                        } else {
                            //区分计划和实际
                            if(ObjectUtil.isEmpty(event.getHandleType()) || !"MARGIN_RECYCLE".equals(event.getHandleType())) {
                                info.setPlanMarginAmount(info.getPlanMarginAmount() + event.getAmount());
                            }
                            info.setPlanMarginDate(event.getPlanCollectionDate());
                            info.setTotalReceivableAmount(totalReceivableAmount);
                            marginBaseInfoMapper.updateById(info);
                        }
                    } else if (event.getCashFlowItem() == CashFlowItemEnum.RETENTION_MONEY) {  //质保金逻辑参考保证金
                        addWarrantyBaseInfo(event, contractBaseInfo, req);
                    }
                }
                break;
            }
            case COMMISSION: {
                if (event.getAmount() > 0) {
                    CollectionBaseInfoAddREQ req = new CollectionBaseInfoAddREQ();
                    ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getOriginId());
                    req.setClientId(main.getLesseeId());
                    req.setContractId(contractBaseInfo.getOriginId());
                    req.setPhase(0);
                    req.setContractCode(contractBaseInfo.getContractCode());
                    req.setPlanCollectionAmount(event.getAmount());
                    req.setPlanCollectionDate(event.getPlanCollectionDate());
                    req.setCashFlowItem(event.getCashFlowItem().name());
                    req.setPaymentCode((String) event.getSource());
                    PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                            .eq(PaymentBaseInfo::getContractId, event.getContractId())
                            .eq(PaymentBaseInfo::getPaymentCode, req.getPaymentCode())
                            .last(StringUtil.mysqlLimitOne()));
                    req.setPaymentId(paymentBaseInfo.getId());
                    req.setCashFlowAmount(event.getAmount());
                    collectionBaseInfoService.addOtherAmountByCommission(req);
                }
                break;
            }
            case FIRST_INSTALLMENT_INTEREST: {
                if (event.getAmount() > 0) {
                    CollectionBaseInfoAddREQ req = new CollectionBaseInfoAddREQ();
                    ContractTenantry main = contractTenantryService.getMain(contractBaseInfo.getOriginId());
                    req.setClientId(main.getLesseeId());
                    req.setContractId(contractBaseInfo.getOriginId());
                    req.setPhase(0);
                    req.setContractCode(contractBaseInfo.getContractCode());
                    req.setPlanCollectionAmount(event.getAmount());
                    req.setInterest(event.getAmount());
                    req.setPlanCollectionDate(event.getPlanCollectionDate());
                    req.setCashFlowItem(event.getCashFlowItem().name());
                    req.setPaymentCode((String) event.getSource());
                    PaymentBaseInfo paymentBaseInfo = paymentBaseInfoMapper.selectOne(Wrappers.<PaymentBaseInfo>lambdaQuery()
                            .eq(PaymentBaseInfo::getContractId, event.getContractId())
                            .eq(PaymentBaseInfo::getPaymentCode, req.getPaymentCode())
                            .last(StringUtil.mysqlLimitOne()));
                    req.setPaymentId(paymentBaseInfo.getId());
                    req.setCashFlowAmount(event.getAmount());
                    collectionBaseInfoService.addOtherAmountByFirstInstallmentInterest(req);
                }
                break;
            }

            default:
                throw new MithrasException("不支持");
        }

    }

    /**
     * PaymentActualDetailEndHandler 中驱动事件
     * 在付款核销流程中，若为{租赁类型为直租}&{收款确认模块中填写的“厂商质保金（元）”＞0}，在付款核销流程审批通过后，生成质保金收款计划
     * */
    private void addWarrantyBaseInfo(CollectionAddEvent event, ContractBaseInfoLib contractBaseInfo, CollectionBaseInfoAddREQ req) {
        if(!contractBaseInfo.getLeaseType().equals(LeaseType.zhi_zu.name())){
            log.info("租赁类型不是直租，不生成质保金");
            return;
        }
        WarrantyBaseInfo info = warrantyBaseInfoMapper.selectOne(Wrappers.<WarrantyBaseInfo>lambdaQuery()
                .eq(WarrantyBaseInfo::getContractId, event.getContractId()));
        //应收金额之和
        List<CollectionBaseInfo> warrantyCollectionList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getOriginId())
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RETENTION_MONEY.name()));
        Long totalReceivableAmount = 0L;
        if (ObjectUtil.isNotEmpty(warrantyCollectionList)) {
            totalReceivableAmount = warrantyCollectionList.stream().map(CollectionBaseInfo::getPlanCollectionAmount).reduce(Long::sum).orElse(0L);
        }
        PaymentCollectionInfo paymentCollectionInfo = paymentCollectionInfoMapper.selectOne(Wrappers.<PaymentCollectionInfo>lambdaQuery()
                .eq(PaymentCollectionInfo::getPaymentId, req.getPaymentId())
                .last(StringUtil.mysqlLimitOne()));
        if (info == null) {
            info = new WarrantyBaseInfo();
            info.setContractId(contractBaseInfo.getOriginId());
            info.setContractCode(contractBaseInfo.getContractCode());
            info.setClientId(req.getClientId());
            //区分计划和实际
            if(ObjectUtil.isEmpty(event.getHandleType()) || !"MARGIN_RECYCLE".equals(event.getHandleType())) {
                info.setPlanWarrantyAmount(event.getAmount());
            }
            info.setTotalReceivableAmount(totalReceivableAmount);
            info.setPlanWarrantyDate(paymentCollectionInfo.getWarrantyReturnDate()); // 收款确认中的 厂商质保金退还日期
            info.setWarrantyCode(getCode(contractBaseInfo.getContractCode()));
            info.setCollectionAmount(0L);
            warrantyBaseInfoMapper.insert(info);
        } else {
            //区分计划和实际
            if(ObjectUtil.isEmpty(event.getHandleType()) || !"MARGIN_RECYCLE".equals(event.getHandleType())) {
                info.setPlanWarrantyAmount(info.getPlanWarrantyAmount() + event.getAmount());
            }
            info.setPlanWarrantyDate(paymentCollectionInfo.getWarrantyReturnDate());
            info.setTotalReceivableAmount(totalReceivableAmount);
            warrantyBaseInfoMapper.updateById(info);
        }
    }

    private String getCode(String contractCode) {
        String year = contractCode.substring(contractCode.indexOf("【") + 1, contractCode.indexOf("】"));
        String code = contractCode.substring(contractCode.indexOf("(") + 1, contractCode.indexOf(")"));
        return year + code.substring(0, code.indexOf("-")) + code.substring(code.indexOf("-") + 1) + "-zbj";
    }


    private FinancialCollectionVO collection2FinancialVo(CollectionBaseInfoAddREQ addREQ) {
        FinancialCollectionVO financialCollectionVO = new FinancialCollectionVO();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
        financialCollectionVO.setContractCode(addREQ.getContractCode());
        financialCollectionVO.setCode(addREQ.getCollectionCode());
        financialCollectionVO.setPlanCollectionAmount(addREQ.getPlanCollectionAmount());
        if (CashFlowItemEnum.EARNEST_MONEY.name().equals(addREQ.getCashFlowItem()) || CashFlowItemEnum.RETENTION_MONEY.name().equals(addREQ.getCashFlowItem())) {
            //业务确认保证金推负数金额给苍穹，产生应收待付的单子
            financialCollectionVO.setPlanCollectionAmount(Math.negateExact(LongUtil.null2zero(financialCollectionVO.getPlanCollectionAmount())));
            //保证金添加到期日
            LocalDate contractExpirationDate = contractRentActualService.getContractRentExpirationDate(addREQ.getContractId());
            if (ObjectUtil.isNotEmpty(contractExpirationDate)) {
                financialCollectionVO.setMaturityDate(contractExpirationDate.format(dateTimeFormatter));
            }
        }
        financialCollectionVO.setPlanCollectionDate(addREQ.getPlanCollectionDate());
        financialCollectionVO.setCashFlowItem(addREQ.getCashFlowItem());
        return financialCollectionVO;
    }

}
