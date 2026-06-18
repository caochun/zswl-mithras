import IconFont from '@/components/Icon'
import { App } from '@zswl/components'
import { Tooltip } from 'antd'
import { useRef, useMemo, useEffect } from 'react'
import { RiskSourceCardCalcModal as CalcModal } from '@/components/Risk/SourceCardEntries'
import { observer } from '@zswl/admin'
import { userIsProjSponsor } from '@/utils'
import Store from './store'
import defaultStyles from './index.less'

const levelColor = ['#06EAB2', '#2D66FF', '#2EC5FF', '#FFCA69', '#FF5962']

const Index = ({ styles = defaultStyles, forceUpdateId, baseStore }) => {
  const $CalcModalRef = useRef(null)

  const { optionsType } = App.getData()
  const { classifyData, clientInfo, clientId, page } = baseStore

  const pageData = page.getData()

  const store = useMemo(() => {
    return new Store({ $CalcModalRef, clientId, baseStore })
  }, [$CalcModalRef, clientId, baseStore])

  const { cardId, cardData, modalStatus, tryData } = store

  useEffect(() => {
    clientId && store.getCardData()
  }, [clientId, forceUpdateId])

  const riskControlIndustryClassify = useMemo(() => {
    return (
      App.matchOption('riskControlIndustryClassify', clientInfo?.riskControlIndustryClassify)
        .label || '-'
    )
  }, [clientInfo?.riskControlIndustryClassify])

  const initClassifyResult = useMemo(() => {
    return App.matchOption('assetClassifyResultEnum', classifyData?.initClassifyResult).label || '-'
  }, [classifyData?.initClassifyResult])

  const getStatusColor = useMemo(() => {
    const index = optionsType.assetClassifyResultEnum.findIndex(
      (item) => item.value === classifyData?.initClassifyResult
    )
    return levelColor[index]
  }, [classifyData?.initClassifyResult])

  return (
    <div className={styles.card}>
      <div className={styles.card_row}>
        <div className={styles.card_row_t}>
          <span className={styles.card_row_t_count}>
            {cardData?.totalPoints ? (
              <span
                onClick={() => store.caclRiskvalue({ clientId, clientInfo, modalStatus: 'view' })}
              >
                {cardData?.totalPoints}
              </span>
            ) : (
              '-'
            )}
          </span>
          {userIsProjSponsor(pageData.projSponsorUserId) && (
            <a
              className={styles.card_row_t_a}
              onClick={() => store.caclRiskvalue({ clientId, clientInfo, modalStatus: 'edit' })}
            >
              计算
            </a>
          )}
        </div>
        <div className={styles.card_row_b}>
          <IconFont type="icon-pingfenkadefen" />
          <span className={styles.card_row_b_text}>评分卡得分</span>
        </div>
      </div>
      <div className={styles.card_row}>
        <div className={styles.card_row_t}>
          <span className={styles.card_row_t_text}>
            <span
              className={styles.card_row_t_badge}
              style={{ backgroundColor: getStatusColor }}
            ></span>
            {initClassifyResult}
          </span>
        </div>
        <div className={styles.card_row_b}>
          <IconFont type="icon-a-icon_projectmanage" />
          <span className={styles.card_row_b_text}>资产五级分类</span>
        </div>
      </div>
      <div className={styles.card_row}>
        <div className={styles.card_row_t}>
          <span className={styles.card_row_t_text}>
            <Tooltip title={riskControlIndustryClassify} placement="left">
              {riskControlIndustryClassify}
            </Tooltip>
          </span>
        </div>
        <div className={styles.card_row_b}>
          <IconFont type="icon-fengxianguanli" />
          <span className={styles.card_row_b_text}>风控行业分类</span>
        </div>
      </div>
      <CalcModal
        modalStore={store.$cardModal}
        id={cardId}
        cardData={cardData}
        tryData={tryData}
        ref={$CalcModalRef}
        modalStatus={modalStatus}
      ></CalcModal>
    </div>
  )
}

export default observer(Index)
