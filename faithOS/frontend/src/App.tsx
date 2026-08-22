import { useEffect, useState } from 'react'
import {
  Bell, CalendarDays, ChevronDown, CircleDollarSign, Grid2X2,
  HandHeart, HeartHandshake, LogOut, Menu, Plus, Search, Settings,
  Sparkles, Users, X,
} from 'lucide-react'
import { api, type ChurchEvent, type Contribution, type Ministry, type Session, type User } from './api'
import { languages, type Language, usePageTranslation } from './i18n'
import { CalendarScreen, GivingScreen, MinistriesScreen } from './ModuleScreens'

type Screen = 'Home' | 'People' | 'Calendar' | 'Giving' | 'Ministries'

const people = [
  { name: 'Ana Martins', initials: 'AM', role: 'Worship team', joined: 'May 18', status: 'Active', color: 'peach' },
  { name: 'Daniel Costa', initials: 'DC', role: 'Small group leader', joined: 'Apr 02', status: 'Active', color: 'mint' },
  { name: 'Helena Rocha', initials: 'HR', role: 'Kids ministry', joined: 'Mar 27', status: 'New', color: 'lilac' },
  { name: 'Marcos Lima', initials: 'ML', role: 'Member', joined: 'Mar 11', status: 'Active', color: 'blue' },
]

const nav = [
  { label: 'Home', icon: Grid2X2 }, { label: 'People', icon: Users },
  { label: 'Calendar', icon: CalendarDays }, { label: 'Giving', icon: CircleDollarSign },
  { label: 'Ministries', icon: HeartHandshake },
] as const

function LanguageSelect({ language, onChange }: { language: Language; onChange: (language: Language) => void }) {
  return <label className="language-select"><span className="sr-only">Language</span><select value={language} onChange={event => onChange(event.target.value as Language)}>{languages.map(([code,label]) => <option key={code} value={code}>{label}</option>)}</select><ChevronDown size={14}/></label>
}

