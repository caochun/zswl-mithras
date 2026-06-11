package cn.zswltech.mithras.others.数据导出;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.others.JasyptUtil;
import cn.zswltech.mithras.customer.enums.SubjectItemType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.customer.mapper.corp.CorpSubjectItemMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpSubjectItem;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.application.ContractGuarantorService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @date 2024/3/13/16:34
 * @description
 */
@RunWith(SpringRunner.class)
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 金控普华数据导出 {
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractGuarantorService contractGuarantorService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private CorpSubjectItemMapper subjectItemMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ClientService clientService;

    @Test
    public void exportData() {
        List<ProjDataExcel> list = dataHandler();
        String fileName = "/Users/yangxiong/Desktop/融租易已评审项目信息.xlsx";
        EasyExcel.write(fileName, ProjDataExcel.class).sheet("融租易已评审项目信息").doWrite(list);
        log.info("数据导出完成！");
    }

    private List<ProjDataExcel> dataHandler() {
        List<ProjDataExcel> res = new LinkedList<>();
        List<ProjReviewBaseInfo> projReviewBaseInfos = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, RecordStatus.TAKE_EFFECT.name()));
        if (CollUtil.isEmpty(projReviewBaseInfos)) {
            return Collections.emptyList();
        }

        projReviewBaseInfos.forEach(projReviewBaseInfo -> {
            //承租人
            if (Objects.nonNull(projReviewBaseInfo.getLesseeInfo())) {
                List<ClientInfo> clientInfos = JSONUtil.toList(projReviewBaseInfo.getLesseeInfo(), ClientInfo.class);
                for (ClientInfo clientInfo : clientInfos) {
                    res.add(build(projReviewBaseInfo, clientInfo, "承租人"));
                }
            }
            //担保人
            if (Objects.nonNull(projReviewBaseInfo.getGuaranteeInfo())) {
                List<ClientInfo> clientInfos = JSONUtil.toList(projReviewBaseInfo.getGuaranteeInfo(), ClientInfo.class);
                for (ClientInfo clientInfo : clientInfos) {
                    res.add(build(projReviewBaseInfo, clientInfo, "担保人"));
                }
            }
            //债权人
            if (Objects.nonNull(projReviewBaseInfo.getCreditorInfo())) {
                List<ClientInfo> clientInfos = JSONUtil.toList(projReviewBaseInfo.getCreditorInfo(), ClientInfo.class);
                for (ClientInfo clientInfo : clientInfos) {
                    res.add(build(projReviewBaseInfo, clientInfo, "债权人"));
                }
            }
            //债务人
            if (Objects.nonNull(projReviewBaseInfo.getDebtorInfo())) {
                List<ClientInfo> clientInfos = JSONUtil.toList(projReviewBaseInfo.getDebtorInfo(), ClientInfo.class);
                for (ClientInfo clientInfo : clientInfos) {
                    res.add(build(projReviewBaseInfo, clientInfo, "债务人"));
                }
            }
        });
        return res;
    }

    private ProjDataExcel build(ProjReviewBaseInfo projReviewBaseInfo, ClientInfo clientInfo, String type) {
        ProjDataExcel excel = new ProjDataExcel();
        excel.setProjName(projReviewBaseInfo.getProjName());
        excel.setClientName(clientInfo.getClientName());
        getCode(clientInfo.getClientId(), excel);
        excel.setClientType(type);
        return excel;
    }

    private void getCode(Long clientId, ProjDataExcel excel) {
        CorpCommerceInfo info = corpCommerceInfoService.detail(clientId, null);
        if (Objects.nonNull(info) && ClientType.CORPORATION.name().equals(info.getClientType())) {
            excel.setCode(info.getUscCode());
            if (info.getOrgType().equals("1")) {
                List<CorpSubjectItem> corpSubjectItems = subjectItemMapper.selectList(Wrappers.<CorpSubjectItem>lambdaQuery()
                        .eq(CorpSubjectItem::getClientId, clientId)
                        .in(CorpSubjectItem::getSubjectType,
                                Arrays.asList(SubjectItemType.CAPITAL_BALANCE.name(),
                                        SubjectItemType.CASH_FLOW.name(), SubjectItemType.PROFIT.name())));
                if (CollUtil.isEmpty(corpSubjectItems)) {
                    excel.setIsComplete("无");
                    return;
                }
                Map<String, List<CorpSubjectItem>> listMap = corpSubjectItems.stream().collect(Collectors.groupingBy(CorpSubjectItem::getSubjectType));
                if (listMap.size() == 3) {
                    excel.setIsComplete("完整");
                } else {
                    excel.setIsComplete("缺少");
                }
            }
        }
        Client client = clientService.getById(clientId);
        if (Objects.nonNull(client) && ClientType.NORMAL.name().equals(client.getClientType())) {
            excel.setCode(client.getCertNumber());
        }
    }
}
