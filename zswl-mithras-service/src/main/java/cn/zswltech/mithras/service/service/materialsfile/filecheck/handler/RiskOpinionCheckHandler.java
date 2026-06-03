package cn.zswltech.mithras.service.service.materialsfile.filecheck.handler;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.dto.file.FileListRSP;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.model.MaterialsList;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.materialsfile.MaterialsListService;
import cn.zswltech.mithras.service.service.materialsfile.filecheck.FileModuleCheck;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionMonitorService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.MithrasException.err;
import static cn.zswltech.mithras.service.others.SpringContextHolder.getBean;

/**
 * @create: 2023-03-09
 **/

@Component
public class RiskOpinionCheckHandler extends FileModuleCheck {


    @Override
    public void checkUpload(String moduleKey, Long mainId, String materialsType) {
        if (Objects.isNull(mainId)) {
            throw new AuthCheckException("id不能为空");
        }
        RiskControlOpinionMonitor opinion = getBean(RiskControlOpinionMonitorService.class).getById(mainId);
        err(isNull(opinion), "舆情记录不存在");
        Client client = getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery().eq(Client::getUscCode, opinion.getCreditCode()));
        err(isNull(client), "客户不存在");
        //err(ObjectUtil.isEmpty(client.getBelongSponsorId()) || ObjectUtil.notEqual(client.getBelongSponsorId(), AccountUtil.getLoginInfo().getId()), "只有对应客户的主办才能操作");
    }



    @Override
    public void checkDownload(String moduleKey, Long mainId, List<Long> fileIds) {
    }

    @Override
    public void checkRemove(String moduleKey, List<Long> fileIds) {
    }

    @Override
    public void checkList(String moduleKey, Long fileId) {
    }

    @Override
    public void checkRemove(String moduleKey, Long fileId) {
        MaterialsList materialsList = getBean(MaterialsListService.class).getById(fileId);
        Long mainId = materialsList.getBelongId();
        RiskControlOpinionMonitor opinion = getBean(RiskControlOpinionMonitorService.class).getById(mainId);
        err(isNull(opinion), "舆情记录不存在");
        Client client = getBean(ClientService.class).getOne(Wrappers.<Client>lambdaQuery().eq(Client::getUscCode, opinion.getCreditCode()));
        err(isNull(client), "客户不存在");
        //err(ObjectUtil.isEmpty(client.getBelongSponsorId()) || ObjectUtil.notEqual(client.getBelongSponsorId(), AccountUtil.getLoginInfo().getId()), "只有对应客户的主办才能操作");
    }

    @Override
    public String getModuleKey() {
        return BusinessModuleEnum.RISK_OPINION.name();
    }
}
