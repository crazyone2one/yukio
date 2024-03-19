<script setup lang="ts">
import { NDivider } from 'naive-ui'
import { ref } from 'vue'
import PersonalMenu from './PersonalMenu.vue'
import logo from '/@/assets/logo.svg'
import { useI18n } from '/@/hooks/use-i18n'

const value = ref('')
const { t } = useI18n()
</script>
<template>
  <div class="navbar">
    <div class="left-side">
      <n-space>
        <div class="one-line-text flex max-w-[145px] items-center">
          <img :src="logo" class="mr-[4px] h-[34px] w-[32px]" />
          <div class="font-['Helvetica_Neue'] text-[16px] font-bold">Yukio</div>
        </div>
      </n-space>
    </div>
    <div class="center-side">
      <n-divider vertical class="ml-0" />
      <n-select
        v-model:value="value"
        :options="[]"
        class="w-[200px] focus-within:!bg-[var(--color-text-n8)] hover:!bg-[var(--color-text-n8)]"
      />
      <n-divider vertical class="mr-0" />
    </div>
    <ul class="right-side">
      <li>
        <n-tooltip trigger="hover">
          <template #trigger>
            <div class="message-box-trigger">
              <n-badge :value="8">
                <n-button secondary size="small">
                  <template #icon>
                    <span class="i-carbon:notification" />
                  </template>
                </n-button>
              </n-badge>
            </div>
          </template>
          {{ t('settings.navbar.alerts') }}
        </n-tooltip>
      </li>
      <li>
        <n-tooltip trigger="hover">
          <template #trigger>
            <n-button secondary size="small">
              <template #icon>
                <span class="i-carbon:task" />
              </template>
            </n-button>
          </template>
          {{ t('settings.navbar.task') }}
        </n-tooltip>
      </li>
      <li>
        <n-dropdown trigger="hover" :options="[]">
          <n-button secondary size="small">
            <template #icon>
              <span class="i-carbon:help" />
            </template>
          </n-button>
        </n-dropdown>
      </li>
      <li>
        <n-dropdown trigger="hover" :options="[]">
          <n-button secondary size="small">
            <template #icon>
              <span class="i-carbon:ibm-watson-language-translator" />
            </template>
          </n-button>
        </n-dropdown>
      </li>
      <li>
        <personal-menu />
      </li>
    </ul>
  </div>
</template>

<style scoped>
.navbar {
  display: flex;
  height: 100%;
  justify-content: space-between;
  background-color: transparent;
}
.left-side {
  display: flex;
  align-items: center;
  padding-left: 24px;
  width: 185px;
}
.center-side {
  @apply flex flex-1 items-center;
}
.right-side {
  @apply flex list-none;

  padding-right: 20px;
  :deep(.locale-select) {
    border-radius: 20px;
  }
  li {
    @apply flex items-center;

    padding-left: 10px;
  }

  .nav-btn {
    font-size: 16px;
    border-color: rgb(var(--gray-2));
    color: rgb(var(--gray-8));
    line-height: 24px;
  }
  .trigger-btn,
  .ref-btn {
    position: absolute;
    bottom: 14px;
  }
  .trigger-btn {
    margin-left: 14px;
  }
}
</style>
