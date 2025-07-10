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

      <!-- 阴影基座 -->
      <div class="lever-shadow-base"></div>

      <!-- 石质底座 -->
      <div class="lever-stone-base">
        <div class="stone-texture"></div>
        <div class="stone-top-face"></div>
        <div class="stone-highlight"></div>
        <div class="stone-depth-lines"></div>

        <!-- 杆子插槽 -->
        <div class="rod-socket"></div>
      </div>

      <!-- 拉杆杆子 -->
      <div class="lever-rod">
        <div class="rod-main"></div>
        <div class="rod-base-connection"></div>
      </div>

      <!-- 红石粒子效果 -->
      <div class="redstone-particles" v-if="status === 'ACTIVE' && !isLoading">
        <div class="particle particle-1"></div>
        <div class="particle particle-2"></div>
        <div class="particle particle-3"></div>
        <div class="particle particle-4"></div>
        <div class="particle particle-5"></div>
        <div class="particle particle-6"></div>
      </div>

      <!-- 电路纹路 -->
      <div class="redstone-circuit" v-if="status === 'ACTIVE'">
        <div class="circuit-line circuit-line-1"></div>
        <div class="circuit-line circuit-line-2"></div>
        <div class="circuit-line circuit-line-3"></div>
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
// MC配色变量
$redstone-red: #DC143C;
$redstone-glow: #FF6B6B;
$stone-light: #D3D3D3;
$stone-base: #A9A9A9;
$stone-dark: #696969;
$wood-light: #DEB887;
$wood-base: #CD853F;
$wood-dark: #8B4513;
$emerald-green: #50C878;
$powered-green: #00FF7F;

// 拉杆开关包装器
.lever-switch-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  position: relative;
  perspective: 1000px;
}

// MC拉杆容器
.mc-lever-container {
  position: relative;
  width: 70px;
  height: 70px;
  cursor: pointer;
  transition: all 0.6s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform-style: preserve-3d;
  filter: drop-shadow(0 12px 24px rgba(0, 0, 0, 0.2));

  &:hover:not(.mc-lever-container--disabled) {
    transform: scale(1.08) rotateY(5deg) rotateX(-3deg);
    filter: drop-shadow(0 16px 32px rgba(0, 0, 0, 0.3));

    .lever-stone-base {
      transform: translateX(-50%) translateZ(6px);
      box-shadow:
          0 16px 40px rgba(105, 105, 105, 0.4),
          0 0 0 4px rgba(80, 200, 120, 0.3),
          inset 0 4px 8px rgba(255, 255, 255, 0.4),
          inset 0 -4px 8px rgba(0, 0, 0, 0.15);
    }

    .lever-glow-ring {
      opacity: 1;
      transform: translateX(-50%) scale(1.3);
    }

    .lever-shadow-base {
      transform: scale(1.2);
      opacity: 0.8;
    }
  }

  &:active:not(.mc-lever-container--disabled) {
    transform: scale(1.02) rotateY(2deg) rotateX(-1deg);
    transition: all 0.1s ease;
  }

  &.mc-lever-container--disabled {
    opacity: 0.5;
    cursor: not-allowed;
    filter: grayscale(0.6) drop-shadow(0 8px 16px rgba(0, 0, 0, 0.15));
  }
}

// 阴影基座
.lever-shadow-base {
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 15px;
  background: radial-gradient(ellipse,
      rgba(0, 0, 0, 0.4) 0%,
      rgba(0, 0, 0, 0.2) 50%,
      transparent 100%);
  border-radius: 50%;
  transition: all 0.4s ease;
  z-index: -2;
}

// 发光环效果
.lever-glow-ring {
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%) scale(1);
  width: 90px;
  height: 90px;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.5s ease;
  pointer-events: none;
  z-index: -1;

  &::before {
    content: '';
    position: absolute;
    inset: -4px;
    border-radius: 50%;
    background: conic-gradient(from 0deg,
        rgba(80, 200, 120, 0.8),
        rgba(0, 255, 127, 0.6),
        rgba(220, 20, 60, 0.8),
        rgba(80, 200, 120, 0.8)
    );
    animation: rotate 4s linear infinite;
    filter: blur(2px);
  }

  &::after {
    content: '';
    position: absolute;
    inset: 8px;
    border-radius: 50%;
    background: radial-gradient(circle,
        rgba(255, 255, 255, 0.9) 20%,
        rgba(255, 255, 255, 0.6) 50%,
        transparent 80%
    );
  }
}

// 石质底座（加宽）
.lever-stone-base {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 70px;
  height: 20px;
  background: linear-gradient(145deg,
      $stone-light 0%,
      $stone-base 40%,
      $stone-dark 100%);
  border: 2px solid #555555;
  border-radius: 8px;
  box-shadow:
      0 8px 20px rgba(0, 0, 0, 0.4),
      inset 0 4px 8px rgba(255, 255, 255, 0.3),
      inset 0 -4px 8px rgba(0, 0, 0, 0.25);
  transition: all 0.5s ease;
  transform-style: preserve-3d;
  overflow: hidden;
}

