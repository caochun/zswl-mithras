import { observer } from '@zswl/admin'
import { Table, Button, Page, SearchBar } from '@zswl/components'
import { getTableColumns } from '@/utils'
import { Card } from 'antd'
import moduleColumns from '../Column'
import { CalculateDateItem } from '../../../EstimationFields'
import store from '../store'
import { useEffect, useState } from 'react'
import { saveServer } from '@/utils'

const { Item } = SearchBar

const Index = ({ params: { id } }) => {
  const { expandKeys, setExpandKeys, detailTable } = store
  const [isExpanded, setIsExpanded] = useState(false)

  const nameColumns = [
    '月份',
    '合同编号',
    '项目名称',
    '项目来源',
    '项目类别',
    '项目类型',
    '首次投放日',
    '考核部门',
    '项目利润-当期值(元)',
    '项目利润-累计值(元)',
    '项目本年累计投放金额(元)',
    '合同本月投放额(元)',
  ]

  const childNameColumns = [
    '人员/部门',
    '占比',
    '利润提奖-当期值(元)',
    '利润提奖-调整值(元)',
    '利润提奖-累计值(元)',
    '投放提奖-当期值(元)',
    '投放提奖-累计值(元)',
    '利润奖金-当期值(元)',
    '利润奖金-调整值(元)',
    '利润奖金-累计值(元)',
    '投放奖金-当期值(元)',
    '投放奖金-累计值(元)',
    '基础提奖比例',
    '项目类型系数',
    '项目规模系数',
  ].filter(Boolean)

  const columns = getTableColumns(moduleColumns(), nameColumns)
  const childColumns = getTableColumns(moduleColumns(), childNameColumns)

  const tableList = detailTable.getList() || []
  useEffect(() => {
    setIsExpanded(expandKeys?.length === tableList.length)
  }, [id, expandKeys, detailTable])

  return (
    <Page store={store.detailPage} params={{ id }}>
      <Table
        onFilter={(key,val) => saveServer('项目绩效测算表_合同维度详情',val)}
        searchbar={{
          items: [<CalculateDateItem></CalculateDateItem>],
        }}
        columnsFilter={'项目绩效测算表_合同维度详情'}
        rowKey={({ id }) => `${id}`}
        store={detailTable}
        actions={[
          tableList?.length && {
            name: isExpanded ? '收起全部' : '展开全部',
            type: 'primary',
            onClick: () => {
              const keys = tableList.map((item) => `${item.id}`)
              if (isExpanded) {
                return setExpandKeys([])
              }
              setExpandKeys(keys)
            },
          },
          <Button type="primary" onClick={() => store.detailExport({ columns, childColumns })}>
            导出
          </Button>,
        ].filter(Boolean)}
        expandable={{
          expandedRowRender: (record) => {
            return (
              <Card size="small" title="分润比" type="inner" headStyle={{ background: '#ddd' }}>
                <Table
                  columnsFilter={'项目绩效测算表_合同维度详情_item'}
                  onFilter={(key,val) => saveServer('项目绩效测算表_合同维度详情_item',val)}
                  scroll={{ x: true }}
                  pagination={false}
                  dataSource={record.weightInfoList || []}
                  columns={childColumns}
                />
              </Card>
            )
          },
          expandedRowKeys: expandKeys,
          onExpand: (expanded, record) => {
            if (expanded) {
              setExpandKeys([...expandKeys, `${record.id}`])
            } else {
              setExpandKeys(expandKeys.filter((item) => item !== `${record.id}`))
            }
          },
        }}
        editable={false}
        scroll={{
          x: 2400,
        }}
        columns={columns}
      />
    </Page>
  )
}
export default observer(Index)
