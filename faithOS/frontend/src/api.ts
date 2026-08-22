export type Session = { fullName: string; email: string; role: string; churchId?: number; churchName?:string }
export type User = { id:number; firstName:string; lastName:string; email:string; phone?:string; role:string; active:boolean; churchName?:string }
export type CreateUser = { firstName:string; lastName:string; email:string; password:string; phone:string; role:string; churchId:number }
export type SetupRequest = { churchName:string; firstName:string; lastName:string; email:string; phone:string; password:string }
export type Ministry={id:number;name:string;description?:string;leaderName?:string;active:boolean}
export type ChurchEvent={id:number;title:string;description?:string;startsAt:string;endsAt?:string;location?:string;category?:string}
export type Contribution={id:number;donorName?:string;amount:number;contributionDate:string;type:string;method?:string;notes?:string}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`/api${path}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })

  if (!response.ok) {
    const error = await response.json().catch(() => null)
    throw new Error(error?.message ?? 'Something went wrong. Please try again.')
  }
  if (response.status === 204) return undefined as T
  return response.json()
}

export const api = {
  login: (email: string, password: string) => request<Session>('/auth/login', {
    method: 'POST', body: JSON.stringify({ email, password }),
  }),
  logout:()=>request<void>('/auth/logout',{method:'POST'}),
  users: () => request<User[]>('/users'),
  createUser: (user: CreateUser) => request<User>('/users', { method:'POST', body:JSON.stringify(user) }),
  setUserStatus: (id:number, active:boolean) => request<User>(`/users/${id}/status`, { method:'PATCH', body:JSON.stringify({active}) }),
  setupStatus: () => request<{available:boolean}>('/setup/status'),
  setup: (setup:SetupRequest) => request<Session>('/setup', { method:'POST', body:JSON.stringify(setup) }),
  registerChurch: (setup:SetupRequest) => request<Session>('/auth/register-church', { method:'POST', body:JSON.stringify(setup) }),
  ministries:()=>request<Ministry[]>('/ministries'),
  createMinistry:(value:Omit<Ministry,'id'>)=>request<Ministry>('/ministries',{method:'POST',body:JSON.stringify(value)}),
  updateMinistry:(id:number,value:Omit<Ministry,'id'>)=>request<Ministry>(`/ministries/${id}`,{method:'PUT',body:JSON.stringify(value)}),
  deleteMinistry:(id:number)=>request<void>(`/ministries/${id}`,{method:'DELETE'}),
  events:()=>request<ChurchEvent[]>('/events'),
  createEvent:(value:Omit<ChurchEvent,'id'>)=>request<ChurchEvent>('/events',{method:'POST',body:JSON.stringify(value)}),
  updateEvent:(id:number,value:Omit<ChurchEvent,'id'>)=>request<ChurchEvent>(`/events/${id}`,{method:'PUT',body:JSON.stringify(value)}),
  deleteEvent:(id:number)=>request<void>(`/events/${id}`,{method:'DELETE'}),
  contributions:()=>request<Contribution[]>('/contributions'),
  createContribution:(value:Omit<Contribution,'id'>)=>request<Contribution>('/contributions',{method:'POST',body:JSON.stringify(value)}),
  updateContribution:(id:number,value:Omit<Contribution,'id'>)=>request<Contribution>(`/contributions/${id}`,{method:'PUT',body:JSON.stringify(value)}),
  deleteContribution:(id:number)=>request<void>(`/contributions/${id}`,{method:'DELETE'}),
}
