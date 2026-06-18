import { Page, Table, Button } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useEffect, useMemo } from 'react'
import Store from './store'
import { PolicyColumns as ALL_COLUMNS } from '@/components/PolicyColumns/PolicyColumnsEntries'
import { getTableColumns, getFormColumns } from '@/utils'
import AddModal from './AddModal'
import { Checkbox } from 'antd'
import { saveServer } from '@/utils'

const nameColumns = [
  {
    title: '保险单号',
    fixed: 'left',
    width: 230,
    actions({ policyCode, id, dataSource }) {
      return [
        {
          name: policyCode,
          to: `/afterLease/policyManage/detail/${id}?dataSource=${dataSource}`,
        },
      ]
    },
  },
  '标识信息',
  '保险机构',
  '保险起始日',
  '保险到期日',
  '是否续保',
  {
    title: '客户名称',
    dataIndex: 'clientName',
  },
  '合同编号',
  '项目名称',
  '项目主办',
  '项目协办',
  '续保保单反馈日',
  '逾期天数',
  '状态',
  '创建时间',
]
const formNameColumns = [
  '保险单号',
  '合同编号',
  '客户名称',
  '保险起始日',
  '保险到期日',
  '项目名称',
  '是否续保',
  '项目主办',
  '状态',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function Index() {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  return (
    <Page store={store}>
      <Table
        columnWidth={180}
        selectable
        store={store.$table}
        editable={false}
        searchbar={{
          labelCol: { span: 6 },
          items: [...formColumns],
        }}
        extra={<Checkbox onChange={store.onCheckboxChange}>15天内到期保单</Checkbox>}
        actions={[
          <Button type="primary" onClick={() => store.handleCreate()}>
            新增保单
          </Button>,
          <Button.Download onClick={store.exportList}>批量导出</Button.Download>,
        ]}
        scroll={{
          x: 1500,
        }}
        columns={[...columns]}
        columnsFilter={'policyManage_list'}
                onFilter={(key,val) => saveServer('policyManage_list',val)}
        
      />
      <AddModal store={store} />
    </Page>
  )
}

export default observer(Index)
