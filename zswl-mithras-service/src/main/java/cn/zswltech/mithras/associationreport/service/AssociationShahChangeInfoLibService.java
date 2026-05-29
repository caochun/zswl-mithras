package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationSeniorExecutiveInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.common.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationShahChangeInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahChangeInfoLib;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @description 股东股权信息一览表-股东变更记录(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationShahChangeInfoLibService extends ServiceImpl<AssociationShahChangeInfoLibMapper, AssociationShahChangeInfoLib> {

    @Resource
    private AssociationShahChangeInfoLibMapper associationShahChangeInfoLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationShahChangeInfoLibAddREQ req) {
        AssociationShahChangeInfoLib info = BeanUtil.copyProperties(req, AssociationShahChangeInfoLib.class);
        associationShahChangeInfoLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationShahChangeInfoLibModifyREQ req) {
        AssociationShahChangeInfoLib originalInfo = associationShahChangeInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationShahChangeInfoLib info = BeanUtil.copyProperties(req, AssociationShahChangeInfoLib.class);
        associationShahChangeInfoLibMapper.updateById(info);
    }

    public Page<AssociationShahChangeInfoLib> list(AssociationShahChangeInfoLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationShahChangeInfoLibRemoveREQ req) {
        AssociationShahChangeInfoLib originalInfo = associationShahChangeInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationShahChangeInfoLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailShahChangeInfoRSP> listByReportInstanceIdAndVersion(String reportInstanceId, String version) {
        LambdaQueryWrapper<AssociationShahChangeInfoLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationShahChangeInfoLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailShahChangeInfoRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailShahChangeInfoRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailShahChangeInfoRSP rsp : result) {
            if (StrUtil.isNotBlank(rsp.getShahCharCode())) {
                rsp.setShahCharDisplay(dictMap.get(AssociationDictionaryCategoryEnum.PTY00021.name()).get(rsp.getShahCharCode()));
            }
            //股东进入方式,是否需要数据字典，待定...

            //股权转让标志,是否需要数据字典，待定...
            if (StrUtil.isNotBlank(rsp.getStorTranFlag())) {
                rsp.setStorTranFlagDisplay(Optional.ofNullable(YesOrNoNumberEnum.findByCodeStr(rsp.getStorTranFlag())).map(YesOrNoNumberEnum::getChinese).orElse(null));
            }

        }
        return result;
    }

}