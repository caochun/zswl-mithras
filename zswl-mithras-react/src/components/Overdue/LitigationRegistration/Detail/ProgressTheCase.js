import { Button, Page, Table, Modal, Form, Select } from '@zswl/components'
import { observer } from '@zswl/admin'
import { getTableColumns } from '@/utils'
import ALL_COLUMNS from '../Column'
import { useMemo } from 'react'
import { saveServer } from '@/utils'

const { Item } = Form

const ProgressModal = observer(({ store }) => {
  const { stage, status } = store.progressModal.getInitialValues() ?? {}

  const buttonConfigs = {
    PREPARE: [{ key: 'save', text: '开始诉讼', status: 'LITIGATION' }],
    LITIGATION: [
      { key: 'save', text: '保存', status: 'LITIGATION' },
      { key: 'startExecution', text: '开始执行', status: 'EXECUTION' },
      { key: 'closeCase', text: '结案', status: 'END' },
    ],
    EXECUTION: [
      { key: 'retrial', text: '再审', status: 'LITIGATION' },
      { key: 'executionEnd', text: '执行终本', status: 'EXECUTION_END' },
      { key: 'closeCase', text: '结案', status: 'END' },
    ],
    EXECUTION_END: [
      { key: 'retrial', text: '再审', status: 'LITIGATION' },
      { key: 'continueExecution', text: '继续执行', status: 'EXECUTION' },
      { key: 'closeCase', text: '结案', status: 'END' },
    ],
    default: [{ key: 'save', text: '诉前准备', status: 'PREPARE' }],
  }

  const renderFooterButtons = () => {
    const currentConfig = status ? buttonConfigs[status] : buttonConfigs.default

    return currentConfig?.map(({ key, text, status }) => (
      <Button key={key} onClick={() => store.updateStatus(status)} type="primary">
        {text}
      </Button>
    ))
  }
  const stageDisabled = [null, 'PREPARE', 'EXECUTION', 'EXECUTION_END'].includes(status)

  return (
    <Modal title="案件进展" width={600} store={store.progressModal} footer={renderFooterButtons()}>
      <Form>
        <Item name="stage" label="诉讼阶段" required>
          <Select
            options={'litigationStageEnum'}
            onChange={store.handleStageChange}
            disabled={stageDisabled}
          />
        </Item>
        <Item name="status" label="当前诉讼状态" required>
          <Select options={'litigationStatus'} disabled />
        </Item>
      </Form>
    </Modal>
  )
})

const OverdueLitigationProgressTheCase = observer(({ path, store }) => {
  const columns = useMemo(() => {
    const nameColumns = ['诉讼阶段', '诉讼状态', '记录人', '记录时间']
    return getTableColumns(ALL_COLUMNS, nameColumns)
  }, [])
  const list = store.table.getList()
  const disabled = list?.[0]?.status === 'END'
  return (
    <div style={{ margin: '12px 0' }}>
      <h2>案件进展 </h2>
      <Table
        store={store.table}
        editable={false}
        selectable={false}
        actions={[
          <Button.Add onClick={store.addProgress} key="add" disabled={disabled}>
            新增
          </Button.Add>,
        ]}
        scroll={{ x: 'auto' }}
        resizable
        columnsFilter={'litigationRegistration_detail_ProgressTheCase'}
        onFilter={(key,val) => saveServer('litigationRegistration_detail_ProgressTheCase',val)}
        columns={columns}
      />
      <ProgressModal store={store} />
    </div>
  )
})

export default OverdueLitigationProgressTheCase
