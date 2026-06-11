package cn.zswltech.mithras.message.service;

import cn.zswltech.mithras.message.model.ZhfkNoticeRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ZhfkNoticeRelationService extends IService<ZhfkNoticeRelation> {

     Long idToMsgId(Long id);

     List<Long> idToMsgIdList(List<Long> ids);

     List<ZhfkNoticeRelation> listByNoticeIds(List<Long> noticeIds);

}
