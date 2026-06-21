import customerRatApi from '@/api/customer/customerRat/customerRatApi'
import debtRatApi from '@/api/customer/customerRat/debtRatApi'
import { observer } from '@zswl/admin'
import { Form, Modal, Select } from '@zswl/components'
import { Col, Input, Radio, Row } from 'antd'
import { useEffect, useState } from 'react'
import { getUserInfo, rules } from '@/utils'
import styles from './index.less'

const CustomerDebtRatCreateModal = ({ store, tableParams }) => {
  const form = store.createModal.getFormStore()
  const initial = store.createModal.getInitialValues() ?? {}
  const isEdit = initial?.editType === 'edit'

  const lesseeChange = (value) => {
    const find = store.lesseeOption.find((item) => item.value === value.value)
    form.setFieldsValue({
      evaluationSubjectUscCode: find.evaluationSubjectUscCode,
    })
    getModel()
  }
  const getModel = async () => {
    const { projSystem, materialLeaseItem, evaluationSubject } = form.getFieldsValue()
    if (projSystem === undefined || !evaluationSubject) return
    const res = await debtRatApi.postAmountModelMatch({
      projSystem,
      evaluationSubjectId: evaluationSubject.value,
    })
    if (res?.name) {
      form.setFieldsValue({ name: res.name, code: res.code })
    }
  }

  return (
    <Modal title="新建债项评级" store={store.createModal} width={800} destroyOnClose>
      <Form>
        <div className={styles.tile}>评级项目</div>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="projName" label="项目名称" required>
              <Input disabled></Input>
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="projCode" label="项目编号" required>
              <Input disabled />
            </Form.Item>
          </Col>
        </Row>
        <div className={styles.tile}>债项类型</div>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="projSystem" label="是否为项目制" rules={[rules.required('请选择')]}>
              <Radio.Group
                onChange={getModel}
                options={[
                  { label: '项目制', value: true },
                  { label: '非项目制', value: false },
                ]}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name="materialLeaseItem"
              label="是否有实质性租赁物"
              rules={[rules.required('请选择')]}
            >
              <Radio.Group
                options={[
                  { label: '是', value: true },
                  { label: '否', value: false },
                ]}
              />
            </Form.Item>
          </Col>
        </Row>
        <div className={styles.tile}>主承租人</div>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="clientId" label="主承租人id" hidden>
              <Input disabled />
            </Form.Item>
            <Form.Item name="clientName" label="主承租人名称" required>
              <Input disabled />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="clientUscCode" label="统一社会信用代码" required>
              <Input disabled />
            </Form.Item>
          </Col>
        </Row>
        <div className={styles.tile}>评估主体</div>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item
              name="evaluationSubject"
              label="评估主体名称"
              rules={[rules.required('请选择')]}
            >
              <Select
                options={store.lesseeOption}
                fieldNames={{ label: 'evaluationSubjectName', value: 'evaluationSubjectId' }}
                labelInValue
                onChange={lesseeChange}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="evaluationSubjectUscCode" label="统一社会信用代码" required>
              <Input disabled />
            </Form.Item>
          </Col>
        </Row>
        <div style={{ color: 'red' }}>
          注：新能源企业评估主体请填写主承租人自身，其余敞口企业按原评估主体填写
        </div>
        <div className={styles.tile}>评级模型</div>
        <Row gutter={12}>
          <Col span={12}>
            <Form.Item name="name" label="评级模型名称" rules={[rules.required('请选择')]}>
              <Select
                fieldNames={{ label: 'name', value: 'name' }}
                options={async () => {
                  const res = await customerRatApi.postRatingModelQuery({
                    bizType: 'PROJ',
                  })
                  return res
                }}
                onChange={(value, option) => {
                  form.setFieldsValue({
                    code: option?.code,
                  })
                }}
              />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item name="code" label="评级类型编号" required>
              <Input disabled />
            </Form.Item>
          </Col>
        </Row>
      </Form>
    </Modal>
  )
}

export default observer(CustomerDebtRatCreateModal)
