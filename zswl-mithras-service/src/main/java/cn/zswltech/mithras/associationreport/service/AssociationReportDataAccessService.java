package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessAddREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessListREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessModifyREQ;
import cn.zswltech.mithras.dto.associationreport.AssociationReportDataAccessRemoveREQ;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationReportDataAccessMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReportDataAccess;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 金融局报表数据权限
* @author hspcadmin
* @date 2025-09-24
*/
@Service
public class AssociationReportDataAccessService extends ServiceImpl<AssociationReportDataAccessMapper, AssociationReportDataAccess> {

    @Resource
    private AssociationReportDataAccessMapper associationReportDataAccessMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationReportDataAccessAddREQ req) {
        AssociationReportDataAccess info = BeanUtil.copyProperties(req, AssociationReportDataAccess.class);
        associationReportDataAccessMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationReportDataAccessModifyREQ req) {
        AssociationReportDataAccess originalInfo = associationReportDataAccessMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationReportDataAccess info = BeanUtil.copyProperties(req, AssociationReportDataAccess.class);
        associationReportDataAccessMapper.updateById(info);
    }

    public Page<AssociationReportDataAccess> list(AssociationReportDataAccessListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationReportDataAccessRemoveREQ req) {
        AssociationReportDataAccess originalInfo = associationReportDataAccessMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationReportDataAccessMapper.deleteById(req.getId());
    }

    //获取金融局报送可以新增的报表类型
    public Set<String> getCurrentUserAccessAddReportCategoryCodes() {
        List<String> userRoles = SpringUtil.getBean(SysUserService.class).getCurrentUserRoles();
        if (CollectionUtil.isEmpty(userRoles)) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<AssociationReportDataAccess> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.in(AssociationReportDataAccess::getRoleCode, userRoles);
        List<AssociationReportDataAccess> associationReportDataAccessList = this.list(conditionQuery);
        if (CollectionUtil.isEmpty(associationReportDataAccessList)) {
            return Collections.emptySet();
        }
        Set<String> addReportCategoryCodes = associationReportDataAccessList.stream().map(AssociationReportDataAccess::getAddReportCategoryCodes).filter(StringUtils::isNotBlank).map(e -> e.split(",")).flatMap(Arrays::stream).collect(Collectors.toSet());
        return addReportCategoryCodes;
    }

    //获取金融局报送可以查询的报表类型
    public Set<String> getCurrentUserAccessQueryReportCategoryCodes() {
        List<String> userRoles = SpringUtil.getBean(SysUserService.class).getCurrentUserRoles();
        if (CollectionUtil.isEmpty(userRoles)) {
            return Collections.emptySet();
        }
        LambdaQueryWrapper<AssociationReportDataAccess> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.in(AssociationReportDataAccess::getRoleCode, userRoles);
        List<AssociationReportDataAccess> associationReportDataAccessList = this.list(conditionQuery);
        if (CollectionUtil.isEmpty(associationReportDataAccessList)) {
            return Collections.emptySet();
        }
        Set<String> queryReportCategoryCodes = associationReportDataAccessList.stream().map(AssociationReportDataAccess::getQueryReportCategoryCodes).filter(StringUtils::isNotBlank).map(e -> e.split(",")).flatMap(Arrays::stream).collect(Collectors.toSet());
        return queryReportCategoryCodes;
    }

    //获取金融局报送提交的工作流
    public List<AssociationReportDataAccess> getFlowByCategoryCodes(List<String> reportCategoryCodeList) {
        if (CollectionUtil.isEmpty(reportCategoryCodeList)) {
            return Collections.emptyList();
        }
        List<AssociationReportDataAccess> dataAccessList = new ArrayList<>();
        List<AssociationReportDataAccess> tempDataAccessList = new ArrayList<>();
        reportCategoryCodeList.forEach(item->{
            LambdaQueryWrapper<AssociationReportDataAccess> conditionQuery = Wrappers.lambdaQuery();
            conditionQuery.like(AssociationReportDataAccess::getAddReportCategoryCodes, item);
            List<AssociationReportDataAccess> associationReportDataAccessList = this.list(conditionQuery);
            if (! CollectionUtil.isEmpty(associationReportDataAccessList)) {
                tempDataAccessList.addAll(associationReportDataAccessList);
            }
        });
        if(tempDataAccessList.size()>0){
            Set<Long> flowIds = tempDataAccessList.stream().filter(item->StringUtils.isNotBlank(item.getFlowName())).map(AssociationReportDataAccess::getId).collect(Collectors.toSet());
            if (CollUtil.isNotEmpty(flowIds)) {
                dataAccessList = this.listByIds(flowIds);
            }
        }
        return dataAccessList;
    }


}