// 石头纹理
.stone-texture {
  position: absolute;
  inset: 2px;
  background-image:
      radial-gradient(circle at 20% 30%, rgba(0,0,0,0.15) 1px, transparent 2px),
      radial-gradient(circle at 80% 20%, rgba(255,255,255,0.3) 1px, transparent 2px),
      radial-gradient(circle at 60% 80%, rgba(0,0,0,0.1) 1px, transparent 2px),
      radial-gradient(circle at 30% 70%, rgba(255,255,255,0.2) 1px, transparent 2px),
      repeating-linear-gradient(45deg,
          transparent, transparent 3px,
          rgba(0,0,0,0.03) 3px, rgba(0,0,0,0.03) 4px),
      repeating-linear-gradient(-45deg,
          transparent, transparent 2px,
          rgba(255,255,255,0.05) 2px, rgba(255,255,255,0.05) 3px);
  border-radius: 6px;
}

// 石头顶面
.stone-top-face {
  position: absolute;
  top: -6px;
  left: -2px;
  right: -2px;
  height: 8px;
  background: linear-gradient(145deg,
      rgba(255,255,255,0.9) 0%,
      $stone-light 30%,
      $stone-base 70%,
      $stone-dark 100%);
  border-radius: 8px 8px 0 0;
  border: 2px solid #777777;
  border-bottom: none;
  box-shadow:
      0 3px 6px rgba(0, 0, 0, 0.3),
      inset 0 2px 4px rgba(255, 255, 255, 0.5);
}

// 高光效果
.stone-highlight {
  position: absolute;
  top: 2px;
  left: 4px;
  right: 4px;
  height: 3px;
  background: linear-gradient(90deg,
      transparent 0%,
      rgba(255, 255, 255, 0.4) 20%,
      rgba(255, 255, 255, 0.7) 50%,
      rgba(255, 255, 255, 0.4) 80%,
      transparent 100%);
  border-radius: 2px;
}

// 深度线条
.stone-depth-lines {
  position: absolute;
  inset: 5px;
  border-radius: 4px;

  &::before,
  &::after {
    content: '';
    position: absolute;
    height: 1px;
    left: 0;
    right: 0;
    background: linear-gradient(90deg,
        transparent 0%,
        rgba(0,0,0,0.2) 20%,
        rgba(0,0,0,0.3) 50%,
        rgba(0,0,0,0.2) 80%,
        transparent 100%);
  }

  &::before {
    top: 25%;
  }

  &::after {
    bottom: 25%;
  }
}

// 杆子插槽
.rod-socket {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  width: 12px;
  height: 12px;
  background: linear-gradient(145deg,
      darken($stone-dark, 20%),
      darken($stone-dark, 10%), $stone-dark);
  border-radius: 50%;
  box-shadow:
      inset 0 2px 4px rgba(0, 0, 0, 0.6),
      inset 0 -1px 2px rgba(255, 255, 255, 0.1);
}

// 拉杆杆子（重新设计 - 加粗加长）
.lever-rod {
  position: absolute;
  width: 9px;
  height: 66px;
  left: 50%;
  bottom: 8px;
  transform-origin: bottom center;
  transition: all 0.7s cubic-bezier(0.25, 0.8, 0.25, 1);
  transform: translateX(-50%) rotate(-30deg);
}

.rod-main {
  position: absolute;
  width: 100%;
  height: 62px;
  background: linear-gradient(to right,
      darken($wood-dark, 15%) 0%,
      $wood-dark 20%,
      $wood-base 50%,
      $wood-dark 80%,
      darken($wood-dark, 15%) 100%);
  border-radius: 3px;
  box-shadow:
      2px 2px 6px rgba(0, 0, 0, 0.5),
      inset 1px 0 0 rgba(222, 184, 135, 0.3),
      inset -1px 0 0 rgba(101, 67, 33, 0.6);

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 1px;
    height: 100%;
    background: linear-gradient(to bottom,
        rgba(222, 184, 135, 0.8) 0%,
        rgba(205, 133, 63, 0.6) 50%,
        rgba(139, 69, 19, 0.4) 100%);
  }

  &::after {
    content: '';
    position: absolute;
    top: 0;
    right: 1px;
    width: 1px;
    height: 100%;
    background: linear-gradient(to bottom,
        rgba(139, 69, 19, 0.8) 0%,
        rgba(101, 67, 33, 0.6) 50%,
        rgba(74, 74, 74, 0.4) 100%);
  }
}

