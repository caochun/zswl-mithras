import { observer } from '@zswl/admin'
import Clients from '/public/assets/risk/monitoringAlertList/clients.svg'
import WarningCustomers from '/public/assets/risk/monitoringAlertList/warningCustomers.svg'
import IconRedLightwarning from '/public/assets/risk/monitoringAlertList/iconRedLightwarning.svg'
import IconYellowLightWarning from '/public/assets/risk/monitoringAlertList/iconYellowLightWarning.svg'
import PublicSentiment from '/public/assets/risk/monitoringAlertList/publicSentiment.svg'
import './index.less'

const TopSection = () => {
  return (
    <div className="haderLeft">
      <div className="top">
        <div className="wrepper">
          <div className="wrepperLeftImg">
            <Clients></Clients>
          </div>
          <div className="wrepperRight">
            <div>748</div>
            <div>监控客户数</div>
          </div>
        </div>

        <div className="wrepper wreppertow">
          <div className="wrepperLeftImg">
            <WarningCustomers></WarningCustomers>
          </div>
          <div className="wrepperRight">
            <div>748</div>
            <div>预警客户数</div>
          </div>
        </div>
      </div>

      <div className="contentCardList">
        <div className="card">
          <div className="cardLeft">
            <div className="topImg">
              {/* <img src={IconRedLightwarning} alt="111" /> */}

              <IconRedLightwarning></IconRedLightwarning>
            </div>
            <div className="wrepperRight">
              <div className="cardRightBock">748</div>
              <div className="zb">红灯预警</div>
            </div>
          </div>
          <div className="cardRight">
            <div className="cardRightBock">
              <div className="zb">748</div>
              <div>今日新增</div>
            </div>
            <div className="cardRightBock bootm">
              <div className="zb">748</div>
              <div>今日关闭</div>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="cardLeft">
            <div className="topImg">
              {/* <img src={IconRedLightwarning} alt="111" /> */}

              <IconYellowLightWarning></IconYellowLightWarning>
            </div>
            <div className="wrepperRight">
              <div className="cardRightBock">748</div>
              <div className="zb">黄灯预警</div>
            </div>
          </div>
          <div className="cardRight">
            <div className="cardRightBock">
              <div className="zb">748</div>
              <div>今日新增</div>
            </div>
            <div className="cardRightBock bootm">
              <div className="zb">748</div>
              <div>今日关闭</div>
            </div>
          </div>
        </div>

        <div className="card">
          <div className="cardLeft">
            <div className="topImg">
              {/* <img src={IconRedLightwarning} alt="111" /> */}

              <PublicSentiment></PublicSentiment>
            </div>
            <div className="wrepperRight">
              <div className="cardRightBock">748</div>
              <div className="zb">舆情</div>
            </div>
          </div>
          <div className="cardRight">
            <div className="cardRightBock">
              <div className="zb">748</div>
              <div>今日新增</div>
            </div>
            <div className="cardRightBock bootm">
              <div className="zb">748</div>
              <div>今日关闭</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default observer(TopSection)
