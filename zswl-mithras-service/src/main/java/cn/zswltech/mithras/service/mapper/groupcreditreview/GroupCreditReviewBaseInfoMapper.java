package cn.zswltech.mithras.service.mapper.groupcreditreview;
import cn.zswltech.mithras.service.mapper.dto.GroupCreditReviewListSelectDTO;
import cn.zswltech.mithras.common.plugin.CustomBaseMapper;
import cn.zswltech.mithras.service.mapper.model.groupcreditreview.GroupCreditReviewBaseInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
* @description 集团授信评审基本信息表
* @author wangchuanhao
* @date 2022-11-11
*/
public interface GroupCreditReviewBaseInfoMapper extends CustomBaseMapper<GroupCreditReviewBaseInfo> {

    Page<GroupCreditReviewBaseInfo> myList(Page<GroupCreditReviewBaseInfo> page,
                                              @Param("dto") GroupCreditReviewListSelectDTO selectDTO);

}