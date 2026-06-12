package cn.zswltech.mithras.application.orchestration.adapter.policy;

import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.policy.application.port.PolicyProjectClientInfoPort;
import cn.zswltech.mithras.policy.application.port.model.PolicyProjectClientInfo;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PolicyProjectClientInfoPortAdapter implements PolicyProjectClientInfoPort {

    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;

    @Override
    public PolicyProjectClientInfo getByProjectId(Long projectId) {
        ProjReviewBaseInfo projectInfo = projReviewBaseInfoMapper.selectById(projectId);
        PolicyProjectClientInfo info = new PolicyProjectClientInfo();
        info.setProjectName(projectInfo.getProjName());
        info.setProjectCode(projectInfo.getProjCode());
        info.setClientId(projectInfo.getClientId());
        info.setProjectSponsorUserId(projectInfo.getProjSponsorUserId());
        info.setProjectCosponsorUserIds(projectInfo.getProjCosponsorUserIds());
        Client client = clientMapper.selectById(projectInfo.getClientId());
        if (client != null) {
            info.setClientName(client.getClientName());
        }
        return info;
    }
}
