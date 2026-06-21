import { observer } from '@zswl/admin'

import { ALL_COLUMNS } from './Column'
import { getDescColumns } from '@/utils'
import { useEffect, useMemo, useRef } from 'react'
import { Block, Button, Page } from '@zswl/components'
import Store from './Store'

function ContractChangeAheadDetail({ contractId, businessVersion, canEdit, taskActivityId }) {
  const isStartUserModify = taskActivityId === 'userTask_startUserModify'
  const store = useMemo(() => {
    return new Store({})
  }, [])
  const editRef = useRef()
  const blockData = store.blockStore.getData()
  const { isEarlySettle } = store

  const columns = getDescColumns(
    ALL_COLUMNS(store, isStartUserModify),
    isEarlySettle === 0
      ? [
          '提前还款日',
          '是否提前结清',
          '到期未付租金(元)',
          '违约金(元)',
          '提前归还本金(元)',
          {
            title: '提前归还利息(元)',
            rename: '应收利息(元)',
          },
          '提前终止补偿金(元)',
          '名义价款(元)',
          '合计金额(元)',
          '提前还款说明',
        ]
      : [
          '提前还款日',
          '是否提前结清',
          '到期未付租金(元)',
          '违约金(元)',
          '未到期本金(元)',
          {
            title: '未到期利息(元)',
            rename: '应收利息(元)',
          },
          '提前终止补偿金(元)',
          '保证金余额(元)',
          '保证金抵扣金额(元)',
          '名义价款(元)',
          '合计金额(元)',
          '提前还款说明',
        ]
  )

  return (
    <Page
      style={{ marginTop: 20 }}
      store={store.blockStore}
      params={{
        businessVersion,
        contractId,
        editRef,
        isStartUserModify,
      }}
    >
      <EditDescription
        formProps={{ preserve: false }}
        ref={editRef}
        title={'还款方案'}
        detail={blockData}
        saveData={store.saveData}
        canEdit={canEdit}
        initEdit={false}
        columns={columns}
      />
    </Page>
  )
}

export default observer(ContractChangeAheadDetail)
