package cn.zswltech.mithras.service.convert;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.gruul.dao.dal.dao.UserOrgJobDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserOrgJobDO;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.config.enumscan.MaterialsTypeFactory;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文件转换
 *
 * @author wangchuanhao
 * @date 2023/2/6 4:35 PM
 */
@Component
public class FileConvert {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private MaterialsListLibHandlerProxy materialsListLibHandlerProxy;
    @Resource
    private UserOrgJobDOMapper userOrgJobDOMapper;

    public FileListRSP entity2RSP(MaterialsList entity) {
        FileListRSP rsp = BeanUtil.copyProperties(entity, FileListRSP.class);
        rsp.setMaterialsTypeName(MaterialsTypeFactory.convert(rsp.getBusinessType(), rsp.getMaterialsType()));
        rsp.setMaterialSubTypeName(MaterialsTypeFactory.convert(rsp.getBusinessType(), rsp.getMaterialSubType()));
        return rsp;
    }

    public void fillName(Collection<? extends FileListRSP> rspList) {
        if (CollectionUtils.isEmpty(rspList)) {
            return;
        }
        Set<Long> userIdSet = new HashSet<>();
        for (FileListRSP fileListRSP : rspList) {
            userIdSet.add(fileListRSP.getCreateBy());
            userIdSet.add(fileListRSP.getUpdateBy());
        }
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(userIdSet);
        // 获取用户相关岗位
        Map<Long, List<String>> userJobsMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(userIdSet)) {
            Example example = new Example(UserOrgJobDO.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("userId", userIdSet);
            List<UserOrgJobDO> userJobList = userOrgJobDOMapper.selectByExample(example);
            if (CollectionUtil.isNotEmpty(userJobList)) {
                for (UserOrgJobDO userOrgJobDO : userJobList) {
                    List<String> jobList = userJobsMap.get(userOrgJobDO.getUserId());
                    if (Objects.isNull(jobList)) {
                        jobList = new LinkedList<>();
                        userJobsMap.put(userOrgJobDO.getUserId(), jobList);
                    }
                    jobList.add(userOrgJobDO.getJobCode());
                }
            }
        }
        for (FileListRSP fileListRSP : rspList) {
            fileListRSP.setCreateByName(userNameMap.get(fileListRSP.getCreateBy()));
            fileListRSP.setUpdateByName(userNameMap.get(fileListRSP.getUpdateBy()));
            fileListRSP.setUploadByPostList(Optional.ofNullable(userJobsMap.get(fileListRSP.getCreateBy())).orElse(Collections.emptyList()));
        }
    }

    public MaterialsList actualLib2Entity(MaterialsListLib t) {
        MaterialsList f = materialsListLibHandlerProxy.lib2Entity(t);
        if (Objects.isNull(f)) {
            return null;
        }
        f.setId(t.getOriginId());
        f.setCreateBy(t.getDataCreateBy());
        f.setUpdateBy(t.getDataUpdateBy());
        f.setCreateTime(t.getDataCreateTime());
        f.setUpdateTime(t.getDataUpdateTime());
        return f;
    }

}
