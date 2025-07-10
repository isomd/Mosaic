<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  status: 'ACTIVE' | 'INACTIVE'
  isLoading?: boolean
  disabled?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isLoading: false,
  disabled: false
})

const emit = defineEmits<{
  toggle: []
}>()

const leverClass = computed(() => ({
  'mc-lever-container--on': props.status === 'ACTIVE',
  'mc-lever-container--off': props.status === 'INACTIVE',
  'mc-lever-container--loading': props.isLoading,
  'mc-lever-container--disabled': props.disabled
}))

const statusPillClass = computed(() => ({
  'status-pill--active': props.status === 'ACTIVE' && !props.isLoading,
  'status-pill--inactive': props.status === 'INACTIVE' && !props.isLoading,
  'status-pill--loading': props.isLoading
}))

const statusIcon = computed(() => {
  if (props.isLoading) return 'mdi-loading'
  return props.status === 'ACTIVE' ? 'mdi-lightning-bolt' : 'mdi-power'
})

const statusText = computed(() => {
  if (props.isLoading) return '切换中'
  return props.status === 'ACTIVE' ? 'POWERED' : 'UNPOWERED'
})

const handleClick = () => {
  if (!props.disabled && !props.isLoading) {
    emit('toggle')
  }
}
</script>

<template>
  <div class="lever-switch-wrapper">
    <div
        class="mc-lever-container"
        :class="leverClass"
        @click="handleClick"
    >
      <!-- 发光环 -->
      <div class="lever-glow-ring"></div>

      <!-- 石质底座 -->
      <div class="lever-stone-base">
        <div class="stone-top-face"></div>
        <div class="stone-front-face"></div>
        <div class="stone-side-face"></div>
      </div>

      <!-- 拉杆杆子 -->
      <div class="lever-rod">
        <div class="rod-segment rod-segment-1"></div>
        <div class="rod-segment rod-segment-2"></div>
        <div class="rod-segment rod-segment-3"></div>
      </div>

      <!-- 拉杆手柄 -->
      <div class="lever-handle-grip">
        <div class="grip-top"></div>
        <div class="grip-side"></div>
      </div>

      <!-- 红石粒子效果 -->
      <div class="redstone-particles" v-if="status === 'ACTIVE' && !isLoading">
        <div class="particle particle-1"></div>
        <div class="particle particle-2"></div>
        <div class="particle particle-3"></div>
        <div class="particle particle-4"></div>
        <div class="particle particle-5"></div>
      </div>
    </div>

    <!-- 状态提示 -->
    <div class="lever-status-indicator">
      <div class="status-pill" :class="statusPillClass">
        <div class="status-icon">
          <v-icon
              :icon="statusIcon"
              :class="{ 'rotating': isLoading }"
              size="12"
          ></v-icon>
        </div>
        <span class="status-text">
          {{ statusText }}
        </span>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
// 沙漠绿洲配色变量
$oasis-green: #4A9B8E;
$shadow-brown: #8B7355;

// 拉杆开关包装器
.lever-switch-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
  position: relative;
}

// MC拉杆容器 - 缩小版
.mc-lever-container {
  position: relative;
  width: 50px;
  height: 50px;
  cursor: pointer;
  transition: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
  transform-style: preserve-3d;
  filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.15));

  &:hover:not(.mc-lever-container--disabled) {
    transform: scale(1.1) rotateY(8deg) rotateX(-6deg);
    filter: drop-shadow(0 12px 24px rgba(0, 0, 0, 0.25));

    .lever-stone-base {
      transform: translateX(-50%) translateZ(4px);
      box-shadow:
          0 12px 32px rgba(139, 115, 85, 0.4),
          0 0 0 3px rgba(74, 155, 142, 0.3),
          inset 0 3px 6px rgba(255, 255, 255, 0.4),
          inset 0 -3px 6px rgba(0, 0, 0, 0.2);
    }

    .lever-rod {
      filter: brightness(1.2) saturate(1.2) contrast(1.05);
    }

    .lever-glow-ring {
      opacity: 1;
      transform: translateX(-50%) scale(1.2);
    }
  }

  &:active:not(.mc-lever-container--disabled) {
    transform: scale(1.05) rotateY(4deg) rotateX(-3deg);
  }

  &.mc-lever-container--disabled {
    opacity: 0.6;
    cursor: not-allowed;
    filter: grayscale(0.3);
  }
}

