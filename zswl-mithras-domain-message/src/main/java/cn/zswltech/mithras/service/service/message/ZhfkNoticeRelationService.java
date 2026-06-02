package cn.zswltech.mithras.service.service.message;

import cn.zswltech.mithras.service.mapper.message.ZhfkNoticeRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ZhfkNoticeRelationService extends IService<ZhfkNoticeRelation> {

     Long idToMsgId(Long id);

     List<Long> idToMsgIdList(List<Long> ids);

     List<ZhfkNoticeRelation> listByNoticeIds(List<Long> noticeIds);

}
