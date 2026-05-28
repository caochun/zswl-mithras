import { useEffect, useMemo } from 'react'
import { observer, getQuery } from '@zswl/admin'
import { Page, Button } from '@zswl/components'
import { Space, Tabs } from 'antd'
import BaseInfo from './BaseInfo'
import ChuFen from './Tab/ChuFen'
import History from './Tab/History'
import Store from './store'
import styles from './index.less'

const Index = ({
  params: { id },
  query: { businessVersion, canEditFlags = 'true', modelKey, curTaskActivityIds },
}) => {
  const isFormApproval = getQuery('typeId') == 'approval'

  const store = useMemo(() => {
    return new Store({})
  }, [])
  const baseInfoDetail = store.page.getData()
  const canEdit = canEditFlags === 'true' && ['WAIT'].includes(baseInfoDetail.nodeStatue)

  return (
    <div className={styles.page}>
      <Page
        store={store}
        current={'详情'}
        header={null}
        params={{ id, isFormApproval, modelKey, businessVersion }}
        style={isFormApproval ? { background: '#fff' } : {}}
      >
        <div>
          <div id="baseInfo" className={styles.baseInfo}>
            <BaseInfo detail={baseInfoDetail} canEdit={false} store={store} />
          </div>
          <div id="actionPanel" className={styles.actionPanel}>
            <Tabs
              defaultActiveKey="1"
              items={[
                {
                  label: `初分结果`,
                  key: '1',
                  children: (
                    <ChuFen
                      store={store}
                      detail={baseInfoDetail}
                      canEdit={canEdit}
                      businessVersion={businessVersion}
                    />
                  ),
                },
                // {
                //   label: `检查内容`,
                //   key: '2',
                //   children: <CheckContent store={store} canEdit={canEdit} />,
                // },
                {
                  label: `五级分类历史`,
                  key: '3',
                  children: <History store={store} />,
                },
              ]}
            />
          </div>
        </div>
      </Page>
    </div>
  )
}

export default observer(Index)
