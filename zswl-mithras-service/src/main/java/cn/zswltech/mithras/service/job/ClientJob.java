package cn.zswltech.mithras.service.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.share.DataShareRegisterCustomREQ;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.client.ClientLevelEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.service.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.service.mapper.model.client.*;
import cn.zswltech.mithras.service.service.client.ClientAuthorityService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpContactInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.share.DataShareService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 客户相关权限
 * @author: jackerhe
 * @date: 2023/11/7 2:21 下午
 **/
@Slf4j
@Component
public class ClientJob {
    @Resource
    private ClientService clientService;
    @Resource
    private ClientAuthorityService clientAuthorityService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;

    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private CorpAddressInfoMapper corpAddressInfoMapper;
    @Resource
    private DataShareService dataShareService;
    @Resource
    private CorpContactInfoService corpContactInfoService;


    /**
     * 每天定时查询到期的客户
     * 客户释放新增job执行，该job只做到期提醒（保留了老代码）
     */
    @XxlJob("clientAuthTypeModify")
    @Transactional(rollbackFor = Throwable.class)
    public void clientAuthTypeModify() {
        try {
//            //项目经理获得客户管护权后，超过60天仍未有立项审批通过的项目，系统自动释放该客户权限
//            projReviewBaseInfoService.releaseClientProjEstablish(60, null, true);
            //提前30天提醒立项到期的
//            projReviewBaseInfoService.noticeOrModifyClientTypeByReview(60, null, true);
//            //90天修改客户信息ListUtil.toList(899L)
//            projReviewBaseInfoService.noticeOrModifyClientTypeByReview(90, null, false);
            //提前30天提醒评审生效未投放的
//            projReviewBaseInfoService.noticeOrModifyClientTypeByPayment(335, null, true);
//            //365修改客户信息
//            projReviewBaseInfoService.noticeOrModifyClientTypeByPayment(365, null, false);
//            //合同结清审批通过的客户，超过90天后无新的立项审批通过的项目，系统自动释放该客户权限
//            projReviewBaseInfoService.releaseClientContractSettle(90, null, false);
        } catch (Exception e) {
            log.error("客户到期提醒任务执行异常" ,e);
        }
    }

    @XxlJob("releaseClientJob")
    public void releaseClientJob() {
        List<Long> targetClientIds;
        String jobParam = XxlJobHelper.getJobParam();
//        String jobParam = "4625";
        if (StrUtil.isNotBlank(jobParam)) {
            targetClientIds = Collections.singletonList(Long.valueOf(jobParam));
        } else {
            // 查询所有存在管护权的客户
            targetClientIds = this.listHasManagerClientIds();
        }
        if (CollectionUtil.isEmpty(targetClientIds)) {
            return;
        }
        // 遍历判断是否可以释放
        for (Long clientId : targetClientIds) {
            String s = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_MS_PATTERN);
            try {
                Client client = clientService.getById(clientId);
                if (Objects.isNull(client)) {
                    log.error("没有找到{}的客户信息", clientId);
                    continue;
                }
                MDC.put(GlobalConstants.LOG_TRACE_ID, String.format("%s-%s", clientId, s));
                clientService.tryReleaseClient(client);
            } catch (Exception e) {
                log.error("释放客户发生异常[clientId:{}]", clientId, e);
            } finally {
                MDC.remove(GlobalConstants.LOG_TRACE_ID);
            }
        }
    }

    private List<Long> listHasManagerClientIds() {
        LambdaQueryWrapper<ClientAuthority> query = Wrappers.lambdaQuery();
        query.eq(ClientAuthority::getLevel, ClientLevelEnum.MANAGE.getLevel());
        List<ClientAuthority> clientAuthorityList = clientAuthorityService.list(query);
        if (CollectionUtil.isEmpty(clientAuthorityList)) {
            return Collections.emptyList();
        }
        return clientAuthorityList.stream().map(ClientAuthority::getClientId).collect(Collectors.toList());
    }

    @XxlJob("supplementClientCode")
    @Transactional(rollbackFor = Throwable.class)
    public void getClientCode() {
        String clientName;
        clientName = XxlJobHelper.getJobParam();
        if (ObjectUtil.isEmpty(clientName)) {
            log.warn("getClientCode 入参为空");
            return;
        }
        //去集团创建客户
        Client client = clientMapper.selectOne(Wrappers.<Client>lambdaQuery().eq(Client::getClientName, clientName));
        DataShareRegisterCustomREQ dataShareRegisterCustomREQ = null;
        try {
            dataShareRegisterCustomREQ = buildRegisterCustom(client.getId());
            client.setClientCode(dataShareService.registerCustom(dataShareRegisterCustomREQ));
            clientMapper.updateById(client);
        } catch (Exception e) {
            log.error("集团创建客户失败, {}", JSONUtil.toJsonStr(dataShareRegisterCustomREQ), e);
        }
    }

    private DataShareRegisterCustomREQ buildRegisterCustom(Long clientId) {
        DataShareRegisterCustomREQ dataShareRegisterCustomREQ = new DataShareRegisterCustomREQ();
        DataShareRegisterCustomREQ.BaseInfo baseInfo = dataShareRegisterCustomREQ.new BaseInfo();
        DataShareRegisterCustomREQ.BusinessInfo businessInfo = dataShareRegisterCustomREQ.new BusinessInfo();
        dataShareRegisterCustomREQ.setBaseInfo(baseInfo);
        dataShareRegisterCustomREQ.setBusinessInfo(businessInfo);
        Client client = clientMapper.selectById(clientId);
        CorpCommerceInfo commerceInfo = commerceInfoMapper.selectOne(Wrappers.<CorpCommerceInfo>lambdaQuery().eq(CorpCommerceInfo::getClientId, clientId).last(StringUtil.mysqlLimitOne()));
        CorpAddressInfo corpAddressInfo = corpAddressInfoMapper.selectOne(Wrappers.<CorpAddressInfo>lambdaQuery().eq(CorpAddressInfo::getClientId, clientId).orderByDesc(CorpAddressInfo::getAddressType).last(StringUtil.mysqlLimitOne()));
        CorpAddressInfo registryAddressInfo = corpAddressInfoMapper.selectOne(Wrappers.<CorpAddressInfo>lambdaQuery()
                .eq(CorpAddressInfo::getClientId, clientId)
                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name())
                .orderByDesc(CorpAddressInfo::getAddressType)
                .last(StringUtil.mysqlLimitOne()));
