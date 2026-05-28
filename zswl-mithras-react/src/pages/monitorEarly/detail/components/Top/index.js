import { Input, Badge, Typography } from 'antd'
import styles from './style.less'
import { getQuery, history, http, observer } from '@zswl/admin'
import { Descriptions, Form, Page } from '@zswl/components'
import Vector from '/public/assets/risk/monitoringAlertList/vector.svg'
import { LeftOutlined } from '@ant-design/icons'
import Frame from '/public/assets/risk/monitoringAlertList/Frame.svg'
import { useEffect } from 'react'

const YqTitle = ({ value, onChange, placeholder }) => {
  return (
    <>
      <Input.TextArea
        value={value}
        placeholder={placeholder}
        onChange={onChange}
        autoSize={{ minRows: 3 }}
        style={{
          background: '#f5f5f5',
          border: '1px solid #d9d9d9',
          borderRadius: '4px',
        }}
      />
      <div style={{ marginTop: '8px', textAlign: 'left' }}>
        <a href="#originalText">查看原文</a>
      </div>
    </>
  )
}
const Jb = ({ value, onChange, placeholder }) => {
  return (
    <>
      <Input
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        prefix={value == '红灯' ? <Vector /> : <Frame />}
        className={value == '红灯' ? styles['warning-input'] : styles['waring-input-yellow']}
      ></Input>
    </>
  )
}

const CustomerDetail = ({ store, params }) => {
  console.log('params: ', params)
  const { id } = params
  const page = Page.useStore({
    request: async (values) => {
      const res = await http.post('/risk/warn/monitor/warn/detail', values)
      return res
    },
  })
  const detail = page.getData()

  const tag = getQuery('tag')

  const haderContentViewYj = [
    {
      title: '预警编号',
      name: 'warnCode',
    },

    {
      title: '企业名称',
      name: 'chiName',
    },
    {
      title: '预警时间',
      name: 'dataTime',
    },
    {
      title: '预警级别',
      name: 'warnLevel',
      element: <Jb></Jb>,
    },

    {
      title: '处置状态',
      name: 'handleStatus',
      matchOption: 'riskControlOpinionHandleStatus',
    },
    {
      title: '预警指标',
      name: 'resFormat',
    },

    {
      title: '触发数据',
      name: 'tableName',
      col: 24, // 强制让舆情标题占整行
      labelCol: { span: 2 },
      wrapperCol: { span: 22 },
      element: (
        <Input.TextArea
          autoSize={{ minRows: 3 }}
          style={{
            background: '#f5f5f5',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
          }}
        />
      ),
    },

    {
      title: '规则说明：',
      name: 'tableName1',
      col: 24, // 强制让舆情标题占整行
      labelCol: { span: 2 },
      wrapperCol: { span: 22 },
      element: (
        <Input.TextArea
          autoSize={{ minRows: 3 }}
          style={{
            background: '#f5f5f5',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
          }}
        />
      ),
    },
    { name: 'id', hidden: true },
  ]

  const haderContentViewYq = [
    {
      title: '客户名称',
      name: 'name',
    },

    {
      title: '统一社会信用代码',
      name: 'url',
    },
    {
      title: '信息发布时间',
      name: 'method',
    },
    {
      title: '预警级别',
      name: 'resFormat',
      element: <Jb></Jb>,
    },

    {
      title: '重要度',
      name: 'resFormat',
      element: (
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            backgroundColor: '#f5f5f5',
            border: '1px solid #d9d9d9',
            borderRadius: '4px',
            padding: '4px 11px',
          }}
        >
          <Badge color="red" text="三星" />
        </div>
      ),
    },
    {
      title: '处置状态',
      name: 'resFormat',
    },
    {
      title: '舆情标题',
      name: 'tableName',
      col: 24, // 强制让舆情标题占整行
      labelCol: { span: 2 },
      wrapperCol: { span: 22 },
      element: <YqTitle />,
    },
    { name: 'id', hidden: true },
  ]

  // 顶部指标数据
  return (
    <Page noStyle store={page} params={{ id }}>
      <div className={styles.contentView}>
        <Descriptions
          initialValues={detail} // 传入模拟数据
          disabled={true}
          store={store.form}
          column={3}
          items={haderContentViewYj}
          layout="horizontal"
        />
      </div>
    </Page>
  )
}

export default observer(CustomerDetail)
