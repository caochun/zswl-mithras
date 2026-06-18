import creditReportApi from '@/api/credit/creditReportApi'
import { FileTable } from '@/components/Table'
import { observer } from '@zswl/admin'
import { message } from 'antd'

const MODULE_TYPE = 'CREDIT_REPORT_SELECT'
const Index = ({ mainId, canEdit = true, dataSource = [] }) => {
  const columns = [
    { title: '资料清单', dataIndex: 'name' },
    { title: '上传人', dataIndex: 'createByName' },
    { title: '上传时间', dataIndex: 'createTime' },
  ]

  return (
    <div>
      <h3>企业资料（必传）</h3>

      {dataSource.map((item) => (
        <div key={item.id}>
          <FileTable
            enumType="enterpriseCreditReportSubTypeEnum"
            title={<div style={{ fontSize: 12 }}>客户名称:{item.clientName}</div>}
            canEdit={canEdit}
            columns={columns}
            canBatchDownload
            uploadProps={{
              accept: '.jpg,.jpeg',
              beforeUpload: (file) => {
                return new Promise((resolve, reject) => {
                  // 只能上传 jpg 和 jpeg 格式的文件
                  if (!/.(jpg|jpeg)$/.test(file.name)) {
                    message.error('只能上传 jpg 和 jpeg 格式的文件')
                    return reject(false)
                  }

                  resolve(true)
                })
              },
            }}
            uploadTips={
              <div style={{ fontSize: 12, color: '#ff4d4f' }}>只能上传 jpg 和 jpeg 格式的文件</div>
            }
            uploadApi={async ({ file, fileType }) =>
              await creditReportApi.postCreditReportSelectFileUpload({
                file,
                mainId: item.id,
                moduleType: MODULE_TYPE,
                materialsType: 'ENTERPRISE_CREDIT_REPORT',
                materialsSubType: fileType,
                sourceBusinessKey: item.clientId,
              })
            }
            params={{
              mainId: item.id,
              moduleType: 'CREDIT_REPORT_SELECT',
              materialsTypes: ['ENTERPRISE_CREDIT_REPORT'],
              ext: {
                clientId: item.clientId,
              },
            }}
          />
        </div>
      ))}
    </div>
  )
}

export default observer(Index)
