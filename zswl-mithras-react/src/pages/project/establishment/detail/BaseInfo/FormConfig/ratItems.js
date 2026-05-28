import { history, observer } from '@zswl/admin'
import { App, Button, Descriptions } from '@zswl/components'

const ratItem = ({ detail, isLog, type = 'establishment' }) => {
  const getDetailValue = (key) => {
    if (isLog) {
      return detail[key]?.value
    }
    return detail[key]
  }
  const scoreId = getDetailValue('ratingClientId')
  const quotaId = getDetailValue('ratingQuotaId')
  const goRat = () => {
    history.push(`/customer/customerRat/detail/${scoreId}?canEditFlags=false`)
  }
  const goBond = () => {
    history.push('/customer/debtRat/detail/' + quotaId)
  }
  return (
    <>
      <Descriptions.Item span={1} label={'评估主体评级'}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>{getDetailValue('ratingFinalScore')}</span>
          {scoreId && (
            <Button type="link" onClick={goRat}>
              评级报告
            </Button>
          )}
        </div>
      </Descriptions.Item>
      {type === 'review' && (
        <Descriptions.Item span={1} label={'债项评级参考额度（万元）'}>
          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
            <span>{getDetailValue('ratingQuota')}</span>
            {quotaId && (
              <Button type="link" onClick={goBond}>
                评级报告
              </Button>
            )}
          </div>
        </Descriptions.Item>
      )}
    </>
  )
}

export default ratItem
