import { useMemo, useEffect } from 'react'
import { Page } from '@zswl/components'
import { observer, setSessionStorage } from '@zswl/admin'
import { Drawer, Radio } from 'antd'
import Tab from '@/pages/creditManage/creditTable/Tab'
import BatchView from '@/pages/creditManage/creditTable/finish/View/Batch'
import AccountView from '@/pages/creditManage/creditTable/finish/View/Account'
import Store from '@/pages/creditManage/creditTable/finish/store'
import { CREATETABLE_PARAMS } from '@/pages/creditManage/creditTable/Tab/config'
import Export from '@/components/Actions/Export'
import Api from '@/pages/creditManage/creditTable/Tab/api'

const options = [
  { label: '批次维度', value: 'PROC_BATCH' },
  { label: '账户维度', value: 'EFFECT' },
]

function Index() {
  const store = useMemo(() => {
    return new Store()
  }, [])

  const { setChannel, channel, batchId, accountId, batchNo } = store

  useEffect(() => {
    setSessionStorage(CREATETABLE_PARAMS, null)
    return () => {
      setSessionStorage(CREATETABLE_PARAMS, null)
    }
  }, [])

  const TableView = useMemo(() => {
    return (
      {
        PROC_BATCH: <BatchView store={store}></BatchView>,
        EFFECT: <AccountView store={store}></AccountView>,
      }[channel] || null
    )
  }, [channel])
  const exportExcel = async () => {
    const params = {
      channel,
      accountId, // 账户维度穿透
    }
    await Api.exportExcel(params)
  }
  return (
    <Page>
      <div className="z-flex-jsb">
        <Radio.Group
          value={channel}
          options={options}
          onChange={setChannel}
          optionType="button"
          style={{ marginBottom: 20 }}
        />
        {channel === 'EFFECT' && <Export onClick={exportExcel} text="一键导出" />}
      </div>
      {TableView}
      <Drawer
        title={`${options.find((item) => item.value === channel).label}${batchNo}`}
        placement="bottom"
        height="85%"
        onClose={store.setShowDrawer}
        open={store.showDrawer}
        destroyOnClose
      >
        <Tab
          channel={channel}
          batchId={batchId}
          accountId={accountId}
          showActionColumn={false}
          canEdit={false}
          showSearch={channel === 'PROC_BATCH'}
        ></Tab>
      </Drawer>
    </Page>
  )
}

export default observer(Index)
