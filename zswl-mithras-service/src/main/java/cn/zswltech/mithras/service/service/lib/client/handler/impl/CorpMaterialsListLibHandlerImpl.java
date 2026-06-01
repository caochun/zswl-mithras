package cn.zswltech.mithras.service.service.lib.client.handler.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.ListBaseRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.enums.InfoModule;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.client.CorporationClientMaterialSubTypeEnum;
import cn.zswltech.mithras.service.mapper.dto.ChangeDTO;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.CommonVersion;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.MaterialsListLib;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.others.LackDataException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.lib.FileCompareDeclaration;
import cn.zswltech.mithras.service.service.lib.MaterialsListLibHandlerProxy;
import cn.zswltech.mithras.service.service.lib.client.handler.ClientLibAbstractHandler;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.util.ClientAuthorityUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 客户 法人 资料清单
 *
 * @author wangchuanhao
 * @date 2022/12/14 7:37 PM
 */
@Component
public class CorpMaterialsListLibHandlerImpl extends ClientLibAbstractHandler<MaterialsListLib, MaterialsList, ListBaseRSP> implements FileCompareDeclaration {

    @Resource
    private MaterialsListLibHandlerProxy materialsListLibHandlerProxy;

    @Resource
    private ClientAuthorityUtil authorityUtil;

    @Override
    protected MaterialsListLib entity2Lib(MaterialsList f) {
        return materialsListLibHandlerProxy.entity2Lib(f);
    }

    @Override
    protected MaterialsList lib2Entity(MaterialsListLib t) {
        return materialsListLibHandlerProxy.lib2Entity(t);
    }

    @Override
    protected ListBaseRSP lib2Rsp(MaterialsListLib f) {
        return materialsListLibHandlerProxy.lib2Rsp(f);
    }

    @Override
    public InfoModule getSubModule() {
        return InfoModule.CORP_MATERIALS_LIST;
    }

    @Override
    public boolean needHandle(Long clientId, ClientType clientType) {
        return ClientType.CORPORATION.equals(clientType);
    }

    @Override
    public BusinessModuleEnum businessModuleEnum() {
        return BusinessModuleEnum.CLIENT;
    }

    /**
     * 过滤出需要处理的编辑区数据 有过滤条件的自实现
     *
     * @param mainId
     * @return
     */
    @Override
    public List<MaterialsList> listNeedHandleEntity(Long mainId, Map<String, Object> extraMap) {
        // 文件表需要特殊处理，不能用默认逻辑
        Client client = SpringUtil.getBean(ClientService.class).getById(mainId);
        if (Objects.isNull(client)) {
            throw new MithrasException("客户数据不存在");
        }
        if (Objects.equals(client.getClientType(), ClientType.NORMAL.name())) {
            return this.listNeedHandleEntity(mainId);
        }
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBelongId, mainId);
        query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
        if (SpringUtil.getBean(ClientAuthorityUtil.class).isIntraGroupCollaboration(client.getId())) {
//            return this.listNeedHandleEntity(mainId);
            return SpringUtil.getBean(MaterialsListService.class).list(query);
        }
        Long userId = (Long) extraMap.get("userId");
        query.eq(BaseModel::getCreateBy, userId);
        return SpringUtil.getBean(MaterialsListService.class).list(query);
    }

    @Override
    public List<MaterialsList> listNeedHandleEntity(Long mainId) {
        throw new MithrasException("暂未支持");
    }

    /**
     * 过滤出需要处理的版本区数据 有过滤条件的自实现
     *
     * @param mainId
     * @return
     */
    @Override
    public List<MaterialsListLib> listNeedHandleLib(Long mainId, String version) {
        return materialsListLibHandlerProxy.listNeedHandleLib(mainId, version, businessModuleEnum());
    }

    @Override
    public String entityMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public String libMainIdFieldName() {
        return "belong_id";
    }

    @Override
    public void validateData(Client client) {
        if (Objects.equals(client.getClientType(), ClientType.CORPORATION.name())) {
            Long currentUserId = AccountUtil.getLoginInfo().getId();
            LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
            query.eq(MaterialsList::getBelongId, client.getId());
            query.eq(MaterialsList::getBusinessType, BusinessModuleEnum.CLIENT.name());
            query.in(MaterialsList::getMaterialSubType, ListUtil.of(CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE.name(), CorporationClientMaterialSubTypeEnum.LEASE_APPLICATION.name(), CorporationClientMaterialSubTypeEnum.CREDIT_LETTER.name()));
            // 公海客户的话不需要指定上传人
            if (!authorityUtil.isIntraGroupCollaboration(client.getId())) {
                query.eq(BaseModel::getCreateBy, currentUserId);
            }
            // 过滤其他非客户管理上传的客户资料
            query.eq(MaterialsList::getSourceBusinessKey, "");
            List<MaterialsList> list = draftMapper.selectList(query);
            boolean hasBusinessLicense = false;
            boolean hasLeaseApplication = false;
            boolean hasCreditLetter = false;
            if (CollectionUtil.isNotEmpty(list)) {
                for (MaterialsList materialsList : list) {
                    if (Objects.equals(materialsList.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.BUSINESS_LICENSE.name())) {
                        hasBusinessLicense = true;
                    }
                    if (Objects.equals(materialsList.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.LEASE_APPLICATION.name())) {
                        hasLeaseApplication = true;
                    }
                    if (Objects.equals(materialsList.getMaterialSubType(), CorporationClientMaterialSubTypeEnum.CREDIT_LETTER.name())) {
                        hasCreditLetter = true;
                    }
                }
            }
            if (!hasBusinessLicense) {
                throw new LackDataException("<基础资料-营业执照>必须上传");
            }
            if (!hasLeaseApplication && !hasCreditLetter) {
                throw new LackDataException("<业务申请书-业务申请书>和<征信授权书-征信授权书>至少需要上传其中一个");
            }
        }
    }
}
