package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseCheckRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardClientAfterLeaseStatisticsRSP;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanProcessStatusEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanStatusEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckPlanTypeEnum;
import cn.zswltech.mithras.afterlease.domain.enums.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.NewAfterLeaseCheckPlanBaseMapper;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.dashboard.DashboardClientAfterLeaseCheckQuery;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @author yangxiong
 * @date 2024/6/18/14:10
 * @description
 */
@Slf4j
@Service
public class DashboardClientAfterLeaseService {

    @Resource
    private NewAfterLeaseCheckPlanBaseMapper afterLeaseCheckPlanBaseMapper;

    public List<DashboardClientAfterLeaseStatisticsRSP> statisticsList() {
        DashboardClientAfterLeaseCheckREQ pageReq = new DashboardClientAfterLeaseCheckREQ();
        pageReq.setPage(1);
        pageReq.setPageSize(5000);
        List<DashboardClientAfterLeaseCheckRSP> list = this.afterLeaseCheckList(pageReq).getList();
        DashboardClientAfterLeaseStatisticsRSP rsp = new DashboardClientAfterLeaseStatisticsRSP();
        rsp.setGroup(DashboardCardGroupEnum.CLIENT_AFTER_LEASE.getDisplay());
        rsp.setGroupCode(DashboardCardGroupEnum.CLIENT_AFTER_LEASE.name());
        if (ObjectUtil.isEmpty(list)) {
            rsp.setLeftCount(0);
            rsp.setRightCount(0);
            return Collections.singletonList(rsp);
        }

        List<DashboardClientAfterLeaseCheckRSP> left = list.stream().filter(o -> AfterLeaseCheckPlanStatusEnum.NEW.name().equals(o.getCheckPlanStatus()))
                .collect(Collectors.toList());
        List<DashboardClientAfterLeaseCheckRSP> right = list.stream().filter(o -> AfterLeaseCheckPlanStatusEnum.CHECKING.name().equals(o.getCheckPlanStatus()))
                .collect(Collectors.toList());
        rsp.setLeftCount(left.size());
        rsp.setRightCount(right.size());
        return Collections.singletonList(rsp);
    }

    public PageR<DashboardClientAfterLeaseCheckRSP> afterLeaseCheckList(DashboardClientAfterLeaseCheckREQ req) {
        AccountVO currentUser = AccountUtil.getLoginInfo();
        if (ObjectUtil.isNull(currentUser)) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        //展示计划状态为【检查中】&审批状态为【变更审批通过】或【新建审批通过】的数据
        Page<DashboardClientAfterLeaseCheckRSP> objectPage = new Page<>();
        objectPage.setCurrent(req.getPage());
        objectPage.setSize(req.getPageSize());
        DashboardClientAfterLeaseCheckQuery query = BeanUtil.copyProperties(req, DashboardClientAfterLeaseCheckQuery.class);
        fillAuthQuery(query, currentUser);
        Page<DashboardClientAfterLeaseCheckRSP> checkPlanBasePage = afterLeaseCheckPlanBaseMapper.pageList(objectPage, query);
        if (ObjectUtil.isEmpty(checkPlanBasePage.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        //如果存在数据，需要填充对应的display
        Map<Long, String> deptId2NameMap = getBean(Id2NameService.class).deptId2Name(checkPlanBasePage.getRecords().stream().map(DashboardClientAfterLeaseCheckRSP::getBizDeptId).collect(Collectors.toList()));
        Map<Long, String> user2NameMap = getBean(Id2NameService.class).sysUserId2Name(checkPlanBasePage.getRecords().stream().map(DashboardClientAfterLeaseCheckRSP::getProjSponsorUserId).collect(Collectors.toList()));
        checkPlanBasePage.getRecords().forEach(one -> {
            one.setReportProcessStatusDisplay(Optional.ofNullable(AfterLeaseCheckPlanProcessStatusEnum.of(one.getReportProcessStatusCode())).map(AfterLeaseCheckPlanProcessStatusEnum::getDisplay).orElse(""));
            one.setCheckWayDisplay(Optional.ofNullable(AfterLeaseCheckWayEnum.find(one.getCheckWayCode())).map(AfterLeaseCheckWayEnum::getDisplay).orElse(""));
            one.setCheckPlanTypeDisplay(Optional.ofNullable(AfterLeaseCheckPlanTypeEnum.of(one.getPlanType())).map(AfterLeaseCheckPlanTypeEnum::getDisplay).orElse(""));
            one.setCheckPlanStatusDisplay(Optional.ofNullable(AfterLeaseCheckPlanStatusEnum.find(one.getCheckPlanStatus())).map(AfterLeaseCheckPlanStatusEnum::getDisplay).orElse(""));
            one.setProjSponsorUserName(user2NameMap.get(one.getProjSponsorUserId()));
            one.setBizDeptName(deptId2NameMap.get(one.getBizDeptId()));
        });
        return PageR.of(checkPlanBasePage.getRecords(), checkPlanBasePage.getTotal(), req.getPage(), req.getPageSize());
    }

    private void fillAuthQuery(DashboardClientAfterLeaseCheckQuery query, AccountVO currentUser) {
        List<Long> viewDeptIds = getBean(SysUserService.class).canViewDeptIds(currentUser);
        if (viewDeptIds == null) {
            return;
        }
        if (viewDeptIds.isEmpty()) {
            query.setAuthCurrentUserId(currentUser.getId());
        } else {
            query.setAuthBizDeptIds(viewDeptIds);
        }
    }
}
