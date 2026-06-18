import { Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import EditModal from './EditModal'
import IconFont from '@/components/Icon'
import { getTableColumns, getFormColumns } from '@/utils'
import { CreditColumns as ALL_COLUMNS } from '@/components/Credit/CreditListEntries'
import { InputEditable, FiledFormat } from '@/components/Format'
import { ClientSelect } from '@/components/Select'
import { saveServer } from '@/utils'

const nameColumns = [
  {
    title: '授信名称',
    dataIndex: 'projName',
    // width: 300,
    // fixed: 'left',
    actions: ({ projName: name, id }) => [
      {
        name,
        to: `/credit/review/detail/${id}`,
        className: 'z-single-line',
        // style: { width: 180 },
      },
    ],
    editable: InputEditable,
  },
  '授信编号',
  '授信额度(元)',
  '业务部门',
  '主办',
  '协办',
  {
    title: '授信主体',
    width: 300,
    render: (val, { clientName }) => clientName,
  },
  {
    title: '审批状态',
    dataIndex: 'groupCreditReviewProcessStatus',
    matchOption: 'groupCreditReviewProcessStatus',
  },
  '评审状态',

  '创建时间',
]
const formNameColumns = [
  { title: '授信名称', editable: InputEditable({ disabled: false }) },
  {
    title: '授信主体',
    editable: {
      element: <ClientSelect canJump={false} />,
      disabled: false,
      functionCode: 'clientlist-groupCreditReview',
    },
  },
  {
    title: '审批状态',
    dataIndex: 'groupCreditReviewProcessStatus',
    matchOption: 'groupCreditReviewProcessStatus',
  },
  { title: '授信编号', editable: InputEditable({ disabled: false }) },
  '授信金额',
  '业务部门',
  {
    title: '主办',
  },

  '创建时间',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)

function Index() {
  return (
    <Page>
      <div>
        <Table
                columnsFilter={'credit_review_1'}
                onFilter={(key,val) => saveServer('credit_review_1',val)}
          store={store.$table}
          editable={false}
          searchbar={{
            labelCol: { span: 6 },
            items: formColumns,
          }}
          actions={[
            {
              name: (
                <span>
                  <IconFont type="icon-icon_add" />
                  发起评审
                </span>
              ),
              onClick: store.$createModal.open,
              type: 'primary',
            },
          ]}
          scroll={{
            x: true,
          }}
          columns={columns}
        />
        <EditModal />
      </div>
    </Page>
  )
}

export default observer(Index)
