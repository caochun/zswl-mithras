package cn.zswltech.mithras.others.数据订正;

import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.ProjEstablishBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.lib.contract.ContractBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishBaseInfoLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewBaseInfoLibService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.web.MithrasApplication;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.enums.JobEnum.businesshead;
import static cn.zswltech.mithras.service.enums.JobEnum.leaderincharge;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yibin
 * 改订正用于
 * 部门修改了名称，或者是合并了部门改了名称，之前的部门已不再使用
 */
@Slf4j
@ActiveProfiles("pre")
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class 项目所属部门订正 {

    //高端装备 部门负责人 赵佳萍 分管领导 鲁素萍
    //2、机械加工、化工建材部门改成 高端装备业务部`

    String srcDeptName = "冷链物流团队";
    String targetDeptName = "交通物流业务部";
    String targetDeptLeaderName = "陈红升";
    String targetDeptDivisionLeaderName = "鲁素萍";


    /*String srcDeptName = "机械和加工业务部";
    //    String srcDeptName = "化工建材业务部";
    String targetDeptName = "高端装备业务部";
    String targetDeptLeaderName = "赵佳萍";
    String targetDeptDivisionLeaderName = "鲁素萍";*/

    Long srcDeptId;

    Long targetDeptId;
    Long targetDeptLeaderId;
    Long targetDeptDivisionLeaderId;


    @Test
    public void fix() {
        //找出源部门id
        OrgDO queryDO = new OrgDO();
        queryDO.setName(srcDeptName);
        srcDeptId = getBean(OrgService.class).selectOne(queryDO).getId();
        //找出目标部门id
        queryDO.setName(targetDeptName);
        targetDeptId = getBean(OrgService.class).selectOne(queryDO).getId();
        //找出目标部门负责人和分管领导
        targetDeptLeaderId = getBean(SysUserService.class).getUserIdByOrgJob(targetDeptId, businesshead.name());
        targetDeptDivisionLeaderId = getBean(SysUserService.class).getUserIdByOrgJob(targetDeptId, leaderincharge.name());
        //判断下目标领导是否正确
        Assertions.assertEquals(getBean(UserService.class).selectByPrimaryKey(targetDeptLeaderId).getUserName(), targetDeptLeaderName);
        Assertions.assertEquals(getBean(UserService.class).selectByPrimaryKey(targetDeptDivisionLeaderId).getUserName(), targetDeptDivisionLeaderName);
        //
        合同变更();
        评审变更();
        立项变更();
        客户变更();

    }

    private void 合同变更() {
        List<ContractBaseInfo> list = getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getBizDeptId, srcDeptId));
        log.info("合同变更：共{}条", list.size());
        List<ContractBaseInfo> toUpdateList = new ArrayList<>(list.size());
        for (ContractBaseInfo contractBaseInfo : list) {
            ContractBaseInfo toUpdate = new ContractBaseInfo();
            toUpdate.setId(contractBaseInfo.getId());
            toUpdate.setBizDeptId(targetDeptId);
            toUpdate.setBizDeptLeaderId(targetDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(targetDeptDivisionLeaderId);
            toUpdateList.add(toUpdate);
        }
        getBean(ContractBaseInfoService.class).updateBatchById(toUpdateList);
        //版本表变更
        List<ContractBaseInfoLib> libList = getBean(ContractBaseInfoLibService.class).list(Wrappers.<ContractBaseInfoLib>lambdaQuery()
                .eq(ContractBaseInfoLib::getVersionType, 1)
                .eq(ContractBaseInfoLib::getBizDeptId, srcDeptId));
        Map<Long, List<ContractBaseInfoLib>> map = libList.stream().collect(Collectors.groupingBy(ContractBaseInfoLib::getOriginId));
        log.info("合同版本表变更：共{}条", map.size());
        List<ContractBaseInfoLib> toUpdateLibList = new ArrayList<>(map.size());
        for (Map.Entry<Long, List<ContractBaseInfoLib>> entry : map.entrySet()) {
            List<ContractBaseInfoLib> valueList = entry.getValue();
            valueList.sort(Comparator.comparing(ContractBaseInfoLib::getId));
            ContractBaseInfoLib contractBaseInfoLib = valueList.get(valueList.size() - 1);
            ContractBaseInfoLib toUpdate = new ContractBaseInfoLib();
            toUpdate.setId(contractBaseInfoLib.getId());
            toUpdate.setBizDeptId(targetDeptId);
            toUpdate.setBizDeptLeaderId(targetDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(targetDeptDivisionLeaderId);
            toUpdateLibList.add(toUpdate);
        }
        getBean(ContractBaseInfoLibService.class).updateBatchById(toUpdateLibList);
    }

    private void 评审变更() {
        List<ProjReviewBaseInfo> list = getBean(ProjReviewBaseInfoService.class).list(Wrappers.<ProjReviewBaseInfo>lambdaQuery().eq(ProjReviewBaseInfo::getBizDeptId, srcDeptId));
        log.info("评审变更：共{}条", list.size());
        List<ProjReviewBaseInfo> toUpdateList = new ArrayList<>(list.size());
        for (ProjReviewBaseInfo baseInfo : list) {
            ProjReviewBaseInfo toUpdate = new ProjReviewBaseInfo();
            toUpdate.setId(baseInfo.getId());
            toUpdate.setBizDeptId(targetDeptId);
            toUpdate.setBizDeptLeaderId(targetDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(targetDeptDivisionLeaderId);
            toUpdateList.add(toUpdate);
        }
        getBean(ProjReviewBaseInfoService.class).updateBatchById(toUpdateList);
        //版本表变更
        List<ProjReviewBaseInfoLib> libList = getBean(ProjReviewBaseInfoLibService.class).list(Wrappers.<ProjReviewBaseInfoLib>lambdaQuery()
                .eq(ProjReviewBaseInfoLib::getVersionType, 1)
                .eq(ProjReviewBaseInfoLib::getBizDeptId, srcDeptId));
        Map<Long, List<ProjReviewBaseInfoLib>> map = libList.stream().collect(Collectors.groupingBy(ProjReviewBaseInfoLib::getOriginId));
        log.info("合同版本表变更：共{}条", map.size());
        List<ProjReviewBaseInfoLib> toUpdateLibList = new ArrayList<>(map.size());
        for (Map.Entry<Long, List<ProjReviewBaseInfoLib>> entry : map.entrySet()) {
            List<ProjReviewBaseInfoLib> valueList = entry.getValue();
            valueList.sort(Comparator.comparing(ProjReviewBaseInfoLib::getId));
            ProjReviewBaseInfoLib baseInfoLib = valueList.get(valueList.size() - 1);
            ProjReviewBaseInfoLib toUpdate = new ProjReviewBaseInfoLib();
            toUpdate.setId(baseInfoLib.getId());
            toUpdate.setBizDeptId(targetDeptId);
            toUpdate.setBizDeptLeaderId(targetDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(targetDeptDivisionLeaderId);
            toUpdateLibList.add(toUpdate);
        }
        getBean(ProjReviewBaseInfoLibService.class).updateBatchById(toUpdateLibList);

    }

    private void 立项变更() {
        List<ProjEstablishBaseInfo> list = getBean(ProjEstablishBaseInfoService.class).list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery().eq(ProjEstablishBaseInfo::getBizDeptId, srcDeptId));
        log.info("评审变更：共{}条", list.size());
        List<ProjEstablishBaseInfo> toUpdateList = new ArrayList<>(list.size());
        for (ProjEstablishBaseInfo baseInfo : list) {
            ProjEstablishBaseInfo toUpdate = new ProjEstablishBaseInfo();
            toUpdate.setId(baseInfo.getId());
            toUpdate.setBizDeptId(targetDeptId);
            toUpdate.setBizDeptLeaderId(targetDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(targetDeptDivisionLeaderId);
            toUpdateList.add(toUpdate);
        }
        getBean(ProjEstablishBaseInfoService.class).updateBatchById(toUpdateList);
        //版本表变更
        List<ProjEstablishBaseInfoLib> libList = getBean(ProjEstablishBaseInfoLibService.class).list(Wrappers.<ProjEstablishBaseInfoLib>lambdaQuery()
                .eq(ProjEstablishBaseInfoLib::getVersionType, 1)
                .eq(ProjEstablishBaseInfoLib::getBizDeptId, srcDeptId));
        Map<Long, List<ProjEstablishBaseInfoLib>> map = libList.stream().collect(Collectors.groupingBy(ProjEstablishBaseInfoLib::getOriginId));
        log.info("合同版本表变更：共{}条", map.size());
        List<ProjEstablishBaseInfoLib> toUpdateLibList = new ArrayList<>(map.size());
        for (Map.Entry<Long, List<ProjEstablishBaseInfoLib>> entry : map.entrySet()) {
            List<ProjEstablishBaseInfoLib> valueList = entry.getValue();
            valueList.sort(Comparator.comparing(ProjEstablishBaseInfoLib::getId));
            ProjEstablishBaseInfoLib baseInfoLib = valueList.get(valueList.size() - 1);
            ProjEstablishBaseInfoLib toUpdate = new ProjEstablishBaseInfoLib();
            toUpdate.setId(baseInfoLib.getId());
            toUpdate.setBizDeptId(targetDeptId);
            toUpdate.setBizDeptLeaderId(targetDeptLeaderId);
            toUpdate.setBizDivisionLeaderId(targetDeptDivisionLeaderId);
            toUpdateLibList.add(toUpdate);
        }
        getBean(ProjEstablishBaseInfoLibService.class).updateBatchById(toUpdateLibList);

    }

    private void 客户变更() {
        List<Client> list = getBean(ClientService.class).list(Wrappers.<Client>lambdaQuery().eq(Client::getBelongDeptId, srcDeptId));
        log.info("客户变更：共{}条", list.size());
        List<Client> toUpdateList = new ArrayList<>(list.size());
        for (Client baseInfo : list) {
            Client toUpdate = new Client();
            toUpdate.setId(baseInfo.getId());
            toUpdate.setBelongDeptId(targetDeptId);
            toUpdateList.add(toUpdate);
        }
        getBean(ClientService.class).updateBatchById(toUpdateList);
    }
}
