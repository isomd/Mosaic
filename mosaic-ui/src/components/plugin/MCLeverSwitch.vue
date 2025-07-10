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
      </div>

      <!-- 拉杆杆子 -->
      <div class="lever-rod">
        <div class="rod-segment rod-segment-1"></div>
        <div class="rod-segment rod-segment-2"></div>
        <div class="rod-segment rod-segment-3"></div>
        <div class="rod-joint rod-joint-1"></div>
        <div class="rod-joint rod-joint-2"></div>
      </div>

      <!-- 拉杆手柄 -->
      <div class="lever-handle-grip">
        <div class="grip-top"></div>
        <div class="grip-shadow"></div>
        <div class="grip-highlight"></div>
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
  width: 60px;
  height: 60px;
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
  width: 70px;
  height: 12px;
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
  width: 80px;
  height: 80px;
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

// 石质底座
.lever-stone-base {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 44px;
  height: 22px;
  background: linear-gradient(145deg,
      $stone-light 0%,
      $stone-base 40%,
      $stone-dark 100%);
  border: 2px solid #555555;
  border-radius: 6px;
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
  border-radius: 4px;
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
  border-radius: 6px 6px 0 0;
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
  left: 3px;
  right: 3px;
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
  inset: 4px;
  border-radius: 3px;

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
    top: 30%;
  }

  &::after {
    bottom: 30%;
  }
}

// 拉杆杆子
.lever-rod {
  position: absolute;
  width: 8px;
  height: 32px;
  left: 50%;
  bottom: 20px;
  transform-origin: bottom center;
  transition: all 0.7s cubic-bezier(0.25, 0.8, 0.25, 1);
  filter: drop-shadow(3px 3px 6px rgba(0, 0, 0, 0.4));
  transform: translateX(-50%) rotate(-40deg);
}

// 杆子分段
.rod-segment {
  position: absolute;
  width: 100%;
  height: 9px;
  background: linear-gradient(145deg, $wood-light, $wood-base, $wood-dark);
  border: 1px solid #654321;
  border-radius: 2px;
  box-shadow:
      inset 2px 2px 3px rgba(222, 184, 135, 0.6),
      inset -1px -1px 3px rgba(101, 67, 33, 0.8),
      0 1px 3px rgba(0, 0, 0, 0.3);

  &:nth-child(1) {
    top: 0;
    background: linear-gradient(145deg,
        lighten($wood-light, 10%), $wood-light, $wood-base);
  }

  &:nth-child(2) {
    top: 11px;
    background: linear-gradient(145deg, $wood-light, $wood-base, $wood-dark);
  }

  &:nth-child(3) {
    top: 22px;
    background: linear-gradient(145deg, $wood-base, $wood-dark, darken($wood-dark, 10%));
  }

  &::after {
    content: '';
    position: absolute;
    inset: 1px;
    background:
        repeating-linear-gradient(0deg,
            transparent, transparent 1px,
            rgba(139, 69, 19, 0.3) 1px, rgba(139, 69, 19, 0.3) 2px),
        repeating-linear-gradient(90deg,
            transparent, transparent 1px,
            rgba(222, 184, 135, 0.2) 1px, rgba(222, 184, 135, 0.2) 2px);
    border-radius: 1px;
  }
}

// 杆子连接点
.rod-joint {
  position: absolute;
  width: 10px;
  height: 2px;
  left: -1px;
  background: linear-gradient(145deg, #8B4513, #654321, #4A4A4A);
  border-radius: 1px;
  box-shadow:
      0 1px 2px rgba(0, 0, 0, 0.5),
      inset 0 1px 0 rgba(139, 69, 19, 0.3);

  &.rod-joint-1 {
    top: 9px;
  }

  &.rod-joint-2 {
    top: 20px;
  }
}

// 拉杆手柄
.lever-handle-grip {
  position: absolute;
  width: 18px;
  height: 12px;
  left: 50%;
  top: 2px;
  transform-origin: center bottom;
  transition: all 0.7s cubic-bezier(0.25, 0.8, 0.25, 1);
  filter: drop-shadow(2px 2px 4px rgba(0, 0, 0, 0.5));
  transform: translateX(-50%) rotate(-40deg) translateY(-26px) translateX(-14px);
}

.grip-top {
  width: 100%;
  height: 10px;
  background: linear-gradient(145deg,
      lighten($wood-light, 15%), $wood-light, $wood-base);
  border: 1px solid $wood-dark;
  border-radius: 4px;
  box-shadow:
      0 4px 8px rgba(0, 0, 0, 0.4),
      inset 0 2px 0 rgba(255, 255, 255, 0.6),
      inset 0 -1px 0 rgba(139, 69, 19, 0.8);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 1px;
    background:
        repeating-linear-gradient(90deg,
            transparent, transparent 2px,
            rgba(139, 69, 19, 0.3) 2px, rgba(139, 69, 19, 0.3) 3px);
    border-radius: 3px;
  }
}

