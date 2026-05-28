package cn.zswltech.mithras.report.service.procsnap;

import cn.zswltech.mithras.report.mapper.procsnap.CrClientProcSnapMapper;
import cn.zswltech.mithras.report.mapper.procsnap.model.CrClientProcSnap;
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
public class CrClientProcSnapService extends ServiceImpl<CrClientProcSnapMapper, CrClientProcSnap> implements IService<CrClientProcSnap> {

}
