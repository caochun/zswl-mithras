import { Modal, Form } from '@zswl/components'
import { observer } from '@zswl/admin'
import { Tabs, DatePicker } from 'antd'
import { cloneElement } from 'react'
import BasePrizeRate from './BasePrizeRate/KpiBaseSetBasePrizeRate'
import ProjectTypeFactor from './ProjectTypeFactor/KpiBaseSetProjectTypeFactor'
import ProjectScaleFactor from './ProjectScaleFactor/KpiBaseSetProjectScaleFactor'
import PutPrizeFactor from './PutPrizeFactor/KpiBaseSetPutPrizeFactor'

const Index = ({ store }) => {
  const { isEdit } = store
  const items = [
    {
      label: '基础提奖比例',
      children: <BasePrizeRate />,
    },
    {
      label: '项目类型系数',
      children: <ProjectTypeFactor />,
    },
    {
      label: '项目规模系数',
      children: <ProjectScaleFactor />,
    },
    {
      label: '投放奖金系数',
      children: <PutPrizeFactor />,
    },
  ]

  const { effectMonth, id } = store.parameterModal.getInitialValues() ?? {}

  const newItems = items.map(({ label, children }) => {
    return {
      label,
      key: label,
      children: cloneElement(children, {
        typeInfo: { isEdit, effectMonth, id },
      }),
    }
  })
  return (
    <Modal
      store={store.parameterModal}
      title={isEdit ? '编辑' : '查看'}
      footer={null}
      width={1000}
      destroyOnClose
    >
      <Form
        disabled={!isEdit}
        initialValues={{
          date: effectMonth && moment(effectMonth),
        }}
      >
        <Form.Item name="date" label="生效月份" rules={[{ required: true }]}>
          <DatePicker picker="month" disabled></DatePicker>
        </Form.Item>
      </Form>
      <Tabs items={newItems}></Tabs>
    </Modal>
  )
}

export default observer(Index)
