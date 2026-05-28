/*
package cn.zswltech.mithras.service.service.creditreport.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.mithras.api.AllSelectApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.creditreport.CreditReportListDTO;
import cn.zswltech.mithras.dto.creditreport.CreditReportListREQ;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.service.mapper.creditreport.CreditReportItemDOMapper;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportDO;
import cn.zswltech.mithras.service.mapper.model.creditreport.CreditReportItemDO;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.creditreport.CreditReportItemService;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreditReportItemServiceImpl implements CreditReportItemService {

*/
/*    @Resource
    private CreditReportItemDOMapper creditReportItemDOMapper;*//*


    @Resource
    private ClientService clientService;

    @Resource
    private Id2NameService id2NameService;

    @Resource
    private AllSelectApi allSelectApi;

    @Override
    public PageR<CreditReportListDTO> selectList(CreditReportListREQ req) {
        req.setSearchTimeFrom(DateUtil.startOfDay(req.getCreateFrom()));
        req.setSearchTimeTo(DateUtil.endOfDay(req.getCreateTo()));

        // 需要按照不同登陆角色处理
        Set<Long> targetClientIds = new HashSet<>();
        // 默认先填一个不可能的值，如果后续逻辑没有往里填充说明没有可看的客户
        targetClientIds.add(-100L);
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        List<UserOrgJobDO> userOrgJobList = SpringUtil.getBean(UserOrgJobDOMapper.class).selectJobCodeByUserId(Collections.singletonList(currentUserId));
        if (CollectionUtil.isEmpty(userOrgJobList)) {
            return new PageR<>();
        }
        // 过滤出业务部门的岗位
        List<OrgDO> orgList = SpringUtil.getBean(SysUserService.class).listBizDept();
        Set<Long> bizDeptIds = orgList.stream().map(OrgDO::getId).collect(Collectors.toSet());
        userOrgJobList.removeIf(e -> !bizDeptIds.contains(e.getOrgId()));
        Map<String, List<UserOrgJobDO>> userOrgMap = userOrgJobList.stream().collect(Collectors.groupingBy(UserOrgJobDO::getJobCode));
        List<UserOrgJobDO> projmanagerList = userOrgMap.get(JobEnum.projmanager.name());
        List<UserOrgJobDO> businessheadList = userOrgMap.get(JobEnum.businesshead.name());
        List<UserOrgJobDO> leaderinchargeList = userOrgMap.get(JobEnum.leaderincharge.name());
        List<CreditReportDO> credits = new ArrayList<>();
        Page<CreditReportDO> page = new Page<>(req.getPage(), req.getPageSize());
        // 中后台看所有
        if (CollectionUtil.isEmpty(projmanagerList) && CollectionUtil.isEmpty(businessheadList) && CollectionUtil.isEmpty(leaderinchargeList)) {
            credits = creditReportItemDOMapper.list(page, req);
        } else {
            // 项目经理
            if (CollectionUtil.isNotEmpty(projmanagerList)) {
                Set<Long> projmanagerTargetClientIds = clientService.findTargetClientIdsByUserId(currentUserId);
                if (CollectionUtil.isNotEmpty(projmanagerTargetClientIds)) {
                    targetClientIds.addAll(projmanagerTargetClientIds);
                }
            }
            // 业务负责人
            if (CollectionUtil.isNotEmpty(businessheadList)) {
                List<Long> deptIds = businessheadList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
                Set<Long> businessheadTargetClientIds = clientService.findTargetClientIdsByDeptIds(deptIds);
                if (CollectionUtil.isNotEmpty(businessheadTargetClientIds)) {
                    targetClientIds.addAll(businessheadTargetClientIds);
                }
            }
            // 分管领导
            if (CollectionUtil.isNotEmpty(leaderinchargeList)) {
                List<Long> deptIds = leaderinchargeList.stream().map(UserOrgJobDO::getOrgId).collect(Collectors.toList());
                Set<Long> leaderinchargeTargetClientIds = clientService.findTargetClientIdsByDeptIds(deptIds);
                if (CollectionUtil.isNotEmpty(leaderinchargeTargetClientIds)) {
                    targetClientIds.addAll(leaderinchargeTargetClientIds);
                }
            }
            // 填充条件
            req.setTargetClientIds(targetClientIds);
            credits = creditReportItemDOMapper.list(page, req);
        }
        List<CreditReportListDTO> creditReportListDTOs = convertCreditReport(credits);
        return PageR.of(creditReportListDTOs, page.getTotal(), page.getCurrent(), page.getSize());
    }


    private List<CreditReportListDTO> convertCreditReport(List<CreditReportDO> credits) {
        if (CollectionUtils.isEmpty(credits)) {
            return Collections.emptyList();
        }

        //提取用户和部门 ID 集合
        List<Long> userIdList = credits.stream()
                .map(CreditReportDO::getApplyUser)
                .distinct()
                .collect(Collectors.toList());

        Set<Long> deptIdList = credits.stream()
                .map(CreditReportDO::getApplyOrg)
                .collect(Collectors.toSet());

        //批量获取用户名和部门名
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIdList);
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIdList);
        Map<String, List<SelectRSP>> map = allSelectApi.allSelect().getData();
        List<SelectRSP> searchGoalEnum = map.get("searchGoalEnum");
        List<SelectRSP> applyStatusEnum = map.get("applyStatusEnum");
        List<SelectRSP> searchStatusEnum = map.get("searchStatusEnum");
        Map<String, String> searchGoalMap = searchGoalEnum.stream().collect(Collectors.toMap(SelectRSP::getValue, SelectRSP::getLabel));
        Map<String, String> applyStatusMap = applyStatusEnum.stream().collect(Collectors.toMap(SelectRSP::getValue, SelectRSP::getLabel));
        Map<String, String> searchStatusMap = searchStatusEnum.stream().collect(Collectors.toMap(SelectRSP::getValue, SelectRSP::getLabel));

        //最终转换为 DTO 列表
        return credits.stream().map(item -> {

            //构造 DTO
            CreditReportListDTO listDTO = new CreditReportListDTO();
            BeanUtils.copyProperties(item, listDTO);

            //设置用户和部门名称
            listDTO.setApplyUserName(userId2Name.getOrDefault(item.getApplyUser(), "未知用户"));
            listDTO.setApplyOrgName(deptId2Name.getOrDefault(item.getApplyOrg(), "未知部门"));
            listDTO.setSelectGoal(searchGoalMap.get(item.getSelectGoal()));
            listDTO.setApplyStatus(applyStatusMap.get(item.getApplyStatus()));
            listDTO.setSelectStatus(searchStatusMap.get(item.getSelectStatus()));
            return listDTO;
        }).collect(Collectors.toList());
    }
}
*/
