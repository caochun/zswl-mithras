import { observer } from '@zswl/admin'
import React, { useEffect, useMemo, useState } from 'react'
import store from './store'
import IconFont from '@/components/Icon'
import { saveServer } from '@/utils'
import { Page, Table } from '@zswl/components'
import ALL_COLUMNS from './Column'
import { getTableColumns } from '@/utils'
import EditModal from './EditModal'

const Index = () => {
  const nameColumns = ['考核名称', '考核年份', '考核月份', '审批状态', '提交人', '提交时间']
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  return (
    <Page>
      <Table
        columnWidth={140}
        resizable
        store={store.table}
        actions={[
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                创建考核
              </span>
            ),
            onClick: store.createModal.open,
            type: 'primary',
          },
        ]}
        columnsFilter={'budgetManagement_assessment_1'}
        onFilter={(key, val) => saveServer('budgetManagement_assessment_1', val)}
        columns={columns}
      />
      <EditModal />
    </Page>
  )
}

export default observer(Index)
