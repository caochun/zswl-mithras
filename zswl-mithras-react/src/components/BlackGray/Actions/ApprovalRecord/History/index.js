import { Button, Card, Timeline, Modal } from 'antd'
import { CheckCircleOutlined, PartitionOutlined, UserOutlined } from '@ant-design/icons'
import styles from './style.less'
import { useEffect, useState } from 'react'
import { http } from '@zswl/admin'
import moment from 'moment'
import { ProcessTaskFlowChart as Flow } from '@/components/Process/ProcessTaskFlowChartEntries'

/**
 * 审批记录，大部分情况下是用ApprovalAction组件即可，
 * 但是如果出现审批历史不在一个抽屉里，那么才会单独使用这个组件
 * @param request 获取审批记录数据，是个列表数据，字段如下
 * id：主建
 * orgInfo：机构信息
 * account：用户名
 * oper：操作
 * suggest：意见
 * time：时间
 * @param params 相关参数，bizId，bizCode，taskId
 */
function Index({ request, params, visible }) {
  const [list, setList] = useState([])
  const [open, setOpen] = useState(false)
  const getList = async () => {
    if (request) {
      const res = await request()
      setList(res)
    } else if (params) {
      const { bizId, bizCode, taskId } = params
      if (bizId && taskId) {
        const res = await http.get('/audit/common/record/get', {
          params: {
            bizId,
            bizType: bizCode,
            taskId,
          },
        })
        setList(res)
      }
    }
  }
  useEffect(() => {
    if (visible) {
      getList()
    }
  }, [visible])
  return (
    <Card
      bordered={false}
      title={'审批记录'}
      extra={
        params.taskId ? (
          <Button type={'primary'} icon={<PartitionOutlined />} onClick={() => setOpen(true)}>
            流程图
          </Button>
        ) : null
      }
    >
      <Timeline>
        {(list || []).map((info) => {
          const { id, orgInfo, account, oper, suggest, time, targetUser } = info
          return (
            <Timeline.Item key={id} dot={<CheckCircleOutlined style={{ fontSize: 18 }} />}>
              <div className={styles.card}>
                <div className={styles.icon}>
                  <UserOutlined />
                </div>
                <div className={styles.info}>
                  <div className={styles.org}>{orgInfo}</div>
                  <div className={styles.user}>
                    <div>
                      <span className={styles.name}>{account}</span>
                      <span>{oper}</span>
                      {oper === '转派到' && <span className={styles.transmit}>{targetUser}</span>}
                    </div>
                    <div className={styles.date}>{moment(time).format('YYYY-MM-DD HH:mm:ss')}</div>
                  </div>

                  <div className={styles.suggest}>{suggest}</div>
                </div>
              </div>
            </Timeline.Item>
          )
        })}
      </Timeline>
      <Modal
        destroyOnClose
        title={'流程图'}
        footer={null}
        open={open}
        style={{ top: 30 }}
        width={'90vw'}
        onCancel={() => setOpen(false)}
      >
        <Flow height={'80vh'} taskId={params.taskId} />
      </Modal>
    </Card>
  )
}

export default Index
