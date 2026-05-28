import { Page, Table, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import styles from './index.less'
import IconFont from '@/components/Icon'
import ALL_COLUMNS from '../Column'
import { getTableColumns, getFormColumns } from '@/utils'
import { FiledFormat, InputEditable } from '@/components/Format'
import EditModal from './EditModal'
import Tags from '../component/Tags'
import { saveServer } from '@/utils'

const nameColumns = [
  {
    title: '模版名称',
    dataIndex: 'templateName',
    width: 200,
    fixed: 'left',
    actions: ({ templateName, templateId }) => [
      {
        name: templateName,
        onClick: () => store.editModal.open(templateId),
        // className: 'z-single-line',
      },
    ],
  },
  {
    title: '业务类型',
    // width: 100,
    dataIndex: 'bizType',
    matchOption: 'projEstablishBizType',
    render(tags) {
      return <Tags value={tags} />
    },
  },
  '模版状态',
  '创建时间',
  '更新时间',
]
const formNameColumns = [
  { title: '模版名称', editable: InputEditable({ disabled: false }) },
  '业务类型',
  '模版状态',
  '创建时间',
  '更新时间',
]
const columns = getTableColumns(ALL_COLUMNS, nameColumns)
const formColumns = getFormColumns(ALL_COLUMNS, formNameColumns)
function Index({ query }) {
  return (
    <Page>
      <div className={styles.customerWrap}>
        <Table
          columnsFilter={'archives_task_1'}
                  onFilter={(key,val) => saveServer('archives_task_1',val)}
          
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
                  新增模版
                </span>
              ),
              onClick: store.editModal.open,
              type: 'primary',
            },
          ]}
          scroll={{
            x: 1200,
          }}
          columns={columns}
        />
      </div>
      <EditModal modalStore={store.editModal} />
    </Page>
  )
}

export default observer(Index)
