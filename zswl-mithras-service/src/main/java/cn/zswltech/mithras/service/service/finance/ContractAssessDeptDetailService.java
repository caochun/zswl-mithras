package cn.zswltech.mithras.service.service.finance;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.mithras.dto.kpi.ContractAssessDeptConfigListREQ;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ContractAssessDeptConfigListRSP;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ContractAssessDeptDto;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.finance.ContractAssessDeptDetailMapper;
import cn.zswltech.mithras.service.mapper.model.finance.ContractAssessDeptDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lizhao
 * @date 2024/6/16
 * @description
 */
@Slf4j
@Service
public class ContractAssessDeptDetailService extends ServiceImpl<ContractAssessDeptDetailMapper, ContractAssessDeptDetail> {

    @Resource
    private ContractAssessDeptDetailMapper contractAssessDeptDetailMapper;
    @Resource
    private OrgDOMapper orgDOMapper;

    //获取合同-考核部门设置详情
    public List<ContractAssessDeptConfigListRSP> getContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ){
        //查看考核部门详情数据
        LambdaQueryWrapper<ContractAssessDeptDetail> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode());
        List<ContractAssessDeptConfigListRSP> assessDeptDetails = new ArrayList<>();
        List<ContractAssessDeptDetail> list = contractAssessDeptDetailMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            for (ContractAssessDeptDetail detail : list) {
                ContractAssessDeptConfigListRSP rsp = new ContractAssessDeptConfigListRSP();
                rsp.setId(detail.getId());
                rsp.setContractId(detail.getContractId());
                rsp.setAssessDeptId(detail.getAssessDeptId());
                assessDeptDetails.add(rsp);
            }
        }
        return assessDeptDetails;
    }

    //保存合同-考核部门设置
    public void saveContractAssessDeptConfig (ContractAssessDeptConfigListREQ configListREQ) {
        List<ContractAssessDeptDto> deptlDtoList = configListREQ.getContractAssessDeptlDtoList();
        Assert.notEmpty(deptlDtoList, () -> MithrasException.newException("考核部门设置信息为空"));
        Set<Long> ids = deptlDtoList.stream().map(ContractAssessDeptDto::getId).collect(Collectors.toSet());
        //查询数据是否是已经存在
        List<ContractAssessDeptDetail> deptDetailList = contractAssessDeptDetailMapper.selectList(Wrappers.<ContractAssessDeptDetail>lambdaQuery()
                .in(ContractAssessDeptDetail::getId, ids).eq(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.NO.getCode()));
        Set<Long> exists = deptDetailList.stream().map(ContractAssessDeptDetail::getId).collect(Collectors.toSet());
        //新增的数据
        List<ContractAssessDeptDetail> addList = new ArrayList<>();
        List<ContractAssessDeptDto> addDepts = deptlDtoList.stream().filter(e -> ObjectUtil.isNull(e.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(addDepts)) {
            addDepts.forEach(dto -> {
                ContractAssessDeptDetail detail = new ContractAssessDeptDetail();
                detail.setContractId(dto.getContractId());
                detail.setAssessDeptId(dto.getAssessDeptId());
                addList.add(detail);
            });
        }
        //编辑的数据
        List<ContractAssessDeptDetail> updateList = new ArrayList<>();
        Map<Long, Long> addMap = new HashMap<>();
        for (ContractAssessDeptDto contractAssessDeptDto : deptlDtoList) {
            addMap.putIfAbsent(contractAssessDeptDto.getContractId(), contractAssessDeptDto.getId());
        }
        List<ContractAssessDeptDto> updateDepts = deptlDtoList.stream().filter(e -> exists.contains(e.getId())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(updateDepts)) {
            for (ContractAssessDeptDto dto : updateDepts) {
                ContractAssessDeptDetail detail = new ContractAssessDeptDetail();
                detail.setContractId(dto.getContractId());
                detail.setAssessDeptId(dto.getAssessDeptId());
                detail.setId(addMap.get(dto.getContractId()));
                updateList.add(detail);
            }
        }
        if(CollectionUtil.isNotEmpty(addList)){
            this.saveBatch(addList);
        }
        if(CollectionUtil.isNotEmpty(updateList)){
            this.updateBatchById(updateList);
        }
    }

    public void deleteContractAssessDeptConfig(ContractAssessDeptConfigListREQ configListREQ) {
        List<ContractAssessDeptDetail> details = contractAssessDeptDetailMapper.selectBatchIds(configListREQ.getIds());
        if (ObjectUtil.isNull(details)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        List<ContractAssessDeptDetail> detailList = new ArrayList<>();
        for (ContractAssessDeptDetail c: details) {
            ContractAssessDeptDetail detail = new ContractAssessDeptDetail();
            detail.setId(c.getId());
            detail.setDeleted(YesOrNoNumberEnum.YES.getCode());
            detailList.add(detail);
        }
        LambdaUpdateWrapper<ContractAssessDeptDetail> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ContractAssessDeptDetail::getId, configListREQ.getIds());
        updateWrapper.set(ContractAssessDeptDetail::getDeleted, YesOrNoNumberEnum.YES.getCode());
        this.update(updateWrapper);
    }
}
