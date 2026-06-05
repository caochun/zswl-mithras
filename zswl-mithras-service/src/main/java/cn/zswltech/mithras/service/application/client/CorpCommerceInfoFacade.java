package cn.zswltech.mithras.service.application.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.customer.application.client.api.CorpCommerceInfoApplicationService;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.CustomerRSP;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.client.commerceinfo.*;
import cn.zswltech.mithras.dto.contract.HighSeasCustomersREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.client.ClientAddSubAuthCheckerNew;
import cn.zswltech.mithras.service.auth.checker.client.ClientModifyMainAuthCheckerNew;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.customer.domain.enums.ClientAuthEnum;
import cn.zswltech.mithras.customer.domain.enums.client.DomesticOrAbroad;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.IndustryType;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.util.StreamUtil;
import cn.zswltech.mithras.customer.application.validator.InstitutionCreditCodeValidator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.zswltech.mithras.service.constant.ResultMsg.ID_CARD_ERROR;
import static cn.zswltech.mithras.service.others.Const.CERT_ID_CARD_CODE;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.others.Util.checkIDCard;
import org.springframework.stereotype.Service;

/**
 * @author luyi
 */
@Service
public class CorpCommerceInfoFacade implements CorpCommerceInfoApplicationService {
    @Resource
    private CorpCommerceInfoService commerceInfoService;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private ClientService clientService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private Id2NameService id2NameService;

