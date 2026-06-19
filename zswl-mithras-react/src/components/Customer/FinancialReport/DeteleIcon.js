import IconFont from '@/components/Icon'
import { Modal } from '@zswl/components'
import { SHEET_TABLE_NAME, subjectReportType } from './columns'
import Api from '@/api/customer/financialReportApi'
import { message } from 'antd'

const Index = ({ dataSource, index, store, handleRemove = () => {} }) => {
  const { canEditFlag } = store
  const deleteTable = (data, key) => {
    const item = data[key]
    const { year, quarter, subjectType, reportType } = item
    const sheetName = SHEET_TABLE_NAME[subjectType]
    const subjectTypeName = subjectReportType[reportType]

    Modal.confirm({
      title: '删除财报二次确认',
      content: (
        <div>
          <div>
            您将删除{year}年-{quarter}月-{sheetName}-{subjectTypeName}!
          </div>
          <div>请确认是否删除！</div>
        </div>
      ),
      onOk: async () => {
        await Api.removeSubjectItem({
          year,
          quarter,
          subjectType,
          reportType,
          clientId: store.clientId,
        })
        message.success('删除成功')
        handleRemove?.()
      },
    })
  }

  return canEditFlag ? (
    <IconFont type="icon-delete" onClick={() => deleteTable(dataSource, index)}></IconFont>
  ) : null
}

export default Index
