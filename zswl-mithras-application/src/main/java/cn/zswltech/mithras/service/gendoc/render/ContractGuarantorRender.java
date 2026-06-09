package cn.zswltech.mithras.service.gendoc.render;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.result.Response;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.domain.enums.client.ClientType;
import cn.zswltech.mithras.contract.enums.contract.ContractTypeEnum;
import cn.zswltech.mithras.contract.enums.contract.GuaranteeMethodEnum;
import cn.zswltech.mithras.service.gendoc.AbstractContractRender;
import cn.zswltech.mithras.service.mapper.file.template.model.FileTemplate;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.document.application.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author dingqi
 * @date 2022/8/22
 * @description 保证合同
 */
@Slf4j
@Component
public class ContractGuarantorRender extends AbstractContractRender<ContractGuarantor> {
    private static final String NORMAL_TEMPLATE = "保证人：【%s】（签字并捺印）";
    private static final String CORPORATION_TEMPLATE = "保证人：%s（盖章）\n        法定代表人/负责人（签字/签章）/授权代表（签字）：";

    @Resource(name = "userServiceAPI")
    private UserService userService;

    @Override
    public String render(OutputStream outputStream, ContractGuarantor contractGuarantor) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        Long contractId = contractGuarantor.getContractId();
        ContractBaseInfo contractBaseInfo = this.getContractBaseInfo(contractId);
//        ContractTenantry contractTenantry = this.getMainTenantry(contractId, this.getContractTenantryType(contractBaseInfo));
        renderMap.put(RenderParameterKeyHolder.RELATED_CONTRACT_CODE, StrUtil.join("、", this.listRelatedContractCode(contractBaseInfo, contractGuarantor.getRelatContracts())));
        renderMap.put(RenderParameterKeyHolder.GUARANTE_CONTRACT_CODE, contractGuarantor.getGuarantorContractCode());
        renderMap.put(RenderParameterKeyHolder.LESSEE_NAME, StrUtil.join("、", this.listContractTenantryName(contractId)));
        // 查询主办信息
        Response<UserVO> userVOResponse = userService.getUserInfoById(contractBaseInfo.getProjSponsorUserId());
        if (Objects.nonNull(userVOResponse) && userVOResponse.isSuccess()) {
            UserVO userVO = userVOResponse.getData();
            String sponsorPhone = userService.getRealPhone(contractBaseInfo.getProjSponsorUserId());
            if (Objects.nonNull(userVO)) {
                renderMap.put(RenderParameterKeyHolder.SPONSOR_NAME, userVO.getUserName());
                renderMap.put(RenderParameterKeyHolder.SPONSOR_MOBILE, sponsorPhone);
                renderMap.put(RenderParameterKeyHolder.SPONSOR_EMAIL, userVO.getEmail());
            }
        }
        ClientType clientType = ClientType.of(contractGuarantor.getGuarantorType());
        if (Objects.isNull(clientType)) {
            throw new MithrasException("未知的保证人类型");
        }
        List<Long> clientIds = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
        if (CollectionUtils.isEmpty(clientIds)) {
            throw new MithrasException("保证人id为空");
        }
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        if (CollectionUtils.isEmpty(clientMap)) {
            throw new MithrasException("通过保证人id没有找到任何客户信息");
        }
        List<String> nameList = new ArrayList<>(clientIds.size());
        for (Map.Entry<Long, Client> entry : clientMap.entrySet()) {
            Client client = entry.getValue();
            nameList.add(client.getClientName());
        }
        InputStream inputStream;
        switch (clientType) {
            case NORMAL: {
                inputStream = getBean(FileTemplateService.class).getTemplate("合同-保证合同", "合同_保证合同_自然人.docx");
                Map<Long, NormalBaseInfoLib> normalBaseInfoMap = businessDataRepository.getNormalBaseInfoMap(clientIds);
                List<RowRenderData> rowRenderDataList = new LinkedList<>();
                List<String> normalNameList = new LinkedList<>();
                for (Long clientId : clientIds) {
                    Client client = clientMap.get(clientId);
                    if (Objects.isNull(client)) {
                        continue;
                    }
                    normalNameList.add(String.format(NORMAL_TEMPLATE, client.getClientName()));
                    NormalBaseInfo normalBaseInfo = normalBaseInfoMap.get(client.getId());
                    rowRenderDataList.add(Rows.of("保证人名称：" + client.getClientName()).rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("身份证号：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getCertNumber).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("联系地址：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getHomeAddress).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("联系电话：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getMobileNumber).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("电子邮箱：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getMail).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
                }
                RowRenderData[] rowRenderDataArray = new RowRenderData[rowRenderDataList.size()];
                rowRenderDataList.toArray(rowRenderDataArray);
                TableRenderData tableRenderData = Tables.of(rowRenderDataArray).create();
                renderMap.put(RenderParameterKeyHolder.NORMAL_TABLE, tableRenderData);
                renderMap.put(RenderParameterKeyHolder.NORMAL_GUARANTOR_TEXT_LIST, Joiner.on("\n\n\n    ").join(normalNameList));
                break;
            }
            case CORPORATION: {
                inputStream = getBean(FileTemplateService.class).getTemplate("合同-保证合同", "合同_保证合同_法人.docx");
//                renderMap.put(RenderParameterKeyHolder.GUARANTOR_NAME, Joiner.on("、").join(nameList));
//                Client client = clientMap.get(clientIds.get(0));
//                CorpCommerceInfoLib corpCommerceInfo = businessDataRepository.getCorpCommerceInfo(client.getId());
//                renderMap.put(RenderParameterKeyHolder.LEGAL_REPRESENTATIVE, corpCommerceInfo.getCorpRepresent());
//                List<CorpAddressInfoLib> corpAddressInfoList = businessDataRepository.getCorpAddressInfo(client.getId());
//                if (!CollectionUtils.isEmpty(corpAddressInfoList)) {
//                    renderMap.put(RenderParameterKeyHolder.ADDRESS, this.getCorpRegistryAddress(corpAddressInfoList));
//                }
//                if (Objects.nonNull(contractGuarantor.getContactId())) {
//                    CorpContactInfoLib corpContactInfoLib = this.getNewestContact(contractGuarantor.getContactId());
//                    if (Objects.nonNull(corpContactInfoLib)) {
//                        renderMap.put(ContractGuarantorRender.RenderParameterKeyHolder.CONTACT, corpContactInfoLib.getName());
//                        renderMap.put(ContractGuarantorRender.RenderParameterKeyHolder.CONTACT_MOBILE, corpContactInfoLib.getTelephone());
//                        renderMap.put(ContractGuarantorRender.RenderParameterKeyHolder.CONTACT_EMAIL, corpContactInfoLib.getMail());
//                        renderMap.put(RenderParameterKeyHolder.CONTACT_TELEPHONE, StrUtil.isBlank(corpContactInfoLib.getLandlineTelephone()) ? "\\" : corpContactInfoLib.getLandlineTelephone());
//                    }
//                }
                // 拼表格
                List<String> corporationNameList = new LinkedList<>();
                List<RowRenderData> rowRenderDataList = new LinkedList<>();
                for (Long clientId : clientIds) {
                    Client client = clientMap.get(clientId);
                    corporationNameList.add(String.format(CORPORATION_TEMPLATE, client.getClientName()));
                    CorpCommerceInfoLib corpCommerceInfoLib = businessDataRepository.getCorpCommerceInfo(client.getId());
                    List<CorpAddressInfoLib> corpAddressInfoList = businessDataRepository.getCorpAddressInfo(client.getId());
                    CorpContactInfoLib corpContactInfoLib = this.getNewestContact(contractGuarantor.getContactId());
                    rowRenderDataList.add(Rows.of("保证人名称：" + client.getClientName(), "").rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("法定代表人：" + Optional.ofNullable(corpCommerceInfoLib).map(CorpCommerceInfo::getCorpRepresent).orElse(""), "").rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("联系地址：" + Optional.ofNullable(corpAddressInfoList).map(this::getCorpRegistryAddress).orElse(""), "").rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("联系人：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getName).orElse(""), "联系人手机：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getTelephone).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
                    rowRenderDataList.add(Rows.of("电子邮箱：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getMail).orElse(""), "固定电话：" + Optional.ofNullable(corpContactInfoLib).map(CorpContactInfoLib::getLandlineTelephone).orElse("\\")).rowAtleastHeight(0.7).verticalCenter().create());
                }
                RowRenderData[] rowRenderDataArray = new RowRenderData[rowRenderDataList.size()];
                rowRenderDataList.toArray(rowRenderDataArray);
                MergeCellRule.MergeCellRuleBuilder builder = MergeCellRule.builder();
                for (int i = 0; i < clientIds.size(); i++) {
                    builder.map(MergeCellRule.Grid.of(i * 5,0), MergeCellRule.Grid.of(i * 5, 1));
                    builder.map(MergeCellRule.Grid.of(i * 5 + 1,0), MergeCellRule.Grid.of(i * 5 + 1, 1));
                    builder.map(MergeCellRule.Grid.of(i * 5 + 2,0), MergeCellRule.Grid.of(i * 5 + 2, 1));
                }
                TableRenderData tableRenderData = Tables.of(rowRenderDataArray).mergeRule(builder.build()).create();
                renderMap.put(RenderParameterKeyHolder.CORPORATION_GUARANTOR_TABLE, tableRenderData);
                renderMap.put(RenderParameterKeyHolder.CORPORATION_GUARANTOR_TEXT_LIST, Joiner.on("\n\n\n\n        ").join(corporationNameList));
                break;
            }
            default: {
                throw new MithrasException("不支持的保证人类型");
            }
        }
        // 担保方式
        GuaranteeMethodEnum guaranteeMethodEnum = GuaranteeMethodEnum.of(contractGuarantor.getGuaranteeMethod());
        String type = Optional.ofNullable(guaranteeMethodEnum).map(GuaranteeMethodEnum::display).orElse("         ");
        renderMap.put(RenderParameterKeyHolder.GUARANTEE_METHOD, type);
        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream);
        template.render(renderMap);
        template.writeAndClose(outputStream);
        return ContractTypeEnum.GUARANTEE_CONTRACT.getDisplay() + "-" + Joiner.on("、").join(nameList) + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @Override
    protected Set<Long> signClientIds(ContractGuarantor contractGuarantor) {
        if (StrUtil.isBlank(contractGuarantor.getGuarantorIds())) {
            return null;
        }
        List<Long> ids = JSONUtil.toList(contractGuarantor.getGuarantorIds(), Long.class);
        return new HashSet<>(ids);
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractGuarantor contractGuarantor) {
        ClientType clientType = ClientType.of(contractGuarantor.getGuarantorType());
        if (Objects.equals(clientType, ClientType.CORPORATION)) {
            FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保证合同", "合同_保证合同_法人.docx");
            return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
        }else if (Objects.equals(clientType, ClientType.NORMAL)) {
            FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保证合同", "合同_保证合同_自然人.docx");
            return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
        }else {
            return false;
        }
    }

    @Override
    protected String customTemplateKey(ContractGuarantor contractGuarantor) {
        ClientType clientType = ClientType.of(contractGuarantor.getGuarantorType());
        if (Objects.equals(clientType, ClientType.CORPORATION)) {
            FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保证合同", "合同_保证合同_法人.docx");
            return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
        }else if (Objects.equals(clientType, ClientType.NORMAL)) {
            FileTemplate templateRecord = getBean(FileTemplateService.class).getTemplateRecord("合同-保证合同", "合同_保证合同_自然人.docx");
            return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
        }else {
            log.error("不支持的保证人类型");
            return null;
        }
    }

    private static class RenderParameterKeyHolder {
        // 主合同编号
        public static final String RELATED_CONTRACT_CODE = "relatedContractCode";
        // 主承租人
        public static final String LESSEE_NAME = "lesseeName";
        // 保证合同编号
        public static final String GUARANTE_CONTRACT_CODE = "guaranteContractCode";
        // 保证人名称
        public static final String GUARANTOR_NAME = "guarantorName";
        // 法定代表人
        public static final String LEGAL_REPRESENTATIVE = "legalRepresentative";
        // 联系地址
        public static final String ADDRESS = "address";
        // 联系人
        public static final String CONTACT = "contact";
        // 联系人手机
        public static final String CONTACT_MOBILE = "contactMobile";
        // 电子邮箱
        public static final String CONTACT_EMAIL = "contactEmail";
        // 座机
        public static final String CONTACT_TELEPHONE = "contactTelephone";
        // 自然人表格
        public static final String NORMAL_TABLE = "guarantorTable";
        // 自然人落款文本列表
        public static final String NORMAL_GUARANTOR_TEXT_LIST = "normalGuarantorTextList";
        // 主办姓名
        public static final String SPONSOR_NAME = "sponsorName";
        // 主办手机
        public static final String SPONSOR_MOBILE = "sponsorMobile";
        // 主办邮箱
        public static final String SPONSOR_EMAIL = "sponsorEmail";
        // 担保方式
        public static final String GUARANTEE_METHOD = "guaranteeMethod";
        // 法人表格
        public static final String CORPORATION_GUARANTOR_TABLE = "corporationGuarantorTable";
        // 法人落款文本列表
        public static final String CORPORATION_GUARANTOR_TEXT_LIST = "corporationGuarantorTextList";
    }
}
