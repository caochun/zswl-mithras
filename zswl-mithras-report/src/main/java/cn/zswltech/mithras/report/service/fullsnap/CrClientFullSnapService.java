package cn.zswltech.mithras.report.service.fullsnap;

import cn.zswltech.mithras.report.mapper.fullsnap.CrClientFullSnapMapper;
import cn.zswltech.mithras.report.mapper.fullsnap.model.CrClientFullSnap;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 征信报送-客户表
 *
 * @author wangchuanhao
 * @date 2022/10/8 10:41 AM
 */
@Service
public class CrClientFullSnapService extends ServiceImpl<CrClientFullSnapMapper, CrClientFullSnap> implements IService<CrClientFullSnap> {

}
