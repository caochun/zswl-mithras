import PublicMonitor from '@/pages/risk/publicMonitor'
import { observer } from '@zswl/admin'
import { App, DescStore, Descriptions } from '@zswl/components'
import { Card, Tabs } from 'antd'
import { useMemo } from 'react'
import styles from './index.less'
import customCycleApi from '@/api/lifeCycle/customCycleApi'

const columns = [{ title: '五级分类', dataIndex: 'fiveLevel' }]

const Detail = ({ id, style, clientName }) => {
  const store = useMemo(() => {
    return new DescStore({
      request: async (params) => {
        const res = await customCycleApi.postLifecycleFivelevel({ ...params, clientId: id })
        const fiveLevel = App.matchOption('assetClassifyResultEnum', res)?.label ?? res
        return { fiveLevel }
      },
    })
  }, [id])
  return (
    <Card title="风险策略助手" style={style}>
      <Descriptions bordered store={store} items={columns} />

      <div className={styles.tab}>
        <Tabs
          defaultActiveKey="1"
          items={[
            {
              label: `舆情风险`,
              key: '1',
              children: <PublicMonitor showPageStyle={false} key="舆情风险" chiName={clientName} />,
            },
          ]}
        ></Tabs>
      </div>
    </Card>
  )
}

export default observer(Detail)
