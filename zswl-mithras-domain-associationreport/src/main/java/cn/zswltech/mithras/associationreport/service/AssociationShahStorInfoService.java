package cn.zswltech.mithras.associationreport.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.associationreport.DeleteData;
import cn.zswltech.mithras.associationreport.AssociationReportDateUtils;
import cn.zswltech.mithras.associationreport.excel.AssociationShahStorInfoModel;
import cn.zswltech.mithras.associationreport.AssociationReportPeriodUtils;
import cn.zswltech.mithras.dto.associationreport.*;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.associationreport.AssociationDictionaryCategoryEnum;
import cn.zswltech.mithras.service.mapper.associationreport.AssociationShahStorInfoMapper;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationReport;
import cn.zswltech.mithras.service.mapper.model.associationreport.AssociationShahStorInfo;
import cn.zswltech.mithras.service.mapper.model.associationreport.BasicAssociationReport;
import cn.zswltech.mithras.service.others.MithrasException;
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
* @description 股东股权信息一览表-股东股权信息
* @author hspcadmin
* @date 2025-08-25
*/
@Service
public class AssociationShahStorInfoService extends ServiceImpl<AssociationShahStorInfoMapper, AssociationShahStorInfo> implements DeleteData {

    @Value("${association.zlAccount:}")
    private String zszlCreditCode;

    @Resource
    protected AssociationReportQueryService associationReportQueryService;

    public List<AssociationDetailShahStorInfoRSP> listByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationShahStorInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationShahStorInfo> dbResult = this.list(query);
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

    public List<AssociationShahStorInfoModel> listModelByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationShahStorInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        query.orderByAsc(BasicAssociationReport::getRowNum);
        List<AssociationShahStorInfo> dbResult = this.list(query);
        return BeanUtil.copyToList(dbResult, AssociationShahStorInfoModel.class);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByReportInstanceId(String reportInstanceId) {
        LambdaQueryWrapper<AssociationShahStorInfo> query = Wrappers.lambdaQuery();
        query.eq(AssociationShahStorInfo::getReportInstanceId, reportInstanceId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void add(AssociationShahStorInfoAddREQ req) {
        AssociationShahStorInfo info = BeanUtil.copyProperties(req, AssociationShahStorInfo.class);
        this.baseMapper.insert(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(AssociationShahStorInfoModifyREQ req) {
        AssociationShahStorInfo originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        AssociationShahStorInfo info = BeanUtil.copyProperties(req, AssociationShahStorInfo.class);
        this.baseMapper.updateById(info);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modifyBatch(List<AssociationShahStorInfoModifyREQ> req){
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
        AssociationReport associationReport = associationReportQueryService.findByReportInstanceId(reportInstanceId);
        if (ObjectUtil.isNull(associationReport)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        String period = AssociationReportPeriodUtils.generatePeriod(associationReport.getReportPeriodCategory(), associationReport.getReportPeriod(), associationReport.getReportYear());

        LambdaQueryWrapper<AssociationShahStorInfo> query = Wrappers.lambdaQuery();
        query.eq(BasicAssociationReport::getReportInstanceId, reportInstanceId);
        List<AssociationShahStorInfo> oldDbResult = this.list(query);

        List<AssociationShahStorInfo> newValList = req.stream().map(item -> {
            AssociationShahStorInfo newVal = BeanUtil.copyProperties(item, AssociationShahStorInfo.class);
            newVal.setAprvTime(AssociationReportDateUtils.parseDateTime(item.getAprvTime()));
            //填充相关属性值
            return newVal;
        }).collect(Collectors.toList());

        UpdateUtils.update(oldDbResult, newValList, AssociationShahStorInfo::getId,
                updateEntityList -> {
                    updateEntityList.forEach(e -> {
                            //填充相关属性值
                    });
                    this.updateBatchById(updateEntityList);
                },
                deleteList -> this.baseMapper.deleteBatchIds(deleteList.stream().map(AssociationShahStorInfo::getId).collect(Collectors.toList())),
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

    public Page<AssociationShahStorInfo> list(AssociationShahStorInfoListREQ req) {
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void remove(AssociationShahStorInfoRemoveREQ req) {
        AssociationShahStorInfo originalInfo = this.baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        this.baseMapper.deleteById(req.getId());
    }

}
