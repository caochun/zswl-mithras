package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.excel.AssociationShahChangeInfoModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationShahChangeInfoMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahChangeInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.UpdateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description 股东股权信息一览表-股东变更记录
* @author hspcadmin
* @date 2025-08-25
*/
@Service
public class AssociationShahChangeInfoService extends ServiceImpl<AssociationShahChangeInfoMapper, AssociationShahChangeInfo> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportService associationReportService;

    public List<AssociationDetailShahChangeInfoRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationShahChangeInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationShahChangeInfo> dbResult = this.list(query);
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

    public List<AssociationShahChangeInfoModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationShahChangeInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationShahChangeInfo> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationShahChangeInfoModel.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationShahChangeInfo> query = Wrappers.lambdaQuery();
        query.eq(AssociationShahChangeInfo::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationShahChangeInfoAddREQ req) {
        AssociationShahChangeInfo info = BeanUtil.copyProperties(req, AssociationShahChangeInfo.class);
        this.baseMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationShahChangeInfoModifyREQ req) {
        AssociationShahChangeInfo originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationShahChangeInfo info = BeanUtil.copyProperties(req, AssociationShahChangeInfo.class);
        this.baseMapper.updateById(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationShahChangeInfoModifyREQ> req){
        //行号自动生成
        for(int rowNum = 1; rowNum < req.size()+1; rowNum++){
            req.get(rowNum-1).setRowNum(rowNum);
            req.get(rowNum-1).setOnum(String.valueOf(rowNum));
        }
        String reportInstanceId = req.get(0).getReportInstanceId();
        if(StringUtils.isBlank(reportInstanceId)){
            throw new MithrasException(ResultMsg.REPORT_INSTANCE_ID_NULL);
        }

        LambdaQueryWrapper<AssociationReport> associationReportLambdaQueryWrapper = Wrappers.lambdaQuery();
        associationReportLambdaQueryWrapper.eq(AssociationReport::getReportInstanceId, reportInstanceId);
        AssociationReport associationReport = associationReportService.getOne(associationReportLambdaQueryWrapper);
        if (ObjectUtil.isNull(associationReport)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        String period = AssociationReportPeriodUtils.generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());

        LambdaQueryWrapper<AssociationShahChangeInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationShahChangeInfo> oldDbResult = this.list(query);

        List<AssociationShahChangeInfo> newValList = req.stream().map(item -> {
            AssociationShahChangeInfo newVal = BeanUtil.copyProperties(item, AssociationShahChangeInfo.class);
            newVal.setAprvTime(DateUtil.parseDateTime(item.getAprvTime()));
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationShahChangeInfo::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                        //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationShahChangeInfo::getId).collect(Collectors.toList())),
                addList -> {
                    addList.forEach(e -> {
                        if(StringUtils.isBlank(e.getUnifSociCredCode())) {//如果统一社会信用代码为空，则取默认配置文件中的统一社会信用代码
                            e.setUnifSociCredCode(this.zszlCreditCode);
                        }
                        e.setReportInstanceId(reportInstanceId);
                        e.setReportInstancePeriod(period);
                        e.setBatchNo(associationReport.getBatchNo());
                        e.setWriteTime(LocalDateTime.now());
                        e.setOp("insert");
                    });
                    this.saveBatch(addList);
                }
        );
    }

    public Page<AssociationShahChangeInfo> list(AssociationShahChangeInfoListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationShahChangeInfoRemoveREQ req) {
        AssociationShahChangeInfo originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.baseMapper.deleteById(req.getId());
    }

}
