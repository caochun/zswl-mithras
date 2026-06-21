import { observer } from '@zswl/admin'
import { Table, TableStore } from '@zswl/components'
import Api from '@/api/afterLease/rentalInspectionReport'
import { useMemo, useState } from 'react'
import { Button, Input, Select, Tooltip } from 'antd'
import { saveServer } from '@/utils'

const { TextArea } = Input
const CHECK_RESULT = [
  {
    label: '是',
    value: 1,
  },
  {
    label: '否',
    value: 0,
  },
  {
    label: '不适用',
    value: -1,
  },
]
const columns = [
  {
    title: '',
    dataIndex: 'required',
    width: 30,
    render: (val) => <span style={{ color: 'red' }}>*</span>,
    editable: false,
  },
  {
    title: '模板条目',
    dataIndex: 'templateTitle',
    render: (value, { checkResult }) => {
      return (
        <Tooltip title={value}>
          <span style={{ color: checkResult === 1 && 'red' }}>{value}</span>
        </Tooltip>
      )
    },
    editable: false,
  },
  {
    title: '检查结果',
    dataIndex: 'checkResult',
    matchOption: CHECK_RESULT,
    width: 180,
    editable: (value) => ({
      element: <Select options={CHECK_RESULT} />,
      rules: [{ required: true, message: '请填写' }],
    }),
  },
  {
    title: '备注',
    dataIndex: 'remark',
    editable: ({ checkResult }, all) => {
      return {
        element: <TextArea style={{ padding: '12px 0' }} />,
        // rules: [{ required: true, message: '请填写' }],
      }
    },
  },
]

function AfterLeaseNoPublicCheckTable({ id, canEdit = true, businessVersion }) {
  const table = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (data) => {
          const currentData = {
            ...data,
            businessVersion,
            id,
          }
          const res = await Api.postExtraGet(currentData)
          // return res.map((v) => ({ ...v, checkResult: 0 }))
          return res
        },
      }),
    []
  )
  const [edit, setEdit] = useState(false)
  const [loading, setLoading] = useState(false)
  const handleSave = async () => {
    const { list, values } = await table.submit()
    const contentList = list.map((v) => ({ ...v, ...values[v.templateId] }))
    const params = {
      contentList,
      checkPlanClientId: id,
    }
    setLoading(true)
    try {
      await Api.postExtraSave(params)
      await table.search({ id })
      setLoading(false)
      setEdit(false)
    } catch (err) {
      setLoading(false)
    }
  }

  return (
    <Table
      columns={columns}
      store={table}
      rowKey={'templateId'}
      columnsFilter={'template_components_NoPublicTable'}
              onFilter={(key,val) => saveServer('template_components_NoPublicTable',val)}
      
      extra={[
        canEdit && edit && (
          <Button onClick={() => setEdit(false)} key={'canle'}>
            取消
          </Button>
        ),
        canEdit && edit && (
          <Button type="primary" onClick={handleSave} key="save" loading={loading}>
            保存
          </Button>
        ),
        canEdit && !edit && (
          <Button type="primary" onClick={() => setEdit(true)} key="edit" disabled={!canEdit}>
            编辑
          </Button>
        ),
      ]}
      editable={edit}
    ></Table>
  )
}

export default observer(AfterLeaseNoPublicCheckTable)
