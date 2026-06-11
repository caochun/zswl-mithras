package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.vo.UserVO;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.application.orchestration.document.gendoc.AbstractContractRender;
import cn.zswltech.mithras.document.model.FileTemplate;
import cn.zswltech.mithras.customer.mapper.model.client.*;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractPledge;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.document.file.template.FileTemplateService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合同_质押合同_应收账款
 */
@Component
public class ContractPledgeReceivableRender extends AbstractContractRender<ContractPledge> {
    private static final String CORPORATION_TEMPLATE = "出质人（盖章）：%s\n\n    法定代表人/负责人或授权代理人（签字或盖章）：";

    @Resource(name = "userServiceAPI")
    private UserService userServiceAPI;

    @Override
    public String render(OutputStream outputStream, ContractPledge contractPledge) throws Exception {
        Map<String, Object> renderMap = new HashMap<>();
        Long contractId = contractPledge.getContractId();
        ContractBaseInfo contractBaseInfo = this.getContractBaseInfo(contractId);
        ClientType clientType = ClientType.of(contractPledge.getPledgeType());
        if (Objects.isNull(clientType)) {
            throw new MithrasException("未知的质押人类型");
        }
        List<Long> clientIds = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
        Map<Long, Client> clientMap = businessDataRepository.getClientMap(clientIds);
        List<String> pledgeNameList = clientMap.values().stream().map(Client::getClientName).collect(Collectors.toList());
        renderMap.put(RenderParameterKeyHolder.PLEDGE_NAME, Joiner.on("、").join(pledgeNameList));

        // 获取主办数据
        UserVO userVO = userServiceAPI.getUserInfoById(contractBaseInfo.getProjSponsorUserId()).getData();
        String sponsorPhone = userServiceAPI.getRealPhone(contractBaseInfo.getProjSponsorUserId());

        // 填充主办信息
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_NAME, Optional.ofNullable(userVO).map(UserVO::getUserName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_MAIL, Optional.ofNullable(userVO).map(UserVO::getEmail).orElse(""));
        renderMap.put(RenderParameterKeyHolder.SPONSOR_USER_PHONE, Optional.ofNullable(sponsorPhone).orElse(""));

        InputStream inputStream = SpringUtil.getBean(FileTemplateService.class).getTemplate("合同-质押合同", "6.质押合同-应收账款.docx");
        List<String> pledgeTextList = new LinkedList<>();
        TableRenderData tableRenderData;
        if (Objects.equals(clientType.name(), ClientType.CORPORATION.name())) {
            tableRenderData = this.corporationTable(clientIds, clientMap);
        } else {
            tableRenderData = this.normalTable(clientIds, clientMap);
        }
        renderMap.put(RenderParameterKeyHolder.PLEDGE_TABLE, tableRenderData);
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            pledgeTextList.add(String.format(CORPORATION_TEMPLATE, Optional.ofNullable(client).map(Client::getClientName).orElse("")));
        }
        renderMap.put(RenderParameterKeyHolder.PLEDGE_TEXT_LIST, Joiner.on("\n\n\n\n\n\n\n\n    ").join(pledgeTextList));
        renderMap.put(RenderParameterKeyHolder.RELATED_CONTRACT_CODE, StrUtil.join("、", this.listRelatedContractCode(contractBaseInfo, contractPledge.getRelatContracts())));
        renderMap.put(RenderParameterKeyHolder.PLEDGE_CONTRACT_CODE, contractPledge.getPledgeContractCode());
        ContractTenantry contractTenantry = this.getMainTenantry(contractId, this.getContractTenantryType(contractBaseInfo));
        Client clientNotNull = businessDataRepository.getClientNotNull(contractTenantry.getLesseeId());
        renderMap.put(RenderParameterKeyHolder.MAIN_LESSEE_NAME, Optional.ofNullable(clientNotNull).map(Client::getClientName).orElse("             "));
        renderMap.put(RenderParameterKeyHolder.CONTRACT_CODE, contractBaseInfo.getContractCode());

