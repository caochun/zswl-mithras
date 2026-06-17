package cn.zswltech.mithras.associationreport.service;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import javax.annotation.Resource;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.associationreport.storedata.DeleteData;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.associationreport.enums.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.associationreport.mapper.AssociationBasicSituationLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationBasicSituationLib;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationShahStorInfo;
import cn.zswltech.mithras.associationreport.mapper.model.BasicAssociationReport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.associationreport.mapper.AssociationShahStorInfoLibMapper;
import cn.zswltech.mithras.associationreport.mapper.model.AssociationShahStorInfoLib;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
* @description 股东股权信息一览表-股东股权信息(流程节点记录版本表)
* @author hspcadmin
* @date 2025-09-14
*/
@Service
public class AssociationShahStorInfoLibService extends ServiceImpl<AssociationShahStorInfoLibMapper, AssociationShahStorInfoLib> {

    @Resource
    private AssociationShahStorInfoLibMapper associationShahStorInfoLibMapper;

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationShahStorInfoLibAddREQ req) {
        AssociationShahStorInfoLib info = BeanUtil.copyProperties(req, AssociationShahStorInfoLib.class);
        associationShahStorInfoLibMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationShahStorInfoLibModifyREQ req) {
        AssociationShahStorInfoLib originalInfo = associationShahStorInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationShahStorInfoLib info = BeanUtil.copyProperties(req, AssociationShahStorInfoLib.class);
        associationShahStorInfoLibMapper.updateById(info);
    }

    public Page<AssociationShahStorInfoLib> list(AssociationShahStorInfoLibListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationShahStorInfoLibRemoveREQ req) {
        AssociationShahStorInfoLib originalInfo = associationShahStorInfoLibMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        associationShahStorInfoLibMapper.deleteById(req.getId());
    }

    public List<AssociationDetailShahStorInfoRSP> listByReportInstanceIdAndVersion(String reportInstanceId,String version) {
        LambdaQueryWrapper<AssociationShahStorInfoLib> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.eq(BasicAssociationReport::getVersion, version);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationShahStorInfoLib> dbResult = this.list(query);
        if (CollectionUtil.isEmpty(dbResult)) {
            return Collections.emptyList();
        }
        List<AssociationDetailShahStorInfoRSP> result = BeanUtil.copyToList(dbResult, AssociationDetailShahStorInfoRSP.class);
        // 枚举转译
        Map<String, Map<String, String>> dictMap = SpringUtil.getBean(AssociationDictionaryService.class).getCode2DisplayMap();
        for (AssociationDetailShahStorInfoRSP rsp : result) {
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