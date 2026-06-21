import { observer } from '@zswl/admin'
import { useRef } from 'react'
import { Space } from 'antd'
import { Modal, Button } from '@zswl/components'
import { ProjectFinancialReportStatistics as FinancialReportStatistics } from '@/components/Project/FinancialReportStatisticsEntries'

// 风控经理:评审流程提交时检验客户管理模块财务报表录入是否完整
const ProcessCheckCorpSubject = (props) => {
  const { store, id } = props
  const financialRef = useRef()

  const submit = async () => {
    const { submit: submitFn } = store.checkCorpSubjectMethods
    await submitFn?.()
    store.checkCorpSubjectModal.close()
  }
  return (
    <div>
      <Modal
        title="提示"
        store={store.checkCorpSubjectModal}
        footer={
          <Space>
            <Button type="primary" onClick={() => financialRef.current?.modal?.open()}>
              查看财务报表情况
            </Button>
            <Button onClick={submit}>继续提交</Button>
            <Button onClick={store?.checkCorpSubjectModal.close}>关闭</Button>
          </Space>
        }
      >
        <div>承租人/担保人财报信息不完整，是否确定继续提交流程？</div>
      </Modal>
      <FinancialReportStatistics
        ref={financialRef}
        id={id}
        canEdit={false}
      ></FinancialReportStatistics>
    </div>
  )
}
ProcessCheckCorpSubject.methods = {}

export default observer(ProcessCheckCorpSubject)
