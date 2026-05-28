package cn.zswltech.mithras.blackgray.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessModifyREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayBreakBusinessRemoveREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayBreakBusinessListRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayBreakBusinessMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayLibraryMapper;
import cn.zswltech.mithras.blackgray.model.BlackGrayBreakBusiness;
import cn.zswltech.mithras.blackgray.service.BlackGrayBreakBusinessService;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.SysUserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
* @description black_gray_break_business
* @author
* @date 2023-11-28
*/
@Service
public class BlackGrayBreakBusinessServiceImpl implements BlackGrayBreakBusinessService {

    @Resource
    private BlackGrayBreakBusinessMapper blackGrayBreakBusinessMapper;
    @Resource
    private BlackGrayLibraryMapper blackGrayLibraryMapper;
    @Resource
    private SysUserService sysUserService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long add(BlackGrayBreakBusinessAddREQ req) {
        //填充部门
        OrgDO rootOrg = sysUserService.getUserDept();
        if(rootOrg == null){
            throw new MithrasException("当前用户无机构");
        }
        Date date = new Date();
        BlackGrayBreakBusiness info = BeanUtil.copyProperties(req, BlackGrayBreakBusiness.class, "applyFileKeys");
        List<OrgDO> deptDos = sysUserService.getUserDeptList();
        if(CollectionUtil.isNotEmpty(deptDos)){
            info.setApplyDept(deptDos.get(0).getCode());
        }
        info.setApplyOrganization(rootOrg.getCode());
        info.setCreateBy(AccountUtil.getLoginInfo().getId());
        info.setUpdateBy(AccountUtil.getLoginInfo().getId());
        info.setCreateTime(date);
        info.setUpdateTime(date);
        info.setAuditStatus((int) AuditStatusEnum.WAIT.getCode());
        info.setApplyFileKeys(JSONUtil.toJsonStr(req.getApplyFileKeys()));
        blackGrayBreakBusinessMapper.insert(info);
        return info.getId();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(BlackGrayBreakBusinessModifyREQ req) {
        BlackGrayBreakBusiness originalInfo = blackGrayBreakBusinessMapper.selectByPrimaryKey(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException("记录不存在");
        }
        BlackGrayBreakBusiness info = BeanUtil.copyProperties(req, BlackGrayBreakBusiness.class, "applyFileKeys");
        info.setApplyFileKeys(JSONUtil.toJsonStr(req.getApplyFileKeys()));
        info.setUpdateTime(new Date());
        blackGrayBreakBusinessMapper.updateByPrimaryKeySelective(info);
    }

    @Override
    public PageR<BlackGrayBreakBusinessListRSP> list(BlackGrayBreakBusinessListREQ req) {
        Example example = getListExample(req);
        example.orderBy(" createTime ").desc();
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayBreakBusiness> blackGrayBreakBusinesses = blackGrayBreakBusinessMapper.selectByExample(example);
        List<BlackGrayBreakBusinessListRSP> blackGrayBreakBusinessListRSPS = BeanUtil.copyToList(blackGrayBreakBusinesses, BlackGrayBreakBusinessListRSP.class);
        PageInfo<BlackGrayBreakBusiness> blackGrayBreakBusinessListRSPPageInfo = new PageInfo<>(blackGrayBreakBusinesses);
        //填充审批数据
       /* Map<Long, String> currentOperator = getCurrentOperator(blackGrayBreakBusinesses.stream().map(BlackGrayBreakBusiness::getId).collect(Collectors.toList()));
        blackGrayBreakBusinessListRSPS.forEach(blackGrayBreakBusiness -> {
            String s = currentOperator.get(blackGrayBreakBusiness.getId());
            blackGrayBreakBusiness.setCurrentOperator(s == null ? gruulAuthService.getAccountById(blackGrayBreakBusiness.getCreateBy()) : s);
        });*/
        return PageR.of(blackGrayBreakBusinessListRSPS, blackGrayBreakBusinessListRSPPageInfo.getTotal());
    }

    /*private Map<Long, String> getCurrentOperator(List<Long> bizIds){
        List<AuditTask> auditTasksByBizIds = blackGrayBreakBusinessAuditService.getAuditTasksByBizIds(AuditBizTypeEnum.BLACK_GRAY_BUSINESS_BREAK.getType(), bizIds);
        if(CollectionUtil.isEmpty(auditTasksByBizIds)){
            return MapUtil.empty();
        }
        return auditTasksByBizIds.stream().collect(Collectors.toMap(AuditTask::getBizId, AuditTask::getCurrentOperator, (a, b) -> b));
    }*/

    private Example getListExample(BlackGrayBreakBusinessListREQ req) {
        OrgDO rootOrg = sysUserService.getUserDept();
        Example example = new Example(BlackGrayBreakBusiness.class);
        Example.Criteria criteria = example.createCriteria();
        if(req.getAuditStatus() != null){
            criteria.andEqualTo(BlackGrayBreakBusiness.AUDIT_STATUS, req.getAuditStatus());
        }
        if(req.getEnterpriseName() != null){
            criteria.andLike(BlackGrayBreakBusiness.ENTERPRISE_NAME, "%" + req.getEnterpriseName() + "%");
        }
        if(req.getProposedBusinessType() != null){
            criteria.andEqualTo(BlackGrayBreakBusiness.PROPOSED_BUSINESS_TYPE, req.getProposedBusinessType());
        }
        if(req.getApplyTimeFrom() != null){
            criteria.andBetween(BlackGrayBreakBusiness.CREATE_TIME, req.getApplyTimeFrom(), req.getApplyTimeTo());
        }
        if(req.getBlackGrayType() != null){
            criteria.andEqualTo(BlackGrayBreakBusiness.BLACK_GRAY_TYPE, req.getBlackGrayType());
        }
        return example;
    }

    @Override
    public BlackGrayBreakBusinessDetailRSP detail(Long id) {
        BlackGrayBreakBusiness blackGrayBreakBusiness = blackGrayBreakBusinessMapper.selectByPrimaryKey(id);
        if(ObjectUtil.isEmpty(blackGrayBreakBusiness)){
            throw new MithrasException("无此记录数据");
        }
        BlackGrayBreakBusinessDetailRSP blackGrayBreakBusinessDetailRSP = BeanUtil.copyProperties(blackGrayBreakBusiness, BlackGrayBreakBusinessDetailRSP.class, "applyFileKeys");
        blackGrayBreakBusinessDetailRSP.setApplyFileKeys(JSONUtil.toList(blackGrayBreakBusiness.getApplyFileKeys(), String.class));
        blackGrayBreakBusinessDetailRSP.setCreateByCode(String.valueOf(AccountUtil.getLoginInfo().getId()));
        //AuditTask auditTasksByBizId = blackGrayBreakBusinessAuditService.getAuditTasksByBizId(AuditBizTypeEnum.BLACK_GRAY_BUSINESS_BREAK.getType(), id);
        //blackGrayBreakBusinessDetailRSP.setAuditTaskId(auditTasksByBizId == null ? null : auditTasksByBizId.getId());
        return blackGrayBreakBusinessDetailRSP;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void remove(BlackGrayBreakBusinessRemoveREQ req) {
        BlackGrayBreakBusiness originalInfo = blackGrayBreakBusinessMapper.selectByPrimaryKey(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            //throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        blackGrayBreakBusinessMapper.deleteByPrimaryKey(req.getId());
    }

    @Override
    public void updateStatue(Long id, Integer status) {
        BlackGrayBreakBusiness blackGrayBreakBusiness = new BlackGrayBreakBusiness();
        blackGrayBreakBusiness.setId(id);
        blackGrayBreakBusiness.setAuditStatus(status);
        blackGrayBreakBusinessMapper.updateByPrimaryKeySelective(blackGrayBreakBusiness);
    }

    @Override
    public void saveBatch(List<BlackGrayBreakBusiness> records) {
        blackGrayBreakBusinessMapper.insertList(records);
    }

}