// 杆子与底座连接部分
.rod-base-connection {
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 10px;
  background: linear-gradient(145deg,
      $wood-base 0%,
      $wood-dark 50%,
      darken($wood-dark, 20%) 100%);
  border-radius: 4px 4px 2px 2px;
  border: 1px solid darken($wood-dark, 10%);
  box-shadow:
      0 2px 4px rgba(0, 0, 0, 0.4),
      inset 0 1px 0 rgba(222, 184, 135, 0.3);
}

// ON状态
.mc-lever-container--on {
  .lever-glow-ring {
    &::before {
      background: conic-gradient(from 0deg,
          rgba(80, 200, 120, 1),
          rgba(0, 255, 127, 0.8),
          rgba(50, 150, 100, 1),
          rgba(80, 200, 120, 1)
      );
    }
  }

  .lever-rod {
    transform: translateX(-50%) rotate(30deg);
  }
}

// 电路纹路
.redstone-circuit {
  position: absolute;
  inset: 0;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.mc-lever-container--on .redstone-circuit {
  opacity: 1;
}

.circuit-line {
  position: absolute;
  background: linear-gradient(90deg,
      transparent,
      $redstone-glow,
      $redstone-red,
      $redstone-glow,
      transparent);
  box-shadow: 0 0 8px $redstone-glow;
  animation: circuitFlow 2s ease-in-out infinite;

  &.circuit-line-1 {
    bottom: 10px;
    left: 12px;
    right: 12px;
    height: 2px;
    border-radius: 1px;
    animation-delay: 0s;
  }

  &.circuit-line-2 {
    bottom: 14px;
    left: 8px;
    width: 2px;
    height: 10px;
    border-radius: 1px;
    animation-delay: 0.3s;
  }

  &.circuit-line-3 {
    bottom: 14px;
    right: 8px;
    width: 2px;
    height: 10px;
    border-radius: 1px;
    animation-delay: 0.6s;
  }
}

// 加载状态
.mc-lever-container--loading {
  .lever-rod {
    animation: leverOscillate 1.2s ease-in-out infinite;
  }

  .lever-stone-base {
    animation: baseGlow 2.5s ease-in-out infinite;
  }

  .lever-glow-ring {
    opacity: 0.9;
    animation: glowPulse 1.8s ease-in-out infinite;
  }
}

// 红石粒子效果
.redstone-particles {
  position: absolute;
  top: -16px;
  left: -16px;
  right: -16px;
  bottom: -16px;
  pointer-events: none;
  overflow: visible;
}

.particle {
  position: absolute;
  width: 5px;
  height: 5px;
  background: radial-gradient(circle, $redstone-glow, $redstone-red);
  border-radius: 50%;
  box-shadow:
      0 0 10px $redstone-glow,
      0 0 20px rgba(220, 20, 60, 0.6),
      0 0 30px rgba(220, 20, 60, 0.3);
  animation: particleFloat 4s ease-in-out infinite;

  &::before {
    content: '';
    position: absolute;
    inset: -3px;
    background: radial-gradient(circle, rgba(220, 20, 60, 0.5), transparent);
    border-radius: 50%;
    animation: particleGlow 2s ease-in-out infinite alternate;
  }

  &.particle-1 {
    top: 15%;
    left: 15%;
    animation-delay: 0s;
  }

  &.particle-2 {
    top: 25%;
    right: 20%;
    animation-delay: 0.7s;
  }

  &.particle-3 {
    bottom: 35%;
    left: 75%;
    animation-delay: 1.4s;
  }

  &.particle-4 {
    top: 55%;
    left: 90%;
    animation-delay: 0.4s;
  }

  &.particle-5 {
    bottom: 20%;
    right: 10%;
    animation-delay: 1.1s;
  }

  &.particle-6 {
    top: 70%;
    left: 5%;
    animation-delay: 1.8s;
  }
}

// 状态指示器
.lever-status-indicator {
  position: relative;
}

.status-pill {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.6rem 1.2rem;
  border-radius: 25px;
  background: linear-gradient(135deg,
      rgba(255, 255, 255, 0.98) 0%,
      rgba(248, 248, 248, 0.95) 50%,
      rgba(240, 240, 240, 0.9) 100%);
  border: 2px solid rgba(200, 200, 200, 0.8);
  box-shadow:
      0 8px 25px rgba(0, 0, 0, 0.15),
      inset 0 2px 0 rgba(255, 255, 255, 0.9),
      inset 0 -2px 0 rgba(200, 200, 200, 0.3);
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  backdrop-filter: blur(20px);
  position: relative;
  overflow: hidden;
  min-width: 120px;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg,
        transparent,
        rgba(255, 255, 255, 0.6) 10%,
        rgba(255, 255, 255, 0.9) 50%,
        rgba(255, 255, 255, 0.6) 90%,
        transparent);
    transition: left 1s ease;
  }

  &:hover {
    transform: translateY(-3px) scale(1.05);
    box-shadow:
        0 12px 35px rgba(0, 0, 0, 0.2),
        inset 0 2px 0 rgba(255, 255, 255, 1),
        inset 0 -2px 0 rgba(200, 200, 200, 0.4);

    &::before {
      left: 100%;
    }
  }
}

