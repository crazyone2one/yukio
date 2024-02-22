import type { LocaleType } from '#/global'
export const setHtmlPageLang = (locale: LocaleType): void => {
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

export const setLoadLocalePool = (cb: (lp: LocaleType[]) => void): void => {
    cb(loadLocalePool)
}