function Login({ onLogin, language, setLanguage }: { onLogin: (session: Session) => void; language: Language; setLanguage: (language: Language) => void }) {
  const setupText={en:['First-time setup','Create your church workspace','This account will become your church administrator.','Church name','First name','Last name','Phone','At least 8 characters','Creating workspace…','Create church and administrator','Back to sign in','Set up FaithOS for the first time →'],fr:['Configuration initiale',"Créez l’espace de votre église","Ce compte deviendra l’administrateur de votre église.","Nom de l’église",'Prénom','Nom','Téléphone','Au moins 8 caractères','Création en cours…',"Créer l’église et l’administrateur",'Retour à la connexion','Configurer FaithOS pour la première fois →'],'pt-BR':['Configuração inicial','Crie o espaço da sua igreja','Esta conta será a administradora da sua igreja.','Nome da igreja','Nome','Sobrenome','Telefone','Pelo menos 8 caracteres','Criando espaço…','Criar igreja e administrador','Voltar para entrar','Configurar o FaithOS pela primeira vez →'],ht:['Premye konfigirasyon','Kreye espas legliz ou a','Kont sa a pral administratè legliz ou a.','Non legliz','Prenon','Siyati','Telefòn','Omwen 8 karaktè','Ap kreye espas la…','Kreye legliz ak administratè','Retounen konekte','Konfigire FaithOS pou premye fwa →']}[language]
  const registrationText={en:['Church registration','Create your church workspace','Create an administrator account for your church.','Creating account…','Create church account','Create a church account →'],fr:["Inscription de l’église","Créez l’espace de votre église","Créez un compte administrateur pour votre église.",'Création du compte…',"Créer le compte de l’église","Créer un compte d’église →"],'pt-BR':['Cadastro da igreja','Crie o espaço da sua igreja','Crie uma conta de administrador para sua igreja.','Criando conta…','Criar conta da igreja','Criar uma conta para a igreja →'],ht:['Enskripsyon legliz','Kreye espas legliz ou a','Kreye yon kont administratè pou legliz ou a.','Ap kreye kont la…','Kreye kont legliz','Kreye yon kont legliz →']}[language]
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [setupAvailable,setSetupAvailable]=useState(true);const [setupMode,setSetupMode]=useState(false)
  const [setup,setSetup]=useState({churchName:'',firstName:'',lastName:'',email:'',phone:'',password:''})
  useEffect(()=>{api.setupStatus().then(result=>setSetupAvailable(result.available)).catch(()=>setSetupAvailable(true))},[])

  async function submit(event: React.FormEvent) {
    event.preventDefault(); setLoading(true); setError('')
    try {
      const session = await api.login(email, password)
      onLogin(session)
    } catch (err) { setError(err instanceof Error ? err.message : 'Unable to sign in.') }
    finally { setLoading(false) }
  }

  async function initialize(event:React.FormEvent){event.preventDefault();setLoading(true);setError('');try{const session=await api.registerChurch(setup);onLogin(session)}catch(err){setError(err instanceof Error?err.message:'Unable to create the church account.')}finally{setLoading(false)}}

  return <main className="login-shell">
    <section className="login-story">
      <div className="brand brand-light"><span className="brand-mark"><Sparkles size={20}/></span> faithOS</div>
      <div className="story-copy">
        <span className="eyebrow light">Church life, beautifully organized</span>
        <h1>Make space for<br/><em>what matters.</em></h1>
        <p>One peaceful place for your people, ministries, giving, and every moment that brings your church together.</p>
      </div>
      <div className="verse">“Let all things be done decently and in order.” <span>1 Corinthians 14:40</span></div>
    </section>
    <section className="login-panel"><div className="login-language"><LanguageSelect language={language} onChange={setLanguage}/></div>
      {!setupMode?<form className="login-card" onSubmit={submit}>
        <span className="eyebrow">Welcome home</span><h2>Sign in to FaithOS</h2>
        <p>Continue to your church workspace.</p>
        <label>Email address<input type="email" placeholder="you@yourchurch.org" value={email} onChange={e=>setEmail(e.target.value)} required/></label>
        <label>Password<input type="password" placeholder="Enter your password" value={password} onChange={e=>setPassword(e.target.value)} required/></label>
        {error && <div className="form-error">{error}</div>}
        <button className="primary full" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button>
        <button type="button" className="text-button">Forgot your password?</button>
        <button type="button" className="setup-link" onClick={()=>{setError('');setSetupMode(true)}}>{registrationText[5]}</button>
      </form>:<form className="login-card setup-card" onSubmit={initialize}>
        <span className="eyebrow">{registrationText[0]}</span><h2>{registrationText[1]}</h2><p>{registrationText[2]}</p>
        <label>{setupText[3]}<input value={setup.churchName} onChange={e=>setSetup({...setup,churchName:e.target.value})} required/></label>
        <div className="setup-name-row"><label>{setupText[4]}<input value={setup.firstName} onChange={e=>setSetup({...setup,firstName:e.target.value})} required/></label><label>{setupText[5]}<input value={setup.lastName} onChange={e=>setSetup({...setup,lastName:e.target.value})} required/></label></div>
        <label>Email address<input type="email" value={setup.email} onChange={e=>setSetup({...setup,email:e.target.value})} required/></label>
        <label>{setupText[6]}<input value={setup.phone} onChange={e=>setSetup({...setup,phone:e.target.value})}/></label>
        <label>Password<input type="password" minLength={8} placeholder={setupText[7]} value={setup.password} onChange={e=>setSetup({...setup,password:e.target.value})} required/></label>
        {error&&<div className="form-error">{error}</div>}<button className="primary full" disabled={loading}>{loading?registrationText[3]:registrationText[4]}</button><button type="button" className="text-button" onClick={()=>{setError('');setSetupMode(false)}}>{setupText[10]}</button>
      </form>}
    </section>
  </main>
}

function Sidebar({ screen, setScreen, open, close, churchName, role }: { screen: Screen; setScreen: (s: Screen)=>void; open:boolean; close:()=>void;churchName?:string;role:string }) {
  const isAdmin=['SUPER_ADMIN','CHURCH_ADMIN'].includes(role);const visibleNav=nav.filter(item=>isAdmin||!['People','Giving'].includes(item.label))
  return <><aside className={`sidebar ${open ? 'open' : ''}`}>
    <div className="brand"><span className="brand-mark"><Sparkles size={18}/></span> faithOS</div>
    <button className="church-switch"><span className="church-avatar">{(churchName||'FaithOS').split(' ').slice(0,2).map(word=>word[0]).join('').toUpperCase()}</span><span><b>{churchName||'Your church'}</b><small>Church workspace</small></span><ChevronDown size={16}/></button>
    <nav>{visibleNav.map(({label,icon:Icon})=><button key={label} className={screen===label?'active':''} onClick={()=>{setScreen(label);close()}}><Icon size={19}/>{label}</button>)}</nav>
    <div className="sidebar-bottom"><button><Settings size={19}/>Settings</button><div className="help-card"><HandHeart size={22}/><b>Need a hand?</b><span>Visit the FaithOS help center.</span><a href="#help">Get support →</a></div></div>
  </aside>{open&&<button className="scrim" onClick={close} aria-label="Close menu"/>}</>
}

