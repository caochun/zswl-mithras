package cn.zswltech.mithras.application.orchestration.document.file.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zswltech.mithras.dto.file.FileListREQ;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.document.enums.MaterialsType;
import cn.zswltech.mithras.document.enums.NormalMaterialsType;
import cn.zswltech.mithras.customer.enums.client.ClientType;
import cn.zswltech.mithras.application.orchestration.document.file.AbstractFileListProvider;
import cn.zswltech.mithras.document.file.bo.FileListExtQuery;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.document.model.MaterialsList;
import cn.zswltech.mithras.customer.mapper.model.client.Client;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 客户模块
 *
 * @author wangchuanhao
 * @date 2023/2/6 11:31 AM
 */
@Component
public class ClientFileListProvider extends AbstractFileListProvider {

    @Resource
    private ClientMapper clientMapper;

    @Override
    public BusinessModuleEnum getBusinessModule() {
        return BusinessModuleEnum.CLIENT;
    }

    @Override
    protected List<MaterialsList> listForGroup(FileListREQ req, FileListExtQuery extQuery) {
        List<MaterialsList> list = super.listForGroup(req, extQuery);
        // 隐藏其他业务模块上传的归属于该客户的资料
        if (CollectionUtil.isNotEmpty(list)) {
            list.removeIf(e -> StrUtil.isNotBlank(e.getSourceBusinessKey()));
        }
        return list;
    }

    @Override
    protected void sortGroup(List<List<FileListRSP>> groupRspList) {
        if (CollectionUtils.isEmpty(groupRspList)) {
            return;
        }
        Long belongId = groupRspList.stream().flatMap(Collection::stream).filter(rsp -> Objects.nonNull(rsp.getBelongId())).map(FileListRSP::getBelongId).findFirst().orElse(null);
        if (Objects.isNull(belongId)) {
            return;
        }
        Client client = clientMapper.selectById(belongId);
        if (ClientType.CORPORATION.name().equals(client.getClientType())) {
            groupRspList.sort(Comparator.comparing(rsp -> Optional.ofNullable(MaterialsType.of(rsp.get(0).getMaterialsType())).map(m -> m.order).orElse(Integer.MAX_VALUE)));
        } else {
            groupRspList.sort(Comparator.comparing(rsp -> Optional.ofNullable(NormalMaterialsType.of(rsp.get(0).getMaterialsType())).map(m -> m.order).orElse(Integer.MAX_VALUE)));
        }
    }
}
