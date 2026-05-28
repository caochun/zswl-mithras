import { Button, Form, Page, Select, Table } from '@zswl/components'
import { observer, http } from '@zswl/admin'
import { Input, message, Radio, Space, Tag } from 'antd'
import { useEffect, useState } from 'react'
import UploadData from './UploadData'
import _ from 'lodash'
import { copyText } from '@/utils'

const ip = 'http://10.158.20.143:7003'

const upload = (data) =>
  http.post(`${ip}/fix/uploadBatch`, data, {
    type: 'upload',
    transformResult: (res) => res.data,
    timeout: 0,
  })

const uploadPre = (data) =>
  http.post(`${ip}/fix/uploadBatchPre`, data, {
    transformResult: (res) => res.data,
    timeout: 0,
  })

const getQueryProjInfo = (params) =>
  http(`${ip}/fix/queryProjInfo`, {
    params,
    timeout: 0,
  })

const getCheckUnFixFile = (params) =>
  http(`${ip}/fix/checkUnFixFile`, {
    params,
    timeout: 0,
  })
const getFixUnFixFileCount = (params) =>
  http(`${ip}/fix/fixUnFixFileCount`, {
    params,
    timeout: 0,
  })
//模块枚举  CLIENT：客户管理  、PROJ_ESTABLISH：项目立项  、PROJ_REVIEW：项目评审  、CONTRACT：合同  、PAYMENT：付款
const options = [
  { label: '客户管理', value: 'CLIENT' },
  { label: '项目立项', value: 'PROJ_ESTABLISH' },
  { label: '项目评审', value: 'PROJ_REVIEW' },
  { label: '合同', value: 'CONTRACT' },
  { label: '付款', value: 'PAYMENT' },
]

const UrlMap = {
  付款数据: 'http://10.158.19.43/cpm/paymentApplication/detail/',
  合同数据: 'http://10.158.19.43/contract/list/detail/',
  客户数据: 'http://10.158.19.43/customer/maintain/detail/',
  立项数据: 'http://10.158.19.43/project/establishment/detail/',
  评审数据: 'http://10.158.19.43/project/review/detail/',
}

function Index() {
  const [form] = Form.useForm()
  const [data, setData] = useState([])

  const [msg, setMsg] = useState('')
  const [allId, setAllId] = useState('')
  const getUrl = (path, key) => {
    const [id, name] = path.split('：')
    const url = UrlMap[key] + id
    return (
      <a target="_blank" href={url}>
        {path}
      </a>
    )
  }
  const copy = (infoRes) => {
    const text = Object.entries(infoRes || {})
      .filter(([key, val]) => !_.isEmpty(val) && val !== '空')
      .map(([key, value]) => {
        const right = Object.entries(value)
          .filter(([k, v]) => !_.isEmpty(v))
          .map(
            ([k, v]) =>
              `${k}: ${_.isArray(v) && v.length > 0 ? (v || [])?.map((file) => file) : '无'}`
          )
          .join('\n')
        return `${key}: ${right}`
      })
      .filter((v) => {
        const [k, val] = v.split(': ')
        return !_.isEmpty(v.split(': ')[1])
      })
      .join('\n')
    copyText(text)
    message.success('复制成功')
  }

  const getImport = async () => {
    getAllCount()
    const { files, businessType, belongId } = form.getFieldsValue()
    if (businessType === 'PROJ_ESTABLISH') {
      const infoRes = await getCheckUnFixFile({ projEstablishId: belongId })
      setMsg('')
      const resStr = Object.entries(infoRes || {})
        .filter(([key, val]) => !_.isEmpty(val) && val !== '空')
        .map(([key, value]) => (
          <div key={key}>
            {key}:
            {Object.entries(value)
              // .filter(([k, v]) => !_.isEmpty(v))
              .map(([k, v]) => (
                <div key={k}>
                  {getUrl(k, key)}:
                  {_.isArray(v) && v.length > 0
                    ? (v || [])?.map((file) => (
                        <span key={file}>
                          <Tag>{file}</Tag>、
                        </span>
                      ))
                    : '无'}
                </div>
              ))}
          </div>
        ))
      resStr.push(
        <div>
          <Button onClick={() => copy(infoRes)}>复制立项相关数据</Button>
        </div>
      )
      setAllId(resStr)
    } else {
      message.info('请填写项目信息')
    }
  }
  const getAllId = async (businessType, belongId) => {
    if (businessType === 'PROJ_ESTABLISH') {
      const res = await getQueryProjInfo({ projEstablishId: belongId })

      const resStr = Object.entries(res || {}).map(([key, value]) => (
        <div key={key}>
          {key}:<Tag>{value}</Tag>
        </div>
      ))

      setAllId(resStr)
    }
  }
  const uploadFile = async (files, restParams = {}) => {
    getAllCount()
    const preParams = {
      ...restParams,
      fileNames: (files || []).map((v) => v.name),
    }
    const preRes = await uploadPre(preParams)
    const successFile = preRes?.data['名字匹配成功']
    const watingFile = preRes?.data['待上传文件']
    const newmessage = [
      <div key={'messge'}>
        名字匹配成功数量：{successFile?.length}, 名字匹配失败数量:
        {preRes?.data['名字匹配失败']?.length}
        待上传文件数量：{watingFile?.length}
      </div>,
      <div key="upload">待上传文件：{watingFile.join('、')}</div>,
    ]

    setMsg(newmessage)
    const newFiles = files.filter((v) => watingFile.includes(v.name))
    if (newFiles.length === 0) {
      message.info('没有匹配倒需要上传的文件')
      return
    }
    const params = {
      ...restParams,
      files: newFiles,
    }
    const res = await upload(params)
    message.success(`${newFiles.map((v) => v.name).join('、')}上传成功`)
    setData(JSON.stringify(res?.data))
  }

  const [allCount, setAllCount] = useState('')
  const getAllCount = async () => {
    const res = await getFixUnFixFileCount()
    setAllCount(res)
  }
  useEffect(() => {
    getAllCount()
  }, [])
  const submitUpload = async () => {
    const { files, businessType, belongId } = form.getFieldsValue()

    await getAllId(businessType, belongId)
    const restParams = {
      businessType,
      belongId,
    }
    return await uploadFile(files, restParams)
    // form.setFieldValue('files', [])
  }

  const [directory, setDirection] = useState(true)
  return (
    <Page>
      <h1>剩余待补录文件数: {allCount}</h1>
      <Space>
        <Button onClick={submitUpload} type="primary">
          提交导入
        </Button>
        <Button onClick={getImport}>获取项目未导入文件列表</Button>
      </Space>
      <div>{msg}</div>
      <h3>立项相关数据:</h3>
      <div>{allId}</div>
      <Form
        form={form}
        initialValues={{
          directory: true,
          businessType: 'PROJ_ESTABLISH',
        }}
      >
        <Form.Item label="模块枚举" name="businessType">
          <Select options={options} />
        </Form.Item>
        <Form.Item label="belongId" name="belongId">
          <Input />
        </Form.Item>
        <Form.Item label="directory" name="directory">
          <Radio.Group
            options={[
              { label: '文件夹模式', value: true },
              { label: '文件模式', value: false },
            ]}
            onChange={(e) => setDirection(e.target.value)}
          />
        </Form.Item>
        <Form.Item label="files" name="files">
          <UploadData multiple maxCount={100} directory={directory} />
        </Form.Item>
      </Form>

      <p>{data}</p>
    </Page>
  )
}

export default observer(Index)