function Dashboard({ peopleCount, firstName, navigate, isAdmin }: { peopleCount: number | null;firstName:string;navigate:(screen:Screen)=>void;isAdmin:boolean }) {
  const [events,setEvents]=useState<ChurchEvent[]>([]);const [contributions,setContributions]=useState<Contribution[]>([]);const [ministries,setMinistries]=useState<Ministry[]>([]);const [loading,setLoading]=useState(true);const [error,setError]=useState('')
  async function load(){setLoading(true);setError('');try{const [eventData,givingData,ministryData]=await Promise.all([api.events(),isAdmin?api.contributions():Promise.resolve([]),api.ministries()]);setEvents(eventData);setContributions(givingData);setMinistries(ministryData)}catch(err){setError(err instanceof Error?err.message:'Unable to load dashboard.')}finally{setLoading(false)}}
  useEffect(()=>{void load()},[])
  const now=new Date();const upcoming=events.filter(event=>new Date(event.startsAt)>=now).slice(0,3);const monthlyGiving=contributions.filter(item=>{const date=new Date(`${item.contributionDate}T12:00:00`);return date.getMonth()===now.getMonth()&&date.getFullYear()===now.getFullYear()}).reduce((sum,item)=>sum+Number(item.amount),0);const activeMinistries=ministries.filter(item=>item.active).length
  return <>
    <header className="page-heading"><div><span className="eyebrow">{now.toLocaleDateString(undefined,{weekday:'long',month:'long',day:'numeric'})}</span><h1>Good morning, {firstName}.</h1><p>Here’s what’s happening in your community.</p></div><button className="primary" onClick={()=>navigate('People')}><Plus size={18}/>Add person</button></header>
    {error&&<div className="dashboard-alert"><span>{error}</span><button onClick={load}>Try again</button></div>}
    <section className="stats-grid">
      <article className="stat feature"><div><span>Church family</span><strong>{isAdmin?(peopleCount??'—'):'Welcome'}</strong><small>{isAdmin?'Registered people':'Your church workspace'}</small></div><Users size={34}/></article>
      <article className="stat"><span>Upcoming events</span><strong>{loading?'—':events.filter(event=>new Date(event.startsAt)>=now).length}</strong><small>On your church calendar</small><button className="stat-link" onClick={()=>navigate('Calendar')}>Open calendar →</button></article>
      {isAdmin&&<article className="stat"><span>Giving this month</span><strong>{loading?'—':monthlyGiving.toLocaleString('pt-BR',{style:'currency',currency:'BRL'})}</strong><small>{contributions.length} total contributions</small></article>}
      <article className="stat"><span>Active ministries</span><strong>{loading?'—':activeMinistries}</strong><small>{ministries.length} ministry teams</small></article>
    </section>
    <section className="dashboard-grid">
      <article className="card"><div className="card-title"><div><span className="eyebrow">Next on the calendar</span><h3>Coming up</h3></div><button className="text-button" onClick={()=>navigate('Calendar')}>View calendar</button></div>
        {!loading&&upcoming.length===0&&<div className="dashboard-empty"><CalendarDays/><b>No upcoming events</b><span>Create an event to see it here.</span></div>}
        {upcoming.map((item,index)=>{const date=new Date(item.startsAt);return <div className="event" key={item.id}><div className={`date ${index===1?'gold':index===2?'rose':''}`}><b>{date.getDate()}</b><span>{date.toLocaleString(undefined,{month:'short'}).toUpperCase()}</span></div><div><b>{item.title}</b><span>{date.toLocaleTimeString(undefined,{hour:'2-digit',minute:'2-digit'})}{item.location?` · ${item.location}`:''}</span></div><span className="tag sage">{item.category||'Event'}</span></div>})}
      </article>
      <article className="card"><div className="card-title"><div><span className="eyebrow">Church snapshot</span><h3>Keep building</h3></div></div>
        <div className="care-row"><span className="care-icon"><HeartHandshake/></span><div><b>{activeMinistries} active ministries</b><span>Teams serving your community</span></div><button onClick={()=>navigate('Ministries')}>View</button></div>
        {isAdmin&&<div className="care-row"><span className="care-icon amber"><CircleDollarSign/></span><div><b>{monthlyGiving.toLocaleString('pt-BR',{style:'currency',currency:'BRL'})} this month</b><span>Recorded generosity</span></div><button onClick={()=>navigate('Giving')}>Review</button></div>}
        {isAdmin&&<div className="care-row"><span className="care-icon blue"><Users/></span><div><b>{peopleCount??0} registered people</b><span>Your church directory</span></div><button onClick={()=>navigate('People')}>Connect</button></div>}
      </article>
    </section>
  </>
}

