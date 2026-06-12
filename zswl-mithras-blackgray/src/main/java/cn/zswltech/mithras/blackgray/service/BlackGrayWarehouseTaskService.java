package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskAddREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskListREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskModifyREQ;
import cn.zswltech.mithras.blackgray.dto.req.BlackGrayWarehouseTaskRemoveREQ;
import cn.zswltech.mithras.blackgray.persistence.model.BlackGrayWarehouseTask;

/**
* @description 黑灰名单任务表
* @author 
* @date 2024-01-16
*/
public interface BlackGrayWarehouseTaskService {

    Long add(BlackGrayWarehouseTaskAddREQ req);

    void modify(BlackGrayWarehouseTaskModifyREQ req);

    BlackGrayWarehouseTask detail(Long id);

    PageR<BlackGrayWarehouseTask> list(BlackGrayWarehouseTaskListREQ req);

    void updateStatue(Long id, Integer status, Long taskId);

    void remove(BlackGrayWarehouseTaskRemoveREQ req);

}