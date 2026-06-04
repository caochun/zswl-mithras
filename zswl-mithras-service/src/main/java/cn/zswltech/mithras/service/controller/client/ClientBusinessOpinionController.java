package cn.zswltech.mithras.service.controller.client;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.client.ClientBusinessOpinionApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionAddREQ;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionListREQ;
import cn.zswltech.mithras.dto.client.ClientBusinessOpinionListRSP;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBusinessOpinion;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.service.service.client.ClientBusinessOpinionService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description 客户工商信息处理意见表
* @author vico
* @date 2023-09-11
*/
@RestController
public class ClientBusinessOpinionController implements ClientBusinessOpinionApi {

    @Resource
    private ClientBusinessOpinionService clientBusinessOpinionService;
    @Resource
    private Id2NameService id2NameService;

    @Override
    public R<Void> add(ClientBusinessOpinionAddREQ req) {
        clientBusinessOpinionService.modifyOpinion(req);
        return R.ok();
    }


    @Override
    public R<PageR<ClientBusinessOpinionListRSP>> list(ClientBusinessOpinionListREQ req){
        Page<ClientBusinessOpinion> data = clientBusinessOpinionService.page(new Page<>(req.getPage(), req.getPageSize()),
                Wrappers.<ClientBusinessOpinion>lambdaQuery()
        .eq(ClientBusinessOpinion::getContractId, req.getContractId()));
        List<ClientBusinessOpinionListRSP> list = BeanUtil.copyToList(data.getRecords(), ClientBusinessOpinionListRSP.class);
        List<Long> userIds = list.stream().map(ClientBusinessOpinionListRSP::getCreateBy).collect(Collectors.toList());
        Map<Long, String> userId2Name = id2NameService.sysUserId2Name(userIds);
        list.forEach(client -> {
            client.setCreateByName(userId2Name.get(client.getCreateBy()));
            client.setCreateDate(Optional.ofNullable(client.getCreateTime()).map(LocalDateTime::toLocalDate).orElse(null));
        });
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }


}