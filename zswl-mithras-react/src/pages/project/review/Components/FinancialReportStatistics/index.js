import { forwardRef, useImperativeHandle, useState } from 'react'
import { Modal, Button, Form, App, Table } from '@zswl/components'
import { Space, Input, Tag } from 'antd'
import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons'
import FinancialReportStatisticsApi from '@/api/project/projReviewFinancialReport'
import { groupBy } from 'lodash'
import { saveServer } from '@/utils'

const { TextArea } = Input

const renderIcon = (value) => {
  const iconConf = {
    1: <CheckCircleOutlined style={{ color: 'green' }}></CheckCircleOutlined>,
    0: <CloseCircleOutlined style={{ color: 'red' }}></CloseCircleOutlined>,
  }
  return iconConf[value]
}

const Index = (props, ref) => {
  const { id, canEdit = true } = props
  const [detail, setDetail] = useState({})
  // 变更原因
  const [extParams, setExtParams] = useState({})
  // 不能提交
  const [canSubmit, setCanSubmit] = useState(true)
  const [form] = Form.useForm()

  const transformResult = (res) => {
    const result = groupBy(res, 'projectRoleName')
    return result
  }

  const modal = Modal.useStore(
    {
      onOpen: async (paramsData = {}) => {
        const { canSubmit: currCanSubmit, ...rest } = paramsData
        const res = await FinancialReportStatisticsApi.checkResultList({ id })
        res?.map((item) => {
          form.setFieldValue(`${item.projectRoleName}_${item.clientId}`, item.reason)
        })
        const newResult = transformResult(res)
        setDetail(newResult)
        setExtParams(rest)
        setCanSubmit(currCanSubmit)
        return {}
      },
      onFinish: async () => {
        modal.close()
      },
    },
    [id]
  )

  const saveData = async () => {
    const formData = await form.validateFields()
    const reasonList = []
    Object.keys(formData).map((key) => {
      reasonList.push({
        clientId: key.split('_')[1],
        reason: formData[key],
      })
    })
    await FinancialReportStatisticsApi.saveReason({ projReviewId: id, reasonList })
    modal.close()
    if (canSubmit) {
      props.onClick?.(extParams)
    }
  }

  useImperativeHandle(ref, () => ({
    modal,
  }))

  return (
    <Modal
      width={800}
      store={modal}
      title="财务报表情况统计"
      getContainer={() => document.body}
      footer={
        canEdit ? (
          <Space>
            <Button onClick={modal.close}>取消</Button>
            <Button type="primary" onClick={saveData}>
              提交
            </Button>
          </Space>
        ) : null
      }
    >
      <Form form={form} layout="vertical">
        {Object.keys(detail).map((key, keyIndex) => {
          return (
            <div key={keyIndex}>
              <h3>{key}</h3>
              {detail[key].map((client, clientIndex) => {
                return (
                  <div key={clientIndex}>
                    <h4> {client.clientName}</h4>
                    <div>
                      <Table
                        columnsFilter={'Components_FinancialReportStatistics_1'}
                        onFilter={(key, val) => saveServer('Components_FinancialReportStatistics_1', val)}
                        dataSource={client.checkResultDataList}
                        columns={[
                          {
                            title: '报告期',
                            dataIndex: 'reportPeriod',
                          },
                          client.orgType === '1' && {
                            title: '资产负债表',
                            dataIndex: 'capitalBalanceCheckResult',
                            render: renderIcon,
                          },
                          client.orgType === '1' && {
                            title: '利润表',
                            dataIndex: 'profitCheckResult',
                            render: renderIcon,
                          },
                          client.orgType !== '1' && {
                            title: '收入支出表',
                            dataIndex: 'incomeExpendResult',
                            render: renderIcon,
                          },
                          client.orgType === '1' && {
                            title: '现金流量表',
                            dataIndex: 'cashFlowCheckResult',
                            render: renderIcon,
                          },
                          {
                            title: '统计结果',
                            dataIndex: 'checkResult',
                            render: (value) => {
                              const textConf = {
                                1: <Tag color="green">通过</Tag>,
                                0: <Tag color="red">不通过</Tag>,
                              }
                              return textConf[value]
                            },
                          },
                        ].filter(Boolean)}
                        pagination={false}
                      ></Table>
                    </div>
                    {/* 若存在不通过的统计结果显示 或者 已填写不完整原因的后续刷新时显示已上传则不再展示  */}
                    {client.checkResult === 0 && (
                      <Form.Item
                        label="请填写财报不完整原因"
                        name={`${client.projectRoleName}_${client.clientId}`}
                        rules={[
                          {
                            required: true,
                            message: '请输入',
                          },
                        ]}
                      >
                        <TextArea autoSize={{ minRows: 2, maxRows: 6 }} disabled={!canEdit} />
                      </Form.Item>
                    )}
                  </div>
                )
              })}
            </div>
          )
        })}
      </Form>
    </Modal>
  )
}

export default forwardRef(Index)
