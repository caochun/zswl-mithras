package cn.zswltech.mithras.others.service.metric;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.contract.core.application.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/7/14 14:36
 */
public class ProjectSponsorExportTest extends ApplicationTest {

    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private Id2NameService id2NameService;

    @Test
    public void test() {
        List<ContractBaseInfo> contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().in(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name()));


        List<Long> projReviewIds = contracts.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList());
        Map<Long, ProjReviewBaseInfo> projReviewMap = projReviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery().in(ProjReviewBaseInfo::getId, projReviewIds)).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, v -> v));

        List<Long> sponsorUserIds = projReviewMap.values().stream().map(ProjReviewBaseInfo::getProjSponsorUserId)
                .collect(Collectors.toList());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(sponsorUserIds);

        List<ProjectRsp> res = new ArrayList<>();
        for (ContractBaseInfo contract : contracts) {
            BigDecimal remainingPrincipal = remainingPrincipalServiceImpl.remainingPrincipal(contract.getId(), LocalDate.now());
            if (remainingPrincipal.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            } else {
                ProjReviewBaseInfo projReviewBaseInfo = projReviewMap.get(contract.getProjReviewId());
                ProjectRsp projectRsp = new ProjectRsp();
                projectRsp.setProjectName(projReviewBaseInfo.getProjName());
                projectRsp.setSponsorName(userId2Name.get(projReviewBaseInfo.getProjSponsorUserId()));
                res.add(projectRsp);
            }
        }

        String s = JSON.toJSONString(res);
        System.out.println(s);
    }

    @Data
    static class ProjectRsp {
        private String projectName;

        private String sponsorName;
    }

}
