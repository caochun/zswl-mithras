package cn.zswltech.mithras.service.service.lib;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.VersionTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.common.util.StringUtil;
import cn.zswltech.mithras.service.util.VersionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public abstract class CommonVersionService<T extends IEntity> {

    @Resource
    protected CommonVersionMapper commonVersionMapper;
    @Resource
    private Id2NameService id2NameService;
    @Autowired
    protected BaseMapper<T> baseMapper;

    @Transactional(rollbackFor = Exception.class)
    public String recordVersion(Long mainId, VersionTypeEnum type, Long operatorId, String processInstanceId, Integer versionType) {
        return recordVersion(mainId, type, operatorId, processInstanceId, versionType, null);
    }

    /**
     * 把临时表数据全量copy到版本表
     *
     * @param type
     */
    @Transactional(rollbackFor = Exception.class)
    public String recordVersion(Long mainId, VersionTypeEnum type, Long operatorId, String processInstanceId, Integer versionType, Map<String, Object> extraMap) {
        T baseModel = baseMapper.selectById(mainId);
        if (Objects.isNull(baseModel)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        CommonVersion commonVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                // 不过滤类型 版本号共用
//                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, getBusinessModule().name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        boolean needClearLastFlag = false;
        String version = null;
        if (commonVersion == null) {
            // 该客户版本数据为空
            CommonVersion newCommonVersion = CommonVersion.builder()
                    .mainId(mainId)
                    .type(type.getType())
                    .module(getBusinessModule().name())
                    .version(VersionUtil.generateVersion(null))
                    .processInstanceId(processInstanceId)
                    .versionType(versionType)
                    .build();
            newCommonVersion.setCreateBy(operatorId);
            newCommonVersion.setUpdateBy(operatorId);
            commonVersionMapper.insert(newCommonVersion);
            version = newCommonVersion.getVersion();
        } else {
            // 新增版本 不清空各版本表旧数据
            CommonVersion newCommonVersion = CommonVersion.builder()
                    .mainId(mainId)
                    .module(getBusinessModule().name())
                    .type(type.getType())
                    .version(VersionUtil.generateVersion(commonVersion.getVersion()))
                    .processInstanceId(processInstanceId)
                    .versionType(versionType)
                    .build();
            newCommonVersion.setCreateBy(operatorId);
            newCommonVersion.setUpdateBy(operatorId);
            commonVersionMapper.insert(newCommonVersion);
            version = newCommonVersion.getVersion();
        }
        if (this.getBusinessModule() == BusinessModuleEnum.CLIENT) {
            customFlushData(baseModel, version, needClearLastFlag, versionType, extraMap);
        } else {
            customFlushData(baseModel, version, needClearLastFlag, versionType);
        }
        return version;
    }

    /**
     * @param t
     * @param version
     * @param needClearLastFlag
     * @param versionType
     */
    @Transactional(rollbackFor = Exception.class)
    public abstract void customFlushData(T t, String version, boolean needClearLastFlag, Integer versionType);

    @Transactional(rollbackFor = Exception.class)
    public void customFlushData(T t, String version, boolean needClearLastFlag, Integer versionType, Map<String, Object> extraMap) {
        throw new MithrasException("暂未支持的功能");
    }

    /**
     * 寻找某个最新版本
     *
     * @param mainId
     * @return
     */
    public CommonVersion findNewestVersion(Long mainId) {
        CommonVersion newestVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, getBusinessModule().name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        return newestVersion;
    }

    public CommonVersion findSpecificLatestVersion(Long mainId, String version) {
        LambdaQueryWrapper<CommonVersion> query = Wrappers.lambdaQuery();
        query.eq(CommonVersion::getMainId, mainId);
        query.eq(CommonVersion::getModule, this.getBusinessModule().name());
        query.eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL);
        query.lt(CommonVersion::getVersion, version);
        query.orderByDesc(CommonVersion::getVersion);
        query.last(StringUtil.mysqlLimitOne());
        return commonVersionMapper.selectOne(query);
    }

    /**
     * 判断数据是否发生变动
     * 要实现需要子模块自实现 但并不是每个模块都需要
     * @param mainId
     * @return
     */
    public ChangeDTO checkActualChange(Long mainId) {
        throw new UnsupportedOperationException();
    }

    /**
     * 把版本表最新版本数据回写到临时表
     *
     * @param mainId
     */
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId) {
        reset(mainId, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId, Integer targetVersionType) {
        T baseModel = baseMapper.selectById(mainId);
        if (Objects.isNull(baseModel)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        LambdaQueryWrapper<CommonVersion> qw = Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, mainId)
                .eq(CommonVersion::getModule, getBusinessModule());
        if (ObjectUtil.isNotEmpty(targetVersionType)) {
            qw.eq(CommonVersion::getVersionType, targetVersionType);
        } else {
            qw.eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL);
        }
        qw.orderByDesc(CommonVersion::getVersion).last("LIMIT 1");
        CommonVersion commonVersion = commonVersionMapper.selectOne(qw);
        if (Objects.nonNull(commonVersion)) {
            customReset(baseModel, commonVersion);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public abstract void customReset(T t, CommonVersion commonVersion);


    /**
     * 数据校验
     * 直接抛出异常
     *
     * @param mainId
     */
    public void validateData(Long mainId) {

    }

    /**
     * 客户版本间数据比较
     *
     * @param id
     * @return
     */
    public CommonVersionDiffRSP comparePreVersion(Long id) {
        CommonVersion newVersion = commonVersionMapper.selectById(id);
        if (Objects.isNull(newVersion)) {
            throw new MithrasException("版本不存在");
        }
        CommonVersion oldVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                .eq(CommonVersion::getMainId, newVersion.getMainId())
                .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                .eq(CommonVersion::getModule, newVersion.getModule())
                .lt(CommonVersion::getVersion, newVersion.getVersion())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        if (Objects.isNull(oldVersion)) {
            throw new MithrasException("版本不存在");
        }
        return doCompare(newVersion, oldVersion);
    }

    /**
     * 比较逻辑
     *
     * @param newVersion
     * @param oldVersion
     * @return
     */
    public abstract CommonVersionDiffRSP doCompare(CommonVersion newVersion, CommonVersion oldVersion);

    /**
     * 查询version列表
     *
     * @param req
     * @return
     */
    public PageR<CommonVersionListRSP> selectPage(CommonVersionListREQ req) {
        T baseModel = baseMapper.selectById(req.getMainId());
        Page<CommonVersion> commonVersionPage = commonVersionMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<CommonVersion>lambdaQuery().eq(CommonVersion::getMainId, req.getMainId())
                        .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(CommonVersion::getModule, req.getModule()));
        if (CollectionUtils.isEmpty(commonVersionPage.getRecords())) {
            return PageR.of(new ArrayList<>(), commonVersionPage.getTotal(),
                    commonVersionPage.getPages(),
                    commonVersionPage.getCurrent(),
                    commonVersionPage.getSize());
        }

        // 处理最后操作人用户名
        Map<Long, String> userNameMap = id2NameService.sysUserId2Name(commonVersionPage.getRecords().stream().map(CommonVersion::getUpdateBy)
                .filter(Objects::nonNull).collect(Collectors.toSet()));


        // 转返回值
        List<CommonVersionListRSP> rspList = commonVersionPage.getRecords().stream()
                .map(cv -> {
                    CommonVersionListRSP rsp = convertPageRsp(cv, baseModel, userNameMap);
                    rsp.setCanCompare(1);
                    rsp.setOperatorId(cv.getUpdateBy());
                    rsp.setOperatorName(userNameMap.get(cv.getCreateBy()));
                    return rsp;
                })
                .collect(Collectors.toList());
        if (Objects.equals(1, req.getPage())) {
            // 第一页的第一个版本不能与前一版本比较
            rspList.get(0).setCanCompare(0);
        }
        return PageR.of(rspList, commonVersionPage.getTotal(),
                commonVersionPage.getPages(),
                commonVersionPage.getCurrent(),
                commonVersionPage.getSize());
    }

    /**
     * @param cv
     * @param baseModel
     * @param userNameMap
     * @return
     */
    protected abstract CommonVersionListRSP convertPageRsp(CommonVersion cv, T baseModel, Map<Long, String> userNameMap);

    /**
     * @return
     */
    public abstract BusinessModuleEnum getBusinessModule();

}