function People({ users, loading, error, refresh, session }: { users:User[];loading:boolean;error:string;refresh:()=>Promise<void>;session:Session }) {
  const [query,setQuery]=useState('');const [showForm,setShowForm]=useState(false);const [saving,setSaving]=useState(false);const [formError,setFormError]=useState('')
  const [form,setForm]=useState({firstName:'',lastName:'',email:'',phone:'',password:'',role:'MEMBER'})
  const filtered=users.filter(user=>`${user.firstName} ${user.lastName} ${user.email}`.toLowerCase().includes(query.toLowerCase()))
  async function create(event:React.FormEvent){event.preventDefault();if(!session.churchId){setFormError('Your account is not linked to a church.');return}setSaving(true);setFormError('');try{await api.createUser({...form,churchId:session.churchId});setShowForm(false);setForm({firstName:'',lastName:'',email:'',phone:'',password:'',role:'MEMBER'});await refresh()}catch(err){setFormError(err instanceof Error?err.message:'Unable to save this person.')}finally{setSaving(false)}}
  async function toggle(user:User){try{await api.setUserStatus(user.id,!user.active);await refresh()}catch(err){setFormError(err instanceof Error?err.message:'Unable to update status.')}}
  return <><header className="page-heading"><div><span className="eyebrow">Community</span><h1>People</h1><p>Know your community and help everyone feel seen.</p></div><button className="primary" onClick={()=>setShowForm(true)}><Plus size={18}/>Add person</button></header>
    <div className="toolbar"><div className="search"><Search size={18}/><input placeholder="Search by name or email…" value={query} onChange={e=>setQuery(e.target.value)}/></div><button className="filter">{users.length} people</button></div>
    {error&&<div className="data-message error-state"><b>We couldn’t load your people.</b><span>{error}</span><button onClick={refresh}>Try again</button></div>}
    {!error&&<section className="card people-card"><div className="people-head"><span>Person</span><span>Ministry / role</span><span>Church</span><span>Status</span><span/></div>
      {loading&&<div className="data-message">Loading people…</div>}
      {!loading&&filtered.length===0&&<div className="data-message"><b>No people found</b><span>Add the first person or change your search.</span></div>}
      {!loading&&filtered.map((user,index)=>{const initials=`${user.firstName[0]??''}${user.lastName[0]??''}`;const colors=['peach','mint','lilac','blue'];return <div className="person-row" key={user.id}><div className="person"><span className={`person-avatar ${colors[index%colors.length]}`}>{initials}</span><div><b>{user.firstName} {user.lastName}</b><small>{user.email}</small></div></div><span>{user.role.replaceAll('_',' ')}</span><span>{user.churchName||'—'}</span><span><i className={`status ${!user.active?'inactive':''}`}/>{user.active?'Active':'Inactive'}</span><button className="status-button" onClick={()=>toggle(user)}>{user.active?'Deactivate':'Activate'}</button></div>})}
    </section>}
    {showForm&&<div className="modal-backdrop" role="presentation" onMouseDown={()=>setShowForm(false)}><form className="person-modal" onSubmit={create} onMouseDown={e=>e.stopPropagation()}><div className="modal-heading"><div><span className="eyebrow">New person</span><h2>Add to your church family</h2></div><button type="button" onClick={()=>setShowForm(false)} aria-label="Close"><X/></button></div><div className="form-grid"><label>First name<input required value={form.firstName} onChange={e=>setForm({...form,firstName:e.target.value})}/></label><label>Last name<input required value={form.lastName} onChange={e=>setForm({...form,lastName:e.target.value})}/></label><label className="span-2">Email address<input type="email" required value={form.email} onChange={e=>setForm({...form,email:e.target.value})}/></label><label>Phone<input value={form.phone} onChange={e=>setForm({...form,phone:e.target.value})}/></label><label>Role<select value={form.role} onChange={e=>setForm({...form,role:e.target.value})}><option value="MEMBER">Member</option><option value="LEADER">Leader</option><option value="PASTOR">Pastor</option><option value="CHURCH_ADMIN">Church admin</option></select></label><label className="span-2">Temporary password<input type="password" minLength={8} required value={form.password} onChange={e=>setForm({...form,password:e.target.value})}/></label></div>{formError&&<div className="form-error">{formError}</div>}<div className="modal-actions"><button type="button" className="secondary" onClick={()=>setShowForm(false)}>Cancel</button><button className="primary" disabled={saving}>{saving?'Saving…':'Add person'}</button></div></form></div>}
  </>
}

