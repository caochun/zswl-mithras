package cn.zswltech.mithras.customer.mobile.persistence.mapper;

import cn.zswltech.mithras.customer.dto.VisitRecordListParam;
import cn.zswltech.mithras.customer.mobile.persistence.model.VisitRecord;
import cn.zswltech.mithras.foundation.persistence.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
public interface VisitRecordMapper extends CustomBaseMapper<VisitRecord> {

    Page<VisitRecord> myList(Page page, @Param("p") VisitRecordListParam param);
}
