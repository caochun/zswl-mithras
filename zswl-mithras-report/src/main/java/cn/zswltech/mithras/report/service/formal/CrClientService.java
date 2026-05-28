package cn.zswltech.mithras.report.service.formal;

import cn.zswltech.mithras.report.mapper.formal.CrClientMapper;
import cn.zswltech.mithras.report.mapper.formal.model.CrClient;
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
public class CrClientService extends ServiceImpl<CrClientMapper, CrClient> implements IService<CrClient> {

}
