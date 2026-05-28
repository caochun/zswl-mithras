package cn.zswltech.mithras.service.job;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.client.ClientStatus;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.mapper.model.client.Client;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.third.opinion.RiskManageOpinionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SyncFocusOpinionToRiskSystem {

    @Autowired
    private RiskManageOpinionService riskManageOpinionService;
    @Resource
    private ClientService clientService;

    /**
     * 1、同步舆情关注客户列表到风控系统
     * 全量同步
     */
    @XxlJob("syncFocusOpinionToRiskSystem")
    public void jobHandler() {
        try {
            log.info("同步舆情关注客户列表开始");
            List<Client> list = clientService.list(Wrappers.<Client>lambdaQuery()
                    .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT)
                    .isNotNull(Client::getUscCode)
                    .eq(Client::getClientType, ClientType.CORPORATION.name()));
            log.info("同步客户列表:[{}]", JSONUtil.toJsonStr(list.stream().map(Client::getId).collect(Collectors.toList())));
            //添加释放的法人客户
            List<Client> releasedList = clientService.list(Wrappers.<Client>lambdaQuery()
                    .eq(Client::getClientStatus, ClientStatus.NEW)
                    .eq(Client::getIsReleased, YesOrNoNumberEnum.YES.getCode())
                    .isNotNull(Client::getUscCode)
                    .eq(Client::getClientType, ClientType.CORPORATION.name()));
            if (ObjectUtil.isNotEmpty(releasedList)) {
                list.addAll(releasedList);
            }
            Boolean result = riskManageOpinionService.registerClient(list);
            if (Boolean.TRUE.equals(result)) {
                log.info("同步舆情关注客户列表结束");
            } else {
                log.error("1-同步舆情关注客户列表失败");
            }
        } catch (Exception e) {
            log.error("2-同步舆情关注客户列表失败");
        }
    }
}