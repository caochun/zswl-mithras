
import { observer } from '@zswl/admin'
import { App, Button, Descriptions, Form, Modal, ModalStore, Select, Table } from '@zswl/components'
import { useEffect, useImperativeHandle, useMemo } from 'react'
import Store from './store'

import { Input, Radio, Space, Tooltip } from 'antd'
import styles from './index.less'
import { dateRangeTransformV2, isOperationmanagementagent, isProjmanager, toHump } from '@/utils'
import { saveServer } from '@/utils'

const TextRender = ({ text, record, style = {} }) => {
  const modal = useMemo(() => new ModalStore(), [])

  const showFullText = () => {
    modal.open()
  }

  return (
    <>
      <div
        style={{
          cursor: text ? 'pointer' : undefined,
          WebkitLineClamp: 3,
          WebkitBoxOrient: 'vertical',
          display: '-webkit-box',
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          ...style,
        }}
        dangerouslySetInnerHTML={{ __html: text ? `${text} ` : '-' }}
        onClick={text ? showFullText : undefined}
      ></div>
      <Modal store={modal} title={record?.title ?? '系统取数'} width={800} footer={null}>
        <div
          style={{
            whiteSpace: 'pre-wrap',
            wordBreak: 'break-all',
            overflowY: 'scroll',
            maxHeight: 600,
          }}
          dangerouslySetInnerHTML={{ __html: `<div> ${text}</div>` }}
        ></div>
      </Modal>
    </>
  )
}

