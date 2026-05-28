import { observer } from '@zswl/admin'
import { Form } from '@zswl/components'
import { getFormColumns, getNewFormColumns } from './utils'
import ALL_COLUMNS from './Column'

const nameColumns = [
  '融资机构',
  '本息收付款状态',
  '收付款编号',
  '总授信额度（元）',
  '剩余授信额度（元）',
  '融资金额（元）',
  '利息总额（元）',
  '增信方式：担保',
  '保理手续费（元）',
  '开征许可证费（元）',
  '保证金金额（元）',
  '其他费用（元）',
  '备注',
  '资金经理',
  '所属部门',
  '部门负责人',
  '分管领导',
]

const columns = getNewFormColumns(ALL_COLUMNS, nameColumns)

function Index() {
  const form = Form.useStore()
  return (
    <Form
      layout={'vertical'}
      // labelCol={{ span: 6 }}
      store={form}
      preserve={false}
      config={{ items: columns, chunk: 3 }}
    />
  )
}

export default observer(Index)
