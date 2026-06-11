package cn.zswltech.mithras.collection.application.job.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.collection.application.job.CollectionMailJobSupportPort;
import cn.zswltech.mithras.collection.application.job.RentExpireToMailJobService;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import cn.zswltech.mithras.customer.mapper.model.client.CorpContactInfo;
import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * @author ylzhang5
 * @date 2025/12/01
 * @description 定时扫描租金是即将到期
 */
@Slf4j
@Component
public class RentExpireToMailJobServiceImpl implements RentExpireToMailJobService {
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private CollectionMailJobSupportPort supportPort;


    @Override
    public void collectionRent() {
        try {
            log.info("租金到期提醒定时任务开始");
            /*查询收款明细表：collection_base_info
            条件1：现金流项目为‘租金’:
            条件2：核销状态<>核销完毕
            条件3：当前日小于计划还款日期
             */
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .ne(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name())
                    .gt(CollectionBaseInfo::getPlanCollectionDate, LocalDate.now()));
            if (collectionBaseInfoList.isEmpty()) {
                log.info("租金到期提醒数量为: 0");
                return;
            }
            LocalDate currentDate = LocalDate.now();
            //判断当前日期是否工作日，非工作日不发邮件
            if(!supportPort.isWorkDay(currentDate)){
                return;
            }
            Set<String> receiverMails = null;//收件人
            Set<String> carbonCopyMails = null;//抄送人
            String receiverMail = "";//收件人邮箱
            Long clientId = null;//客户号
            String clientName = "";//客户名称
            Long manageUser;//主办人ID
            String contractCode = "";//合同编号
            RentCollectionBaseInfo rentCollectionBaseInfo;
            List<CorpContactInfo> corpContactInfoList = null;//联系人
            Integer sendSum = 0;
            Long lNominalPrice = null;//名义价款

            for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
                receiverMails = new HashSet<>();
                carbonCopyMails = new HashSet<>();
                //取计划还款日期
                LocalDate planCollectionDate = collectionBaseInfo.getPlanCollectionDate();
                //计算当前日期到计划还款日期相差的工作日天数
                Integer workDays = supportPort.countWorkdayNumber(currentDate,planCollectionDate);
                //提前7天发送提醒邮件(workDays计算的天数差距包含起止日期，故计算的天数少一天，条件中加一天)
                if (workDays==8) {
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
                    //获取主办人邮箱
                    carbonCopyMails = supportPort.getUserEmailSet(Collections.singleton(manageUser));
                    //获取客户编号
                    clientId = contractBaseInfo.getClientId();
                    Client client = supportPort.getClientById(clientId);
                    if(client == null){
                        log.info("客户"+clientId+"不存在");
                        continue;
                    }
                    clientName = client.getClientName();
                    //根据客户号查询联系人
                    corpContactInfoList = supportPort.listCorpContactInfo(clientId);//查询联系人
                    if(corpContactInfoList.isEmpty()){
                        log.info("客户"+contractBaseInfo.getClientId()+"没有联系人");
                        continue;
                    }
                    //获得联系人邮箱
                    receiverMail = corpContactInfoList.get(0).getMail();
                    receiverMails.add(receiverMail);

                    //判断当前是否最后期项，然后赋值名义价款
                    //再根据合同id查所有还款流水中最后的期项
                    Integer lastPhase = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                            .eq(CollectionBaseInfo::getContractId, collectionBaseInfo.getContractId())
                            .orderByDesc(CollectionBaseInfo::getPhase)).get(0).getPhase();

                    //取最新合同版本号
                    String version = supportPort.getLatestContractVersion(collectionBaseInfo.getContractId());
                    if (ObjectUtil.isNull(version)) {
                        log.info("合同"+collectionBaseInfo.getContractId()+"没有版本数据");
                        continue;
                    }
                    //如果是最后期项
                    if(Objects.equals(lastPhase, collectionBaseInfo.getPhase())){
                        //取最新报价方案
                        lNominalPrice = supportPort.getNominalPrice(collectionBaseInfo.getContractId(), version);
                    }

                    rentCollectionBaseInfo = new RentCollectionBaseInfo();
                    rentCollectionBaseInfo.setCollectionId(collectionBaseInfo.getId());
                    rentCollectionBaseInfo.setContractId(collectionBaseInfo.getContractId());
                    rentCollectionBaseInfo.setContractCode(contractCode);
                    rentCollectionBaseInfo.setClientId(clientId);
                    rentCollectionBaseInfo.setClientName(clientName);
                    rentCollectionBaseInfo.setPhase(collectionBaseInfo.getPhase());
                    rentCollectionBaseInfo.setPlanYear(String.valueOf(collectionBaseInfo.getPlanCollectionDate().getYear()));
                    rentCollectionBaseInfo.setPlanMonth(String.valueOf(collectionBaseInfo.getPlanCollectionDate().getMonth().getValue()));
                    rentCollectionBaseInfo.setPlanDay(String.valueOf(collectionBaseInfo.getPlanCollectionDate().getDayOfMonth()));
                    //最后期项，名义价款取最新报价方案中的名义价款，否则展示“/”
                    if(lNominalPrice!=null){
                        rentCollectionBaseInfo.setLNominalPrice(new BigDecimal(lNominalPrice).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toString());
                    }else{
                        rentCollectionBaseInfo.setLNominalPrice("/");
                    }
                    //发送邮件
                    supportPort.sendRentExpireEmail(receiverMails,carbonCopyMails,rentCollectionBaseInfo);
                    sendSum++;
                }
            }
            log.info("定时任务提醒结束了！数量为：{}", sendSum);
        } catch (Exception e) {
            log.error("定时任务提醒出错了，错误信息：%s", e);
        }
    }
}
