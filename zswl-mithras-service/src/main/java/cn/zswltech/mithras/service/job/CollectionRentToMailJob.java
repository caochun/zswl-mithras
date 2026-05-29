package cn.zswltech.mithras.service.job;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.creditreport.model.CrRepayPlan;
import cn.zswltech.mithras.creditreport.service.CreditRepayPlanService;
import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractTenantryLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpContactInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantryLib;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpContactInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.email.CollectionRentEmailHandler;
import cn.zswltech.mithras.common.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ylzhang5
 * @date 2025/12/01
 * @description 定时扫描租金是否按时还款
 */
@Slf4j
@Component
public class CollectionRentToMailJob {

    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private CorpContactInfoService corpContactInfoService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private UserService userServiceAPI;
    @Resource
    private CollectionRentEmailHandler collectionRentEmailHandler;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientService clientService;
    @Autowired
    private ContractTenantryLibMapper contractTenantryLibMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;


    @XxlJob(value = "collectionRentToMailJob")
    public void collectionRent() {
        try {
            log.info("定时任务催收开始");
            /*查询收款明细表：collection_base_info
            条件1：现金流项目为‘租金’:
            条件2：核销状态<>核销完毕
            条件3：当前日大于计划还款日期
             */
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED)
                    .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()));
            if (collectionBaseInfoList.isEmpty()) {
                log.info("定时任务催收数量为: 0");
                return;
            }
            List<Long> contractIds;
            LocalDate currentDate = LocalDate.now();
            Set<String> receiverMails = null;//收件人邮箱
            Set<String> carbonCopyMails = null;//抄送人邮箱
            String receiverMail = "";//收件人邮箱
            Long clientId = null;//客户号
            String clientName = "";//客户名称
            Long manageUser;//主办人ID
            String contractCode = "";//合同编号
            RentCollectionBaseInfo rentCollectionBaseInfo;
            List<CorpContactInfo> corpContactInfoList = null;//联系人
            Integer sendSum = 0;
            String contractStatus = "";//合同状态
            String version = "";//版本号
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                String gracePeriod = "0";//宽限期
                receiverMails = new HashSet<>();
                //查询 征信报送-还款计划表 取宽限期，查询条件 合同号和期项
                CreditRepayPlanService repayPlanService = SpringContextHolder.getBean(CreditRepayPlanService.class);
                CrRepayPlan creditRepayPlan = repayPlanService.getOne(Wrappers.<CrRepayPlan>lambdaQuery()
                        .eq(CrRepayPlan::getContractId,collectionBaseInfo.getContractId())
                        .eq(CrRepayPlan::getPaymentId,collectionBaseInfo.getPaymentId())
                        .eq(CrRepayPlan::getPhase,collectionBaseInfo.getPhase()));
                if(creditRepayPlan!=null && !creditRepayPlan.getGracePeriod().isEmpty()){
                    gracePeriod = creditRepayPlan.getGracePeriod();
                }
                //取计划还款日期(计划还款日+宽限期)
                LocalDate planCollectionDate = collectionBaseInfo.getPlanCollectionDate().plusDays(Integer.parseInt(gracePeriod));
                //计算 当前日期 与 计划还款日期+1 之差
                long diffDays = Math.abs(ChronoUnit.DAYS.between(planCollectionDate.plusDays(1),currentDate));
                //如果是还款日第二天，发送邮件催收；如果 当前日期 与 计划还款日期+1 之差7的整数倍发送邮件催收
                if (planCollectionDate.isBefore(currentDate)&&diffDays%7==0) {
                    //查询合同信息
                    ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(collectionBaseInfo.getContractId());
                    contractStatus = contractBaseInfo.getContractStatus();
                    //合同结清的话，跳过
                    if(ContractStatus.SETTLE.name().equals(contractStatus)){
                        continue;
                    }
                    contractIds = new ArrayList<>();
                    contractIds.add(contractBaseInfo.getId());
                    contractCode = contractBaseInfo.getContractCode();
                    //获取主办人ID
                    manageUser = contractBaseInfo.getProjSponsorUserId();
                    if(manageUser==null){
                        log.info("合同【{}】没有主办人", contractCode);
                        continue;
                    }
                    //查询所有资产管理岗的用户
                    Set<Long> userIDs = sysUserService.getUserIdsByRole("ZCGL");
                    if(userIDs.isEmpty()){
                        //获取主办人邮箱
                        carbonCopyMails = getUserEmailSet(Collections.singleton(manageUser));
                    }else {
                        userIDs.add(manageUser);
                        carbonCopyMails = getUserEmailSet(userIDs);
                    }
                    //获取客户编号
                    clientId = contractBaseInfo.getClientId();
                    Client client = clientService.getById(clientId);
                    if(client == null){
                        log.info("客户{}不存在", clientId);
                        continue;
                    }
                    clientName = client.getClientName();
                    //根据客户号查询联系人
                    LambdaQueryWrapper<CorpContactInfo> query = Wrappers.lambdaQuery();
                    query.eq(CorpContactInfo::getClientId, clientId);
                    query.orderByDesc(CorpContactInfo::getMain);
                    query.orderByDesc(CorpContactInfo::getId);
                    corpContactInfoList = corpContactInfoService.list(query);//查询联系人
                    if(corpContactInfoList.isEmpty()){
                        log.info("合同【{}】下客户没有联系人", contractCode);
                        continue;
                    }

                    //获得联系人邮箱
                    receiverMail = corpContactInfoList.get(0).getMail();
                    receiverMails.add(receiverMail);

                    //根据合同号查询承租人信息
                    StringBuilder tenantrys = new StringBuilder();//拼接后的承租人
                    //取最新合同版本号
                    CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                            .eq(CommonVersion::getMainId, contractBaseInfo.getId())
                            .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                            .eq(CommonVersion::getModule, BusinessModuleEnum.CONTRACT.name())
                            .orderByDesc(CommonVersion::getVersion)
                            .last("LIMIT 1"));
                    if (ObjectUtil.isNull(commonVersion)) {
                        log.info("合同【{}】没有版本数据", contractCode);
                        continue;
                    }
                    version = commonVersion.getVersion();
                    //主承租人
                    ContractTenantryLib contractTenantryLib = contractTenantryLibMapper.selectOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                            .eq(ContractTenantryLib::getContractId, contractBaseInfo.getId())
                            .eq(ContractTenantryLib::getVersion, version)
                            .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                            .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
                    );
                    if(contractTenantryLib==null){
                        log.info("合同【{}】没有主承租人", contractCode);
                        continue;
                    }
                    tenantrys.append(contractTenantryLib.getLesseeName());
                    // 联合承租人
                    List<ContractTenantryLib> contractTenantryLibList = new ArrayList<>();
                    contractTenantryLibList = contractTenantryLibMapper.selectList(Wrappers.<ContractTenantryLib>lambdaQuery()
                            .eq(ContractTenantryLib::getContractId, contractBaseInfo.getId())
                            .eq(ContractTenantryLib::getVersion, version)
                            .eq(ContractTenantryLib::getLesseeType, LesseeTypeEnum.JOINT_LESSEE.name())
                            .eq(ContractTenantryLib::getIsReport, YesOrNoNumberEnum.YES.getCode())
                    );
                    //如果有联合承租人，拼接联合承租人名称
                    if(!contractTenantryLibList.isEmpty()){
                        for(ContractTenantryLib unitContractTenantryLib:contractTenantryLibList){
                            //拼接主承租人和联合承租人
                            tenantrys.append("、").append(unitContractTenantryLib.getLesseeName());
                        }

                    }
                    //查询的数据放入实体类中
                    rentCollectionBaseInfo = new RentCollectionBaseInfo();
                    rentCollectionBaseInfo.setLesseeName(tenantrys.toString());
                    rentCollectionBaseInfo.setCollectionId(collectionBaseInfo.getId());
                    rentCollectionBaseInfo.setContractId(collectionBaseInfo.getContractId());
                    rentCollectionBaseInfo.setContractCode(contractCode);
                    rentCollectionBaseInfo.setClientId(clientId);
                    rentCollectionBaseInfo.setClientName(clientName);
                    rentCollectionBaseInfo.setPhase(collectionBaseInfo.getPhase());
                    rentCollectionBaseInfo.setPlanYear(String.valueOf(collectionBaseInfo.getPlanCollectionDate().getYear()));
                    rentCollectionBaseInfo.setPlanMonth(String.valueOf(collectionBaseInfo.getPlanCollectionDate().getMonth().getValue()));
                    rentCollectionBaseInfo.setPlanDay(String.valueOf(collectionBaseInfo.getPlanCollectionDate().getDayOfMonth()));
                    //发送邮件
                    collectionRentEmailHandler.sendEmail(receiverMails,carbonCopyMails,new HashSet<>(),rentCollectionBaseInfo);
                    sendSum++;
                }
            }
            log.info("定时任务收租提醒结束了！待核销数量为：{}", sendSum);
        } catch (Exception e) {
            log.error("定时任务收租提醒出错了，错误信息：%s", e);
        }
    }

    /**
     * 获取用户邮箱
     *
     */
    public Set<String>  getUserEmailSet(Set<Long> idSet){
        // 1. 空值校验：避免空指针
        if (CollectionUtils.isEmpty(idSet)) {
            return new HashSet<>();
        }
        // 2. 调用接口获取用户列表
        List<UserVO> userVOList = userServiceAPI.getUserInfoByIds(new ArrayList<>(idSet));
        if (CollectionUtils.isEmpty(userVOList)) {
            return new HashSet<>();
        }
        Set<String> emailSet = userVOList.stream()
                .filter(userVO -> Objects.equals(0,userVO.getStatus()))
                .filter(userVO -> StringUtils.isNotBlank(userVO.getEmail()))
                .map(UserVO::getEmail)
                .collect(Collectors.toSet());
        return emailSet;
    }
}
