import ReactDOMServer from 'react-dom/server'
import { amountFormat } from '@/utils'

export const getChartsTooltip = (props) => {
  return {
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    extraCssText: 'box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.3)',
    formatter: function (params) {
      return ReactDOMServer.renderToStaticMarkup(
        <div style={{ padding: '8px 16px' }}>
          <div style={{ marginBottom: 6 }}>{params?.[0]?.name}</div>
          {params?.map(({ color, seriesName, data = {} }, index) => {
            return (
              <div key={index} style={{ marginBottom: 5, display: 'flex', alignItems: 'center' }}>
                {seriesName && (
                  <>
                    <span
                      style={{
                        display: 'inline-block',
                        marginRight: '6px',
                        width: '7px',
                        height: '7px',
                        backgroundColor: color,
                      }}
                    ></span>
                    <span
                      style={{
                        fontSize: '14px',
                        color: '#5e6066',
                      }}
                    >
                      {seriesName}
                    </span>
                    <span>：</span>
                  </>
                )}

                <span
                  style={{
                    fontSize: '14px',
                    color: '#5e6066',
                  }}
                >
                  {amountFormat(data?.value ?? data)}
                </span>
                <span>{data?.unit}</span>
              </div>
            )
          })}
        </div>
      )
    },
    ...props,
  }
}

export const RenderTooltip = ({ params, style }) => {
  return (
    <div style={{ display: 'flex', width: 400, flexWrap: 'wrap', ...style }}>
      {params.map(({ color, data = {}, seriesName }, index) => {
        return (
          <div
            key={index}
            style={{
              marginBottom: 5,
              display: 'flex',
              alignItems: 'center',
              width: '50%',
            }}
          >
            <span
              style={{
                display: 'inline-block',
                marginRight: '6px',
                width: '12px',
                height: '12px',
                backgroundColor: color,
              }}
            ></span>
            <span
              style={{
                fontSize: '14px',
                color: '#5e6066',
              }}
            >
              {seriesName}
            </span>
            <span>：</span>
            <span
              style={{
                fontSize: '14px',
                color: '#5e6066',
              }}
            >
              {amountFormat(data?.value ?? data)}
              {data?.unit ?? ''}
            </span>
          </div>
        )
      })}
    </div>
  )
}