        // 渲染文档
        XWPFTemplate template = XWPFTemplate.compile(inputStream).render(renderMap);
        template.writeAndClose(outputStream);
        return "质押合同-应收账款.docx";
    }

    @Override
    protected Set<Long> signClientIds(ContractPledge contractPledge) {
        if (StrUtil.isBlank(contractPledge.getPledgeIds())) {
            return null;
        }
        List<Long> ids = JSONUtil.toList(contractPledge.getPledgeIds(), Long.class);
        return new HashSet<>(ids);
    }

    @Override
    protected boolean needSignByMyself() {
        return true;
    }

    @Override
    protected boolean customShowFile(ContractPledge contractPledge) {
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord("合同-质押合同", "6.质押合同-应收账款.docx");
        return Optional.of(templateRecord).map(e -> Objects.equals(e.getFaceSignShowFlag(), YesOrNoNumberEnum.YES.getCode())).orElse(false);
    }

    @Override
    protected String customTemplateKey(ContractPledge contractPledge) {
        FileTemplate templateRecord = SpringUtil.getBean(FileTemplateService.class).getTemplateRecord("合同-质押合同", "6.质押合同-应收账款.docx");
        return Optional.of(templateRecord).map(FileTemplate::getFileTemplateKey).orElse(null);
    }

    private static class RenderParameterKeyHolder {
        // 项目主办名称
        public static final String SPONSOR_USER_NAME = "sponsorUserName";
        // 项目主办邮箱
        public static final String SPONSOR_USER_MAIL = "sponsorUserMail";
        // 项目主办手机
        public static final String SPONSOR_USER_PHONE = "sponsorUserPhone";
        // 主合同编号
        public static final String CONTRACT_CODE = "contractCode";
        // 质押合同编号
        public static final String PLEDGE_CONTRACT_CODE = "pledgeContractCode";
        // 关联合同编号
        public static final String RELATED_CONTRACT_CODE = "relatedContractCode";
        // 出质人名称
        public static final String PLEDGE_NAME = "pledgeName";
        // 主承租人名称
        public static final String MAIN_LESSEE_NAME = "mainLesseeName";
        // 出质人表格
        public static final String PLEDGE_TABLE = "pledgeTable";
        // 出质人落款文本列表
        public static final String PLEDGE_TEXT_LIST = "pledgeTextList";
    }

    private TableRenderData corporationTable(Collection<Long> clientIds, Map<Long, Client> clientMap) {
        List<RowRenderData> rowRenderDataList = new LinkedList<>();
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            CorpCommerceInfoLib corpCommerceInfoLib = businessDataRepository.getCorpCommerceInfo(client.getId());
            List<CorpAddressInfoLib> corpAddressInfoList = businessDataRepository.getCorpAddressInfo(client.getId());
            CorpContactInfoLib corpContactInfoLib = this.getMainContact(clientId);
            rowRenderDataList.add(Rows.of("出质人名称：" + client.getClientName(), "").rowAtleastHeight(0.7).verticalCenter().create());
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
        return Tables.of(rowRenderDataArray).mergeRule(builder.build()).create();
    }

    private TableRenderData normalTable(Collection<Long> clientIds, Map<Long, Client> clientMap) {
        List<RowRenderData> rowRenderDataList = new LinkedList<>();
        for (Long clientId : clientIds) {
            Client client = clientMap.get(clientId);
            NormalBaseInfo normalBaseInfo = businessDataRepository.getNormalBaseInfo(clientId);
            rowRenderDataList.add(Rows.of("出质人名称：" + client.getClientName(), "").rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("联系地址：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getHomeAddress).orElse(""), "").rowAtleastHeight(0.7).verticalCenter().create());
            // 自然人联系人填写自己
            rowRenderDataList.add(Rows.of("联系人：" + Optional.ofNullable(client).map(Client::getClientName).orElse(""), "联系人手机：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getMobileNumber).orElse("")).rowAtleastHeight(0.7).verticalCenter().create());
            rowRenderDataList.add(Rows.of("电子邮箱：" + Optional.ofNullable(normalBaseInfo).map(NormalBaseInfo::getMail).orElse(""), "固定电话：\\").rowAtleastHeight(0.7).verticalCenter().create());
        }
        RowRenderData[] rowRenderDataArray = new RowRenderData[rowRenderDataList.size()];
        rowRenderDataList.toArray(rowRenderDataArray);
        MergeCellRule.MergeCellRuleBuilder builder = MergeCellRule.builder();
        for (int i = 0; i < clientIds.size(); i++) {
            builder.map(MergeCellRule.Grid.of(i * 4,0), MergeCellRule.Grid.of(i * 4, 1));
            builder.map(MergeCellRule.Grid.of(i * 4 + 1,0), MergeCellRule.Grid.of(i * 4 + 1, 1));
        }
        return Tables.of(rowRenderDataArray).mergeRule(builder.build()).create();
    }

}
