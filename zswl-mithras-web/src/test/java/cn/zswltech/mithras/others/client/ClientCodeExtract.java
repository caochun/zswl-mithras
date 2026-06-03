package cn.zswltech.mithras.others.client;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.contactinfo.CorpContactInfoListREQ;
import cn.zswltech.mithras.dto.client.share.DataShareRegisterCustomREQ;
import cn.zswltech.mithras.customer.domain.enums.CorpAddressType;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpAddressInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.CorpContactInfo;
import cn.zswltech.mithras.datashare.mapper.model.DataShareMerchants;
import cn.zswltech.mithras.service.service.client.CorpContactInfoService;
import cn.zswltech.mithras.datashare.service.DataShareMerchantsService;
import cn.zswltech.mithras.datashare.service.DataShareService;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yibin
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientCodeExtract {

    @Resource
    DataShareMerchantsService service;

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

    @Test
    @Rollback(false)
    public void extract() {
        List<String> lines = FileUtil.readUtf8Lines("/Users/luyi/Downloads/a.txt");
        for (int i = 0; i < lines.size(); i++) {
            try {
                String line = lines.get(i);
                JSONArray array = (JSONArray) JSONUtil.getByPath(JSONUtil.parse(line), "data.list");
                List<DataShareMerchants> merchants = new ArrayList<>(array.size());
                for (int j = 0; j < array.size(); j++) {
                    merchants.add(array.getBean(j, DataShareMerchants.class));
                }
                service.saveBatch(merchants);
            } catch (Exception e) {
                log.error("line {} error.", i);
            }
        }


    }

    @Test
    public void getClientCode() {
        List<String> clientNames = ListUtil.toList("衡阳力天林跃汽车销售服务有限公司","长沙力天宝崐汽车销售服务有限公司","常德力天林跃汽车销售服务有限公司","湘潭力天丰田汽车销售服务有限公司");

        //去集团创建客户
        List<Long> clientIds = clientMapper.selectList(Wrappers.<Client>lambdaQuery().in(Client::getClientName, clientNames)).stream().map(Client::getId).collect(Collectors.toList());
            clientIds.forEach(id -> {
                DataShareRegisterCustomREQ dataShareRegisterCustomREQ = null;
                try {
                    dataShareRegisterCustomREQ = buildRegisterCustom(id);
                    dataShareService.registerCustom(dataShareRegisterCustomREQ);
                } catch (Exception e) {
                    log.error("集团创建客户失败, {}", JSONUtil.toJsonStr(dataShareRegisterCustomREQ), e);
                }
            });
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
        CorpContactInfoListREQ req = new CorpContactInfoListREQ();
        req.setClientId(clientId);
        Page<CorpContactInfo> pageContactInfo = corpContactInfoService.list(req);
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
