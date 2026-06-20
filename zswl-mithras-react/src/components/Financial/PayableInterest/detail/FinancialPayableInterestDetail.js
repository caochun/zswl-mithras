import { observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { Page, Table, TableStore } from '@zswl/components'
import { EditDescription } from '@/components/Table'
import ALL_COLUMNS from '../Column'
import { getDescColumns, getTableColumns } from '@/utils'
import Store from './Store'
import { saveServer } from '@/utils'
import Api from '@/api/financial/payableInterestApi'
import { message, Tabs } from 'antd'
import { FormTable } from '@/components/Form'

const InterestTable = observer(({ dataSource, isSummary, getList }) => {
  const tableColumns = getTableColumns(ALL_COLUMNS, [
    '日期',
    '融资余额(元)',
    !isSummary && '还款本金(元)',
    !isSummary && '还款利息(元)',
    !isSummary && '融资利率',
    !isSummary && '日利率',
    '当日应付利息(元)',
    '当日累计应付利息(元)',
    '是否已确认',
    '钆差金额',
    '期初计提利息余额',
    '期末计提利息余额',
  ])

  const editBeforeUpdate = async (list, record, newData) => {
    const { endOfPeriodInterestBalance, financingCostDiff, id } = newData
    await Api.postInterestCalDetailModify({
      endOfPeriodInterestBalance,
      financingCostDiff,
      id,
    })
    getList()
    message.success('编辑成功')
  }
  return (
    <FormTable
      columnsFilter={'财务管理_还本利息详情列表'}
      onFilter={(key, val) => saveServer('财务管理_还本利息详情列表', val)}
      value={dataSource}
      canAddDelete={false}
      columnWidth={160}
      beforeUpdate={editBeforeUpdate}
      onlyRead={isSummary}
      columns={tableColumns}
    />
  )
})

function Index({ params, query: { type } }) {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const detail = store.page.getData()
  const financingId = params.id
  const descColumns = getDescColumns(ALL_COLUMNS, [
    '融资编号',
    '融资渠道',
    '起息日',
    '融资金额(元)',
    '业务类型',
    '借款性质',
  ])
  const [interestList, setInterestList] = useState([])
  const getList = async () => {
    const res = await Api.postInterestCalDetail({ financingId, type })
    setInterestList(
      res.map(({ abbreviation, dates }, index) => ({
        label: abbreviation,
        key: abbreviation,
        children: (
          <InterestTable
            dataSource={dates}
            isSummary={res.length > 1 && index === 0}
            getList={getList}
          />
        ),
      }))
    )
  }
  useEffect(() => {
    getList()
  }, [params])
  return (
    <Page params={{ financingId, type }} store={store}>
      <EditDescription detail={detail} canEdit={false} columns={descColumns} />
      <h3 style={{ marginTop: 20 }}>应付利息计提</h3>
      {interestList.length === 1 && interestList[0].children}
      {interestList.length > 1 && <Tabs items={interestList} />}
    </Page>
  )
}

export default observer(Index)
