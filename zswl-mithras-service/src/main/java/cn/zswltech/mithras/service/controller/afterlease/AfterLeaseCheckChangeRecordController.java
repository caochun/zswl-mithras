package cn.zswltech.mithras.service.controller.afterlease;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.api.afterlease.AfterLeaseCheckChangeRecordApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckChangeRecordListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckChangeRecordListRSP;
import cn.zswltech.mithras.afterlease.infrastructure.persistence.mapper.model.AfterLeaseCheckChangeRecord;
import cn.zswltech.mithras.system.service.Id2NameService;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckChangeRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description 租后检查计划-基本信息表
* @author vico
* @date 2024-04-22
*/
@RestController
public class AfterLeaseCheckChangeRecordController implements AfterLeaseCheckChangeRecordApi {

    @Resource
    private AfterLeaseCheckChangeRecordService afterLeaseCheckChangeRecordService;
    @Resource
    private Id2NameService id2NameService;


    @Override
    public R<PageR<AfterLeaseCheckChangeRecordListRSP>> list(AfterLeaseCheckChangeRecordListREQ req){
        Page<AfterLeaseCheckChangeRecord> data = afterLeaseCheckChangeRecordService.list(req);
        List<AfterLeaseCheckChangeRecordListRSP> list = BeanUtil.copyToList(data.getRecords(), AfterLeaseCheckChangeRecordListRSP.class);
        if(CollectionUtil.isNotEmpty(list)){
            Map<Long, String> userId2Name = id2NameService.sysUserId2Name(list.stream().map(AfterLeaseCheckChangeRecordListRSP::getCreateBy).collect(Collectors.toList()));
            list.forEach(e -> {
                e.setCreateByName(userId2Name.get(e.getCreateBy()));
            });
        }
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }


}