    @SneakyThrows
    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientAddSubAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> add(CorpCommerceInfoAddREQ req) {
        // 中征码还需要验证数字和字母
        err(isNotBlank(req.getZhongZhengCode()) && !req.getZhongZhengCode().matches("^[a-zA-Z0-9]+$"), "中征码只能包含数字和字母");
        err(isNotBlank(req.getZhongZhengCode()) && req.getZhongZhengCode().length() != 16,"中征码位数须为16位");
        if (!Objects.equals(1, req.getGroupFlag())) {
            checkParam(req);
            err(isBlank(req.getCorpRepresent()), "法人代表不能为空");
            err(isBlank(req.getCorpGender()), "法人性别不能为空");
            err(isBlank(req.getCorpCertType()), "法人证件类型不能为空");
            err(isBlank(req.getCorpCertCode()), "法人证件号码不能为空");
            if (CERT_ID_CARD_CODE.equals(req.getCorpCertType())) {
                if (!checkIDCard(req.getCorpCertCode())) {
                    throw new MithrasException(ID_CARD_ERROR);
                }
            }
        }

        //
      /*  String registerCurrencyType = req.getRegisterCurrencyType();
        String realCurrencyType = req.getRealCurrencyType();
        if (StrUtil.isBlank(realCurrencyType) || ObjectUtil.equal(registerCurrencyType, realCurrencyType)) {
            if (ObjectUtil.isNull(req.getRegisterCapitalRate())) {
                throw new MissingServletRequestParameterException("资本到位率不能为空", null);
            }
        }*/
        Client client = clientService.getById(req.getClientId());
        req.setDomesticOrAbroad(client.getDomesticOrAbroad());
        req.setSpecialOrgCode(client.getSpecialOrgCode());
        commerceInfoService.add(req);
        return R.ok();
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientModifyMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<Void> modify(CorpCommerceInfoModifyREQ req) {
        // 验证参数
        if (Objects.nonNull(req.getGroupFlag()) && !Objects.equals(1, req.getGroupFlag())) {
            // 不是集团公司时，看看指标隶属省份是否为空，为空直接抛异常
            err(CharSequenceUtil.isBlank(req.getProvinceOfAffiliation()), "指标隶属省份不能为空");
        }
        Client client = clientService.getById(req.getClientId());
        DomesticOrAbroad domesticOrAbroad = DomesticOrAbroad.valueOf(client.getDomesticOrAbroad());
        switch (domesticOrAbroad) {
            case DOMESTIC:
                checkParam(req);
                // 中征码还需要验证数字和字母
                err(isNotBlank(req.getZhongZhengCode()) && !req.getZhongZhengCode().matches("^[a-zA-Z0-9]+$"), "中征码只能包含数字和字母");
                err(isNotBlank(req.getZhongZhengCode()) && req.getZhongZhengCode().length() != 16,"中征码位数须为16位");
                if (!Objects.equals(1, req.getGroupFlag())) {
                    err(isBlank(req.getCorpRepresent()), "法人代表不能为空");
                    err(isBlank(req.getCorpGender()), "法人性别不能为空");
                    err(isBlank(req.getCorpCertType()), "法人证件类型不能为空");
                    err(isBlank(req.getCorpCertCode()), "法人证件号码不能为空");
                    if (CERT_ID_CARD_CODE.equals(req.getCorpCertType())) {
                        if (!checkIDCard(req.getCorpCertCode())) {
                            throw new MithrasException(ID_CARD_ERROR);
                        }
                    }
                }
                req.setDomesticOrAbroad(null);
                break;
            case ABROAD:
                break;
            default:
                break;
        }
        commerceInfoService.modify(req);

        if (!Objects.equals(req.getProvinceOfAffiliation(), client.getProvinceOfAffiliation())) {
            Client toUpdate = new Client();
            toUpdate.setId(client.getId());
            toUpdate.setProvinceOfAffiliation(req.getProvinceOfAffiliation());
            clientService.updateById(toUpdate);
        }
        return R.ok();
    }

    private void checkParam(CorpCommerceInfoAddREQ req) {
        Assert.notNull(req.getContinuousStatus(), () -> MithrasException.newException("存续状态不能为空"));
        Assert.notNull(req.getEstablishDate(), () -> MithrasException.newException("成立日期不能为空"));
        Assert.notNull(req.getApprovalDate(), () -> MithrasException.newException("核准日期不能为空"));
        Assert.notNull(req.getBizLicenseEndDate(), () -> MithrasException.newException("营业许可证到期日不能为空"));
        Assert.notNull(req.getBizScope(), () -> MithrasException.newException("业务范围不能为空"));
        Assert.notNull(req.getIndustryType(), () -> MithrasException.newException("行业分类不能为空"));
        Assert.notNull(req.getEconomyType(), () -> MithrasException.newException("经济类型不能为空"));
        Assert.notNull(req.getOrgType(), () -> MithrasException.newException("组织机构类型不能为空"));
        Assert.notNull(req.getOrgScale(), () -> MithrasException.newException("企业规模不能为空"));
        Assert.notNull(req.getRegisterCurrencyType(), () -> MithrasException.newException("注册币种不能为空"));
        Assert.notNull(req.getRegisterCapital(), () -> MithrasException.newException("注册资本不能为空"));
        Assert.notNull(req.getRealCapital(), () -> MithrasException.newException("实收资本不能为空"));
        Assert.notNull(req.getIsRelated(), () -> MithrasException.newException("是否关联企业不能为空"));
        Assert.notNull(req.getRiskControlIndustryClassify(), () -> MithrasException.newException("风控行业分类不能为空"));
    }

    @Override
//    @DataAuthCheck(keyFieldName = "clientId", checkerClass = ClientViewMainAuthCheckerNew.class, businessModule = BusinessModuleEnum.CLIENT)
    public R<CorpCommerceInfoDetailRSP> detail(CorpCommerceInfoDetailREQ req) {
        Client client = clientService.getById(req.getClientId());
        if (client == null) {
            throw new MithrasException("客户已不存在");
        }
        if (StringUtils.isBlank(req.getVersion())) {
            CorpCommerceInfoDetailRSP rsp = copyProperties(commerceInfoService.detail(req.getClientId(), req.getStartUserId()), CorpCommerceInfoDetailRSP.class);
            rsp.setProvinceOfAffiliation(client.getProvinceOfAffiliation());
            if (ObjectUtil.isEmpty(rsp.getDomesticOrAbroad())) {
                rsp.setDomesticOrAbroad(client.getDomesticOrAbroad());
                rsp.setSpecialOrgCode(client.getSpecialOrgCode());
            }
            // 找到行业分类及其父分类
            if (StrUtil.isNotBlank(rsp.getIndustryType())) {
                int length = rsp.getIndustryType().length();
                List<String> industryTypeCodes = new LinkedList<>();
                // code的父子关系呈现位数关系，eg: A A01 A011 A0111
                // 查询条件不特殊处理A0，不存在这样的数据，多带一个无效条件问题不大
                for (int i = length; i > 0; i--) {
                    String s = rsp.getIndustryType().substring(0, i);
                    industryTypeCodes.add(s);
                }
                LambdaQueryWrapper<IndustryType> query = Wrappers.lambdaQuery();
                query.in(IndustryType::getCode, industryTypeCodes);
                List<IndustryType> industryTypeList = industryTypeMapper.selectList(query);
                industryTypeList.sort(Comparator.comparing(IndustryType::getLevel));
                List<String> result = new ArrayList<>(industryTypeList.size());
                StringBuilder builder = new StringBuilder();
                for (IndustryType industryType : industryTypeList) {
//                    if (Objects.equals(rsp.getIndustryType(), industryType.getCode())) {
//                        rsp.setIndustryTypeName(industryType.getDisplay());
//                    }
                    if (builder.length() != 0){
                        builder.append("/");
                    }
                    builder.append(industryType.getDisplay());
                    result.add(industryType.getCode());
                }
                rsp.setIndustryTypeName(builder.toString());
                rsp.setIndustryTypeWithParent(result);
            }
            //填充客户权限类型
//            rsp.setClientAuthType(clientService.checkClientAuth(req.getClientId(), req.getSourceScene()));
            if (client.getBelongSponsorId() != null) {
                rsp.setSponsorUserName(id2NameService.sysUserId2NameSingle(client.getBelongSponsorId()));
            }
            return R.ok(rsp);
        } else {
            CorpCommerceInfoDetailRSP rsp = corpCommerceInfoLibService.detail(req.getClientId(), req.getVersion());
            rsp.setProvinceOfAffiliation(client.getProvinceOfAffiliation());
            if (client.getBelongSponsorId() != null) {
                rsp.setSponsorUserName(id2NameService.sysUserId2NameSingle(client.getBelongSponsorId()));
            }
            return R.ok(rsp);
        }
    }

    @Override
    public R<List<SelectRSP>> listHighSegasCustomers(HighSeasCustomersREQ req) {
        // 获取合同所属项目经理id,查询该用户（项目经理）名下所有客户
        List<CustomerRSP> userList = new ArrayList<>();
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(req.getContractId());
        if (ObjectUtils.isNotEmpty(contractBaseInfo)){
            LambdaQueryWrapper<Client> query = new LambdaQueryWrapper<>();
            query.eq(Client::getBelongSponsorId, contractBaseInfo.getProjSponsorUserId());
            List<Client> clientList = clientMapper.selectList(query)
                    .stream().filter(StreamUtil.distinctByKey(Client::getId))
                    .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(clientList)){
                clientList.forEach(client -> {
                    CustomerRSP userRSP = new CustomerRSP();
                    userRSP.setCustomerId(client.getId());
                    userRSP.setCustomerName(client.getClientName());
                    userList.add(userRSP);
                });
            }
        }
        //查询公海客户clientId
     /*   List<CorpCommerceInfo> commerceInfoList = ccfMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery().
                eq(CorpCommerceInfo::getRiskControlIndustryClassify, RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
       */
        List<Client> clients = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                .in(Client::getAuthType, ClientAuthEnum.HIGH_SEAS.name(), ClientAuthEnum.NO_AFFILIATION.name()));
        if (CollectionUtil.isNotEmpty(clients)) {
            List<Long> clientIds = clients.stream().map(Client::getId).collect(Collectors.toList());
            List<Client> clientsList = clientMapper.selectBatchIds(clientIds);
            if (CollectionUtil.isNotEmpty(clientsList)) {
                clientsList.forEach(client -> {
                    CustomerRSP userRSP = new CustomerRSP();
                    userRSP.setCustomerId(client.getId());
                    userRSP.setCustomerName(client.getClientName());
                    userList.add(userRSP);
                });
            }
        }
        //用户列表列表去重，获取唯一用户
        List<CustomerRSP> uniqueUsers = userList.stream().distinct().collect(Collectors.toList());
        //模糊查询
        if (StringUtils.isNotEmpty(req.getCustomerName())) {
            uniqueUsers = uniqueUsers.stream().filter(user -> user.getCustomerName().contains(req.getCustomerName())).collect(Collectors.toList());
        }
        List<SelectRSP> result = uniqueUsers.stream().map(e -> new SelectRSP(e.getCustomerName() , e.getCustomerId().toString())).collect(Collectors.toList());
        return R.ok(result);
    }

    @Override
    public R<ClientCorpCommerceInfoValidRSP> valid(ClientCorpCommerceInfoValidREQ req) {
        return R.ok(commerceInfoService.valid(req));
    }
}
