import { Button, Modal, Tabs, Form, Select } from '@zswl/components'
import { Alert, DatePicker, Image, Input, Radio, Upload, message } from 'antd'
import asyncPool from './asyncPool'
import ocrInvoiceApi from '@/api/ocr/ocrInvoiceApi'
import { getQuery, history, observer } from '@zswl/admin'
import { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react'
import styles from './index.less'
import { InboxOutlined } from '@ant-design/icons'
import vehicleCertificateApi from '@/api/ocr/vehicleCertificateApi'
import moment from 'moment'
import { FormAmount } from '@/components/Form'

const { Dragger } = Upload

const ReVerify = () => {
  const { type } = getQuery()
  return (
    <>
      <Alert
        message="支持增值税专用发票、增值税普通发票（折叠票）、增值税普通发票（卷票)、增值税电子普通发票（含收费公路通行费增值税电子普通发票）、机动车销售统一发票、二手车销售统一发票在线查验。"
        description={
          <div style={{ color: '#b8c4d4' }}>
            <div>· 当日开具发票次日可查验</div>
            <div>· 每份发票每天最多可查验5次</div>
            <div>· 可查验最近5年增值税发票管理新系统开具的发票</div>
          </div>
        }
        type="info"
        showIcon
        style={{ marginBottom: 20 }}
      />
      <Form.Item
        label="发票类型"
        name="invoiceType"
        required
        rules={[{ required: true, message: '请选择发票类型' }]}
      >
        <Select options={'leaseVatInvoiceType'} />
      </Form.Item>
      <Form.Item
        label="发票代码"
        name="invoiceCode"
        required
        rules={[{ required: true, message: '请输入发票代码' }]}
        tooltip="全电代码非必填"
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="发票号码"
        required
        name="invoiceNo"
        rules={[{ required: true, message: '请输入发票号码' }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label="开票日期"
        rules={[{ required: true, message: '请输入开票日期' }]}
        name="invoiceDate"
        transform={(val) => ({
          invoiceDate: val && moment(val).format('yyyy-MM-DD'),
        })}
      >
        <DatePicker />
      </Form.Item>
      <Form.Item
        label="发票金额"
        required
        name="invoiceSum"
        rules={[{ required: true, message: '请输入发票金额' }]}
        tooltip="增值税专用发票、机动车销售统一发票、增值税电子专用发票填写发票不含税金额：二手车销售统一发票填写发票车价合计：电子发票（增值税专用发票）、电子发票（增值税普通发票）填写含税金额。其他可为空。"
      >
        <FormAmount />
      </Form.Item>
      <Form.Item
        label="校验码"
        required
        name="verifyCode"
        placeholder="请输入校验码后6位或5位，增值税发票专用发票和购车发票可不填"
      >
        <Input />
      </Form.Item>
    </>
  )
}
const DraggerUpload = forwardRef(({ type, uploadType, leaseholdId }, ref) => {
  const title = type === 'invoice' ? '发票' : '车证'
  const [fileList, setFileList] = useState([])
  const defaultAccept = '.png,.jpeg,.jpg,.gif,.bmp,.tif'
  const accept = uploadType === 'reRowUpload' ? defaultAccept : `${defaultAccept},.doc,.docx,.pdf,`
  const cleanRepeatFile = (list) => {
    const newFileList = fileList
    const noRepeatList = newFileList.filter((v) => !list.includes(v.name.split('.')[0]))

    setFileList([...noRepeatList])
  }

  const validFile = async () => {
    return new Promise(async (resolve) => {
      const fileNameList = fileList.map((v) => v.name)
      const res = await ocrInvoiceApi.postVatInvoiceFileNameComparison({
        fileNameList,
        leaseholdId,
        leaseOCRType: type === 'invoice' ? 'LEASE_VAT_INVOICE' : 'LEASE_VEHICLE_REGISTRATION',
        operateType: uploadType === 'reUpload' ? 'REPLACE' : undefined,
      })
      if (res?.length) {
        Modal.confirm({
          title: '提示',
          content: `已存在${res.join(',')}，是否继续上传？`,
          closable: true,
          okText: '继续上传',
          cancelText: '跳过',
          onCancel: async () => {
            //去除重复文件
            cleanRepeatFile(res)
            resolve()
          },
          onOk: () => {
            resolve()
          },
        })
      } else {
        resolve()
      }
    })
  }
  const [previewData, setPreviewData] = useState({})
  const uploadProps = {
    name: 'file',
    listType: 'picture-card',
    fileList,
    multiple: true,
    accept,
    max: uploadType === 'reRowUpload' ? 1 : undefined,
    onChange(info) {
      const newFileList = info.fileList
      setFileList(newFileList)
    },
    beforeUpload: (file) => {
      const isPNG = file.type === 'image/png'
      const isImage = file.type.indexOf('image') > -1

      return false
    },
    onPreview: (file) => {
      const blob = new Blob([file.originFileObj])
      const src = URL.createObjectURL(blob)
      const newPreview = {
        visible: true,
        src,
        title: file.name,
      }
      setPreviewData(newPreview)
    },
  }

  const uploadProgress = (evt, i) => {
    const newFileList = [...fileList]
    if (evt.lengthComputable) {
      var percent = Math.round((evt.loaded * 100) / evt.total)
      newFileList[i].percent = percent
      if (percent === 100) {
        newFileList[i].status = 'success'
      }
    } else {
      newFileList[i].status = 'error'
    }
    setFileList(newFileList)
  }
  const submit = async (uploadFunc) => {
    if (fileList.length === 0) {
      message.error('请选择文件')
      return Promise.reject('请选择文件')
    }
    const files = fileList.map((item) => item.originFileObj)
    //   .map(async (file, i) => await uploadFunc(file))
    // setFileList(fileList.map((item) => ({ ...item, status: 'uploading' })))
    // await Promise.all(files)

    const maxConcurrentRequests = 5
    const requestManager = asyncPool(maxConcurrentRequests)

    for (let i = 0; i < files.length; i++) {
      requestManager.enqueue(async () => await uploadFunc(files[i]))
    }
    return await requestManager.queueEmptyPromise
      .then((succeed) => {
        setFileList([])
        return { fileList, succeed }
      })
      .catch((error) => {
        console.error('队列执行出错:', error)
      })
  }
  useImperativeHandle(ref, () => ({
    submit,
    validFile,
    fileList,
  }))
  return (
    <>
      <Dragger {...uploadProps}>
        <p className="ant-upload-drag-icon">
          <InboxOutlined />
        </p>
        <p className="ant-upload-text">{title}</p>
        <p className="ant-upload-hint">点击/将文件拖拽到这里上传</p>
        <p className="ant-upload-hint">支持{accept}等格式，图片大小不超过10M</p>
      </Dragger>
      <Image
        width={200}
        style={{ display: 'none' }}
        preview={{
          visible: previewData.visible,
          src: previewData.src,
          onVisibleChange: (value) => {
            setPreviewData({ visible: value })
          },
        }}
      />
    </>
  )
})
const FileUploadModal = ({ type, store, tableStore, ...rest }) => {
  const isPage = rest.closable === false
  const query = isPage ? getQuery() : store?.getInitialValues()
  const { uploadType = 'upload', id: leaseholdId, rowId, fileId: changeRecordId } = query ?? {}

  const isVerify = uploadType === 'reVerify'

  const title = type === 'invoice' ? '发票' : '车证'
  const [activeTab, setActiveTab] = useState('ocr')
  const getUploadFunc = () => {
    const carFuncMap = {
      upload: (files, onUploadProgress) =>
        vehicleCertificateApi.postVehicleUpload(
          { files, vehicleId: rowId, leaseholdId },
          { onUploadProgress }
        ),

      reUpload: (files, onUploadProgress) =>
        vehicleCertificateApi.postVehicleUpload({ files, leaseholdId }, { onUploadProgress }),
      reRowUpload: (files, onUploadProgress) =>
        vehicleCertificateApi.postVehicleUpload(
          {
            files,
            vehicleId: rowId,
            leaseholdId,
            operateType: 'RE_UPLOAD',
            changeRecordId,
          },
          { onUploadProgress }
        ),
    }

    const reUploadFunc = (files, onUploadProgress) =>
      ocrInvoiceApi.postVatInvoiceAnewUpload(
        { files, vatInvoiceId: rowId, leaseholdId, invoiceType },
        { onUploadProgress }
      )
    const invoiceFuncMap = {
      upload: (files, onUploadProgress) =>
        ocrInvoiceApi.postVatInvoiceUpload(
          { files, vatInvoiceId: rowId, leaseholdId, invoiceType },
          { onUploadProgress }
        ),
      reVerify: reUploadFunc,
      reUpload: (files, onUploadProgress) =>
        ocrInvoiceApi.postVatInvoiceUpload(
          { files, leaseholdId, invoiceType },
          { onUploadProgress }
        ),
      reRowUpload: reUploadFunc,
    }
    const funcMap = type === 'invoice' ? invoiceFuncMap : carFuncMap
    return funcMap[uploadType]
  }

  const handleSubmit = async () => {
    const uploadFunc = getUploadFunc()
    const deleteFunc =
      type === 'invoice'
        ? ocrInvoiceApi.postVatInvoiceDelete
        : vehicleCertificateApi.postVehicleDelete

    // 重新上传进来要先清空数据
    if (uploadType === 'reUpload') {
      await deleteFunc({
        leaseItemInfoId: leaseholdId,
        leaseholdId,
        operateType: 'REPLACE',
      })
    }
    const { succeed, fileList } = await uploadRef.current.submit(uploadFunc)
    if (succeed) {
      message.success(
        `共选择${fileList.length}个文件， 识别完成${succeed}条, 失败${fileList.length - succeed}条 `
      )
      afterClose()
    }
  }
  const afterClose = () => {
    if (isPage) {
      history.push(`/ocr/list?id=${leaseholdId}`)
    } else {
      store?.close()
      tableStore?.search()
    }
  }
  const handleOk = async () => {
    if (isVerify && activeTab === 'manual') {
      const values = await store.submit()
      const res = await ocrInvoiceApi.postVatInvoiceRetest({ ...values, invoiceId: rowId })
      message.success('重新验真成功')
      afterClose()
      return
    } else {
      if (uploadRef.current.fileList.length === 0) {
        message.error('请选择文件')
        return Promise.reject()
      }
      if (uploadRef.current.fileList.length > 50) {
        message.error('一次最多上传50个文件')
        return Promise.reject()
      }
      await uploadRef.current.validFile()
      await handleSubmit()
    }
  }
  const onCancel = () => {
    isPage ? history.push(`/ocr/list?id=${leaseholdId}`) : store.close?.()
  }
  const uploadRef = useRef()
  const [invoiceType, setInvoiceType] = useState(undefined)
  const OCRUpload = () => {
    return (
      <div>
        {type === 'invoice' && (
          <Radio.Group
            onChange={(e) => setInvoiceType(e.target.value)}
            value={invoiceType}
            style={{ marginBottom: 12 }}
          >
            <Radio value={undefined}>增值税发票</Radio>
            <Radio value={'MOTOR_VEHICLE_SALE_INVOICE'}>机动车购车发票</Radio>
          </Radio.Group>
        )}
        <DraggerUpload
          type={type}
          uploadType={uploadType}
          ref={uploadRef}
          leaseholdId={leaseholdId}
        />
      </div>
    )
  }
  const items = [
    {
      label: 'OCR识别',
      key: 'ocr',
      children: OCRUpload(),
    },
    {
      label: '手工识别',
      key: 'manual',
      children: <ReVerify />,
    },
  ]
  return (
    <Modal
      centered
      destroyOnClose
      maskClosable={false}
      store={store}
      title={`OCR 识别-${title}上传`}
      width={750}
      wrapClassName={isPage && styles.modalWrap}
      footer={[
        <Button onClick={onCancel}>取消</Button>,
        <Button type="primary" onClick={handleOk}>
          确定
        </Button>,
      ]}
      {...rest}
    >
      <Form>
        <div className={styles.uploadModal}>
          {isVerify ? (
            <Tabs items={items} activeKey={activeTab} onChange={setActiveTab} />
          ) : (
            OCRUpload()
          )}
        </div>
      </Form>
    </Modal>
  )
}
export default observer(FileUploadModal)