//        CorpContactInfoListREQ req = new CorpContactInfoListREQ();
//        req.setClientId(clientId);
//        Page<CorpContactInfo> pageContactInfo = corpContactInfoService.list(req);
        Page<CorpContactInfo> pageContactInfo = corpContactInfoService.page(new Page<>(1, 10), Wrappers.<CorpContactInfo>lambdaQuery().eq(ClientBaseModel::getClientId, clientId));
        baseInfo.setMerchantName(client.getClientName());
        if (ClientType.CORPORATION.name().equals(client.getClientType())) {
            baseInfo.setMerchantType("QIYE");
            baseInfo.setCreditCode(client.getUscCode());
        } else {
            baseInfo.setMerchantType("GEREN");
            baseInfo.setIdentificationNumber(client.getCertNumber());
        }
        //填充地址
        if (ObjectUtil.isNotEmpty(corpAddressInfo)) {
            //目前只有中国
            List<String> addresses = new ArrayList<>();
            addresses.add("CN");
            addresses.add(corpAddressInfo.getProvince());
            addresses.add(corpAddressInfo.getCity());
            baseInfo.setAreaCodeList(addresses);
        }
        //工商信息
        businessInfo.setRegCapital(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(commerceInfo.getRegisterCapital()))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        businessInfo.setRegCapitalCurrencyType(commerceInfo.getRegisterCurrencyType());
        businessInfo.setActualCapital(LongUtil.tenThousand2Dollar(String.valueOf(LongUtil.null2zero(commerceInfo.getRealCapital()))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        businessInfo.setActualCapitalCurrencyType(commerceInfo.getRealCurrencyType());
        businessInfo.setRegNumber(client.getCertNumber());
        businessInfo.setLegalPersonName(commerceInfo.getCorpRepresent());
        businessInfo.setEstablishTime(commerceInfo.getEstablishDate());
        businessInfo.setApprovalTime(commerceInfo.getApprovalDate());
        businessInfo.setBusinessTime(commerceInfo.getBizLicenseEndDate() == null ? null : commerceInfo.getBizLicenseEndDate().toString());
        businessInfo.setOrgNumber(commerceInfo.getOrgCode());
        businessInfo.setCompanyOrgType(commerceInfo.getOrgType());
        businessInfo.setIndustry(commerceInfo.getIndustryType());
        if (ObjectUtil.isNotEmpty(registryAddressInfo)) {
            businessInfo.setRegLocation(registryAddressInfo.getDetail());
            businessInfo.setRegProvince(registryAddressInfo.getProvince());
            businessInfo.setRegCity(registryAddressInfo.getCity());
            businessInfo.setRegCountry(registryAddressInfo.getCountry());
        }
        //联系人信息
        if (ObjectUtil.isNotEmpty(pageContactInfo) && ObjectUtil.isNotEmpty(pageContactInfo.getRecords())) {
            List<DataShareRegisterCustomREQ.LinkmanInfo> list = new ArrayList<>();
            pageContactInfo.getRecords().forEach(record -> {
                DataShareRegisterCustomREQ.LinkmanInfo linkmanInfo = dataShareRegisterCustomREQ.new LinkmanInfo();
                linkmanInfo.setName(record.getName());
                linkmanInfo.setPhoneNo(record.getTelephone());
                linkmanInfo.setDepartment(record.getPosition());
                linkmanInfo.setDuty(record.getPosition());
                linkmanInfo.setEmail(record.getMail());
                list.add(linkmanInfo);
            });
            dataShareRegisterCustomREQ.setLinkmanInfo(list);
        }
        return dataShareRegisterCustomREQ;

    }

}
