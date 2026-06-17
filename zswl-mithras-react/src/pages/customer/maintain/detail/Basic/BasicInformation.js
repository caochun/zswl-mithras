import React, { useEffect, useState } from 'react'
import { Descriptions, Input } from 'antd'
import { Select, Form } from '@zswl/components'
import { observer, getQuery } from '@zswl/admin'
import styles from './index.less'
import Api from '@/api/customer/maintainApi'
import { getAge } from '@/utils/base'

const BasicInformation = ({ id, clientType, save, store, startUserId, businessVersion }) => {
  const [compare, setCompare] = useState({})
  const [form] = Form.useForm()
  const initDetail = async () => {
    const data = await Api.naturalDetail({ clientId: id })
    store.setBaseInfo(data)

    form.setFieldsValue({
      ...data,
      clientType: clientType == 'CORPORATION' ? '法人' : '自然人',
      age: data.certType == '10' ? getAge(data.certNumber) : data.age,
    })
  }
  useEffect(() => {
    if ((id, clientType)) {
      //store.getDetail(id, clientType)
      initDetail()
    }
  }, [id, clientType, save])
  // 对比接口
  const getApprovalNormaldetail = async () => {
    const data = await Api.getApprovalNormaldetail({ clientId: id, startUserId, businessVersion })
    setCompare(data)
  }
  useEffect(() => {
    if (getQuery('typeId') == 'approval') {
      getApprovalNormaldetail()
    }
  }, [getQuery('typeId')])
  const starDom = (name, must, obj) => {
    return (
      <span className={styles.colorsWrap}>
        {must && <span className={styles.colors}>*</span>}
        <span style={{ color: obj?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>{name}</span>
      </span>
    )
  }
  return (
    <div>
      <div className={styles.basicWrap}></div>
      <Form store={store.naturalForm} cache="false" form={form}>
        <Descriptions
          title="基本信息"
          bordered
          column={2}
          labelStyle={{ background: '#F5F6FA' }}
          size={'small'}
          className={styles.des}
        >
          <Descriptions.Item
            label={starDom('客户名称', true, compare.clientName)}
            labelStyle={{ width: '180px' }}
            contentStyle={{ width: '400px' }}
          >
            <Form.Item name="clientName">
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item
            label={starDom('客户分类', false, compare.clientType)}
            labelStyle={{ width: '180px' }}
            contentStyle={{ width: '400px' }}
          >
            <Form.Item name="clientType">
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('客户编号', false, compare.clientCode)}>
            <Form.Item
              name="clientCode"
              // rules={[
              //   {
              //     required: true,
              //     message: '请输入客户编号',
              //   },
              // ]}
            >
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('证件类型', true, compare.certType)}>
            <Form.Item name="certType">
              <Select style={{ width: '100%' }} options="certType" disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('证件号码', true, compare.certNumber)}>
            <Form.Item name="certNumber">
              <Input disabled />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('性别', true, compare.gender)}>
            <Form.Item
              name="gender"
              rules={[
                {
                  required: true,
                  message: '请选择性别',
                },
              ]}
            >
              <Select style={{ width: '100%' }} options="genderType" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('婚姻状况', true, compare.marriageType)}>
            <Form.Item
              name="marriageType"
              rules={[
                {
                  required: true,
                  message: '请选择婚姻状况',
                },
              ]}
            >
              <Select style={{ width: '100%' }} options="marriageType" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('国别', true, compare.country)}>
            <Form.Item
              name="country"
              rules={[
                {
                  required: true,
                  message: '请选择国别',
                },
              ]}
            >
              <Select style={{ width: '100%' }} options="countryList" />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('年龄', false, compare.age)}>
            <Form.Item name="age">
              <Input />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('手机号', false, compare.mobileNumber)}>
            <Form.Item name="mobileNumber">
              <Input />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('家庭住址', false, compare.homeAddress)}>
            <Form.Item name="homeAddress">
              <Input />
            </Form.Item>
          </Descriptions.Item>
          <Descriptions.Item label={starDom('邮箱', false, compare.mail)}>
            <Form.Item name="mail">
              <Input />
            </Form.Item>
          </Descriptions.Item>
        </Descriptions>
      </Form>
    </div>
  )
}

export default observer(BasicInformation)
