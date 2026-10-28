import { defineStore } from 'pinia'
import { apiNotes } from '../api'

type NoteBlock = { type: 'p' | 'h4'; text: string }
type TagRef = { text: string; class: string }

interface NoteRow {
  id: string
  path: string
  folderId: string
  title: string
  tag: string
  tagClass: string
  timeAgo: string
  words: number
  paragraphs: NoteBlock[]
  tags: TagRef[]
  updatedAt: string
}

interface FolderRow { id: string; icon: string; name: string; count: number; parentId?: string }
interface TagStat { name: string; count: number; percent: number; color: string }

interface NotesState {
  banner: { title: string; desc: string; chips: string[]; total?: number; addedThisMonth?: number; editedThisWeek?: number }
  metrics: { label: string; value: string; unit: string }[]
  folders: FolderRow[]
  notes: NoteRow[]
  tagStats: TagStat[]
  activeFolderId: string
  activeNote: NoteRow | null
  loading: boolean
  error: string | null
}

export const useNotesStore = defineStore('notes', {
  state: (): NotesState => ({
    banner: { title: '', desc: '', chips: [] },
    metrics: [],
    folders: [],
    notes: [],
    tagStats: [],
    activeFolderId: '',
    activeNote: null,
    loading: false,
    error: null,
  }),
  getters: {
    activeNoteId: (s) => s.activeNote?.id ?? '',
    visibleNotes: (s) => s.notes,
  },
  actions: {
    async fetchOverview() {
      this.loading = true
      this.error = null
      try {
        const d = await apiNotes.overview()
        this.banner = d.banner ?? { title: '', desc: '', chips: [] }
        this.metrics = d.metrics ?? []
        this.folders = d.folders ?? []
        this.tagStats = d.tagStats ?? []
        this.activeFolderId = d.activeFolderId ?? (this.folders[0]?.id ?? '')
        if (d.activeNote) this.activeNote = d.activeNote
        await this.fetchList()
      } catch (e: any) {
        this.error = e?.detail ?? '笔记加载失败'
      } finally {
        this.loading = false
      }
    },
    async fetchList(folder?: string) {
      try {
        const targetFolder = folder ?? this.activeFolderId
        const d = await apiNotes.list({ folder: targetFolder, page: 1, size: 50 })
        const items = (d.items ?? d.notes ?? []) as NoteRow[]
        this.notes = items
        if (items.length && (!this.activeNote || !items.find((n) => n.id === this.activeNote?.id))) {
          this.activeNote = items[0]
        }
      } catch (e: any) {
        this.error = e?.detail ?? '笔记列表加载失败'
      }
    },
    selectNote(id: string) {
      const found = this.notes.find((n) => n.id === id)
      if (found) this.activeNote = found
    },
    async selectFolder(id: string) {
      this.activeFolderId = id
      await this.fetchList(id)
    },
    async rescan() {
      try {
        await apiNotes.rescan()
        await this.fetchOverview()
      } catch (e: any) {
        this.error = e?.detail ?? '笔记索引刷新失败'
      }
    },
  },
})
