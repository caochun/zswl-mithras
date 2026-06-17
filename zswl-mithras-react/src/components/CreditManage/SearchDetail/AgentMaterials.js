import fileList from '@/api/common/fileList'
import { FileTable } from '@/components'
import { toHump3 } from '@/utils'
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
      <h3>经办人资料</h3>
      {dataSource.map((item) => (
        <div key={item.id}>
          <FileTable
            enumType="handlerCreditReportSubTypeEnum"
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
            uploadApi={async ({ file, fileType }) =>
              await fileList.postFileUpload(
                {
                  file,
                  mainId: item.id,
                  moduleType: MODULE_TYPE,
                  materialsType: 'HANDLER_CREDIT_REPORT',
                  materialsSubType: fileType,
                  sourceBusinessKey: item.clientId,
                },
                `${toHump3(MODULE_TYPE)}FileUpload`
              )
            }
            params={{
              mainId: item.id,
              moduleType: MODULE_TYPE,
              materialsTypes: ['HANDLER_CREDIT_REPORT'],
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
