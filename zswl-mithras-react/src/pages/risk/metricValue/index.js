import { observer } from '@zswl/admin'
import { Page } from '@zswl/components'
import { Tabs, Space, Tooltip } from 'antd'
import {
  RiskMetricControl as Control,
  RiskMetricJinKon as JinKon,
  RiskMetricTarget as Target,
} from '@/components/Risk/MetricValueEntries'
import IconFont from '@/components/Icon'

function Index() {
  return (
    <Page>
      <Tabs
        defaultActiveKey="1"
        items={[
          {
            label: `指标报送`,
            key: '1',
            children: <Target></Target>,
          },
          {
            label: (
              <Space>
                关联交易报送
                <Tooltip
                  overlayStyle={{ minWidth: 300 }}
                  placement="topLeft"
                  title={
                    <div>
                      <div>1. 关联交易关联方名单从金控每天定时获取。</div>
                    </div>
                  }
                >
                  <IconFont type="icon-icon_info"></IconFont>
                </Tooltip>
              </Space>
            ),
            key: '2',
            children: <JinKon></JinKon>,
          },
          {
            label: (
              <Space>
                集中度报送
                <Tooltip
                  overlayStyle={{ minWidth: 300 }}
                  placement="topLeft"
                  title={
                    <div>
                      <div>1. 集中度报送频次为：季度报送。</div>
                      <div>2. 集中度报送只能全量报送，不能单条报送。</div>
                    </div>
                  }
                >
                  <IconFont type="icon-icon_info"></IconFont>
                </Tooltip>
              </Space>
            ),
            key: '3',
            children: <Control></Control>,
          },
        ]}
      ></Tabs>
    </Page>
  )
}

export default observer(Index)
