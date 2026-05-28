package cn.zswltech.mithras.service.overdue.infrastructure.dao.mapper;

import cn.zswltech.mithras.service.overdue.application.query.LitigationPageQuery;
import cn.zswltech.mithras.service.overdue.infrastructure.dao.model.LitigationRegistration;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
* @description 诉讼登记
* @author zhaozhengkang
* @date 2024-10-30
*/
public interface LitigationRegistrationMapper extends BaseMapper<LitigationRegistration> {

    Page<LitigationRegistration> advancedList(Page<Object> objectPage,
                                              @Param("query") LitigationPageQuery query);
}