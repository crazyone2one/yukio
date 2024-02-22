import { ref, unref } from 'vue'
import type { LocaleType, Recordable } from '../types/global'
import { loadLocalePool, setHtmlPageLang } from './helper'
import { i18n } from '/@/locale'

interface LangModule {
    message: Recordable
    // dayjsLocale: Recordable;
    // dayjsLocaleName: string;
}

const setI18nLanguage = (locale: LocaleType): void => {
    if (i18n.mode === 'legacy') {
        i18n.global.locale = locale
    } else {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        ;(i18n.global.locale as any).value = locale
    }
    localStorage.setItem('locale', locale)
    setHtmlPageLang(locale)
}

const changeLocale = async (
    locale: LocaleType,
): Promise<LocaleType | undefined> => {
    const globalI18n = i18n.global
    const currentLocale = unref(globalI18n.locale)
    if (currentLocale === locale) {
        return locale
    }
    if (loadLocalePool.includes(locale)) {
        setI18nLanguage(locale)
        return locale
    }
    const langModule = (await import(`./${locale}/index.ts`))
        .default as LangModule
    if (!langModule) return

    const { message } = langModule
    globalI18n.setLocaleMessage(locale, message)
    loadLocalePool.push(locale)
    setI18nLanguage(locale)
    window.location.reload()
    return locale
}
export default function useLocale() {
    const { locale } = i18n.global
    const currentLocale = ref(locale as LocaleType)
    return {
        currentLocale,
        changeLocale,
    }
}
