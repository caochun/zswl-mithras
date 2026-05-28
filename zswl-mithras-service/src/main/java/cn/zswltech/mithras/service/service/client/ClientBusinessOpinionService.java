package cn.zswltech.mithras.service.service.client;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionAddREQ;
import cn.zswltech.mithras.dto.contract.ContractCompareBusinessRSP;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.mapper.client.ClientBusinessOpinionMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBusinessOpinion;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 客户工商信息处理意见表
* @author vico
* @date 2023-09-11
*/
@Service
public class ClientBusinessOpinionService extends ServiceImpl<ClientBusinessOpinionMapper, ClientBusinessOpinion> {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    @Transactional(rollbackFor = Throwable.class)
    public void modifyOpinion(ClientBusinessOpinionAddREQ req){
        ClientBusinessOpinion clientBusinessOpinion = this.baseMapper.selectOne(Wrappers.<ClientBusinessOpinion>lambdaQuery()
                .eq(ClientBusinessOpinion::getFlowId, req.getFlowId())
                .eq(ClientBusinessOpinion::getNodeName, req.getNodeName())
                .last(StringUtil.mysqlLimitOne()));
        if(clientBusinessOpinion == null){
            clientBusinessOpinion = new ClientBusinessOpinion();
        }
        clientBusinessOpinion.setFlowId(req.getFlowId());
        clientBusinessOpinion.setModuleName(req.getModuleName());
        clientBusinessOpinion.setNodeName(req.getNodeName());
        clientBusinessOpinion.setContractId(req.getContractId());
        clientBusinessOpinion.setOpinion(req.getOpinion());
        this.saveOrUpdate(clientBusinessOpinion);
    }

    public void checkOpinion(String flowId, String moduleName, String nodeName, Long contractId){
        List<ContractCompareBusinessRSP> contractCompareBusinessRSPS = contractBaseInfoService.compareBusiness(contractId, true);
        StringBuilder sb = new StringBuilder();
        ClientBusinessOpinion clientBusinessOpinion = baseMapper.selectOne(Wrappers.<ClientBusinessOpinion>lambdaQuery()
                .eq(ClientBusinessOpinion::getFlowId, flowId)
                .eq(ClientBusinessOpinion::getModuleName, moduleName)
                .eq(ClientBusinessOpinion::getNodeName, nodeName)
                .last(StringUtil.mysqlLimitOne()));
        if(CollectionUtil.isNotEmpty(contractCompareBusinessRSPS)){
            contractCompareBusinessRSPS.forEach( rsp -> {
                if(YesOrNoNumberEnum.YES.getCode().equals(rsp.getChangeFlag())){
                    if(ObjectUtil.isEmpty(clientBusinessOpinion) || ObjectUtil.isEmpty(clientBusinessOpinion.getOpinion())) {
                        sb.append(rsp.getClientType());
                        sb.append(":");
                        sb.append(rsp.getClientName());
                        sb.append(";");
                    }
                }
            });
        }
        if(sb.length() > 0){
            sb.append("存在交易结构中承租人/担保人信息与外数校验不一致,请前往基本信息页面完成工商信息校验!");
            throw new MithrasException(sb.toString());
        }

    }

}