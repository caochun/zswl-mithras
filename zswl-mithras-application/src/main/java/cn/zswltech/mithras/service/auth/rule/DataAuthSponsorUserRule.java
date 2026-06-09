package cn.zswltech.mithras.service.auth.rule;


import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.service.auth.checker.AuthHelper;
import cn.zswltech.mithras.service.auth.DataAuthBusinessModule;
import cn.zswltech.mithras.service.enums.JobEnum;
import cn.zswltech.mithras.filingmaterials.domain.enums.FilingMaterialsBusinessTypeEnum;
import cn.zswltech.mithras.service.mapper.model.SponsorField;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.service.service.materialsfile.FileService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 判断当前操作数据所属主表的创建人 与 当前登陆用户是否一致
 *
 * @author wangchuanhao
 * @date 2022/7/21 11:52 PM
 */
@Component
@Slf4j
public class DataAuthSponsorUserRule {

    @Resource
    private AuthHelper authHelper;
    @Resource
    private FileService fileService;

    public void check(DataAuthBusinessModule businessModule, Long mainId) {
        if (BusinessModuleEnum.CONTARCT_DEPOSIT.equals(businessModule)) {
            return;
        }
        if (BusinessModuleEnum.CREDIT_REPORT.equals(businessModule)) {
            // 暂时啥也不干
            return;
        }
        if(BusinessModuleEnum.CREDIT_REPORT_SELECT.equals(businessModule)) {
            return;
        }

        BaseMapper mainTableMapper = SpringContextHolder.getBean(businessModule.getMainMapperClass());
        Object mainObject = mainTableMapper.selectById(mainId);
        if (Objects.isNull(mainObject)) {
            throw new AuthCheckException("主表数据不存在");
        }

        // 该模块主表自己没有主办字段 要从别的模块取 获取实际要取主办值的模块对象
        if (StringUtils.isNotBlank(businessModule.getSponsorModule())) {
            mainObject = authHelper.getAuthObj(businessModule, mainObject);
        }

        String field = "createBy";
        SponsorField annotation = mainObject.getClass().getAnnotation(SponsorField.class);
        if (null != annotation) {
            field = annotation.value();
        }
        Long maintainerId = (Long) ReflectUtil.getFieldValue(mainObject, field);
        if (null == maintainerId) {
            return;
        }
        // 如果当前用户是运营经办人并且操作的是合同相关文件，则不校验
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (BusinessModuleEnum.CONTRACT.equals(businessModule) || BusinessModuleEnum.PUBLIC_INFO.equals(businessModule)) {
            Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
            if (allOperateUserJob.containsKey(loginInfo.getId())) {
                return;
            }
            Map<Long, List<String>> projectManagerJob = fileService.getAllOperateUserJob(JobEnum.projmanager);
            if (projectManagerJob.containsKey(loginInfo.getId())) {
                return;
            }
        }

        if(FilingMaterialsBusinessTypeEnum.getBusinessTypeAll().contains(businessModule.name())
                || Objects.equals(BusinessModuleEnum.FUND_FILING.name(),businessModule.name())
                || Objects.equals(BusinessModuleEnum.AFTER_LEASING_FILING.name(),businessModule.name())
                || Objects.equals(BusinessModuleEnum.OTHER_FILING.name(),businessModule.name())){
            return;
        }

        if (!Objects.equals(maintainerId, loginInfo.getId())) {
            throw new AuthCheckException("非数据主办，不支持该种操作");
        }
    }



    public void appCheck(DataAuthBusinessModule businessModule, Long mainId, Long createdBy) {
        if (BusinessModuleEnum.CREDIT_REPORT.equals(businessModule)) {
            // 暂时啥也不干
            return;
        }

        if(BusinessModuleEnum.CREDIT_REPORT_SELECT.equals(businessModule)) {
            return;
        }

        BaseMapper mainTableMapper = SpringContextHolder.getBean(businessModule.getMainMapperClass());
        Object mainObject = mainTableMapper.selectById(mainId);
        if (Objects.isNull(mainObject)) {
            throw new AuthCheckException("主表数据不存在");
        }

        // 该模块主表自己没有主办字段 要从别的模块取 获取实际要取主办值的模块对象
        if (StringUtils.isNotBlank(businessModule.getSponsorModule())) {
            mainObject = authHelper.getAuthObj(businessModule, mainObject);
        }

        String field = "createBy";
        SponsorField annotation = mainObject.getClass().getAnnotation(SponsorField.class);
        if (null != annotation) {
            field = annotation.value();
        }
        Long maintainerId = (Long) ReflectUtil.getFieldValue(mainObject, field);
        if (null == maintainerId) {
            return;
        }
        // 如果当前用户是运营经办人并且操作的是合同相关文件，则不校验
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if ((createdBy != null && loginInfo == null)) {
            loginInfo = new AccountVO();
            loginInfo.setId(createdBy);
        } else if (createdBy != null && loginInfo != null && !createdBy.equals(loginInfo.getId())) {
            loginInfo.setId(createdBy);
        }
        if (BusinessModuleEnum.CONTRACT.equals(businessModule) || BusinessModuleEnum.PUBLIC_INFO.equals(businessModule)) {
            Map<Long, List<String>> allOperateUserJob = fileService.getAllOperateUserJob(JobEnum.yunYingGuanLi);
            if (allOperateUserJob.containsKey(loginInfo.getId())) {
                return;
            }
            Map<Long, List<String>> projectManagerJob = fileService.getAllOperateUserJob(JobEnum.projmanager);
            if (projectManagerJob.containsKey(loginInfo.getId())) {
                return;
            }
        }

        if (!Objects.equals(maintainerId, loginInfo.getId())) {
            throw new AuthCheckException("非数据主办，不支持该种操作");
        }
    }

}
