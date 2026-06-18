import { Page, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import styles from './index.less'
import EditModal from './EditModal'
import IconFont from '@/components/Icon'
import { CreditColumns as ALL_COLUMNS } from '@/components/Credit/CreditEntries'
import { getTableColumns, getFormColumns } from '@/utils'
import { FiledFormat, InputEditable } from '@/components/Format'
import { ClientSelect } from '@/components'
import { saveServer } from '@/utils'

const nameColumns = [
  '授信名称',
  '授信编号',
  '授信额度(元)',
  '业务部门',
  '主办',
  '协办',
  {
    title: '授信主体',
    width: 250,
    render: (val, { clientName }) => clientName,
  },
  '审批状态',
  '立项状态',
  '创建时间',
]
const formNameColumns = [
  { title: '授信名称', editable: InputEditable({ disabled: false }) },
  {
    title: '授信主体',
    editable: {
      // clientlist-groupCreditEstablish
      element: <ClientSelect canJump={false} />,
      disabled: false,
    },
  },
  '审批状态',
  { title: '授信编号', editable: InputEditable({ disabled: false }) },
  '授信金额',
  '业务部门',
  '主办',
  '创建时间',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function Index({ query }) {
  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
        columnsFilter={'credit_establish_1'}
        onFilter={(key,val) => saveServer('credit_establish_1',val)}
          store={store.table}
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
                  创建立项
                </span>
              ),
              onClick: store.createModal.open,
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
