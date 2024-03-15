export type LocaleType = 'zh-CN' | 'en-US'
export function setHtmlPageLang(locale: LocaleType) {
  document.querySelector('html')?.setAttribute('lang', locale)
  let fontFamily = ''
  if (locale === 'en-US') {
    fontFamily = 'Helvetica Neue, Arial'
  } else {
    fontFamily = 'PingFang SC, Microsoft YaHei'
  }
  document.body.style.fontFamily = fontFamily
}

export const loadLocalePool: LocaleType[] = []

export function setLoadLocalePool(cb: (lp: LocaleType[]) => void) {
  cb(loadLocalePool)
}
