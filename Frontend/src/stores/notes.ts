import { defineStore } from 'pinia'
import { mockNotesBanner, mockNotesMetrics, mockNoteFolders, mockNotes, mockTagStats } from '../mock/notes'

export const useNotesStore = defineStore('notes', {
  state: () => ({
    banner: { ...mockNotesBanner },
    metrics: mockNotesMetrics.map((m) => ({ ...m })),
    folders: mockNoteFolders.map((f) => ({ ...f })),
    notes: mockNotes.map((n) => ({ ...n })),
    tagStats: mockTagStats.map((t) => ({ ...t })),
    activeFolderId: 'notes',
    activeNoteId: 'n1',
  }),
  getters: {
    activeNote: (s) => s.notes.find((n) => n.id === s.activeNoteId) ?? s.notes[0],
    visibleNotes: (s) => {
      const childIds = s.folders.filter((f) => f.parentId === s.activeFolderId).map((f) => f.id)
      const ids = [s.activeFolderId, ...childIds]
      return s.notes.filter((n) => ids.includes(n.folderId))
    },
  },
  actions: {
    selectNote(id: string) {
      this.activeNoteId = id
    },
    selectFolder(id: string) {
      this.activeFolderId = id
      const first = this.visibleNotes[0]
      if (first) this.activeNoteId = first.id
    },
  },
})
