package cn.zswltech.mithras.third.qiyuesuo.persistence.mapper;

import cn.zswltech.mithras.third.qiyuesuo.persistence.model.QiyuesuoInvokeLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 契约锁调用日志记录表 Mapper
 */
@Mapper
public interface QiyuesuoInvokeLogMapper extends BaseMapper<QiyuesuoInvokeLog> {
}