// 发光环效果
.lever-glow-ring {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%) scale(1);
  width: 66px;
  height: 66px;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.4s ease;
  pointer-events: none;
  z-index: -1;

  &::before {
    content: '';
    position: absolute;
    inset: -3px;
    border-radius: 50%;
    background: conic-gradient(from 0deg,
        rgba(74, 155, 142, 0.8),
        rgba(107, 182, 176, 0.6),
        rgba(45, 90, 39, 0.8),
        rgba(74, 155, 142, 0.8)
    );
    animation: rotate 3s linear infinite;
    filter: blur(1.5px);
  }

  &::after {
    content: '';
    position: absolute;
    inset: 6px;
    border-radius: 50%;
    background: radial-gradient(circle,
        rgba(244, 228, 188, 0.9) 30%,
        rgba(244, 228, 188, 0.7) 60%,
        transparent 80%
    );
  }
}

// 石质底座 - 缩小版
.lever-stone-base {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 38px;
  height: 18px;
  background: linear-gradient(145deg, #C0C0C0, #A0A0A0, #888888);
  border: 2px solid #666666;
  border-radius: 4px;
  box-shadow:
      0 6px 16px rgba(0, 0, 0, 0.4),
      inset 0 3px 6px rgba(255, 255, 255, 0.3),
      inset 0 -3px 6px rgba(0, 0, 0, 0.3);
  transition: all 0.4s ease;
  transform-style: preserve-3d;

  // 石头纹理
  &::before {
    content: '';
    position: absolute;
    inset: 2px;
    background-image:
        radial-gradient(circle at 25% 25%, rgba(0,0,0,0.2) 1px, transparent 1.5px),
        radial-gradient(circle at 75% 40%, rgba(255,255,255,0.4) 1px, transparent 1.5px),
        radial-gradient(circle at 40% 75%, rgba(0,0,0,0.15) 1px, transparent 1.5px),
        radial-gradient(circle at 15% 70%, rgba(255,255,255,0.3) 1px, transparent 1.5px),
        repeating-linear-gradient(45deg,
            transparent, transparent 2px,
            rgba(0,0,0,0.05) 2px, rgba(0,0,0,0.05) 3px);
    border-radius: 2px;
  }

  &::after {
    content: '';
    position: absolute;
    top: -1px;
    left: -1px;
    right: -1px;
    height: 6px;
    background: linear-gradient(145deg,
        rgba(255,255,255,0.7) 0%,
        rgba(255,255,255,0.4) 50%,
        rgba(255,255,255,0.1) 100%);
    border-radius: 4px 4px 0 0;
    box-shadow: 0 1px 3px rgba(255, 255, 255, 0.3);
  }
}

// 石头3D面
.stone-top-face {
  position: absolute;
  top: -4px;
  left: -1px;
  right: -1px;
  height: 6px;
  background: linear-gradient(145deg, #E0E0E0, #C8C8C8, #B0B0B0);
  border-radius: 4px 4px 0 0;
  border: 2px solid #999999;
  border-bottom: none;
  box-shadow:
      0 2px 4px rgba(0, 0, 0, 0.2),
      inset 0 1px 3px rgba(255, 255, 255, 0.4);
}

.stone-front-face {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg,
      #C0C0C0 0%,
      #A8A8A8 30%,
      #909090 70%,
      #808080 100%);
  border-radius: 4px;
  box-shadow:
      inset 2px 2px 4px rgba(255, 255, 255, 0.2),
      inset -1px -1px 4px rgba(0, 0, 0, 0.2);
}

.stone-side-face {
  position: absolute;
  top: 3px;
  right: -4px;
  width: 4px;
  height: 12px;
  background: linear-gradient(180deg,
      #999999 0%,
      #808080 50%,
      #606060 100%);
  border-radius: 0 4px 4px 0;
  box-shadow:
      inset 1px 0 2px rgba(255, 255, 255, 0.1),
      2px 0 4px rgba(0, 0, 0, 0.3);
}

// 拉杆杆子
.lever-rod {
  position: absolute;
  width: 7px;
  height: 28px;
  left: 50%;
  bottom: 16px;
  transform-origin: bottom center;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.3));

  transform: translateX(-50%) rotate(-35deg);
}

// 杆子分段
.rod-segment {
  position: absolute;
  width: 100%;
  height: 8px;
  background: linear-gradient(145deg, #B8860B, #A0522D, #8B4513);
  border: 1px solid #654321;
  border-radius: 1px;
  margin-bottom: 1px;
  box-shadow:
      inset 1px 1px 2px rgba(184, 134, 11, 0.6),
      inset -1px -1px 2px rgba(101, 67, 33, 0.8),
      0 1px 2px rgba(0, 0, 0, 0.2);

  &:nth-child(1) {
    top: 0;
    background: linear-gradient(145deg, #DAA520, #B8860B, #A0522D);
  }
  &:nth-child(2) {
    top: 9px;
    background: linear-gradient(145deg, #B8860B, #A0522D, #8B4513);
  }
  &:nth-child(3) {
    top: 18px;
    background: linear-gradient(145deg, #A0522D, #8B4513, #654321);
  }

  &::after {
    content: '';
    position: absolute;
    inset: 1px;
    background:
        repeating-linear-gradient(0deg,
            transparent, transparent 1px,
            rgba(139, 69, 19, 0.4) 1px, rgba(139, 69, 19, 0.4) 2px),
        repeating-linear-gradient(90deg,
            transparent, transparent 1px,
            rgba(184, 134, 11, 0.3) 1px, rgba(184, 134, 11, 0.3) 2px);
    border-radius: 1px;
  }
}

// 拉杆手柄
.lever-handle-grip {
  position: absolute;
  width: 15px;
  height: 10px;
  left: 50%;
  top: 2px;
  transform-origin: center bottom;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  filter: drop-shadow(1px 1px 3px rgba(0, 0, 0, 0.4));

  transform: translateX(-50%) rotate(-35deg) translateY(-22px) translateX(-12px);
}

.grip-top {
  width: 100%;
  height: 8px;
  background: linear-gradient(145deg, #DEB887, #CD853F, #A0522D);
  border: 1px solid #8B4513;
  border-radius: 3px;
  box-shadow:
      0 3px 6px rgba(0, 0, 0, 0.4),
      inset 0 1px 0 rgba(222, 184, 135, 0.8),
      inset 0 -1px 0 rgba(139, 69, 19, 0.8);

  &::before {
    content: '';
    position: absolute;
    inset: 1px;
    background:
        repeating-linear-gradient(90deg,
            transparent, transparent 1px,
            rgba(139, 69, 19, 0.4) 1px, rgba(139, 69, 19, 0.4) 2px);
    border-radius: 2px;
  }

  &::after {
    content: '';
    position: absolute;
    top: 1px;
    left: 1px;
    right: 1px;
    height: 2px;
    background: linear-gradient(90deg,
        transparent 0%,
        rgba(255, 255, 255, 0.6) 30%,
        rgba(255, 255, 255, 0.8) 50%,
        rgba(255, 255, 255, 0.6) 70%,
        transparent 100%);
    border-radius: 1px;
  }
}

.grip-side {
  position: absolute;
  top: 6px;
  right: -3px;
  width: 3px;
  height: 6px;
  background: linear-gradient(180deg, #A0522D, #8B4513, #654321);
  border-radius: 0 2px 2px 0;
  box-shadow:
      inset 1px 0 1px rgba(160, 82, 45, 0.3),
      2px 0 3px rgba(0, 0, 0, 0.4);
}

// ON状态
.mc-lever-container--on {
  .lever-glow-ring {
    &::before {
      background: conic-gradient(from 0deg,
          rgba(74, 155, 142, 1),
          rgba(107, 182, 176, 0.8),
          rgba(45, 90, 39, 1),
          rgba(74, 155, 142, 1)
      );
    }
  }

  .lever-rod {
    transform: translateX(-50%) rotate(35deg);
    filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.3)) brightness(1.1) saturate(1.1);
  }

  .lever-handle-grip {
    transform: translateX(-50%) rotate(35deg) translateY(-22px) translateX(12px);
  }

  .lever-stone-base {
    background: linear-gradient(145deg, #98E598, #7FDD7F, #4A9B8E);
    border-color: #2D5A27;
    box-shadow:
        0 6px 16px rgba(74, 155, 142, 0.5),
        inset 0 3px 6px rgba(255, 255, 255, 0.4),
        inset 0 -3px 6px rgba(45, 90, 39, 0.4),
        0 0 24px rgba(74, 155, 142, 0.4);
  }

  .stone-top-face {
    background: linear-gradient(145deg, #B8FFB8, #9AFF9A, #7FDD7F);
    border-color: #4A9B8E;
    box-shadow:
        0 2px 4px rgba(74, 155, 142, 0.3),
        0 0 12px rgba(74, 155, 142, 0.4),
        inset 0 1px 3px rgba(255, 255, 255, 0.5);
  }

  .stone-front-face {
    background: linear-gradient(180deg,
        #98E598 0%,
        #7FDD7F 30%,
        #4A9B8E 70%,
        #2D5A27 100%);
  }

  .stone-side-face {
    background: linear-gradient(180deg, #7FDD7F, #4A9B8E, #2D5A27);
  }
}

// 加载状态
.mc-lever-container--loading {
  .lever-rod {
    animation: leverOscillate 1s ease-in-out infinite;
  }

  .lever-handle-grip {
    animation: handleOscillate 1s ease-in-out infinite;
  }

  .lever-stone-base {
    animation: baseGlow 2s ease-in-out infinite;
  }

  .lever-glow-ring {
    opacity: 0.8;
    animation: glowPulse 1.5s ease-in-out infinite;
  }
}

// 红石粒子效果
.redstone-particles {
  position: absolute;
  top: -12px;
  left: -12px;
  right: -12px;
  bottom: -12px;
  pointer-events: none;
  overflow: visible;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: radial-gradient(circle, #FF4444, #CC0000);
  border-radius: 50%;
  box-shadow:
      0 0 8px #FF4444,
      0 0 16px rgba(255, 68, 68, 0.5),
      0 0 24px rgba(255, 68, 68, 0.2);
  animation: particleFloat 3s ease-in-out infinite;

  &::before {
    content: '';
    position: absolute;
    inset: -2px;
    background: radial-gradient(circle, rgba(255, 68, 68, 0.4), transparent);
    border-radius: 50%;
    animation: particleGlow 1.5s ease-in-out infinite alternate;
  }

  &.particle-1 {
    top: 10%;
    left: 10%;
    animation-delay: 0s;
  }

  &.particle-2 {
    top: 20%;
    right: 15%;
    animation-delay: 0.6s;
  }

  &.particle-3 {
    bottom: 30%;
    left: 70%;
    animation-delay: 1.2s;
  }

  &.particle-4 {
    top: 50%;
    left: 85%;
    animation-delay: 0.3s;
  }

  &.particle-5 {
    bottom: 15%;
    right: 5%;
    animation-delay: 0.9s;
  }
}

// 状态指示器 - 缩小版
.lever-status-indicator {
  position: relative;
}

.status-pill {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  background:
      linear-gradient(135deg,
          rgba(255, 255, 255, 0.98) 0%,
          rgba(244, 228, 188, 0.95) 40%,
          rgba(230, 211, 163, 0.9) 100%);
  border: 2px solid rgba(230, 211, 163, 0.8);
  box-shadow:
      0 6px 20px rgba(139, 115, 85, 0.2),
      inset 0 2px 0 rgba(255, 255, 255, 0.9),
      inset 0 -2px 0 rgba(230, 211, 163, 0.5);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(15px);
  position: relative;
  overflow: hidden;
  min-width: 110px;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg,
        transparent,
        rgba(255, 255, 255, 0.4) 10%,
        rgba(255, 255, 255, 0.8) 50%,
        rgba(255, 255, 255, 0.4) 90%,
        transparent);
    transition: left 0.8s ease;
  }

  &:hover {
    transform: translateY(-2px) scale(1.05);
    box-shadow:
        0 8px 25px rgba(139, 115, 85, 0.3),
        inset 0 2px 0 rgba(255, 255, 255, 1),
        inset 0 -2px 0 rgba(230, 211, 163, 0.6);

    &::before {
      left: 100%;
    }
  }
}

// 状态颜色区分
.status-pill--active {
  background: linear-gradient(135deg, rgba(74, 155, 142, 0.15), rgba(107, 182, 176, 0.1));
  border-color: rgba(74, 155, 142, 0.6);
  box-shadow:
      0 6px 20px rgba(74, 155, 142, 0.25),
      inset 0 2px 0 rgba(255, 255, 255, 0.8),
      0 0 16px rgba(74, 155, 142, 0.1);

  .status-icon {
    color: #2E7D70;
    background: linear-gradient(135deg, rgba(74, 155, 142, 0.2), rgba(74, 155, 142, 0.1));
    box-shadow: 0 0 10px rgba(74, 155, 142, 0.3);
  }

  .status-text {
    color: #2E7D70;
    font-weight: 600;
    text-shadow: 0 1px 2px rgba(74, 155, 142, 0.2);
  }
}

.status-pill--inactive {
  background: linear-gradient(135deg, rgba(212, 165, 116, 0.12), rgba(212, 165, 116, 0.08));
  border-color: rgba(212, 165, 116, 0.5);
  box-shadow:
      0 6px 20px rgba(212, 165, 116, 0.2),
      inset 0 2px 0 rgba(255, 255, 255, 0.8);

  .status-icon {
    color: #B8956A;
    background: rgba(212, 165, 116, 0.15);
  }

  .status-text {
    color: #B8956A;
    font-weight: 500;
    text-shadow: 0 1px 2px rgba(212, 165, 116, 0.2);
  }
}

.status-pill--loading {
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.15), rgba(255, 193, 7, 0.1));
  border-color: rgba(255, 193, 7, 0.6);
  box-shadow:
      0 6px 20px rgba(255, 193, 7, 0.25),
      inset 0 2px 0 rgba(255, 255, 255, 0.8);

  .status-icon {
    color: #E6A500;
    background: rgba(255, 193, 7, 0.2);
  }

  .status-text {
    color: #E6A500;
    font-weight: 500;
    animation: textPulse 1s ease-in-out infinite;
  }
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.status-text {
  font-size: 0.7rem;
  font-weight: 500;
  letter-spacing: 0.4px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

// 动画
@keyframes leverOscillate {
  0%, 100% { transform: translateX(-50%) rotate(-35deg); }
  25% { transform: translateX(-50%) rotate(-40deg); }
  50% { transform: translateX(-50%) rotate(35deg); }
  75% { transform: translateX(-50%) rotate(40deg); }
}

@keyframes handleOscillate {
  0%, 100% { transform: translateX(-50%) rotate(-35deg) translateY(-22px) translateX(-12px); }
  25% { transform: translateX(-50%) rotate(-40deg) translateY(-22px) translateX(-13px); }
  50% { transform: translateX(-50%) rotate(35deg) translateY(-22px) translateX(12px); }
  75% { transform: translateX(-50%) rotate(40deg) translateY(-22px) translateX(13px); }
}

@keyframes baseGlow {
  0%, 100% {
    box-shadow:
        0 6px 16px rgba(0, 0, 0, 0.4),
        inset 0 3px 6px rgba(255, 255, 255, 0.3),
        inset 0 -3px 6px rgba(0, 0, 0, 0.3);
  }
  50% {
    box-shadow:
        0 6px 16px rgba(255, 193, 7, 0.5),
        inset 0 3px 6px rgba(255, 255, 255, 0.4),
        inset 0 -3px 6px rgba(255, 193, 7, 0.3),
        0 0 24px rgba(255, 193, 7, 0.4);
  }
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.6;
    transform: translateX(-50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translateX(-50%) scale(1.1);
  }
}

@keyframes particleFloat {
  0%, 100% {
    opacity: 0;
    transform: translateY(0) scale(0.3) rotate(0deg);
  }
  25% {
    opacity: 0.8;
    transform: translateY(-6px) scale(0.8) rotate(90deg);
  }
  50% {
    opacity: 1;
    transform: translateY(-12px) scale(1.2) rotate(180deg);
  }
  75% {
    opacity: 0.6;
    transform: translateY(-8px) scale(0.9) rotate(270deg);
  }
}

@keyframes particleGlow {
  from { transform: scale(1); opacity: 0.5; }
  to { transform: scale(1.8); opacity: 0.1; }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes textPulse {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
}

.rotating {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 响应式调整
@media (max-width: 768px) {
  .mc-lever-container {
    width: 42px;
    height: 42px;
  }

  .lever-stone-base {
    width: 32px;
    height: 15px;
  }

  .lever-rod {
    width: 6px;
    height: 24px;
    bottom: 14px;
  }

  .status-pill {
    padding: 0.4rem 0.8rem;
    min-width: 95px;
  }

  .status-text {
    font-size: 0.65rem;
  }
}
</style>