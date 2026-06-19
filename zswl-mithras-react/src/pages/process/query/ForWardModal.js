import { Row, Col } from 'antd'
import { ApiSelect } from '@/components/Select'
import { observer } from '@zswl/admin'
import { Modal, Form } from '@zswl/components'
import Api from '@/api/process/flowProcessQueryApi'
import { rules } from '@/utils'

const ForWardModal = ({ store }) => {
  const transformResult = (data) => {
    const res = []
    data?.userList?.map((item) => {
      res.push({
        label: item.userName,
        value: item.id,
      })
    })
    return res
  }
  return (
    <Modal
      store={store.forWardModalStore}
      propsBy={() => ({
        title: '转办',
      })}
      destroyOnClose
    >
      <Form>
        <Row>
          <Col span={24}>
            <Form.Item name="user" label={'用户'} rules={[rules.required()]}>
              <ApiSelect
                api={Api.getUserList}
                transformResult={transformResult}
                searchField="userName"
                debounceSearch
              ></ApiSelect>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default observer(ForWardModal)
