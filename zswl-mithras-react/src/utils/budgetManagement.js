let refreshProvisionForecastList = null

export const registerProvisionForecastRefresh = (refresh) => {
  refreshProvisionForecastList = refresh
}

export const refreshProvisionForecast = () => {
  refreshProvisionForecastList?.()
}
