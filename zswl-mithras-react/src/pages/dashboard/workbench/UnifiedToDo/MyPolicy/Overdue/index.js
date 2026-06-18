import { observer } from '@zswl/admin'
import { Table, SearchBar } from '@zswl/components'
import { getTableColumns } from '@/utils'
// import { ALL_COLUMNS } from '../Column'
import { Space, Input, Row, Col } from 'antd'
import { PolicyColumns as ALL_COLUMNS } from '@/components/Policy/PolicyEntries'
import { FormDateRange } from '@/components/Form'
import { saveServer } from '@/utils'

const { Item } = SearchBar

const Index = ({ store }) => {
  const columns = getTableColumns(ALL_COLUMNS, [
    '合同编号',
    '剩余未还本金(元）',
    '合同到期日',
    '客户名称',
    '项目名称',
    '项目主办',
    '项目协办',
    {
      title: '保险单号',
      width: 200,
      actions({ policyCode, id }) {
        return [
          {
            name: policyCode,
            to: `/afterLease/policyManage/detail/${id}?dataSource=policy`,
          },
        ]
      },
    },
    '标识信息',
    '逾期天数',
    '保单金额(元)',
    '保险机构',
    '险种',
    {
      title: '保险起始日-详情',
      rename: '保险起始日',
    },
    {
      title: '保险到期日-详情',
      rename: '保险到期日',
    },
  ])
  return (
    <Table
      searchbar={
        <SearchBar>
          <Item name="identificationInformation" label="标识信息">
            <Input placeholder="请输入标识信息" />
          </Item>

          <Item name="contractCode" label="合同编号">
            <Input placeholder="请输入合同编号" />
          </Item>
          <Item name="policyCode" label="保险单号">
            <Input placeholder="请输入保险单号" />
          </Item>

          <FormDateRange.Item label={'保险起始日'} name="insuranceStartDate" col={12} />
          <FormDateRange.Item label={'保险到期日'} name="insuranceEndDate" col={12} />
        </SearchBar>
      }
      selectable={{
        type: 'checkbox',
      }}
      scroll={{ x: true }}
      columns={[
        ...columns,
        {
          title: '操作',
          fixed: 'right',
          render: (record) => {
            return (
              <Space style={{ width: 90 }}>
                <a onClick={() => store.handleCreate(record)}>新增保单</a>{' '}
              </Space>
            )
          },
        },
      ]}
      store={store.table}
      editable={false}
      columnsFilter={'工作台_统一视图_待维护保单'}
      onFilter={(key, val) => saveServer('工作台_统一视图_待维护保单', val)}

    ></Table>
  )
}

export default observer(Index)
