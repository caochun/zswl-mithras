package cn.zswltech.mithras.message.mapper;

import cn.zswl.notice.model.CommonQry;
import cn.zswltech.mithras.message.model.ZhfkNotice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
/**
* @description 通知消息
* @author vico
* @date 2024-03-12
*/
public interface ZhfkNoticeMapper extends BaseMapper<ZhfkNotice> {

    Page<ZhfkNotice> getList(Page<ZhfkNotice> page, @Param("dto") CommonQry commonQry);
}