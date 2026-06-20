import { Table, SearchBar } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import IconFont from '@/components/Icon'
import { useEffect, useMemo } from 'react'
import { Tooltip } from 'antd'
import { amountFormat } from '@/utils'
import styles from './index.less'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function Index({ cashArr, store }) {
  const { keys } = store.cashFlowTable.getSelected()
  const selectColumn = keys.length !== 0
  const { rentActualCode } = getQuery()

  return (
    <div className={styles.cashFlowTable}>
      <div className={styles.cashSelect}></div>
      <Table
        columnsFilter={'contractCpm_CashFlowTable_1'}
        onFilter={(key, val) => saveServer('contractCpm_CashFlowTable_1', val)}
        searchbar={{
          initialValues: {
            cashtype: rentActualCode || 'ALL',
          },
          cache: false,
          items: [
            {
              label: '查看项目',
              name: 'cashtype',
              options: cashArr,
            },
          ],
          searchButton: false,
          resetButton: false,
        }}
        selectable
        rowKey={'rentCode'}
        //autoRequest={false}
        store={store.cashFlowTable}
        actions={[
          {
            name: (
              <>
                <IconFont type="icon-icon_link" style={{ fontSize: '16px' }} />
                &nbsp; 导出
              </>
            ),
            onClick: () => {
              return store.cashDetailList(keys)
            },
            type: 'primary',
            disabled: !selectColumn,
          },
        ]}
        scroll={{
          x: 1200,
        }}
        columns={[
          {
            title: '核销状态',
            dataIndex: 'writeOffStatus',
            key: 'writeOffStatus',
            tooltip: true,
          },
          {
            title: '编号',
            dataIndex: 'rentCode',
            key: 'rentCode',
            render: (v, t) => {
              if (t.recordSource == 'COLLECTION') {
                return (
                  <a target="_self" href={`/cpm/collectionWriteOff/detail/${t.recordId}`}>
                    <Tooltip title={v}>{v}</Tooltip>
                  </a>
                )
              }
              if (t.recordSource == 'PAYMENT') {
                return (
                  <a target="_self" href={`/cpm/paymentWriteOff/detail/${t.recordId}`}>
                    <Tooltip title={v}>{v}</Tooltip>
                  </a>
                )
              }
              if (t.recordSource == 'MARGIN') {
                return (
                  <a target="_self" href={`/cpm/marginManagement/detail/${t.recordId}`}>
                    <Tooltip title={v}>{v}</Tooltip>
                  </a>
                )
              }
            },
          },
          {
            title: '日期',
            dataIndex: 'rentDate',
            key: 'rentDate',
            dateFormat: 'yyyy-MM-DD',
            tooltip: true,
          },
          {
            title: '期项',
            dataIndex: 'phase',
            key: 'phase',
            tooltip: true,
          },
          {
            title: '现金流项目',
            dataIndex: 'cashItem',
            key: 'cashItem',
            tooltip: true,
          },
          {
            title: '现金流金额(元)',
            dataIndex: 'cashFlowAmount',
            key: 'cashFlowAmount',
            tooltip: true,
            align: 'right',
            render: (v) => (
              <Tooltip title={amountFormat(v / 10000)}>{amountFormat(v / 10000)}</Tooltip>
            ),
          },
          {
            title: '本金(元)',
            dataIndex: 'principal',
            key: 'principal',
            tooltip: true,
            align: 'right',
            render: (v) => (
              <Tooltip title={amountFormat(v / 10000)}>{amountFormat(v / 10000)}</Tooltip>
            ),
          },
          {
            title: '利息(元)',
            dataIndex: 'interest',
            key: 'interest',
            tooltip: true,
            align: 'right',
            render: (v) => (
              <Tooltip title={amountFormat(v / 10000)}>{amountFormat(v / 10000)}</Tooltip>
            ),
          },
          {
            title: '罚息(元)',
            dataIndex: 'penaltyInterest',
            key: 'penaltyInterest',
            tooltip: true,
            align: 'right',
            render: (v) => (
              <Tooltip title={amountFormat(v / 10000)}>{amountFormat(v / 10000)}</Tooltip>
            ),
          },
        ]}
      />
    </div>
  )
}

export default observer(Index)
