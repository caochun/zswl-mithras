package cn.zswltech.mithras.customer.infrastructure.persistence.mapper.app;

import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.dto.VisitRecordListParam;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.AppContractSign;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
public interface AppContractSignMapper extends CustomBaseMapper<AppContractSign> {

}
