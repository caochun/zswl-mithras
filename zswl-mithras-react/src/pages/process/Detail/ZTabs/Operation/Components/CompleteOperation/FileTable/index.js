import { UploadOutlined } from '@ant-design/icons'
import { observer } from '@zswl/admin'
import { Form, Table, Button } from '@zswl/components'
import { Upload, message, Modal, Space } from 'antd'
import _ from 'lodash'
import { useEffect, useMemo } from 'react'
import styles from './index.less'
import Store from './store'
import Api from '@/api/process/flowFile'
import { saveServer } from '@/utils'

function Index({ label, rules, params, materialsType, detail }) {
  const store = useMemo(() => {
    return new Store()
  }, [])
  const propsUpload = () => {
    return {
      name: 'file',
      multiple: false,
      beforeUpload(info) {
        let formdata = new FormData()
        formdata.append('file', info)
        formdata.append('materialsType', materialsType)
        formdata.append('processInstanceId', params.processInstanceId)
        formdata.append('taskId', params.taskId)
        store.uploadApproval(formdata, params)
        return false
      },
    }
  }
  useEffect(() => {
    if (!_.isEmpty(params)) store.listApprovalTable.search(params)
  }, [JSON.stringify(params)])

  const autoGenerateFile = async () => {
    const fileId = await Api.generateEarningsRate({ projReviewId: detail.businessKey })
    store.listApprovalTable.search(params)
    window.open(`/preview/reportPreview/${fileId}?editType=1`)
  }
  return (
    <div>
      <div className={styles.header}>
        <span>{label}</span>
        <div>
          <Space>
            <Upload {...propsUpload()}>
              <Button icon={<UploadOutlined />}>上传</Button>
            </Upload>
            {/* 项目定价评审、变更，财务主管节点增加自动生成  【项目评审】项目收益率审查意见书自动取值-http://tw.zswltech.cn:8888/s/1/30918*/}
            {['userTask_financeOfficer_1'].includes(detail.taskActivityId) &&
              ['ProjReviewPricingApprovalFlow', 'ProjReviewPricingModifyApprovalFlow'].includes(
                detail.modelKey
              ) && (
                <Button type="primary" className={styles.generateBtn} onClick={autoGenerateFile}>
                  生成报告
                </Button>
              )}
          </Space>
        </div>
      </div>
      <div className={styles.completeOperationtable}>
        <Table
          columnsFilter={'CompleteOperation_FileTable_1'}
          onFilter={(key, val) => saveServer('CompleteOperation_FileTable_1', val)}
          resizable
          autoRequest={false}
          store={store.listApprovalTable}
          columns={[
            { title: '资料名称', dataIndex: 'fileName', width: 200 },
            {
              title: '操作',
              width: 140,
              fixed: 'right',
              actions(value) {
                return [
                  {
                    name: '删除',
                    onClick: () => {
                      store.deleteReport(value, params)
                    },
                  },
                  {
                    name: '预览',
                    onClick: () => window.open(`/preview/reportPreview/${value.id}`),
                  },
                  {
                    name: '下载',
                    onClick: () => {
                      store.postReportDownload(value.id, params)
                    },
                  },
                ]
              },
            },
          ]}
        />
      </div>
    </div>
  )
}

export default observer(Index)