const CpmPaymentApplicationPublicInformation = ({ paymentId, style, taskActivityId, canEditFlag, taskStatus, buttonProps }, ref) => {
  const canEditTask = ['1'].includes(taskStatus)

  const isProj = isProjmanager()
  const isProjCanEdit = canEditTask && taskActivityId === 'userTask_projectmanager' && isProj
  const isOperationDept = isOperationmanagementagent()
  const isOperationCanEdit = canEditFlag && isOperationDept
  const canEdit = isProj || isOperationDept
  const store = useMemo(() => new Store({ paymentId }), [paymentId])
  const {
    table,
    editable,
    setEditable,
    currentInfo,
    getClientList,
    clientList,
    dateList,
    clientId,
    currentDate,
    getParams,
  } = store
  const { clientType, originClientType } = getParams()
  const clientTypeName = App.matchOption('publicInfoClientTypeEnum', clientType)?.label
  const isNormal = originClientType === 'NORMAL'
  const descColumns = [
    { title: '查询起始日', dataIndex: 'queryFrom' },
    { title: '查询结束日', dataIndex: 'queryTo' },
    { title: `${clientTypeName}名称`, dataIndex: 'clientName' },
  ]

  // useEffect(() => getClientList(), [paymentId])
  const columns = [
    {
      title: '查询事项',
      dataIndex: 'title',
      colSpan: 2,
      width: 220,
      editable: false,
      render: (val) => (
        <Tooltip title={val} getPopupContainer={() => document.body}>
          {val}
        </Tooltip>
      ),
    },
    {
      title: '查询事项',
      dataIndex: 'description',
      colSpan: 0,
      ellipsis: false,
      width: 300,
      render: (val) => {
        return (
          <Tooltip title={<div dangerouslySetInnerHTML={{ __html: `<div> ${val}</div>` }} />}>
            <div
              dangerouslySetInnerHTML={{ __html: `${val}` }}
              style={{ whiteSpace: 'pre-wrap' }}
            />
          </Tooltip>
        )
      },
      editable: false,
    },

    {
      title: '排查结果',
      dataIndex: 'investigationType',
      colSpan: 2,
      width: 200,
      editable: (val, index) => {
        if (!isOperationDept) return false
        return (
          <>
            <Form.Item
              name={[`${val.id}`, 'investigationType']}
              initialValue={val.investigationType}
            >
              <Select options={'investigationResultEnum'} getPopupContainer={() => document.body} />
            </Form.Item>
            <Form.Item
              name={[`${val.id}`, 'investigationExplain']}
              initialValue={val.investigationExplain}
              style={{ marginBottom: 12 }}
            >
              <Input.TextArea />
            </Form.Item>
          </>
        )
      },
      render: (val, record) => {
        const isRed = record.investigationType === 'WITH_EXPLANATION'
        return (
          <div style={{ color: isRed ? 'red' : undefined }}>
            <div>{App.matchOption('investigationResultEnum', record.investigationType).label}</div>
            <Tooltip title={record.investigationExplain} getPopupContainer={() => document.body}>
              <div
                style={{
                  WebkitLineClamp: 3,
                  '-webkit-box-orient': 'vertical',
                  whiteSpace: 'normal',
                  display: '-webkit-box',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                }}
              >
                {record.investigationExplain}
              </div>
            </Tooltip>
          </div>
        )
      },
    },

    UploadColumn({
      title: '排查结果',
      dataIndex: 'resultFileList',
      colSpan: 0,
      editable: !!isOperationDept,
      params: (record) => {
        const params = {
          mainId: record.id,
          moduleType: 'PUBLIC_INFO',
          materialsSubType: `${record.rowKey}_YY`,
        }
        return params
      },
    }),
    TextAreaColumn({
      title: '解释说明(项目经理填写)',
      dataIndex: 'projectManagerExplain',
      width: 180,
      colSpan: 2,
      editable: (record) => {
        if (!isProj) return false
        const required = record.investigationType === 'WITH_EXPLANATION'
        return {
          initialValue: record.projectManagerExplain,
          rules: required ? [{ required: true, message: '请输入解释说明' }] : [],
          element: <Input.TextArea rows={4} />,
          style: { marginBottom: 12 },
        }
      },
      render: (val) => (
        <Tooltip title={val}>
          <div
            style={{
              WebkitLineClamp: 3,
              '-webkit-box-orient': 'vertical',
              whiteSpace: 'normal',
              display: '-webkit-box',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
            }}
          >
            {val}
          </div>
        </Tooltip>
      ),
    }),
    UploadColumn({
      title: '解释说明(项目经理填写)',
      dataIndex: 'projectManagerFileList',
      colSpan: 0,
      editable: isProj,
      params: (record) => {
        const params = {
          mainId: record.id,
          moduleType: 'PUBLIC_INFO',
          materialsSubType: `${record.rowKey}_XMJL`,
        }
        return params
      },
    }),
  ]
  useImperativeHandle(ref, () => ({
    searchPublicInfo: store.searchPublicInfo,
  }))
  return (
    <>
      <Button onClick={store.searchPublicInfo} type="link" style={style} {...buttonProps}>
        公开信息查询
      </Button>
      <Modal store={store.publicModal} title="公开信息查询报告" width={1500} footer={null}>
        <div>
          <div className="z-flex-jsb">
            <div></div>
            <Space>
              <Button type="primary" onClick={store.download}>
                报告批量下载
              </Button>
            </Space>
          </div>
          <div>
            <Radio.Group
              buttonStyle="solid"
              onChange={(e) => store.clientChange(e.target.value, true)}
              value={clientId}
            >
              <Space wrap>
                {(clientList ?? []).map((item) => (
                  <div
                    className={item.isExistRequiredNotFill ? styles.requireRadio : undefined}
                    key={item.clientId}
                  >
                    <Radio.Button value={item.clientId}>{item.clientName}</Radio.Button>
                  </div>
                ))}
              </Space>
            </Radio.Group>
          </div>
          <div style={{ marginBottom: 12 }}>
            <Radio.Group
              defaultValue="a"
              buttonStyle="solid"
              onChange={(e) => store.currentDateChange(e.target.value, true)}
              value={currentDate}
              style={{ marginTop: 12 }}
            >
              <Space wrap>
                {(dateList ?? []).map(({ isExistRequiredNotFill, queryFrom, queryTo, id }) => {
                  const value = [queryFrom, queryTo].join('~')
                  return (
                    <div
                      className={isExistRequiredNotFill ? styles.requireRadio : undefined}
                      key={id}
                    >
                      <Radio.Button value={id}>{value}</Radio.Button>
                    </div>
                  )
                })}
                {isOperationCanEdit && (
                  <Button onClick={store.createPublicInfo} type="primary">
                    添加
                  </Button>
                )}
              </Space>
            </Radio.Group>
          </div>
          <div className="z-flex-jsb" style={{ marginBottom: 12 }}>
            <Space>
              <span> 查询人：{currentInfo?.confirmName} </span>
              <span> 确认时间：{currentInfo?.confirmTime} </span>
            </Space>

            {canEdit && (
              <Space>
                {/* {!isNormal && (
                  <Button type="primary" onClick={store.getNewData} disabled={!currentDate}>
                    重新取数
                  </Button>
                )} */}
                {editable ? (
                  <>
                    <Button onClick={store.remove} disabled={!isOperationCanEdit}>
                      删除
                    </Button>
                    <Button onClick={store.cancel}>取消</Button>
                    <Button onClick={store.save} type="primary">
                      保存
                    </Button>
                  </>
                ) : (
                  <Button type="primary" onClick={() => setEditable(true)} disabled={!currentDate}>
                    编辑
                  </Button>
                )}
              </Space>
            )}
          </div>
          <div style={{ maxHeight: 450, overflowY: 'scroll' }}>
            <Descriptions items={descColumns} editable={false} dataSource={currentInfo} />
            <Table
              columnsFilter={'detail_PublicInformation_1'}
              onFilter={(key, val) => saveServer('detail_PublicInformation_1', val)}
              columns={columns}
              store={table}
              bordered
              autoRequest={false}
              editable={canEdit && editable}
            />
          </div>
        </div>
      </Modal>
      <Modal store={store.createModal} title="请选择公开信息查询时间区间">
        <Form>
          <Form.Item
            label="查询日期"
            name="query"
            rules={[{ required: true, message: '请选择查询日期' }]}
            transform={(value) => dateRangeTransformV2(value, 'query')}
          >
            <FormDateRange />
          </Form.Item>
        </Form>
      </Modal>
    </>
  )
}
export default observer(React.forwardRef(CpmPaymentApplicationPublicInformation))
