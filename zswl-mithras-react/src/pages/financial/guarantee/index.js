import { Button, Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import EditModal from './EditModal'
import IconFont from '@/components/Icon'
import { getTableColumns, getFormColumns } from '@/utils'
import ALL_COLUMNS from './Column'
import { InputEditable } from '@/components/Format'
import { saveServer } from '@/utils'

import orgApi from '@/api/financial/orgManage'
import { ApiSelect } from '@/components/Select'

const OrgSelect = (props) => {
  const { params, ...rest } = props
  return (
    <ApiSelect
      api={orgApi.postAgencyPulldown}
      params={params}
      fieldNames={{ label: 'guaranteeAgencyName', value: 'guaranteeAgencyName' }}
      searchField="guaranteeAgencyName"
      {...rest}
    />
  )
}
const nameColumns = [
  {
    title: '担保机构名称',
    width: 220,
    actions: ({ guaranteeAgencyName: name, id }) => [
      {
        name,
        to: `/financial/guarantee/detail/${id}`,
        className: 'z-single-line',
        // style: { width: 190 },
      },
    ],
  },
  '机构编号',
  '总担保额度（元）',
  '已使用担保额度（元）',
  '剩余担保额度（元）',
  '担保期限',
  '创建日期',
  '更新日期',
  '创建人',
]
const formNameColumns = [
  {
    title: '担保机构名称',
    editable: {
      element: <OrgSelect />,
    },
  },
  '担保额度',
  { title: '创建日期', rename: '担保日期' },
  '创建人',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function Index() {
  const canDelete = store.table.selectedRowKeys.length > 0
  return (
    <Page>
      <div>
        <Table
          columnsFilter={'financial_guarantee_1'}
          onFilter={(key,val) => saveServer('financial_guarantee_1',val)}
          store={store.table}
          editable={false}
          selectable
          searchbar={{
            labelCol: { span: 8 },
            items: formColumns,
          }}
          actions={[
            <Button.Add
              onClick={store.createModal.open}
              key="add"
              access={'fundguaranteeagencyadd'}
            >
              新增担保机构
            </Button.Add>,
            <Button.Delete
              onClick={store.delete}
              key="delete"
              disabled={!canDelete}
              access="fundguaranteeagencyremove"
            >
              删除机构
            </Button.Delete>,
          ]}
          scroll={{
            x: 1600,
          }}
          columns={columns}
        />
        <EditModal />
      </div>
    </Page>
  )
}

export default observer(Index)