// 状态颜色
.status-pill--active {
  background: linear-gradient(135deg,
      rgba(80, 200, 120, 0.15),
      rgba(0, 255, 127, 0.1));
  border-color: rgba(80, 200, 120, 0.6);
  box-shadow:
      0 8px 25px rgba(80, 200, 120, 0.3),
      inset 0 2px 0 rgba(255, 255, 255, 0.8),
      0 0 20px rgba(80, 200, 120, 0.15);

  .status-icon {
    color: darken($emerald-green, 20%);
    background: linear-gradient(135deg,
        rgba(80, 200, 120, 0.3),
        rgba(80, 200, 120, 0.15));
    box-shadow: 0 0 12px rgba(80, 200, 120, 0.4);
  }

  .status-text {
    color: darken($emerald-green, 20%);
    font-weight: 500;
    text-shadow: 0 1px 3px rgba(80, 200, 120, 0.3);
  }
}

.status-pill--inactive {
  background: linear-gradient(135deg,
      rgba(169, 169, 169, 0.12),
      rgba(169, 169, 169, 0.08));
  border-color: rgba(169, 169, 169, 0.5);

  .status-icon {
    color: #888888;
    background: rgba(169, 169, 169, 0.15);
  }

  .status-text {
    color: #888888;
    font-weight: 500;
  }
}

.status-pill--loading {
  background: linear-gradient(135deg,
      rgba(255, 193, 7, 0.15),
      rgba(255, 193, 7, 0.1));
  border-color: rgba(255, 193, 7, 0.6);
  box-shadow:
      0 8px 25px rgba(255, 193, 7, 0.3),
      inset 0 2px 0 rgba(255, 255, 255, 0.8);

  .status-icon {
    color: #E6A500;
    background: rgba(255, 193, 7, 0.2);
  }

  .status-text {
    color: #E6A500;
    font-weight: 600;
    animation: textPulse 1.5s ease-in-out infinite;
  }
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.status-text {
  font-size: 0.5rem;
  font-weight: 500;
  letter-spacing: 0.3px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

// 动画
@keyframes leverOscillate {
  0%, 100% { transform: translateX(-50%) rotate(-30deg); }
  25% { transform: translateX(-50%) rotate(-35deg); }
  50% { transform: translateX(-50%) rotate(30deg); }
  75% { transform: translateX(-50%) rotate(35deg); }
}

@keyframes baseGlow {
  0%, 100% {
    box-shadow:
        0 8px 20px rgba(0, 0, 0, 0.4),
        inset 0 4px 8px rgba(255, 255, 255, 0.3),
        inset 0 -4px 8px rgba(0, 0, 0, 0.25);
  }
  50% {
    box-shadow:
        0 8px 20px rgba(255, 193, 7, 0.6),
        inset 0 4px 8px rgba(255, 255, 255, 0.4),
        inset 0 -4px 8px rgba(255, 193, 7, 0.4),
        0 0 32px rgba(255, 193, 7, 0.5);
  }
}

@keyframes glowPulse {
  0%, 100% {
    opacity: 0.7;
    transform: translateX(-50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translateX(-50%) scale(1.2);
  }
}

@keyframes particleFloat {
  0%, 100% {
    opacity: 0;
    transform: translateY(0) scale(0.2) rotate(0deg);
  }
  25% {
    opacity: 0.9;
    transform: translateY(-8px) scale(0.9) rotate(90deg);
  }
  50% {
    opacity: 1;
    transform: translateY(-16px) scale(1.3) rotate(180deg);
  }
  75% {
    opacity: 0.7;
    transform: translateY(-10px) scale(1) rotate(270deg);
  }
}

@keyframes particleGlow {
  from { transform: scale(1); opacity: 0.6; }
  to { transform: scale(2); opacity: 0.1; }
}

@keyframes circuitFlow {
  0%, 100% {
    opacity: 0.3;
    box-shadow: 0 0 4px $redstone-glow;
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 16px $redstone-glow, 0 0 24px $redstone-red;
  }
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
  animation: spin 1.5s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 响应式调整
@media (max-width: 768px) {
  .mc-lever-container {
    width: 60px;
    height: 60px;
  }

  .lever-stone-base {
    width: 46px;
    height: 20px;
  }

  .lever-rod {
    width: 5px;
    height: 38px;
    bottom: 18px;
  }

  .status-pill {
    padding: 0.5rem 1rem;
    min-width: 100px;
  }

  .status-text {
    font-size: 0.7rem;
  }
}
</style>