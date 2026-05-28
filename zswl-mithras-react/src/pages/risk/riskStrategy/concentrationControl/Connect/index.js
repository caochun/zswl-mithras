import { useEffect, useMemo, useState } from 'react'
import { Table, TableStore, SearchBar } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Space } from 'antd'
import { getTableColumns, getFormColumns, amountFormat, hasValue, formatPercent } from '@/utils'
import ALL_COLUMNS from '@/pages/risk/riskStrategy/concentrationControl/Column'
import Api from '@/pages/risk/riskStrategy/concentrationControl/api'
import { TIME_POINT } from '@/pages/risk/riskStrategy/concentrationControl/utils'
import { saveServer } from '@/utils'

const nameColumns = [
  '客户名称',
  '剩余未还本金(元)',
  '集中度占比',
  '不良余额(元)',
  '不良余额占比',
  '预警状态',
]
const formNameColumns = ['客户名称', '数据时点', '预警状态']
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function Index() {
  const [detail, setDetail] = useState()
  const $table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        const data = await Api.postConcentrationRelateList(params)
        const res = await Api.postConcentrationAllRelate(params)
        setDetail(res)
        return data
      },
    })
  }, [])

  const Header = useMemo(() => {
    if (!detail) return null
    const { remainingPrincipal, concentrationRatio, badBalance, badBalanceRatio } = detail
    return (
      <div style={{ marginTop: 20 }}>
        <Space>
          <div>全部关联方集中度：</div>
          <div>
            剩余未还本金：
            {hasValue(remainingPrincipal) ? amountFormat(formatPercent(remainingPrincipal)) : '-'}，
          </div>
          <div>
            集中度占比：{hasValue(concentrationRatio) ? concentrationRatio / 100 + '%' : '-'}，
          </div>
          <div>
            不良余额：{hasValue(badBalance) ? amountFormat(formatPercent(badBalance)) : '-'}，
          </div>
          <div>不良余额占比：{hasValue(badBalanceRatio) ? badBalanceRatio / 100 + '%' : '-'}</div>
        </Space>
      </div>
    )
  }, [detail])

  return (
    <div>
      <SearchBar
        store={$table}
        labelCol={{ span: 7 }}
        items={formColumns}
        initialValues={{
          dataTimePoint: TIME_POINT,
        }}
      ></SearchBar>
      {Header}
      <Table columnsFilter={'concentrationControl_Connect_1'}
        onFilter={(key, val) => saveServer('concentrationControl_Connect_1', val)} store={$table} editable={false} scroll={{ x: 1600 }} columns={[...columns]} />
    </div>
  )
}

export default observer(Index)
