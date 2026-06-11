package cn.zswltech.mithras.blackgray.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.biz.service.OrgService;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundModifyREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayManualOutboundRemoveREQ;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundDetailRSP;
import cn.zswltech.mithras.blackgray.dto.rsp.BlackGrayManualOutboundListRSP;
import cn.zswltech.mithras.blackgray.enums.AuditStatusEnum;
import cn.zswltech.mithras.blackgray.enums.BlackGrayOrgEnum;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayLibraryMapper;
import cn.zswltech.mithras.blackgray.mapper.BlackGrayManualOutboundMapper;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayLibrary;
import cn.zswltech.mithras.blackgray.mapper.model.BlackGrayManualOutbound;
import cn.zswltech.mithras.blackgray.service.BlackGrayManualOutboundService;
import cn.zswltech.mithras.blackgray.service.BlackGrayWarehouseRuleConfigService;
import cn.zswltech.mithras.blackgray.service.GruulAuthService;
import cn.zswltech.mithras.blackgray.service.audit.BlackGrayOutboundAuditService;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.port.CurrentUserOrgResolver;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
* @description 黑灰名单人工出库表
* @author
* @date 2023-11-29
*/
@Service
public class BlackGrayManualOutboundServiceImpl implements BlackGrayManualOutboundService {

