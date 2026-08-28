export type Session = {
  id: number;
  fullName: string;
  email: string;
  role: string;
  churchId?: number;
  churchName?: string;
  mustChangePassword?: boolean;
};
export type User = {
  id: number;
  memberCode?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  cpf?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  role: string;
  active: boolean;
  churchName?: string;
  mustChangePassword?: boolean;
};
export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};
export type CreateUser = {
  memberCode: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phone: string;
  cpf: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  role: string;
  churchId: number;
};
export type UpdateUser = {
  memberCode: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  cpf: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  role: string;
};
export type SetupRequest = {
  churchName: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  password: string;
};
export type MinistryMember = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
};
export type Ministry = {
  id: number;
  name: string;
  description?: string;
  leaderId?: number;
  leaderName?: string;
  members: MinistryMember[];
  active: boolean;
};
export type MinistryPayload = {
  name: string;
  description?: string;
  leaderId?: number;
  memberIds?: number[];
  active: boolean;
};
export type MinistryMessage = {
  id: number;
  senderName: string;
  senderId: number;
  message?: string;
  attachmentName?: string;
  attachmentType?: string;
  attachmentSize?: number;
  createdAt: string;
};
export type AttendanceCheckIn = {
  userId: number;
  memberCode?: string;
  memberName: string;
  checkedInAt: string;
  status: "ON_TIME" | "LATE";
  source: "MANUAL" | "MEMBER" | "DEVICE";
};
export type AttendanceSession = {
  id: number;
  title: string;
  type: "WORSHIP" | "BIBLE_STUDY";
  sessionDate: string;
  opensAt: string;
  onTimeUntil: string;
  closesAt?: string;
  attendees: MinistryMember[];
  checkIns: AttendanceCheckIn[];
};
export type AttendancePayload = {
  title: string;
  type: "WORSHIP" | "BIBLE_STUDY";
  sessionDate: string;
  opensAt: string;
  onTimeUntil: string;
  closesAt?: string;
  attendeeIds: number[];
};
export type Absence = {
  id: number;
  memberId: number;
  memberName: string;
  absenceDate: string;
  reason: string;
  status: string;
  createdAt: string;
  reviewedAt?: string;
};
export type MessageContact = {
  id: number;
  name: string;
  email: string;
  role: string;
};
export type MemberMessage = {
  id: number;
  senderId: number;
  senderName: string;
  recipientId: number;
  message: string;
  attachmentName?: string;
  attachmentType?: string;
  attachmentSize?: number;
  createdAt: string;
  readAt?: string;
};
export type AttendanceReport = {
  from: string;
  to: string;
  totalSessions: number;
  totalCheckIns: number;
  uniqueAttendees: number;
  averageAttendance: number;
  sessionsByType: Record<"WORSHIP" | "BIBLE_STUDY", number>;
  checkInsByType: Record<"WORSHIP" | "BIBLE_STUDY", number>;
};
export type ChurchEvent = {
  id: number;
  title: string;
  description?: string;
  startsAt: string;
  endsAt?: string;
  location?: string;
  category?: string;
};
export type Contribution = {
  id: number;
  donorName?: string;
  amount: number;
  contributionDate: string;
  type: string;
  method?: string;
  notes?: string;
};
export type Expense = {
  id: number;
  description: string;
  amount: number;
  expenseDate: string;
  category: string;
  payee?: string;
  notes?: string;
};
export type FinancialReport = {
  from: string;
  to: string;
  income: number;
  expenses: number;
  netBalance: number;
  incomeByType: Record<string, number>;
  expensesByCategory: Record<string, number>;
};
export type Notification = {
  id: number;
  title: string;
  message: string;
  type: string;
  createdAt: string;
  read: boolean;
};
export type ChurchFile = {
  id: number;
  name: string;
  contentType?: string;
  size: number;
  createdAt: string;
  uploadedBy?: string;
};
export type Church = {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  cnpj: string;
  principalPastor: string;
  pixKey?: string;
  pixRecipient?: string;
  pixCity?: string;
};
export type ChurchPayload = Omit<Church, "id">;
export type PixConfiguration = { key: string; recipient: string; city: string };
export type SuperAdminOverview = {
  totalChurches: number;
  activeChurches: number;
  totalUsers: number;
  activeUsers: number;
  totalMinistries: number;
  auditEvents: number;
};
export type SuperAdminChurch = {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  cnpj: string;
  principalPastor: string;
  active: boolean;
  userCount: number;
  administratorId?: number;
  administratorName?: string;
  administratorEmail?: string;
};
export type SuperAdminUser = {
  id: number;
  fullName: string;
  email: string;
  role: string;
  active: boolean;
};
export type SuperAdminAudit = {
  id: number;
  actorEmail: string;
  action: string;
  targetType: string;
  targetId?: number;
  reason: string;
  createdAt: string;
};
const API_ROOT = (import.meta.env.VITE_API_URL || "/api").replace(/\/$/, "");
const apiUrl = (path: string) => `${API_ROOT}${path}`;

