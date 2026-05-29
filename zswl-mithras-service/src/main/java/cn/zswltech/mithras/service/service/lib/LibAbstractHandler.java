package cn.zswltech.mithras.service.service.lib;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.dto.version.CommonVersionDiffBO;
import cn.zswltech.mithras.dto.version.DiffValue;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.service.mapper.tag.ILib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.util.CompareUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.common.collect.Sets;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.service.util.CompareUtil.MODULE_CHANGED_FLAG_KEY;

/**
 * 版本处理器
 *
 * @author wangchuanhao
 * @date 2022/6/22 8:56 PM
 */
@Slf4j
public abstract class LibAbstractHandler<LIB extends ILib, ENTITY extends IEntity, RSP extends ListBaseRSP> {

    @Autowired
    protected BaseMapper<LIB> mapper;
    @Autowired
    protected IService<LIB> service;
    @Autowired
    protected CustomBaseMapper<ENTITY> draftMapper;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, Long mainId, boolean needClearLast, Integer versionType) {
        flushData(version, mainId, needClearLast, versionType, null);
    }

    /**
     * 把临时表数据 插入版本表
     *
     * @param version       版本
     * @param needClearLast 是否需要清空同版本旧数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void flushData(String version, Long mainId, boolean needClearLast, Integer versionType, Map<String, Object> extraMap) {
        if (needClearLast) {
            List<LIB> needDeleteLibList = listNeedHandleLib(mainId, version);
            if (CollectionUtils.isNotEmpty(needDeleteLibList)) {
                mapper.delete(Wrappers.<LIB>query().in(LIB.FIELD_ID, needDeleteLibList.stream().map(LIB::getId).collect(Collectors.toList())));
            }
        }
        List<ENTITY> draftDataList;
        if (this.businessModuleEnum() == BusinessModuleEnum.CLIENT) {
            draftDataList = listNeedHandleEntity(mainId, extraMap);
        } else {
            draftDataList = listNeedHandleEntity(mainId);
        }
        if (CollectionUtils.isNotEmpty(draftDataList)) {
            List<LIB> dataList = new LinkedList<>();
            for (ENTITY entity : draftDataList) {
                LIB lib = this.actualEntity2Lib(entity, version, versionType);
                if (Objects.nonNull(lib)) {
                    dataList.add(lib);
                }
            }
            if (CollectionUtil.isNotEmpty(dataList)) {
                service.saveBatch(dataList);
            }
        }
    }

    /**
     * 审批拒绝 把版本表最新的数据还原到 临时表
     *
     * @param version
     */
    @Transactional(rollbackFor = Exception.class)
    public void reset(Long mainId, String version) {
        // 清空临时表数据
        List<ENTITY> needDeleteEntityList = listNeedHandleEntity(mainId);
        if (CollectionUtils.isNotEmpty(needDeleteEntityList)) {
            draftMapper.delete(Wrappers.<ENTITY>query()
                    .in(LIB.FIELD_ID, needDeleteEntityList.stream().map(ENTITY::getId).collect(Collectors.toList())));
        }
        // 默认把生效版本的刷回编辑区 业务自扩展再说
        List<LIB> versionList = listNeedHandleLib(mainId, version);
        if (CollectionUtils.isNotEmpty(versionList)) {
            List<ENTITY> draftList = new ArrayList<>();
            for (LIB lib : versionList) {
                ENTITY entity = this.actualLib2Entity(lib);
                if (Objects.nonNull(entity)) {
                    draftList.add(entity);
                }
            }
            if (CollectionUtil.isNotEmpty(draftList)) {
                draftMapper.insertListWithId(draftList);
            }
        }
    }

    /**
     * 新旧版本数据比较
     *
     * @param newVersion
     * @param oldVersion
     * @return
     */
    public CommonVersionDiffBO libCompareLib(CommonVersion newVersion, CommonVersion oldVersion) {
        if (Objects.isNull(newVersion) || Objects.isNull(oldVersion)) {
            throw new MithrasException("数据比较的两个版本不能为空");
        }
        CommonVersionDiffBO<RSP> commonVersionDiffBO = new CommonVersionDiffBO<>();
        List<LIB> newVersionLIBList = listNeedHandleLib(newVersion.getMainId(), newVersion.getVersion());
        List<RSP> newVersionRSPList = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(newVersionLIBList)) {
            for (LIB lib : newVersionLIBList) {
                RSP rsp = this.actualLib2Rsp(lib);
                if (Objects.nonNull(rsp)) {
                    newVersionRSPList.add(rsp);
                }
            }
        }
        List<LIB> oldVersionLIBList = listNeedHandleLib(oldVersion.getMainId(), oldVersion.getVersion());
        List<RSP> oldVersionRSPList = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(oldVersionLIBList)) {
            for (LIB lib : oldVersionLIBList) {
                RSP rsp = this.actualLib2Rsp(lib);
                if (Objects.nonNull(rsp)) {
                    oldVersionRSPList.add(rsp);
                }
            }
        }
        // 根据数据id取值
        Map<Long, RSP> oldVersionRSPMap = oldVersionRSPList.stream().collect(Collectors.toMap(RSP::getId, rsp -> rsp));
        List<Map<String, DiffValue>> diffDataList = newVersionRSPList.stream()
                .map(n -> {
                    Map<String, DiffValue> compareResult =
                            CompareUtil.compare(n, oldVersionRSPMap.get(n.getId()), compareIgnoreFieldNames());
                    if (compareResult.containsKey(MODULE_CHANGED_FLAG_KEY)) {
                        if (commonVersionDiffBO.getModuleChanged().equals(Boolean.FALSE)) {
                            commonVersionDiffBO.setModuleChanged(Boolean.TRUE);
                        }
                        compareResult.remove(MODULE_CHANGED_FLAG_KEY);
                    }
                    return compareResult;
                })
                .collect(Collectors.toList());
        commonVersionDiffBO.setBeforeData(oldVersionRSPList);
        commonVersionDiffBO.setAfterData(diffDataList);
        //判读数据条数是否一致
        if (commonVersionDiffBO.getModuleChanged().equals(Boolean.FALSE)) {
            commonVersionDiffBO.setModuleChanged(!(newVersionRSPList.size() == oldVersionRSPList.size()));
        }
        return commonVersionDiffBO;
    }

    /**
     * 编辑区数据和最新版本数据比较 判断数据是否发生变更
     * 用于判断是否可生成新版本、是否需要审批
     *
     * @return
     */
    public ChangeDTO checkActualChange(CommonVersion newestVersion) {
        ChangeDTO changeDTO = new ChangeDTO();

        List<ENTITY> versionList = listNeedHandleLib(newestVersion.getMainId(), newestVersion.getVersion())
                .stream()
                .map(this::actualLib2Entity)
                .collect(Collectors.toList());
        List<ENTITY> draftList = listNeedHandleEntity(newestVersion.getMainId());
        if (this instanceof FileCompareDeclaration) {
            return ((FileCompareDeclaration) this).fileCheckActualChange(versionList, draftList);
        }
        Map<Long, ENTITY> versionMap = versionList.stream().collect(Collectors.toMap(ENTITY::getId, e -> e));
        Set<Long> draftIdSet = draftList.stream().map(ENTITY::getId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(Sets.difference(draftIdSet, versionMap.keySet()))
                || CollectionUtils.isNotEmpty(Sets.difference(versionMap.keySet(), draftIdSet))) {
            // 先根据id比较 如果数据有新增或删除 就是变更了
            changeDTO.setChangeFlag(true);
            changeDTO.setNeedApprovalChangeFlag(true);
            return changeDTO;
        }
        // 再逐条比较数据，判断是否发生变更
        for (ENTITY draft : draftList) {
            ENTITY versionData = versionMap.get(draft.getId());
            ChangeDTO columnChangeDTO = CompareUtil.checkActualChange(draft, versionData);
            if (Boolean.TRUE.equals(columnChangeDTO.getNeedApprovalChangeFlag())) {
                // 快速返回
                changeDTO.setChangeFlag(true);
                changeDTO.setNeedApprovalChangeFlag(true);
                return changeDTO;
            } else if (Boolean.TRUE.equals(columnChangeDTO.getChangeFlag())) {
                // 还需要找到最坏情况
                changeDTO.setChangeFlag(true);
            }
        }
        return changeDTO;
    }

    /**
     * 根据版本区id，查找此版本区id对应的数据的最新版本数据
     * 会调用
     * @see this#queryLatestDataByOriginId
     * @param libId
     * @return
     */
    public LIB queryLatestDataByLibId(Long libId) {
        LIB lib = mapper.selectById(libId);
        if (Objects.isNull(lib)) {
            return null;
        }
        return queryLatestDataByOriginId(lib.getOriginId());
    }

    /**
     * 根据编辑区id 查找该数据的最新版本（生效数据）
     * 如果返回null 说明
     * 1.该originId不存在版本数据
     * 2.该数据对应的主表数据最新版本 和该数据的版本对不上
     * @param originId
     * @return
     */
    public LIB queryLatestDataByOriginId(Long originId) {
        // 找到版本里最新的数据
        LIB latestLib = mapper.selectOne(Wrappers.<LIB>query()
                        .eq(ILib.FIELD_ORIGIN_ID, originId)
                        .eq(ILib.FIELD_VERSION_TYPE, VersionTypeConstants.NORMAL)
                        .orderByDesc(ILib.FIELD_VERSION)
                        .last("LIMIT 1"));
        if (Objects.isNull(latestLib)) {
            return null;
        }
        // 如果是主表的LibHandler，就不用再查主表
        if (isMainTable()) {
            return latestLib;
        }
        BusinessModuleEnum businessModuleEnum = businessModuleEnum();
        // 找到主表最新版本
        CommonVersion mainTableLatestVersion = commonVersionMapper.selectOne(Wrappers.<CommonVersion>lambdaQuery()
                        .eq(CommonVersion::getVersionType, VersionTypeConstants.NORMAL)
                        .eq(CommonVersion::getModule, businessModuleEnum.name())
                        .eq(CommonVersion::getMainId, latestLib.getMainId())
                        .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        // 如果主表最新版本不存在 返回空
        if (!Objects.equals(latestLib.getVersion(), mainTableLatestVersion.getVersion())) {
            return null;
        }
        return latestLib;
    }

    public List<LIB> listLatestByOriginIds(Collection<Long> originIds) {
        List<LIB> dataList = mapper.selectList(Wrappers.<LIB>query()
                .select("max(id) as id")
                .in(ILib.FIELD_ORIGIN_ID, originIds)
                .eq(ILib.FIELD_VERSION_TYPE, VersionTypeConstants.NORMAL)
                .groupBy(ILib.FIELD_ORIGIN_ID)
        );
        if (CollectionUtil.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<Long> ids = dataList.stream().map(IEntity::getId).collect(Collectors.toList());
        return mapper.selectBatchIds(ids);
    }

    protected abstract LIB entity2Lib(ENTITY f);

    public LIB actualEntity2Lib(ENTITY f, String version, Integer versionType) {
        LIB t = entity2Lib(f);
        if (Objects.isNull(t)) {
            return null;
        }
        t.setId(null);
        t.setOriginId(f.getId());
        t.setVersion(version);
        // 记录版本数据的操作人及审计数据
        t.setUpdateBy(f.getUpdateBy());
        t.setCreateBy(f.getUpdateBy());
        t.setCreateTime(LocalDateTime.now());
        t.setUpdateTime(LocalDateTime.now());
        // 记录原数据的操作人及审计数据
        t.setDataCreateBy(f.getCreateBy());
        t.setDataCreateTime(f.getCreateTime());
        t.setDataUpdateBy(f.getUpdateBy());
        t.setDataUpdateTime(f.getUpdateTime());
        // 版本类型
        t.setVersionType(versionType);
        return t;
    }

    /**
     * 业务数据转换 处理器内部使用
     *
     * @param t
     * @return
     */
    protected abstract ENTITY lib2Entity(LIB t);

    /**
     * 实际转换 统一处理id、createTime等字段
     * 外部可调
     *
     * @param t
     * @return
     */
    public ENTITY actualLib2Entity(LIB t) {
        ENTITY f = lib2Entity(t);
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

    public RSP actualLib2Rsp(LIB f) {
        RSP rsp = lib2Rsp(f);
        rsp.setId(f.getOriginId());
        return rsp;
    }

    public List<RSP> actualLib2RspList(List<LIB> fList) {
        if (CollectionUtils.isEmpty(fList)) {
            return new ArrayList<>();
        }
        List<RSP> rspList = lib2RspList(fList);
        Map<Long, LIB> libIdMap = fList.stream().collect(Collectors.toMap(l -> l.getId(), l -> l));
        // 子类不能自己setId了 请注意
        rspList.forEach(r -> r.setId(Optional.ofNullable(libIdMap.get(r.getId())).map(LIB::getOriginId).orElse(null)));
        return rspList;
    }

    protected abstract RSP lib2Rsp(LIB f);

    protected List<RSP> lib2RspList(List<LIB> fList){
        return new ArrayList<>();
    };

    public abstract String libMainIdFieldName();

    public abstract String entityMainIdFieldName();

    public abstract Set<String> compareIgnoreFieldNames();

    public abstract BusinessModuleEnum businessModuleEnum();

    /**
     * 该libHandler 是否是主表
     * @return
     */
    public boolean isMainTable() {
        return false;
    }

    public List<ENTITY> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        throw new MithrasException("暂不支持的操作");
    }

    /**
     * 过滤出需要处理的编辑区数据 有过滤条件的自实现
     * 默认抄全表
     * @param mainId
     * @return
     */
    public List<ENTITY> listNeedHandleEntity(Long mainId) {
        List<ENTITY> draftDataList = draftMapper.selectList(Wrappers.<ENTITY>query()
                .eq(entityMainIdFieldName(), mainId));
        return draftDataList;
    }

    /**
     * 过滤出需要处理的版本区数据 有过滤条件的自实现
     * 默认抄全表
     * @param mainId
     * @return
     */
    public List<LIB> listNeedHandleLib(Long mainId, String version) {
        List<LIB> versionList = mapper.selectList(Wrappers.<LIB>query()
                .eq(ILib.FIELD_VERSION, version)
                .eq(libMainIdFieldName(), mainId));
        return versionList;
    }

}
