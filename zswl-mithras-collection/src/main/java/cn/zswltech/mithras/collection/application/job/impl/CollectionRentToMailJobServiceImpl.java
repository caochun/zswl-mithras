package cn.zswltech.mithras.collection.application.job.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.application.job.CollectionMailJobSupportPort;
import cn.zswltech.mithras.collection.application.job.CollectionRentToMailJobService;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfo;
import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
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
public class CollectionRentToMailJobServiceImpl implements CollectionRentToMailJobService {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionMailJobSupportPort supportPort;


    @Override
    public void collectionRent() {
        try {
            log.info("定时任务催收开始");
            /*查询收款明细表：collection_base_info
            条件1：现金流项目为‘租金’:
            条件2：核销状态<>核销完毕
            条件3：当前日大于计划还款日期
             */
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                    .lt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()));
            if (collectionBaseInfoList.isEmpty()) {
                log.info("定时任务催收数量为: 0");
                return;
            }
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
            String version = "";//版本号
            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                String gracePeriod;//宽限期
                receiverMails = new HashSet<>();
                //查询 征信报送-还款计划表 取宽限期，查询条件 合同号和期项
                gracePeriod = supportPort.getGracePeriod(collectionBaseInfo.getContractId(), collectionBaseInfo.getPaymentId(), collectionBaseInfo.getPhase());
                //取计划还款日期(计划还款日+宽限期)
                LocalDate planCollectionDate = collectionBaseInfo.getPlanCollectionDate().plusDays(Integer.parseInt(gracePeriod));
                //计算 当前日期 与 计划还款日期+1 之差
                long diffDays = Math.abs(ChronoUnit.DAYS.between(planCollectionDate.plusDays(1),currentDate));
                //如果是还款日第二天，发送邮件催收；如果 当前日期 与 计划还款日期+1 之差7的整数倍发送邮件催收
                if (planCollectionDate.isBefore(currentDate)&&diffDays%7==0) {
                    //查询合同信息
                    ContractBaseInfo contractBaseInfo = supportPort.getContractById(collectionBaseInfo.getContractId());
                    if (contractBaseInfo == null) {
                        log.info("合同{}不存在", collectionBaseInfo.getContractId());
                        continue;
                    }
                    //合同结清的话，跳过
                    if(ContractStatus.SETTLE.name().equals(contractBaseInfo.getContractStatus())){
                        continue;
                    }
                    contractCode = contractBaseInfo.getContractCode();
                    //获取主办人ID
                    manageUser = contractBaseInfo.getProjSponsorUserId();
                    if(manageUser==null){
                        log.info("合同【{}】没有主办人", contractCode);
                        continue;
                    }
                    //查询所有资产管理岗的用户
                    Set<Long> userIDs = supportPort.getUserIdsByRole("ZCGL");
                    if(userIDs.isEmpty()){
                        //获取主办人邮箱
                        carbonCopyMails = supportPort.getUserEmailSet(Collections.singleton(manageUser));
                    }else {
                        userIDs.add(manageUser);
                        carbonCopyMails = supportPort.getUserEmailSet(userIDs);
                    }
                    //获取客户编号
                    clientId = contractBaseInfo.getClientId();
                    Client client = supportPort.getClientById(clientId);
                    if(client == null){
                        log.info("客户{}不存在", clientId);
                        continue;
                    }
                    clientName = client.getClientName();
                    //根据客户号查询联系人
                    corpContactInfoList = supportPort.listCorpContactInfo(clientId);//查询联系人
                    if(corpContactInfoList.isEmpty()){
                        log.info("合同【{}】下客户没有联系人", contractCode);
                        continue;
                    }

                    //获得联系人邮箱
                    receiverMail = corpContactInfoList.get(0).getMail();
                    receiverMails.add(receiverMail);

                    //根据合同号查询承租人信息
                    //取最新合同版本号
                    version = supportPort.getLatestContractVersion(contractBaseInfo.getId());
                    if (ObjectUtil.isNull(version)) {
                        log.info("合同【{}】没有版本数据", contractCode);
                        continue;
                    }
                    String tenantryNames = supportPort.getReportedLesseeNames(contractBaseInfo.getId(), version);
                    if(tenantryNames == null){
                        log.info("合同【{}】没有主承租人", contractCode);
                        continue;
                    }
                    //查询的数据放入实体类中
                    rentCollectionBaseInfo = new RentCollectionBaseInfo();
                    rentCollectionBaseInfo.setLesseeName(tenantryNames);
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
                    supportPort.sendCollectionRentEmail(receiverMails,carbonCopyMails,rentCollectionBaseInfo);
                    sendSum++;
                }
            }
            log.info("定时任务收租提醒结束了！待核销数量为：{}", sendSum);
        } catch (Exception e) {
            log.error("定时任务收租提醒出错了，错误信息：%s", e);
        }
    }
}