async function getCsrfToken(): Promise<string> {
  const response = await fetch(apiUrl("/auth/csrf"), {
    credentials: "include",
  });
  if (!response.ok)
    throw new Error(
      "Unable to initialize request security. Please refresh the page."
    );
  const csrf = (await response.json()) as { token?: string };
  if (csrf.token) return csrf.token;
  const cookie = document.cookie
    .split("; ")
    .find((value) => value.startsWith("XSRF-TOKEN="));
  if (cookie) return decodeURIComponent(cookie.substring("XSRF-TOKEN=".length));
  throw new Error(
    "Unable to initialize request security. Please refresh the page."
  );
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const method = (options.method ?? "GET").toUpperCase();
  const unsafe = !["GET", "HEAD", "OPTIONS"].includes(method);
  const execute = async () => {
    const token = unsafe ? await getCsrfToken() : null;
    return fetch(apiUrl(path), {
      ...options,
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        ...(token ? { "X-XSRF-TOKEN": token } : {}),
        ...options.headers,
      },
    });
  };
  let response = await execute();
  if (unsafe && response.status === 403) response = await execute();

  if (!response.ok) {
    if (response.status === 401 && !path.startsWith("/auth/login")) {
      window.dispatchEvent(new Event("faithos:session-expired"));
    }
    const error = await response.json().catch(() => null);
    throw new Error(
      error?.message ?? "Something went wrong. Please try again."
    );
  }
  if (response.status === 204) return undefined as T;
  return response.json();
}

