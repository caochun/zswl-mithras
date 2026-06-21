import { observer, history } from '@zswl/admin'
import { Page, Table, SearchBar, Button } from '@zswl/components'
import { DatePicker, Input, Select, Spin } from 'antd'
import { useMemo, useEffect } from 'react'
import DataUpload from '@/components/DataUpload'
import { getKeyOptionsLabelMap, saveServer } from '@/utils'
import store from './store'
import styles from './index.less'
import RefreshModal from './RefreshModal'

const { Item } = SearchBar
const RiskFinanceSheetFileList = ({ pathname }) => {
  const { allSelect, loading, $refreshModal } = store
  useEffect(() => {
    store.getSelect()
  }, [])
  const columns = useMemo(() => {
    const baseColumns = [
      {
        title: '报告期',
        dataIndex: 'sheetDate',
        render: (val) => {
          return val || '-'
        },
      },
      {
        title: '报表类型',
        dataIndex: 'sheetName',
        render: (v) => {
          return getKeyOptionsLabelMap('riskMetricFactorTable', allSelect)[v] || '-'
        },
      },
      {
        title: '导入时间',
        dataIndex: 'updateTime',
        render: (val) => {
          return val || '-'
        },
      },
      {
        title: '操作',
        width: 120,
        fixed: 'right',
        actions(record, rowIndex) {
          return [
            {
              name: '查看',
              key: 'edit',
              onClick: () => {
                // window.open(`/preview/reportPreview/${record.fileId}`)
                const sheetName = getKeyOptionsLabelMap('riskMetricFactorTable', allSelect)[
                  record.sheetName
                ]
                history.push(
                  `${pathname}/detail/1?factorTable=${sheetName}&factorDate=${record.sheetDate}`
                )
              },
            },
            {
              name: '删除',
              key: 'cancel',
              onClick: () => store.deleteItem(record),
            },
          ].filter(Boolean)
        },
      },
    ]

    return baseColumns
  }, [allSelect])
  return (
    <Page>
      {loading && (
        <div className={styles.spinWrap}>
          <span className={styles.spin}>
            <Spin size="middle"></Spin>
          </span>
        </div>
      )}

      <Table
        columnsFilter="budget_financeSheet_1"
        onFilter={(key, val) => saveServer('budget_financeSheet_1', val)}
        resizable
        columnWidth={180}
        store={store.table}
        extra={[
          {
            name: '刷新',
            type: 'primary',
            onClick: () => $refreshModal.open(),
            style: {
              marginTop: 10,
            },
          },
          {
            name: (
              <DataUpload maxCount={1} onChange={store.importSheet} accept=".xlsx">
                <Button type="primary">财报导入</Button>
              </DataUpload>
            ),
            type: 'link',
          },
        ]}
        searchbar={
          <SearchBar>
            <Item label="报告期" name="sheetDate">
              <DatePicker picker={'month'} allowClear={false} />
            </Item>
            <Item label="报表类型" name="sheetName">
              <Select options={allSelect.riskMetricFactorTable} />
            </Item>
          </SearchBar>
        }
        columns={columns}
      />
      <RefreshModal store={store}></RefreshModal>
    </Page>
  )
}

export default observer(RiskFinanceSheetFileList)
