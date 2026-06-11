package cn.zswltech.mithras.workflow.process;

import cn.hutool.core.util.StrUtil;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.mithras.workflow.flow.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.workflow.mapper.ProcessModifyRemarkMapper;
import cn.zswltech.mithras.workflow.process.ProcessModifyRemarkLibService;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.workflow.mapper.model.ProcessModifyRemark;
import cn.zswltech.mithras.workflow.mapper.model.ProcessModifyRemarkLib;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.hutool.core.bean.BeanUtil.copyProperties;
import static cn.zswltech.mithras.foundation.util.VersionUtil.generateVersion;

/**
 * @author yibin
 */
@Service
public class ProcessModifyRemarkService extends ServiceImpl<ProcessModifyRemarkMapper, ProcessModifyRemark> {
    @Resource
    private ProcessModifyRemarkLibService remarkLibService;
    @Resource
    private CommonVersionMapper commonVersionMapper;


    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateByKey(ProcessModifyRemark remark) {
        this.saveOrUpdate(remark, Wrappers.<ProcessModifyRemark>lambdaQuery()
                .eq(ProcessModifyRemark::getRemarkType, remark.getRemarkType())
                .eq(ProcessModifyRemark::getMainId, remark.getMainId())
                .eq(ProcessModifyRemark::getModuleType, remark.getModuleType())
        );
    }


    @Transactional(rollbackFor = Exception.class)
    public void processEnd(Long mainId, Integer endType, String modelKey, Long processInstanceId) {
        boolean processPass = ProcessBusinessStatusEnum.success(endType);
        if (processPass) {
            ProcessModelTypeEnum processModelType = ProcessModelTypeEnum.getByName(modelKey);
            if (null != processModelType) {
                //记录版本，然后清空编辑区
                String businessModuleName = processModelType.getBusinessModuleName();
                List<ProcessModifyRemark> list = this.list(Wrappers.<ProcessModifyRemark>lambdaQuery()
                        .eq(ProcessModifyRemark::getModuleType, businessModuleName)
                        .eq(ProcessModifyRemark::getMainId, mainId)
                );
                if (!list.isEmpty()) {
                    List<ProcessModifyRemarkLib> libList = new ArrayList<>(list.size());
                    for (ProcessModifyRemark remark : list) {
                        ProcessModifyRemarkLib lib = copyProperties(remark, ProcessModifyRemarkLib.class,
                                "id", "create_time", "update_time", "create_by", "update_by");
                        lib.setOriginId(remark.getId());
                        lib.setVersionType(1);
                        lib.setVersion(getVersion(mainId, businessModuleName, processInstanceId));
                        lib.setDataCreateTime(remark.getCreateTime());
                        lib.setDataUpdateBy(remark.getUpdateBy());
                        lib.setDataCreateBy(remark.getCreateBy());
                        lib.setDataUpdateTime(remark.getUpdateTime());
                        libList.add(lib);
                    }
                    remarkLibService.saveBatch(libList);
                    this.removeByIds(list.stream().map(ProcessModifyRemark::getId).collect(Collectors.toList()));
                }
            }
        }
    }

    private String getVersion(Long mainId, String businessModuleName, Long processInstanceId) {
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, businessModuleName)
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if (null == commonVersion) {
            return generateVersion(null);
        }
        if (StrUtil.equals(commonVersion.getProcessInstanceId(), String.valueOf(processInstanceId))) {
            return commonVersion.getVersion();
        }
        return generateVersion(commonVersion.getVersion());
    }
}