export const api = {
  session: () => request<Session>("/auth/session"),
  currentUser: () => request<User>("/users/me"),
  changePassword: (currentPassword: string, newPassword: string) =>
    request<void>("/auth/change-password", {
      method: "POST",
      body: JSON.stringify({ currentPassword, newPassword }),
    }),
  forgotPassword: (email: string) =>
    request<void>("/auth/forgot-password", {
      method: "POST",
      body: JSON.stringify({ email }),
    }),
  resetPassword: (token: string, newPassword: string) =>
    request<void>("/auth/reset-password", {
      method: "POST",
      body: JSON.stringify({ token, newPassword }),
    }),
  login: (email: string, password: string) =>
    request<Session>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    }),
  logout: () => request<void>("/auth/logout", { method: "POST" }),
  users: (page = 0, size = 10, search = "") =>
    request<PageResponse<User>>(
      `/users?page=${page}&size=${size}&search=${encodeURIComponent(search)}`
    ),
  createUser: (user: CreateUser) =>
    request<User>("/users", { method: "POST", body: JSON.stringify(user) }),
  updateUser: (id: number, user: UpdateUser) =>
    request<User>(`/users/${id}`, {
      method: "PUT",
      body: JSON.stringify(user),
    }),
  setUserStatus: (id: number, active: boolean) =>
    request<User>(`/users/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ active }),
    }),
  inviteUser: (id: number) =>
    request<void>(`/users/${id}/invite`, { method: "POST" }),
  submitSupport: (value: { page: string; expected: string; error: string }) =>
    request<void>("/support", { method: "POST", body: JSON.stringify(value) }),
  setupStatus: () => request<{ available: boolean }>("/setup/status"),
  setup: (setup: SetupRequest) =>
    request<Session>("/setup", { method: "POST", body: JSON.stringify(setup) }),
  registerChurch: (setup: SetupRequest) =>
    request<Session>("/auth/register-church", {
      method: "POST",
      body: JSON.stringify(setup),
    }),
  ministries: () => request<Ministry[]>("/ministries"),
  createMinistry: (value: MinistryPayload) =>
    request<Ministry>("/ministries", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  updateMinistry: (id: number, value: MinistryPayload) =>
    request<Ministry>(`/ministries/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  updateMinistryMembers: (id: number, memberIds: number[]) =>
    request<Ministry>(`/ministries/${id}/members`, {
      method: "PUT",
      body: JSON.stringify({ memberIds }),
    }),
  deleteMinistry: (id: number) =>
    request<void>(`/ministries/${id}`, { method: "DELETE" }),
  ministryMessages: (id: number) =>
    request<MinistryMessage[]>(`/ministries/${id}/messages`),
  sendMinistryMessage: async (id: number, message: string, file?: File) => {
    const body = new FormData();
    if (message.trim()) body.append("message", message.trim());
    if (file) body.append("file", file);
    const send = async () =>
      fetch(apiUrl(`/ministries/${id}/messages`), {
        method: "POST",
        credentials: "include",
        headers: { "X-XSRF-TOKEN": await getCsrfToken() },
        body,
      });
    let response = await send();
    if (response.status === 403) response = await send();
    if (!response.ok) {
      if (response.status === 401)
        window.dispatchEvent(new Event("faithos:session-expired"));
      const error = await response.json().catch(() => null);
      throw new Error(error?.message ?? "Unable to send message.");
    }
    return response.json() as Promise<MinistryMessage>;
  },
  ministryAttachment: (ministryId: number, messageId: number) =>
    apiUrl(`/ministries/${ministryId}/messages/${messageId}/attachment`),
  attendance: () => request<AttendanceSession[]>("/attendance"),
  attendanceReport: (from: string, to: string) =>
    request<AttendanceReport>(`/attendance/report?from=${from}&to=${to}`),
  createAttendance: (value: AttendancePayload) =>
    request<AttendanceSession>("/attendance", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  updateAttendance: (id: number, value: AttendancePayload) =>
    request<AttendanceSession>(`/attendance/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  deleteAttendance: (id: number) =>
    request<void>(`/attendance/${id}`, { method: "DELETE" }),
  attendanceCheckIn: () =>
    request<AttendanceCheckIn>("/attendance/check-in", { method: "POST" }),
  absencesMine: () => request<Absence[]>("/absences/mine"),
  absences: () => request<Absence[]>("/absences"),
  submitAbsence: (absenceDate: string, reason: string) =>
    request<Absence>("/absences", {
      method: "POST",
      body: JSON.stringify({ absenceDate, reason }),
    }),
  acknowledgeAbsence: (id: number) =>
    request<Absence>(`/absences/${id}/acknowledge`, { method: "PATCH" }),
  messageContacts: () => request<MessageContact[]>("/member-messages/contacts"),
  memberConversation: (id: number) =>
    request<MemberMessage[]>(`/member-messages/with/${id}`),
  sendMemberMessage: async (id: number, message: string, file?: File) => {
    const body = new FormData();
    if (message.trim()) body.append("message", message.trim());
    if (file) body.append("file", file);
    const send = async () =>
      fetch(apiUrl(`/member-messages/to/${id}`), {
        method: "POST",
        credentials: "include",
        headers: { "X-XSRF-TOKEN": await getCsrfToken() },
        body,
      });
    let response = await send();
    if (response.status === 403) response = await send();
    if (!response.ok) {
      const error = await response.json().catch(() => null);
      throw new Error(error?.message ?? "Unable to send message.");
    }
    return response.json() as Promise<MemberMessage>;
  },
  memberMessageAttachment: (id: number) =>
    apiUrl(`/member-messages/${id}/attachment`),
  events: () => request<ChurchEvent[]>("/events"),
  createEvent: (value: Omit<ChurchEvent, "id">) =>
    request<ChurchEvent>("/events", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  updateEvent: (id: number, value: Omit<ChurchEvent, "id">) =>
    request<ChurchEvent>(`/events/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  deleteEvent: (id: number) =>
    request<void>(`/events/${id}`, { method: "DELETE" }),
  contributions: () => request<Contribution[]>("/contributions"),
  createContribution: (value: Omit<Contribution, "id">) =>
    request<Contribution>("/contributions", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  pixConfiguration: () => request<PixConfiguration>("/donations/pix"),
  recordPixDonation: (amount: number, notes: string) =>
    request<Contribution>("/donations/pix", {
      method: "POST",
      body: JSON.stringify({ amount, notes }),
    }),
  updateContribution: (id: number, value: Omit<Contribution, "id">) =>
    request<Contribution>(`/contributions/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  deleteContribution: (id: number) =>
    request<void>(`/contributions/${id}`, { method: "DELETE" }),
  expenses: () => request<Expense[]>("/expenses"),
  createExpense: (value: Omit<Expense, "id">) =>
    request<Expense>("/expenses", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  updateExpense: (id: number, value: Omit<Expense, "id">) =>
    request<Expense>(`/expenses/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  deleteExpense: (id: number) =>
    request<void>(`/expenses/${id}`, { method: "DELETE" }),
  financialReport: (from: string, to: string) =>
    request<FinancialReport>(`/finance/report?from=${from}&to=${to}`),
  notifications: () => request<Notification[]>("/notifications"),
  createNotification: (value: {
    title: string;
    message: string;
    type: string;
  }) =>
    request<Notification>("/notifications", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  updateNotification: (
    id: number,
    value: { title: string; message: string; type: string }
  ) =>
    request<Notification>(`/notifications/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  markNotificationRead: (id: number) =>
    request<Notification>(`/notifications/${id}/read`, { method: "PATCH" }),
  deleteNotification: (id: number) =>
    request<void>(`/notifications/${id}`, { method: "DELETE" }),
  files: () => request<ChurchFile[]>("/files"),
  uploadFile: async (file: File) => {
    const body = new FormData();
    body.append("file", file);
    const send = async () =>
      fetch(apiUrl("/files"), {
        method: "POST",
        credentials: "include",
        headers: { "X-XSRF-TOKEN": await getCsrfToken() },
        body,
      });
    let response = await send();
    if (response.status === 403) response = await send();
    if (!response.ok) {
      if (response.status === 401)
        window.dispatchEvent(new Event("faithos:session-expired"));
      const error = await response.json().catch(() => null);
      throw new Error(error?.message ?? "Unable to upload file.");
    }
    return response.json() as Promise<ChurchFile>;
  },
  fileContent: (id: number) => apiUrl(`/files/${id}/content`),
  deleteFile: (id: number) =>
    request<void>(`/files/${id}`, { method: "DELETE" }),
  currentChurch: () => request<Church>("/churches/current"),
  updateCurrentChurch: (value: ChurchPayload) =>
    request<Church>("/churches/current", {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  churches: () => request<Church[]>("/churches"),
  createChurch: (value: ChurchPayload) =>
    request<Church>("/churches", {
      method: "POST",
      body: JSON.stringify(value),
    }),
  updateChurch: (id: number, value: ChurchPayload) =>
    request<Church>(`/churches/${id}`, {
      method: "PUT",
      body: JSON.stringify(value),
    }),
  deleteChurch: (id: number) =>
    request<void>(`/churches/${id}`, { method: "DELETE" }),
  superAdminOverview: () =>
    request<SuperAdminOverview>("/super-admin/overview"),
  superAdminChurches: () =>
    request<SuperAdminChurch[]>("/super-admin/churches"),
  superAdminChurchUsers: (churchId: number) =>
    request<SuperAdminUser[]>(`/super-admin/churches/${churchId}/users`),
  setSuperAdminChurchStatus: (churchId: number, active: boolean, reason: string) =>
    request<SuperAdminChurch>(`/super-admin/churches/${churchId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ active, reason }),
    }),
  assignSuperAdminChurchAdministrator: (
    churchId: number,
    userId: number,
    reason: string
  ) =>
    request<SuperAdminChurch>(`/super-admin/churches/${churchId}/administrator`, {
      method: "PATCH",
      body: JSON.stringify({ userId, reason }),
    }),
  superAdminAuditLog: () =>
    request<SuperAdminAudit[]>("/super-admin/audit-log"),
};