function Placeholder({ screen }: { screen: Exclude<Screen, 'Home' | 'People'> }) {
  const copy = { Calendar: ['Your shared rhythm', 'Plan services, gatherings, and every moment in between.'], Giving: ['Generosity at a glance', 'Track contributions and steward every gift with clarity.'], Ministries: ['Teams with purpose', 'Equip leaders, care for volunteers, and grow healthy ministries.'] }[screen]!
  return <><header className="page-heading"><div><span className="eyebrow">{screen}</span><h1>{copy[0]}</h1><p>{copy[1]}</p></div><button className="primary"><Plus size={18}/>Create new</button></header><section className="placeholder card"><div className="placeholder-orb">{screen==='Calendar'?<CalendarDays/>:screen==='Giving'?<CircleDollarSign/>:<HeartHandshake/>}</div><h2>{screen} is ready for the next step</h2><p>The navigation and visual system are in place. This workspace will connect to the next FaithOS API module.</p><button className="secondary">Explore the layout</button></section></>
}

function App() {
  const [session, setSession] = useState<Session | null>(()=>{try{return JSON.parse(localStorage.getItem('faithos_session')||'null')}catch{return null}})
  const [users,setUsers]=useState<User[]>([]);const [usersLoading,setUsersLoading]=useState(false);const [usersError,setUsersError]=useState('')
  const [screen,setScreen]=useState<Screen>('Home'); const [menu,setMenu]=useState(false)
  const [language,setLanguageState]=useState<Language>(()=>(localStorage.getItem('faithos_language') as Language)||'pt-BR')
  const setLanguage=(next:Language)=>{localStorage.setItem('faithos_language',next);setLanguageState(next)}
  async function refreshUsers(){if(!session||!['SUPER_ADMIN','CHURCH_ADMIN'].includes(session.role))return;setUsersLoading(true);setUsersError('');try{setUsers(await api.users())}catch(err){setUsersError(err instanceof Error?err.message:'Unable to load people.')}finally{setUsersLoading(false)}}
  useEffect(()=>{if(session)void refreshUsers()},[session?.email])
  function signedIn(next:Session){localStorage.setItem('faithos_session',JSON.stringify(next));setSession(next)}
  usePageTranslation(language,screen)
  if (!session) return <Login onLogin={signedIn} language={language} setLanguage={setLanguage}/>
  const isAdmin=['SUPER_ADMIN','CHURCH_ADMIN'].includes(session.role)
  return <div className="app-shell"><Sidebar screen={screen} setScreen={setScreen} open={menu} close={()=>setMenu(false)} churchName={session.churchName} role={session.role}/><main className="main">
    <div className="topbar"><button className="mobile-menu" onClick={()=>setMenu(true)}><Menu/></button><div/><div className="top-actions"><LanguageSelect language={language} onChange={setLanguage}/><button className="icon-button"><Bell size={19}/><i/></button><button className="profile"><span>{session.fullName.split(' ').slice(0,2).map(name=>name[0]).join('').toUpperCase()}</span><div><b>{session.fullName}</b><small>{session.role.replaceAll('_',' ').toLowerCase()}</small></div><ChevronDown size={16}/></button><button className="logout" title="Sign out" onClick={async()=>{await api.logout().catch(()=>undefined);localStorage.removeItem('faithos_session');setSession(null)}}><LogOut size={18}/></button></div></div>
    <div className="content">{screen === 'Home' ? <Dashboard peopleCount={usersError?null:users.length} firstName={session.fullName.split(' ')[0]} navigate={setScreen} isAdmin={isAdmin} /> : screen === 'People' && isAdmin ? <People users={users} loading={usersLoading} error={usersError} refresh={refreshUsers} session={session} /> : screen === 'Calendar' ? <CalendarScreen role={session.role} /> : screen === 'Giving' && isAdmin ? <GivingScreen /> : <MinistriesScreen role={session.role} />}</div>
  </main></div>
}

export default App
