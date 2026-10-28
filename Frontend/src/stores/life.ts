import { defineStore } from 'pinia'
import {
  mockLifeBanner,
  mockLifeMetrics,
  mockActivityDist,
  mockMediaDist,
  mockReading,
  mockWatching,
  mockFinishedMedia,
} from '../mock/life'

export const useLifeStore = defineStore('life', {
  state: () => ({
    banner: { ...mockLifeBanner },
    metrics: mockLifeMetrics.map((m) => ({ ...m })),
    activityDist: mockActivityDist.map((a) => ({ ...a })),
    mediaDist: mockMediaDist.map((m) => ({ ...m })),
    reading: mockReading.map((r) => ({ ...r })),
    watching: mockWatching.map((w) => ({ ...w })),
    finishedMedia: mockFinishedMedia.map((m) => ({ ...m })),
  }),
})