.grip-shadow {
  position: absolute;
  top: 8px;
  right: -4px;
  width: 4px;
  height: 8px;
  background: linear-gradient(180deg,
      darken($wood-base, 10%),
      darken($wood-dark, 10%),
      #2F2F2F);
  border-radius: 0 3px 3px 0;
  box-shadow:
      inset 1px 0 2px rgba(160, 82, 45, 0.2),
      3px 0 4px rgba(0, 0, 0, 0.5);
}

.grip-highlight {
  position: absolute;
  top: 1px;
  left: 2px;
  right: 2px;
  height: 3px;
  background: linear-gradient(90deg,
      transparent 0%,
      rgba(255, 255, 255, 0.6) 30%,
      rgba(255, 255, 255, 0.9) 50%,
      rgba(255, 255, 255, 0.6) 70%,
      transparent 100%);
  border-radius: 2px;
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
    transform: translateX(-50%) rotate(40deg);
    filter: drop-shadow(3px 3px 6px rgba(0, 0, 0, 0.4))
    brightness(1.1) saturate(1.2);
  }

  .lever-handle-grip {
    transform: translateX(-50%) rotate(40deg) translateY(-26px) translateX(14px);
  }

  .lever-stone-base {
    background: linear-gradient(145deg,
        lighten($powered-green, 20%) 0%,
        $emerald-green 40%,
        darken($emerald-green, 20%) 100%);
    border-color: darken($emerald-green, 30%);
    box-shadow:
        0 8px 20px rgba(80, 200, 120, 0.6),
        inset 0 4px 8px rgba(255, 255, 255, 0.4),
        inset 0 -4px 8px rgba(50, 150, 100, 0.4),
        0 0 32px rgba(80, 200, 120, 0.5);
  }

  .stone-top-face {
    background: linear-gradient(145deg,
        rgba(255,255,255,0.9) 0%,
        lighten($powered-green, 25%) 30%,
        $emerald-green 70%,
        darken($emerald-green, 15%) 100%);
    border-color: $emerald-green;
    box-shadow:
        0 3px 6px rgba(80, 200, 120, 0.4),
        0 0 16px rgba(80, 200, 120, 0.5),
        inset 0 2px 4px rgba(255, 255, 255, 0.6);
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
    bottom: 8px;
    left: 10px;
    right: 10px;
    height: 2px;
    border-radius: 1px;
    animation-delay: 0s;
  }

  &.circuit-line-2 {
    bottom: 12px;
    left: 5px;
    width: 2px;
    height: 8px;
    border-radius: 1px;
    animation-delay: 0.3s;
  }

  &.circuit-line-3 {
    bottom: 12px;
    right: 5px;
    width: 2px;
    height: 8px;
    border-radius: 1px;
    animation-delay: 0.6s;
  }
}

// 加载状态
.mc-lever-container--loading {
  .lever-rod {
    animation: leverOscillate 1.2s ease-in-out infinite;
  }

  .lever-handle-grip {
    animation: handleOscillate 1.2s ease-in-out infinite;
  }

  .lever-stone-base {
    animation: baseGlow 2.5s ease-in-out infinite;
  }

  .lever-glow-ring {
    opacity: 0.9;
    animation: glowPulse 1.8s ease-in-out infinite;
  }
}

// 红石粒子效果（增强版）
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
    font-weight: 700;
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
  font-size: 0.6rem;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  transition: all 0.3s ease;
}

// 动画
@keyframes leverOscillate {
  0%, 100% { transform: translateX(-50%) rotate(-40deg); }
  25% { transform: translateX(-50%) rotate(-45deg); }
  50% { transform: translateX(-50%) rotate(40deg); }
  75% { transform: translateX(-50%) rotate(45deg); }
}

@keyframes handleOscillate {
  0%, 100% { transform: translateX(-50%) rotate(-40deg) translateY(-26px) translateX(-14px); }
  25% { transform: translateX(-50%) rotate(-45deg) translateY(-26px) translateX(-15px); }
  50% { transform: translateX(-50%) rotate(40deg) translateY(-26px) translateX(14px); }
  75% { transform: translateX(-50%) rotate(45deg) translateY(-26px) translateX(15px); }
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
    width: 50px;
    height: 50px;
  }

  .lever-stone-base {
    width: 36px;
    height: 18px;
  }

  .lever-rod {
    width: 7px;
    height: 28px;
    bottom: 16px;
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