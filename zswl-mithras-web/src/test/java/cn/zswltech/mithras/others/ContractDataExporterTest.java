package cn.zswltech.mithras.others;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.message.MessageAddREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.convert.MessageConver;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.enums.MessageUrlEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.notice.MessageTypeEnum;
import cn.zswltech.mithras.service.excel.AbstractSimpleExcelExporter;
import cn.zswltech.mithras.service.excel.model.ExcelModel;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantor;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.contract.ContractGuarantorLibService;
import cn.zswltech.mithras.contract.archive.handler.impl.ContractBaseInfoLibHandler;
import cn.zswltech.mithras.service.service.message.MessageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author dingqi
 * @date 2023/1/12
 * @description
 */
@Slf4j
public class ContractDataExporterTest extends ApplicationTest {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractBaseInfoLibHandler contractBaseInfoLibHandler;
    @Resource
    private ContractGuarantorLibService contractGuarantorLibService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private MessageConver messageConver;
    @Resource
    private MessageService messageService;
    @Resource
    private ClientService clientService;

    @Test
    public void sendMessage() {
        Client client = clientService.getById(3478L);
        MessageAddREQ addREQ = new MessageAddREQ();
        addREQ.setTo(sysUserService.queryJobUserIds(JobEnum.Informationpost.name()));
        addREQ.setMessageType(MessageTypeEnum.PROJ_REVIEW_AUDITED.name());
        addREQ.setNeedOa(true);
        addREQ.setNoticeSource(BusinessModuleEnum.PROJ_REVIEW.name());
        addREQ.setPcurl(String.format(MessageUrlEnum.CLIENT_NEW.pcUrl, client.getId(), client.getClientType()));
        addREQ.setFrom("系统通知");
        addREQ.setRelation(String.format("项目【%s】评审通过，需至客商系统创建客户【%s】", "test项目", client.getClientName()));
        addREQ.setBusinessId("9999");
        addREQ.setContent(client.getClientName());
        messageService.sendMessage(messageConver.reqToMessage(addREQ));
    }

    @Test
    public void test() {
        LambdaQueryWrapper<ContractBaseInfo> contractBaseInfoQuery = Wrappers.lambdaQuery();
        contractBaseInfoQuery.ne(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.INVALID.name(), ContractStatus.NEW.name()));
        contractBaseInfoQuery.orderByAsc(ContractBaseInfo::getContractCode);
        List<ContractBaseInfo> allContract = contractBaseInfoService.list(contractBaseInfoQuery);
        if (CollectionUtil.isEmpty(allContract)) {
            return;
        }
        List<ContractInfoExcelModel> contractInfoExcelModelList = new LinkedList<>();
        for (ContractBaseInfo contractBaseInfo : allContract) {
            ContractBaseInfoLib contractBaseInfoLib = contractBaseInfoLibHandler.queryLatestDataByOriginId(contractBaseInfo.getId());
            if (Objects.isNull(contractBaseInfoLib)) {
                log.info("{}没有版本数据", contractBaseInfo.getContractCode());
                continue;
            }
            // 找到主办和部门
            String belongSponsorName = id2NameService.sysUserId2NameSingle(contractBaseInfoLib.getProjSponsorUserId());
            String belongDeptName = id2NameService.deptId2NameSingle(contractBaseInfoLib.getBizDeptId());
            // 找对应版本的担保措施
            List<ContractGuarantorLib> contractGuarantorLibList = contractGuarantorLibService.listByVersion(contractBaseInfoLib.getOriginId(), contractBaseInfoLib.getVersion());
            if (CollectionUtil.isEmpty(contractGuarantorLibList)) {
                contractInfoExcelModelList.add(this.buildContractInfoExcelModel(contractBaseInfoLib, belongSponsorName, belongDeptName));
            } else {
                contractGuarantorLibList.sort(Comparator.comparing(ContractGuarantor::getGuarantorContractCode));
                for (ContractGuarantorLib contractGuarantorLib : contractGuarantorLibList) {
                    List<Long> clientIdList = JSONUtil.toList(contractGuarantorLib.getGuarantorIds(), Long.class);
                    for (Long clientId : clientIdList) {
                        ContractInfoExcelModel contractInfoExcelModel = this.buildContractInfoExcelModel(contractBaseInfoLib, belongSponsorName, belongDeptName);
                        String clientName = id2NameService.clientId2NameSingle(clientId);
                        contractInfoExcelModel.setGuarantorName(clientName);
                        contractInfoExcelModel.setGuarantorContractCode(contractGuarantorLib.getGuarantorContractCode());
                        contractInfoExcelModel.setIsReport(Objects.equals(contractGuarantorLib.getIsReport(), YesOrNoNumberEnum.YES.getCode()) ? "是" : "否");
                        contractInfoExcelModelList.add(contractInfoExcelModel);
                    }
                }
            }
        }
        new AbstractSimpleExcelExporter<ContractInfoExcelModel>() {
            @Override
            protected LinkedHashMap<String, String> getHeaderAliasMap() {
                LinkedHashMap<String, String> header = new LinkedHashMap<>();
                header.put("contractCode", "主合同编号");
                header.put("projectName", "项目名称");
                header.put("belongSponsorName", "所属主办");
                header.put("belongDeptName", "所属部门");
                header.put("guarantorName", "保证人名称");
                header.put("guarantorContractCode", "保证合同编号");
                header.put("isReport", "是否上报征信");
                return header;
            }

            @Override
            protected Map<Integer, CellStyle> getColumnStyleMap(Workbook workbook) {
                return null;
            }

            @Override
            protected void customStrategy(Workbook workbook) {

            }

            @Override
            protected Class<ContractInfoExcelModel> modelClz() {
                return ContractInfoExcelModel.class;
            }
        }.exportExcel(contractInfoExcelModelList, FileUtil.getOutputStream("/Users/mockorz/合同担保措施信息.xlsx"));
    }

    private ContractInfoExcelModel buildContractInfoExcelModel(ContractBaseInfoLib contractBaseInfoLib, String belongSponsorName, String belongDeptName) {
        ContractInfoExcelModel contractInfoExcelModel = new ContractInfoExcelModel();
        contractInfoExcelModel.setContractCode(contractBaseInfoLib.getContractCode());
        contractInfoExcelModel.setProjectName(contractBaseInfoLib.getProjName());
        contractInfoExcelModel.setBelongDeptName(belongDeptName);
        contractInfoExcelModel.setBelongSponsorName(belongSponsorName);
        return contractInfoExcelModel;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ContractInfoExcelModel extends ExcelModel {
        private String contractCode;
        private String projectName;
        private String belongSponsorName;
        private String belongDeptName;
        private String guarantorName;
        private String guarantorContractCode;
        private String isReport;
    }
}
