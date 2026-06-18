import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import EditModal from './EditModal'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { AutoComplete, Input } from 'antd'
import PageListDown from '@/components/PageListDown'
import { saveServer } from '@/utils'
import api from './api'
import { useCallback, useState } from 'react'
import { debounce, throttle } from 'lodash'

const nameColumns = [
  {
    title: '机构名称',
    actions: ({ organizationName: name, id }) => [
      {
        name,
        onClick: () => store.createModal.open({ id }),
        className: 'z-single-line',
        // style: { width: 190 },
      },
    ],
    access: 'fundorganizationdetail',
  },
  '机构简称',
  '机构编号',
  '机构类型',
  '联系人',
  '创建日期',
  '更新日期',
  '创建人',
]

function Index() {
  const [options, setOptions] = useState([])
  const canDelete = store.table.selectedRowKeys.length > 0
  const handleSearch = useCallback(
    throttle(async (val) => {
      const list = await api
        .searchAbbreviation({ abbreviation: val })
        .then((res) => res?.map((label) => ({ label, value: label })))
      setOptions(list)
    }, 600),
    []
  )
  const formNameColumns = [
    {
      title: '机构名称',
      dataIndex: 'organizationName',
      editable: {
        element: <Input />,
      },
    },
    {
      title: '机构简称',
      dataIndex: 'abbreviation',
      editable: {
        element: <AutoComplete onSearch={handleSearch} options={options} />,
      },
    },
    '机构类型',
    '创建日期',
    { title: '创建人', dataIndex: 'createBy' },
  ]
  const columns = getTableColumns(ALL_COLUMNS, nameColumns)
  const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
  return (
    <Page>
      <Table
        columnsFilter={'financial_org_1'}
        onFilter={(key, val) => saveServer('financial_org_1', val)}
        store={store.table}
        editable={false}
        searchbar={{
          labelCol: { span: 6 },
          items: formColumns,
        }}
        selectable
        actions={[
          <Button.Add onClick={store.createModal.open} key="add" access={'fundorganizationadd'}>
            新增机构
          </Button.Add>,
          <Button.Delete
            onClick={store.delete}
            key="delete"
            disabled={!canDelete}
            access="fundorganizationremove"
          >
            删除机构
          </Button.Delete>,
        ]}
        extra={[<PageListDown key="1" module="org" table={store.table} />]}
        scroll={{
          x: 1200,
        }}
        columns={columns}
      />
      <EditModal store={store} />
    </Page>
  )
}

export default observer(Index)
