package cn.zswltech.mithras.ep.service;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.ep.EpCourtSessionRSP;
import cn.zswltech.mithras.dto.utils.BeanCopyUtils;
import cn.zswltech.mithras.ep.mapper.EpCourtSessionMapper;
import cn.zswltech.mithras.ep.mapper.model.EpCourtSession;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * @author 张欣
 */
@Slf4j
@Service
public class EpCourtSessionService extends ServiceImpl<EpCourtSessionMapper, EpCourtSession> {

    @Resource
    private EpCourtSessionMapper epCourtSessionMapper;
    /***
     * 保存开庭公告
     * @param epCourtSession
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(EpCourtSession epCourtSession) {
        this.save(epCourtSession);
    }

    public R<EpCourtSessionRSP> getEpCourtSession(Long id) {
        QueryWrapper<EpCourtSession> wrapper = new QueryWrapper<>();
        wrapper.eq("msg_id",id);
        EpCourtSession epCourtSession = epCourtSessionMapper.selectOne(wrapper);
        if (epCourtSession != null) {
            EpCourtSessionRSP epCourtSessionRSP = BeanCopyUtils.generatorObject(epCourtSession, EpCourtSessionRSP.class);
            epCourtSessionRSP.setInsertTime(dateConvertLocalDateTime(epCourtSession.getInsertTime()));
            epCourtSessionRSP.setLawfulDay(dateConvertLocalDateTime(epCourtSession.getLawfulDay()));
            epCourtSessionRSP.setUpdateTime(dateConvertLocalDateTime(epCourtSession.getMsgUpdateTime()));
            epCourtSessionRSP.setScheduleDate(dateConvertLocalDateTime(epCourtSession.getScheduleDate()));

            return R.ok(epCourtSessionRSP);
        }
        return null;
    }

    /**
     * 校验开庭公告是否已经存在
     *
     * @param jsid
     * @param msgId
     * @return
     */
    public boolean checkExistsByJsIdAndMsgId(Long jsid, String msgId) {
        QueryWrapper<EpCourtSession> wrapper = new QueryWrapper<>();
        wrapper.eq("jsid", jsid);
        wrapper.eq("msg_id", msgId);
        return epCourtSessionMapper.selectCount(wrapper) > 0;
    }

    /***
     * Date转LocalDateTime
     * @param date
     * @return
     */
    public String dateConvertLocalDateTime(Date date) {
        //date为null
        if (Objects.isNull(date)) {
            return null;
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = localDateTime.format(dateTimeFormatter);

        return format;
    }
}
