import { Button, Modal, Table, Select, Form } from '@zswl/components'
import { ShareAltOutlined, PlusOutlined } from '@ant-design/icons'
import { DatePicker, message } from 'antd'
import OrgTree from './OrgTree'
import moment from 'moment'
import OrgSelect from './OrgSelect'
import { http } from '@zswl/admin'
import { saveServer } from '@/utils'

/**
 * @param {object} props
 * @param {string} [props.lineType] 归属条线
 * @param {import('moment').Moment | string} [props.deadline] 最大截止日期
 * @param {string} props.bizType 业务类型
 * @param {number} props.parentTaskId 上级任务id
 * @param {function} props.onSuccess 用于提交成功后列表刷新
 * @returns
 */
function Index({
  lineType,
  isShowDate = true,
  deadline: date,
  parentTaskId,
  bizType,
  onSuccess,
  ...rest
}) {
  const tableStore = Table.useStore({
    request() {
      return [{ id: '__ZS_INITIAL_ROW' }]
    },
  })
  const modalStore = Modal.useStore({})
  const handleOk = async () => {
    try {
      const { list } = await tableStore.submit()
      if (list.length) {
        let data = {
          parentTaskId,
          bizType,
        }
        // 将日期格式化
        data.subTaskList = list.map((item) => {
          const { id, deadline, ...values } = item
          const format = 'YYYY-MM-DD HH:mm:ss'
          // 设置 23:59:59 截止日期
          // const time = isShowDate
          //   ? moment(deadline).hours(23).minutes(59).seconds(59).format(format)
          //   : undefined
          const time = isShowDate ? moment(deadline).endOf('days').format(format) : undefined
          return {
            ...values,
            deadline: time,
          }
        })
        await http.post('/subtask/assign', data)
        modalStore.close()
        onSuccess?.()
        message.success('派发成功')
      } else {
        message.warn('至少添加一项')
      }
    } catch (e) {
      const { errorFields } = e
      // 提示第一个错误
      const error = errorFields?.[0]?.errors?.[0]
      if (error) {
        message.error(error)
      }
    }
  }

  return (
    <>
      <Button type="primary" icon={<ShareAltOutlined />} onClick={modalStore.open} {...rest}>
        派发
      </Button>
      <Modal width={1000} store={modalStore} title="派发" onOk={handleOk} destroyOnClose>
        <Table
          serial
          store={tableStore}
          scroll={null}
          pagination={false}
        columnsFilter={'RiskActions_Distribute_1'}
        onFilter={(key,val) => saveServer('RiskActions_Distribute_1',val)}

          actions={[
            {
              name: '添加',
              type: 'primary',
              icon: <PlusOutlined />,
              onClick() {
                tableStore.addRow()
              },
            },
          ]}
          columns={[
            {
              title: '派发机构',
              dataIndex: 'deptCode',
              editable: {
                element: <OrgTree />,
                rules: [{ required: true, message: '请选择机构' }],
              },
            },
            {
              title: '接收角色',
              dataIndex: 'assignSubmitRole',
              render: (val, record, index) => {
                return (
                  <Form.Item noStyle dependencies={[[record.id, 'deptCode']]}>
                    {(form) => {
                      const orgCode = form.getFieldValue([record.id, 'deptCode'])
                      return (
                        <OrgSelect
                          form={form}
                          name={[record.id, 'assignSubmitRole']}
                          subName={[record.id, 'assignSubmitUser']}
                          lineType={lineType}
                          code={orgCode}
                          type="role"
                          fieldNames={{
                            label: 'name',
                            value: 'code',
                          }}
                        />
                      )
                    }}
                  </Form.Item>
                )
              },
            },
            {
              title: '接收用户',
              dataIndex: 'assignSubmitUser',
              render: (val, record, index) => {
                return (
                  <Form.Item noStyle dependencies={[[record.id, 'assignSubmitRole']]}>
                    {(form) => {
                      const role = form.getFieldValue([record.id, 'assignSubmitRole'])

                      return (
                        <OrgSelect
                          form={form}
                          name={[record.id, 'assignSubmitUser']}
                          lineType={lineType}
                          code={role}
                          type="user"
                          fieldNames={{
                            label: 'userName',
                            value: 'account',
                          }}
                        />
                      )
                    }}
                  </Form.Item>
                )
              },
            },
            isShowDate && {
              title: '报送截止日期',
              dataIndex: 'deadline',
              editable: {
                element: (
                  <DatePicker
                    showToday={false}
                    defaultPickerValue={moment(date)}
                    disabledDate={(currentTime) => {
                      if (!date) return false
                      return moment(date).isBefore(currentTime)
                    }}
                  />
                ),
                rules: [{ required: true, message: '请选择截止日期' }],
              },
            },
            {
              title: '操作',
              width: 100,
              actions({ id }) {
                return [
                  {
                    name: '删除',
                    onClick() {
                      tableStore.deleteRow(id)
                    },
                  },
                ]
              },
            },
          ]}
        />
      </Modal>
    </>
  )
}

export default Index
