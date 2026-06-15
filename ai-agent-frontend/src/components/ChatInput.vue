<template>
  <form class="chat-input" @submit.prevent="submit">
    <textarea
      :value="modelValue"
      :placeholder="placeholder"
      :disabled="disabled"
      rows="3"
      @input="onInput"
      @keydown.enter.exact.prevent="submit"
      @keydown.enter.shift.exact.stop
    />

    <div class="input-actions">
      <button v-if="isStreaming" class="secondary" type="button" @click="$emit('stop')">
        停止生成
      </button>
      <button class="primary" type="submit" :disabled="disabled || !modelValue.trim()">
        发送
      </button>
    </div>
  </form>
</template>

<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    modelValue: string;
    placeholder?: string;
    disabled?: boolean;
    isStreaming?: boolean;
  }>(),
  {
    placeholder: '输入消息，回车发送，Shift+Enter 换行',
    disabled: false,
    isStreaming: false,
  },
);

const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void;
  (event: 'send'): void;
  (event: 'stop'): void;
}>();

const onInput = (event: Event) => {
  emit('update:modelValue', (event.target as HTMLTextAreaElement).value);
};

const submit = () => {
  if (!props.disabled && props.modelValue.trim()) {
    emit('send');
  }
};
</script>

<style scoped>
.chat-input {
  display: grid;
  gap: 0.85rem;
  padding: 1rem;
  border-radius: 1.5rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(255, 255, 255, 0.76);
  box-shadow: 0 16px 50px rgba(15, 23, 42, 0.06);
  backdrop-filter: blur(18px);
}

textarea {
  width: 100%;
  resize: vertical;
  min-height: 6rem;
  padding: 1rem 1.05rem;
  border-radius: 1rem;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: rgba(248, 250, 252, 0.96);
  color: #0f172a;
  outline: none;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.5);
}

textarea:focus {
  border-color: rgba(15, 23, 42, 0.22);
}

textarea:disabled {
  opacity: 0.7;
}

.input-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

button {
  border: 0;
  border-radius: 999px;
  padding: 0.8rem 1.25rem;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    opacity 0.15s ease;
}

button:hover:not(:disabled) {
  transform: translateY(-1px);
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.55;
}

.primary {
  color: #fff;
  background: #0f172a;
}

.secondary {
  color: #0f172a;
  background: rgba(15, 23, 42, 0.06);
}
</style>
