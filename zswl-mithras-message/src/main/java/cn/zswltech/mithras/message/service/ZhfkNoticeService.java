package cn.zswltech.mithras.message.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.zswl.notice.model.CommonQry;
import cn.zswl.notice.model.NoticeVo;
import cn.zswl.notice.service.ExportService;
import cn.zswltech.gruul.biz.service.UserService;
import cn.zswltech.gruul.common.util.spring.SpringContextUtil;
import cn.zswltech.gruul.dao.dal.dao.UserDOMapper;
import cn.zswltech.gruul.dao.dal.entity.UserDO;
import cn.zswltech.mithras.message.persistence.mapper.ZhfkNoticeMapper;
import cn.zswltech.mithras.message.persistence.model.ZhfkNotice;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @description 通知消息
* @author vico
* @date 2024-03-12
*/
@Service
@Slf4j
public class ZhfkNoticeService extends ServiceImpl<ZhfkNoticeMapper, ZhfkNotice> {

    @Resource
    private ZhfkNoticeMapper zhfkNoticeMapper;
    @Resource
    private UserService userService;
    @Autowired(required = false)
    private ExportService exportService;

    @Transactional(rollbackFor = Throwable.class)
    public void read(Long messageId){
        if(ObjectUtil.isEmpty(messageId)){
            return;
        }
        LambdaUpdateWrapper<ZhfkNotice> update = new LambdaUpdateWrapper<>();
        //update.set(ZhfkNotice::getReadStatus, YesOrNoNumberEnum.YES.getCode());
        update.eq(ZhfkNotice::getId, messageId);
        baseMapper.update(null, update);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void read(List<Long> messageIds){
        if(CollectionUtil.isEmpty(messageIds)){
            return;
        }
        LambdaUpdateWrapper<ZhfkNotice> update = new LambdaUpdateWrapper<>();
        //update.set(ZhfkNotice::getReadStatus, YesOrNoNumberEnum.YES.getCode());
        update.in(ZhfkNotice::getId, messageIds);
        baseMapper.update(null, update);
    }

    public PageInfo<NoticeVo> list(CommonQry commonQry) {
        Page<ZhfkNotice> zhfkNoticePage = zhfkNoticeMapper.getList(new Page<>( commonQry.getPageNum(), commonQry.getPageSize()), commonQry);
        PageInfo<NoticeVo> noticeVoPageInfo = new PageInfo<>();
        List<NoticeVo> noticeVos = new ArrayList<>();
        zhfkNoticePage.getRecords().forEach(record -> {
            NoticeVo noticeVo = BeanUtil.copyProperties(record, NoticeVo.class);
           // noticeVo.setReadFlag(ObjectUtil.equals(YesOrNoNumberEnum.YES.getCode(), record.getReadStatus()));
            noticeVos.add(noticeVo);
        });
        noticeVoPageInfo.setList(noticeVos);
        noticeVoPageInfo.setTotal(zhfkNoticePage.getTotal());
        noticeVoPageInfo.setPageNum((int) zhfkNoticePage.getCurrent());
        noticeVoPageInfo.setPageSize((int)zhfkNoticePage.getSize());
        return noticeVoPageInfo;
    }

    //这里同步redis消息状态到数据库，不需要回滚
    public void syncRedisFlag(){
        //一次性脚本，借用消息组件的已读查询，依次获取客户已读消息
        StopWatch st = new StopWatch();
        st.start("准备用户");
        //获取所有用户
        List<UserDO> userDOS = SpringContextUtil.getBean(UserDOMapper.class).selectAll();
        //用户ID
        List<Long> userIds = userDOS.stream().map(UserDO::getId).collect(Collectors.toList());
        //转换手机号
        List<String> phoneList = userIds.stream().map(userService::getRealPhone).collect(Collectors.toList());
        st.stop();
        st.start("同步状态");
        for (String phone : phoneList) {
            CompletableFuture.runAsync(() -> {
                CommonQry commonQry = new CommonQry();
                commonQry.setPageNum(1);
                commonQry.setPageSize(Integer.MAX_VALUE);
                commonQry.setUserId(phone);
                commonQry.setNeedRead(YesOrNoNumberEnum.YES.getCode());
                PageInfo<NoticeVo> noticeVoPageInfo = exportService.pageList(commonQry);
                if (ObjectUtil.isNotEmpty(noticeVoPageInfo) && ObjectUtil.isNotEmpty(noticeVoPageInfo.getList())) {
                    List<Long> noticeIds = noticeVoPageInfo.getList().stream().map(NoticeVo::getId).collect(Collectors.toList());
                    //不需要回滚，互相不影响
                    this.read(noticeIds);
                }
            });
        }
        st.stop();
        log.info("消息状态同步完成{}", st.prettyPrint(TimeUnit.SECONDS));
    }

}
