package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.common.annotation.BirCompareColumn;
import cn.zswltech.mithras.common.constant.ResultMsg;
import cn.zswltech.mithras.common.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.client.DomesticOrAbroad;
import cn.zswltech.mithras.service.mapper.client.ClientMapper;
import cn.zswltech.mithras.service.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.lib.CommonVersionService;
import cn.zswltech.mithras.service.service.lib.client.dto.CorpCommerceInfoCompareDTO;
import cn.zswltech.mithras.service.util.BirCompareUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @Description: 工商信息必填变更才做流程审批
 * @Author: huangping
 * @Date: 2025/11/25  17:40
 * @Version: 1.0
 */
@Service
@Slf4j
public class ClientChangeCheckService {




    @Resource
    private ClientMapper clientMapper;

    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;

    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;

    @Resource
    protected CommonVersionMapper commonVersionMapper;




    /**
     * @param clientId
     * @return int2无需设置按以前的逻辑走  1需要审批[以前的逻辑更广泛]  0无需审批
     */
    public int checkCommerceInfoChange(Long clientId) {
        Client client = clientMapper.selectById(clientId);
        if (Objects.isNull(client)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        //自然人去除
        if (!ClientType.CORPORATION.name().equals(client.getClientType())) {
            return 2;
        }
        //找不到版本 我们不修改 由之前的逻辑进行判断
        CommonVersion newestVersion = findNewestVersion(clientId);
        if (Objects.isNull(newestVersion)) {
            return 2;
        }
        //查询版本
        LambdaQueryWrapper<CorpCommerceInfoLib> libQuery = Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                .eq(CorpCommerceInfoLib::getVersion, newestVersion.getVersion())
                .eq(CorpCommerceInfoLib::getClientId, newestVersion.getMainId());
        List<CorpCommerceInfoLib> versionList = corpCommerceInfoLibMapper.selectList(libQuery);
        CorpCommerceInfoLib corpCommerceInfoLib = versionList.get(0);
        CorpCommerceInfoCompareDTO infoLibDto = new CorpCommerceInfoCompareDTO();
        BeanUtil.copyProperties(corpCommerceInfoLib, infoLibDto);
        //查询草稿，目前仅查询
        LambdaQueryWrapper<CorpCommerceInfo> query = Wrappers.<CorpCommerceInfo>lambdaQuery()
                .eq(CorpCommerceInfo::getClientId, newestVersion.getMainId());
        List<CorpCommerceInfo> draftList = commerceInfoMapper.selectList(query);
        CorpCommerceInfo draft = draftList.get(0);
        CorpCommerceInfoCompareDTO daaftDTO = new CorpCommerceInfoCompareDTO();
        BeanUtil.copyProperties(draft, daaftDTO);
        BirCompareColumn.CompareLv compareLv = BirCompareColumn.CompareLv.ABROAD;
        //境外
        if (DomesticOrAbroad.DOMESTIC.name().equals(client.getDomesticOrAbroad())) {
            compareLv = BirCompareColumn.CompareLv.DOMESTIC;
            //判断当前是否为非集团客户   有变更  境内判断就能判断出来。
            if (!Objects.equals(1, daaftDTO.getGroupFlag())) {
                compareLv = BirCompareColumn.CompareLv.DOMESTIC_UNGROUP;
            }
        }
        ChangeDTO changeDTO = BirCompareUtil.checkActualChange(infoLibDto, daaftDTO, compareLv);
        if (Boolean.TRUE.equals(changeDTO.getNeedApprovalChangeFlag())) {
            return 1;
        }
        return 0;

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
                .eq(CommonVersion::getModule, BusinessModuleEnum.CLIENT.name())
                .orderByDesc(CommonVersion::getVersion)
                .last("LIMIT 1"));
        return newestVersion;
    }

    private static void collectField(Class<?> tableClass, Map<String, Field> fieldMap) {
        if (fieldMap == null) {
            fieldMap = new HashMap<>();
        }
        if (tableClass.equals(Object.class)) {
            return;
        }
        // 先找父类的字段，如有同名，子类覆盖父类
        Class<?> superClass = tableClass.getSuperclass();
        if (superClass != null
                && !superClass.equals(Object.class)
                && !Map.class.isAssignableFrom(superClass)
                && !Collection.class.isAssignableFrom(superClass)) {
            collectField(tableClass.getSuperclass(), fieldMap);
        }
        // 找本类的字段
        Field[] fields = tableClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            //排除静态字段
            if (!Modifier.isStatic(field.getModifiers())) {
                fieldMap.put(field.getName(), field);
            }
        }
    }

}
