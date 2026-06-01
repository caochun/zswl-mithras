package cn.zswltech.mithras.service.mapper.app;

import cn.zswltech.mithras.service.mapper.dto.VisitRecordListParam;
import cn.zswltech.mithras.service.mapper.model.app.VisitRecord;
import cn.zswltech.mithras.service.plugin.CustomBaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhouning
 * @date 2024/5/12 17:00
 */
public interface VisitRecordMapper extends CustomBaseMapper<VisitRecord> {

    Page<VisitRecord> myList(Page page, @Param("p") VisitRecordListParam param);
}
