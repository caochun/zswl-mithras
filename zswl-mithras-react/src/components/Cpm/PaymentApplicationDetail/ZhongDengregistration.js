import { Button, Modal } from '@zswl/components'
import { observer } from '@zswl/admin'
import Api from '@/api/cpm/payment/paymentApplicationDetail'
import { getUserInfo, isYunYingGuanLi } from '@/utils'
import { InfoCircleFilled, InfoCircleOutlined } from '@ant-design/icons'

const ZhongDengRegistration = ({ detail = {}, userNames }) => {
  const { bizTypeCode, contractId, id: paymentId, clientName, contractCode } = detail

  // 判断是否为租赁类型(直租、回租)
  const isLeaseType = ['ZZ', 'ZL'].includes(bizTypeCode)
  const isOperator = isYunYingGuanLi()
  const { id: userId } = getUserInfo()
  // 处理中登网登记
  const handleRegister = async () => {
    const baseUrl = __ENV__ === 'prod' ? 'http://10.158.33.114' : 'http://10.158.33.114:8001'
    const newUrl = `${baseUrl}/sso-redirect?userId=${userId}&toPage=fxMovables-smart-registration&userNames=${userNames}&contractNo=${contractCode}`
    if (!isOperator) {
      // 非发起人打开有数登记列表
      window.open(newUrl)
      return
    }
    const { code, message } = await Api.postPaymentAutoRegister({ paymentId })
    if (code === '000001') {
      // 已登记
      window.open(newUrl)
      return
    }
    const isSuccess = code === '000000'
    if (isSuccess) {
      Modal.confirm({
        title: '提示',
        icon: null,
        content: (
          <div>
            <InfoCircleOutlined style={{ color: 'red' }} /> 中登网登记成功！
          </div>
        ),
        okText: '查看详情',
        cancelText: '关闭',
        onOk: () => {
          window.open(newUrl)
        },
      })
      return
    } else {
      Modal.confirm({
        title: '提示',
        content: (
          <div>
            <div style={{ color: 'red' }}>
              <InfoCircleOutlined /> 中登网登记失败！请查看失败原因并解决后再重试
            </div>
            <div style={{ marginTop: 10 }}> 失败原因:</div>
            <div>
              {code}:{message}
            </div>
          </div>
        ),
        okText: '查看详情',
        onOk: () => {
          window.open(newUrl)
        },
      })
    }
  }
  if (!isLeaseType) return null

  return (
    <Button type="link" onClick={handleRegister}>
      中登网登记
    </Button>
  )
}

export default observer(ZhongDengRegistration)
