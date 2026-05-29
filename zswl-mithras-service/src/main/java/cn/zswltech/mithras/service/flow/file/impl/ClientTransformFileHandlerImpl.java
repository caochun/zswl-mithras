package cn.zswltech.mithras.service.flow.file.impl;

import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.flow.file.IFileHandler;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.service.mapper.model.client.ClientTransferApply;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.client.ClientTransferApplyService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author dingqi
 * @date 2024/9/23
 * @description
 */
@Component
public class ClientTransformFileHandlerImpl implements IFileHandler {
    @Resource
    private MaterialsListService materialsListService;
    @Resource
    private ClientTransferApplyService clientTransferApplyService;

    @Override
    public List<MaterialsList> listFile(ProcessResp processResp, List<String> materialsTypeList) {
        ClientTransferApply clientTransferApply = clientTransferApplyService.findByBatchNo(processResp.getBusinessKey());
        if (Objects.isNull(clientTransferApply)) {
            throw new MithrasException("客户移交申请记录不存在");
        }
        Long currentUserId = AccountUtil.getLoginInfo().getId();
        LambdaQueryWrapper<MaterialsList> query = Wrappers.lambdaQuery();
        query.eq(MaterialsList::getBelongId, clientTransferApply.getId());
        query.eq(BaseModel::getCreateBy, currentUserId);
        return materialsListService.list(query);
    }

    @Override
    public BusinessModuleEnum businessModule() {
        return BusinessModuleEnum.CLIENT_TRANSFER;
    }

    @Override
    public String convertMaterialsType(String materialsType) {
        return "补充资料";
    }
}
