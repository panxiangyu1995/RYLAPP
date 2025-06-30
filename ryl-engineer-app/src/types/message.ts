export interface UnifiedMessage {
  id: string;
  type: 'chat' | 'task' | 'system' | 'announcement' | 'assistance' | string;
  title: string;
  content: string;
  time: string;
  icon: string;
  iconClass?: string;
  isRead: boolean;
  sourceType: 'chat' | 'announcement' | 'assistance' | string;
  extraData?: Record<string, any>;
} 