    @Resource
    private BlackGrayManualOutboundMapper blackGrayManualOutboundMapper;
    @Resource
    private OrgService orgService;
    @Resource
    private BlackGrayOutboundAuditService blackGrayOutboundAuditService;
    @Resource
    private BlackGrayLibraryMapper blackGrayLibraryMapper;
    @Resource
    private BlackGrayWarehouseRuleConfigService blackGrayWarehouseRuleConfigService;
    @Resource
    GruulAuthService gruulAuthService;
    @Resource
    private CurrentUserOrgResolver currentUserOrgResolver;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long add(BlackGrayManualOutboundAddREQ req) {
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if(ObjectUtil.isEmpty(loginInfo)){
            throw new MithrasException("登录信息不存在");
        }
        Date date = new Date();
        BlackGrayLibrary blackGrayLibrary = blackGrayLibraryMapper.selectByPrimaryKey(req.getBlackGrayId());
        BlackGrayManualOutbound info = BeanUtil.copyProperties(req, BlackGrayManualOutbound.class, "applyFileKeys", "applyReason");
        info.setApplyFileKeys(JSONUtil.toJsonStr(req.getApplyFileKeys()));
        if(ObjectUtil.isNotEmpty(req.getApplyReason())){
            info.setApplyReason(JSONUtil.toJsonStr(req.getApplyReason()));
        }
        //补全信息
        if(ObjectUtil.isNotEmpty(blackGrayLibrary)){
            info.setSource(blackGrayLibrary.getSource());
            info.setWarehouseOrganization(blackGrayLibrary.getApplyOrganization());
            info.setReportFlag(blackGrayLibrary.getReportFlag());
        }
        info.setUpdateBy(loginInfo.getId());
        info.setCreateBy(loginInfo.getId());
        info.setCreateTime(date);
        info.setUpdateTime(date);
        info.setApplyOrganization(BlackGrayOrgEnum.ZSZL.name());
        List<OrgDO> deptDos = currentUserOrgResolver.getUserDeptList();
        if(CollectionUtil.isNotEmpty(deptDos)){
            info.setApplyDept(deptDos.get(0).getCode());
        }
        info.setAuditStatus((int) AuditStatusEnum.WAIT.getCode());
        blackGrayManualOutboundMapper.insert(info);
        return info.getId();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void modify(BlackGrayManualOutboundModifyREQ req) {
        BlackGrayManualOutbound originalInfo = blackGrayManualOutboundMapper.selectByPrimaryKey(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException("记录不存在");
        }

        BlackGrayManualOutbound info = BeanUtil.copyProperties(req, BlackGrayManualOutbound.class, "applyFileKeys", "applyReason");
        info.setApplyFileKeys(JSONUtil.toJsonStr(req.getApplyFileKeys()));
        if(ObjectUtil.isNotEmpty(req.getApplyReason())){
            info.setApplyReason(JSONUtil.toJsonStr(req.getApplyReason()));
        }
        info.setUpdateTime(new Date());
        //补全信息
        BlackGrayLibrary blackGrayLibrary = blackGrayLibraryMapper.selectByPrimaryKey(req.getBlackGrayId());
        if(ObjectUtil.isNotEmpty(blackGrayLibrary)){
            info.setSource(blackGrayLibrary.getSource());
            info.setWarehouseOrganization(blackGrayLibrary.getApplyOrganization());
        }
        blackGrayManualOutboundMapper.updateByPrimaryKeySelective(info);
    }

    @Override
    public PageR<BlackGrayManualOutboundListRSP> list(BlackGrayManualOutboundListREQ req) {
        Example example = getListExample(req);
        example.orderBy(" updateTime ").desc();
        PageHelper.startPage(req.getPage(), req.getPageSize());
        List<BlackGrayManualOutbound> blackGrayManualOutbounds = blackGrayManualOutboundMapper.selectByExample(example);
        if(CollectionUtil.isEmpty(blackGrayManualOutbounds)){
            return null;
        }
        PageInfo<BlackGrayManualOutbound> blackGrayManualOutboundPageInfo = new PageInfo<>(blackGrayManualOutbounds);
        List<BlackGrayManualOutboundListRSP> blackGrayManualOutboundListRSPS = BeanUtil.copyToList(blackGrayManualOutbounds, BlackGrayManualOutboundListRSP.class);
       /* Map<Long, AuditTask> currentOperator = getCurrentOperator(blackGrayManualOutboundListRSPS.stream().map(BlackGrayManualOutboundListRSP::getId).collect(Collectors.toList()));
        blackGrayManualOutboundListRSPS.forEach(blackGrayManualOutboundList -> {
            AuditTask auditTask = currentOperator.get(blackGrayManualOutboundList.getId());
            if(ObjectUtil.isNotEmpty(auditTask)){
                blackGrayManualOutboundList.setCurrentOperator(auditTask.getCurrentOperator() == null ? gruulAuthService.getAccountById(blackGrayManualOutboundList.getCreateBy()) : auditTask.getCurrentOperator());
                blackGrayManualOutboundList.setAuditTaskId(auditTask.getId());
                blackGrayManualOutboundList.setPreOperator(auditTask.getPreOperator());
                blackGrayManualOutboundList.setLatestMsg(auditTask.getLatestMsg());
            }
        });*/
        return PageR.of(blackGrayManualOutboundListRSPS, blackGrayManualOutboundPageInfo.getTotal());
    }


    private Example getListExample(BlackGrayManualOutboundListREQ req) {
        Example example = new Example(BlackGrayManualOutbound.class);
        Example.Criteria criteria = example.createCriteria();
        OrgDO rootOrg = currentUserOrgResolver.getUserDept();
        if(rootOrg == null){
            throw new MithrasException("当前用户无机构");
        }
        if(ObjectUtil.isNotEmpty(rootOrg)){
            criteria.andEqualTo(BlackGrayManualOutbound.APPLY_ORGANIZATION, rootOrg.getCode());
        }
        if(req.getEnterpriseName() != null){
            criteria.andLike(BlackGrayManualOutbound.ENTERPRISE_NAME, "%" + req.getEnterpriseName() + "%");
        }
        if(req.getBusinessType() != null){
            criteria.andEqualTo(BlackGrayManualOutbound.BUSINESS_TYPE, req.getBusinessType());
        }
        if(req.getAuditStatus() != null){
            criteria.andEqualTo(BlackGrayManualOutbound.AUDIT_STATUS, req.getAuditStatus());
        }
        if(req.getApplyTimeFrom() != null){
            criteria.andBetween(BlackGrayManualOutbound.CREATE_TIME, req.getApplyTimeFrom(), req.getApplyTimeTo());
        }
        if(req.getBlackGrayType() != null){
            criteria.andEqualTo(BlackGrayManualOutbound.BLACK_GRAY_TYPE, req.getBlackGrayType());
        }
        if(req.getPlanOutboundTimeFrom() != null){
            criteria.andBetween(BlackGrayManualOutbound.PLAN_OUTBOUND_TIME, req.getPlanOutboundTimeFrom(), req.getPlanOutboundTimeTo());
        }
        if(ObjectUtil.isNotEmpty(req.getIds())){
            criteria.andIn(BlackGrayLibrary.ID, req.getIds());
        }
        return example;
    }

    @Override
    public BlackGrayManualOutboundDetailRSP detail(Long id) {
        BlackGrayManualOutbound req = new BlackGrayManualOutbound();
        req.setId(id);
        BlackGrayManualOutbound blackGrayManualOutbound = blackGrayManualOutboundMapper.selectOne(req);
        if(ObjectUtil.isEmpty(blackGrayManualOutbound)){
            throw new MithrasException("无此记录");
        }
        BlackGrayManualOutboundDetailRSP blackGrayManualOutboundDetailRSP = BeanUtil.copyProperties(blackGrayManualOutbound, BlackGrayManualOutboundDetailRSP.class, "applyFileKeys", "applyReason");
        blackGrayManualOutboundDetailRSP.setApplyFileKeys(JSONUtil.toList(blackGrayManualOutbound.getApplyFileKeys(), String.class));
        if(ObjectUtil.isNotEmpty(blackGrayManualOutbound.getApplyReason())){
            blackGrayManualOutboundDetailRSP.setApplyReason(JSONUtil.toList(blackGrayManualOutbound.getApplyReason(), BlackGrayManualOutboundAddREQ.Reason.class));
            /*Map<String, String> map = blackGrayWarehouseRuleConfigService.num2NameBatch(blackGrayManualOutboundDetailRSP.getApplyReason().stream().map(BlackGrayManualOutboundAddREQ.Reason::getApplyReasonType).collect(Collectors.toList()));
            blackGrayManualOutboundDetailRSP.getApplyReason().forEach(reason -> {
                reason.setApplyReasonType(map.get(reason.getApplyReasonType()));
            });*/
        }
        blackGrayManualOutboundDetailRSP.setCreateByCode(gruulAuthService.getAccountById(blackGrayManualOutboundDetailRSP.getCreateBy()));
        /*AuditTask auditTask = blackGrayOutboundAuditService.getAuditTasksByBizId(AuditBizTypeEnum.BLACK_GRAY_MANUAL_OUTBOUND.getType(), id);
        if (auditTask != null) {
            blackGrayManualOutboundDetailRSP.setAuditTaskId(auditTask.getId());
            blackGrayManualOutboundDetailRSP.setCurrentOperator(auditTask.getCurrentOperator());
        }*/
        return blackGrayManualOutboundDetailRSP;
    }

    @Override
    public void updateStatue(Long id, Integer status) {
        BlackGrayManualOutbound blackGrayManualOutbound = new BlackGrayManualOutbound();
        blackGrayManualOutbound.setId(id);
        blackGrayManualOutbound.setAuditStatus(status);
        if(ObjectUtil.equals(status, 4)){
            blackGrayManualOutbound.setActualOutboundTime(new Date());
        }
        blackGrayManualOutboundMapper.updateByPrimaryKeySelective(blackGrayManualOutbound);
    }

    @Override
    public void remove(BlackGrayManualOutboundRemoveREQ req) {
        Example example = new Example(BlackGrayManualOutbound.class);
        example.createCriteria().andIn(BlackGrayManualOutbound.ID, req.getIds());
        List<BlackGrayManualOutbound> blackGrayManualOutbounds = blackGrayManualOutboundMapper.selectByExample(example);
        if(ObjectUtil.isNotEmpty(blackGrayManualOutbounds)){
            blackGrayManualOutbounds.forEach(blackGrayManualOutbound -> {
                if(!ObjectUtil.equals(blackGrayManualOutbound.getAuditStatus(), 0)){
                    throw new MithrasException("非未提及状态，不可删除");
                }
            });
            blackGrayManualOutboundMapper.deleteByExample(example);
        }
    }


}
