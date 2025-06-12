import {createI18n} from "vue-i18n";
import zhCN from "../locales/zh-CN.json";
import en from "../locales/en.json";

export default createI18n({
    legacy: false,
    locale: localStorage.getItem('locale'),
    messages: {
        'zh': zhCN,
        'en': en
    }
})
