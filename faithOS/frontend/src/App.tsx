import { useEffect, useState } from 'react'
import {
  BarChart3, Bell, CalendarDays, ChevronDown, CircleDollarSign, ClipboardCheck, Grid2X2,
  BookOpen, FolderOpen, HandHeart, HeartHandshake, KeyRound, LogOut, Mail, Menu, Plus, Search, Settings,
  Eye, Pencil, ShieldCheck, Sparkles, Users, X,
} from 'lucide-react'
import { api, type Church, type ChurchEvent, type Contribution, type Ministry, type Session, type User } from './api'
import { languages, type Language, usePageTranslation } from './i18n'
import { AttendanceScreen, CalendarScreen, FilesScreen, GivingScreen, MinistriesScreen, NotificationsScreen, ReportsScreen } from './ModuleScreens'
import { AdminChurches } from './AdminChurches'
import { PixDonation } from './PixDonation'
import { LegalLinks } from './LegalLinks'

type Screen = 'Home' | 'People' | 'Calendar' | 'Attendance' | 'Giving' | 'Ministries' | 'Notifications' | 'Files' | 'Reports' | 'Settings' | 'Help'

const people = [
  { name: 'Ana Martins', initials: 'AM', role: 'Worship team', joined: 'May 18', status: 'Active', color: 'peach' },
  { name: 'Daniel Costa', initials: 'DC', role: 'Small group leader', joined: 'Apr 02', status: 'Active', color: 'mint' },
  { name: 'Helena Rocha', initials: 'HR', role: 'Kids ministry', joined: 'Mar 27', status: 'New', color: 'lilac' },
  { name: 'Marcos Lima', initials: 'ML', role: 'Member', joined: 'Mar 11', status: 'Active', color: 'blue' },
]

const nav = [
  { label: 'Home', icon: Grid2X2 }, { label: 'People', icon: Users },
  { label: 'Calendar', icon: CalendarDays }, { label: 'Giving', icon: CircleDollarSign },
  { label: 'Attendance', icon: ClipboardCheck },
  { label: 'Notifications', icon: Bell },
  { label: 'Files', icon: FolderOpen },
  { label: 'Reports', icon: BarChart3 },
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
  const [recoveryMode,setRecoveryMode]=useState(false);const [recoverySent,setRecoverySent]=useState(false);const [recoveryEmail,setRecoveryEmail]=useState('');const [resetToken,setResetToken]=useState('');const [resetPassword,setResetPassword]=useState('');const [recoveryMessage,setRecoveryMessage]=useState('')
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
  async function requestReset(event:React.FormEvent){event.preventDefault();setLoading(true);setError('');try{await api.forgotPassword(recoveryEmail);setRecoverySent(true);setRecoveryMessage('If an active account matches that email, a reset code has been sent.')}catch(err){setError(err instanceof Error?err.message:'Unable to request a reset code.')}finally{setLoading(false)}}
  async function confirmReset(event:React.FormEvent){event.preventDefault();setLoading(true);setError('');try{await api.resetPassword(resetToken,resetPassword);setRecoveryMode(false);setRecoverySent(false);setRecoveryMessage('');setResetToken('');setResetPassword('');setEmail(recoveryEmail)}catch(err){setError(err instanceof Error?err.message:'Unable to reset your password.')}finally{setLoading(false)}}

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
      {recoveryMode?<form className="login-card" onSubmit={recoverySent?confirmReset:requestReset}><span className="eyebrow">Account recovery</span><h2>{recoverySent?'Enter your reset code':'Reset your password'}</h2><p>{recoverySent?recoveryMessage:'Enter your account email. For security, the response is the same whether or not an account exists.'}</p>{!recoverySent?<label>Email address<input type="email" value={recoveryEmail} onChange={e=>setRecoveryEmail(e.target.value)} required/></label>:<><label>Reset code<input value={resetToken} onChange={e=>setResetToken(e.target.value)} required autoComplete="one-time-code"/></label><label>New password<input type="password" minLength={12} value={resetPassword} onChange={e=>setResetPassword(e.target.value)} required/></label></>}{error&&<div className="form-error">{error}</div>}<button className="primary full" disabled={loading}>{loading?'Please wait…':recoverySent?'Reset password':'Send reset code'}</button><button type="button" className="text-button" onClick={()=>{setRecoveryMode(false);setRecoverySent(false);setError('')}}>Back to sign in</button></form>:!setupMode?<form className="login-card" onSubmit={submit}>
        <span className="eyebrow">Welcome home</span><h2>Sign in to FaithOS</h2>
        <p>Continue to your church workspace.</p>
        <label>Email address<input type="email" placeholder="you@yourchurch.org" value={email} onChange={e=>setEmail(e.target.value)} required/></label>
        <label>Password<input type="password" placeholder="Enter your password" value={password} onChange={e=>setPassword(e.target.value)} required/></label>
        {error && <div className="form-error">{error}</div>}
        <button className="primary full" disabled={loading}>{loading ? 'Signing in…' : 'Sign in'}</button>
        <button type="button" className="text-button" onClick={()=>{setError('');setRecoveryEmail(email);setRecoveryMode(true)}}>Forgot your password?</button>
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

function ChangePassword({ session, onComplete, language, setLanguage }: { session:Session;onComplete:(session:Session)=>void;language:Language;setLanguage:(language:Language)=>void }) {
  const [currentPassword,setCurrentPassword]=useState('');const [newPassword,setNewPassword]=useState('');const [confirm,setConfirm]=useState('');const [error,setError]=useState('');const [saving,setSaving]=useState(false)
  async function submit(event:React.FormEvent){event.preventDefault();setError('');if(newPassword!==confirm){setError('The new passwords do not match.');return}setSaving(true);try{await api.changePassword(currentPassword,newPassword);onComplete(await api.session())}catch(err){setError(err instanceof Error?err.message:'Unable to change password.')}finally{setSaving(false)}}
  return <main className="login-shell"><section className="login-story"><div className="brand brand-light"><span className="brand-mark"><Sparkles size={20}/></span> faithOS</div><div className="story-copy"><span className="eyebrow light">Secure your account</span><h1>Choose a password<br/><em>only you know.</em></h1><p>Your administrator gave you a temporary password. Replace it before entering your church workspace.</p></div></section><section className="login-panel"><div className="login-language"><LanguageSelect language={language} onChange={setLanguage}/></div><form className="login-card" onSubmit={submit}><span className="eyebrow">First sign-in</span><h2>Create your password</h2><p>Welcome, {session.fullName}. Use at least 12 characters.</p><label>Temporary password<input type="password" value={currentPassword} onChange={e=>setCurrentPassword(e.target.value)} required/></label><label>New password<input type="password" minLength={12} value={newPassword} onChange={e=>setNewPassword(e.target.value)} required/></label><label>Confirm new password<input type="password" minLength={12} value={confirm} onChange={e=>setConfirm(e.target.value)} required/></label>{error&&<div className="form-error">{error}</div>}<button className="primary full" disabled={saving}>{saving?'Saving…':'Save password and continue'}</button></form></section></main>
}

function Sidebar({ screen, setScreen, open, close, churchName, role }: { screen: Screen; setScreen: (s: Screen)=>void; open:boolean; close:()=>void;churchName?:string;role:string }) {
  const isAdmin=['SUPER_ADMIN','CHURCH_ADMIN'].includes(role);const visibleNav=nav.filter(item=>isAdmin||!['People','Giving','Reports'].includes(item.label))
  return <><aside className={`sidebar ${open ? 'open' : ''}`}>
    <div className="brand"><span className="brand-mark"><Sparkles size={18}/></span> faithOS</div>
    <button className="church-switch"><span className="church-avatar">{(churchName||'FaithOS').split(' ').slice(0,2).map(word=>word[0]).join('').toUpperCase()}</span><span><b>{churchName||'Your church'}</b><small>Church workspace</small></span><ChevronDown size={16}/></button>
    <nav>{visibleNav.map(({label,icon:Icon})=><button key={label} className={screen===label?'active':''} onClick={()=>{setScreen(label);close()}}><Icon size={19}/>{label}</button>)}</nav>
    <div className="sidebar-bottom"><button className={screen==='Settings'?'active':''} onClick={()=>{setScreen('Settings');close()}}><Settings size={19}/>Settings</button><div className="help-card"><HandHeart size={22}/><b>Need a hand?</b><span>Visit the FaithOS help center.</span><button onClick={()=>{setScreen('Help');close()}}>Get support →</button></div></div>
  </aside>{open&&<button className="scrim" onClick={close} aria-label="Close menu"/>}</>
}

function Dashboard({ peopleCount, firstName, navigate, isAdmin, language }: { peopleCount: number | null;firstName:string;navigate:(screen:Screen)=>void;isAdmin:boolean;language:Language }) {
  const [events,setEvents]=useState<ChurchEvent[]>([]);const [contributions,setContributions]=useState<Contribution[]>([]);const [ministries,setMinistries]=useState<Ministry[]>([]);const [loading,setLoading]=useState(true);const [error,setError]=useState('')
  async function load(){setLoading(true);setError('');try{const [eventData,givingData,ministryData]=await Promise.all([api.events(),isAdmin?api.contributions():Promise.resolve([]),api.ministries()]);setEvents(eventData);setContributions(givingData);setMinistries(ministryData)}catch(err){setError(err instanceof Error?err.message:'Unable to load dashboard.')}finally{setLoading(false)}}
  useEffect(()=>{void load()},[])
  const now=new Date();const upcoming=events.filter(event=>new Date(event.startsAt)>=now).slice(0,3);const monthlyGiving=contributions.filter(item=>{const date=new Date(`${item.contributionDate}T12:00:00`);return date.getMonth()===now.getMonth()&&date.getFullYear()===now.getFullYear()}).reduce((sum,item)=>sum+Number(item.amount),0);const activeMinistries=ministries.filter(item=>item.active).length
  return <>
    <header className="page-heading"><div><span className="eyebrow">{now.toLocaleDateString(language,{weekday:'long',month:'long',day:'numeric'})}</span><h1>Good morning, {firstName}.</h1><p>Here’s what’s happening in your community.</p></div><button className="primary" onClick={()=>navigate('People')}><Plus size={18}/>Add person</button></header>
    {error&&<div className="dashboard-alert"><span>{error}</span><button onClick={load}>Try again</button></div>}
    <section className="stats-grid">
      <article className="stat feature"><div><span>Church family</span><strong>{isAdmin?(peopleCount??'—'):'Welcome'}</strong><small>{isAdmin?'Registered people':'Your church workspace'}</small></div><Users size={34}/></article>
      <article className="stat"><span>Upcoming events</span><strong>{loading?'—':events.filter(event=>new Date(event.startsAt)>=now).length}</strong><small>On your church calendar</small><button className="stat-link" onClick={()=>navigate('Calendar')}>Open calendar →</button></article>
      {isAdmin&&<article className="stat"><span>Giving this month</span><strong>{loading?'—':monthlyGiving.toLocaleString('pt-BR',{style:'currency',currency:'BRL'})}</strong><small>{contributions.length} total contributions</small></article>}
      <article className="stat"><span>Active ministries</span><strong>{loading?'—':activeMinistries}</strong><small>{ministries.length} ministry teams</small></article>
    </section>
    {!isAdmin&&<PixDonation/>}
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

function People({ users, loading, error, refresh, session, query, setQuery, page, totalPages, totalElements, setPage }: { users:User[];loading:boolean;error:string;refresh:()=>Promise<void>;session:Session;query:string;setQuery:(value:string)=>void;page:number;totalPages:number;totalElements:number;setPage:(page:number)=>void }) {
  const [showForm,setShowForm]=useState(false);const [editing,setEditing]=useState<User|null>(null);const [selected,setSelected]=useState<User|null>(null);const [saving,setSaving]=useState(false);const [formError,setFormError]=useState('');const [actionMessage,setActionMessage]=useState('')
  const [form,setForm]=useState({firstName:'',lastName:'',email:'',phone:'',cpf:'',emergencyContactName:'',emergencyContactPhone:'',password:'',role:'MEMBER'})
  const emptyForm={firstName:'',lastName:'',email:'',phone:'',cpf:'',emergencyContactName:'',emergencyContactPhone:'',password:'',role:'MEMBER'}
  function addPerson(){setEditing(null);setForm(emptyForm);setFormError('');setShowForm(true)}
  function editPerson(user:User){setEditing(user);setForm({firstName:user.firstName,lastName:user.lastName,email:user.email,phone:user.phone??'',cpf:user.cpf??'',emergencyContactName:user.emergencyContactName??'',emergencyContactPhone:user.emergencyContactPhone??'',password:'',role:user.role});setFormError('');setShowForm(true)}
  function closeForm(){setShowForm(false);setEditing(null);setFormError('')}
  async function save(event:React.FormEvent){event.preventDefault();if(!validCpf(form.cpf)){setFormError('Enter a valid CPF, including its verification digits.');return}if(!editing&&!session.churchId){setFormError('Your account is not linked to a church.');return}setSaving(true);setFormError('');try{if(editing){await api.updateUser(editing.id,{firstName:form.firstName,lastName:form.lastName,email:form.email,phone:form.phone,cpf:form.cpf,emergencyContactName:form.emergencyContactName,emergencyContactPhone:form.emergencyContactPhone,role:form.role})}else{await api.createUser({...form,churchId:session.churchId!})}closeForm();setForm(emptyForm);await refresh()}catch(err){setFormError(err instanceof Error?err.message:'Unable to save this person.')}finally{setSaving(false)}}
  async function toggle(user:User){try{await api.setUserStatus(user.id,!user.active);await refresh()}catch(err){setFormError(err instanceof Error?err.message:'Unable to update status.')}}
  async function invite(user:User){setActionMessage('');try{await api.inviteUser(user.id);setActionMessage(`Invitation sent to ${user.email}.`)}catch(err){setActionMessage(err instanceof Error?err.message:'Unable to send invitation.')}}
  return <><header className="page-heading"><div><span className="eyebrow">Community</span><h1>People</h1><p>Know your community and help everyone feel seen.</p></div><button className="primary" onClick={addPerson}><Plus size={18}/>Add person</button></header>
    <div className="toolbar"><div className="search"><Search size={18}/><input placeholder="Search by name or email…" value={query} onChange={e=>{setQuery(e.target.value);setPage(0)}}/></div><button className="filter">{totalElements} people</button></div>{actionMessage&&<div className="form-success directory-message">{actionMessage}</div>}
    {error&&<div className="data-message error-state"><b>We couldn’t load your people.</b><span>{error}</span><button onClick={refresh}>Try again</button></div>}
    {!error&&<section className="card people-card"><div className="people-head"><span>Person</span><span>Ministry / role</span><span>Church</span><span>Status</span><span/></div>
      {loading&&<div className="data-message">Loading people…</div>}
      {!loading&&users.length===0&&<div className="data-message"><b>No people found</b><span>Add the first person or change your search.</span></div>}
      {!loading&&users.map((user,index)=>{const initials=`${user.firstName[0]??''}${user.lastName[0]??''}`;const colors=['peach','mint','lilac','blue'];return <div className="person-row" key={user.id}><div className="person"><span className={`person-avatar ${colors[index%colors.length]}`}>{initials}</span><div><b>{user.firstName} {user.lastName}</b><small>{user.email}</small></div></div><span>{user.role.replaceAll('_',' ')}</span><span>{user.churchName||'—'}</span><span><i className={`status ${!user.active?'inactive':''}`}/>{user.active?'Active':'Inactive'}</span><div className="person-actions"><button className="status-button" onClick={()=>setSelected(user)} aria-label={`View ${user.firstName}`}><Eye size={14}/>View</button><button className="status-button" disabled={!user.active} onClick={()=>invite(user)} aria-label={`Invite ${user.firstName}`}><Mail size={14}/>Invite</button><button className="status-button" onClick={()=>editPerson(user)} aria-label={`Edit ${user.firstName}`}><Pencil size={14}/>Edit</button><button className="status-button" onClick={()=>toggle(user)}>{user.active?'Deactivate':'Activate'}</button></div></div>})}
      {!loading&&totalPages>1&&<div className="pagination"><button disabled={page===0} onClick={()=>setPage(page-1)}>Previous</button><span>Page {page+1} of {totalPages}</span><button disabled={page+1>=totalPages} onClick={()=>setPage(page+1)}>Next</button></div>}
    </section>}
    {showForm&&<div className="modal-backdrop" role="presentation" onMouseDown={closeForm}><form className="person-modal" onSubmit={save} onMouseDown={e=>e.stopPropagation()}><div className="modal-heading"><div><span className="eyebrow">{editing?'Edit person':'New person'}</span><h2>{editing?'Update their details':'Add to your church family'}</h2></div><button type="button" onClick={closeForm} aria-label="Close"><X/></button></div><div className="form-grid"><label>First name<input required value={form.firstName} onChange={e=>setForm({...form,firstName:e.target.value})}/></label><label>Last name<input required value={form.lastName} onChange={e=>setForm({...form,lastName:e.target.value})}/></label><label className="span-2">Email address<input type="email" required value={form.email} onChange={e=>setForm({...form,email:e.target.value})}/></label><label>Phone<input value={form.phone} onChange={e=>setForm({...form,phone:e.target.value})}/></label><label>CPF<input inputMode="numeric" placeholder="000.000.000-00" pattern="(?:\d{3}\.?){3}-?\d{2}|\d{11}" required value={form.cpf} onChange={e=>setForm({...form,cpf:e.target.value})}/></label><label>Emergency contact<input required value={form.emergencyContactName} onChange={e=>setForm({...form,emergencyContactName:e.target.value})}/></label><label>Emergency phone<input type="tel" required value={form.emergencyContactPhone} onChange={e=>setForm({...form,emergencyContactPhone:e.target.value})}/></label><label>Role<select value={form.role} onChange={e=>setForm({...form,role:e.target.value})}><option value="MEMBER">Member</option><option value="LEADER">Leader</option><option value="PASTOR">Pastor</option><option value="CHURCH_ADMIN">Church admin</option></select></label>{!editing&&<label>Temporary password<input type="password" minLength={12} required value={form.password} onChange={e=>setForm({...form,password:e.target.value})}/></label>}</div>{formError&&<div className="form-error">{formError}</div>}<div className="modal-actions"><button type="button" className="secondary" onClick={closeForm}>Cancel</button><button className="primary" disabled={saving}>{saving?'Saving…':editing?'Save changes':'Add person'}</button></div></form></div>}
    {selected&&<div className="modal-backdrop" role="presentation" onMouseDown={()=>setSelected(null)}><section className="person-modal profile-modal" role="dialog" aria-modal="true" aria-labelledby="member-profile-title" onMouseDown={e=>e.stopPropagation()}><div className="modal-heading"><div><span className="eyebrow">Member profile</span><h2 id="member-profile-title">{selected.firstName} {selected.lastName}</h2></div><button type="button" onClick={()=>setSelected(null)} aria-label="Close"><X/></button></div><dl className="profile-details"><div><dt>Email</dt><dd>{selected.email}</dd></div><div><dt>Phone</dt><dd>{selected.phone||'Not provided'}</dd></div><div><dt>CPF</dt><dd>{formatCpf(selected.cpf)}</dd></div><div><dt>Role</dt><dd>{selected.role.replaceAll('_',' ')}</dd></div><div><dt>Church</dt><dd>{selected.churchName||'Not assigned'}</dd></div><div><dt>Status</dt><dd>{selected.active?'Active':'Inactive'}</dd></div><div className="span-2"><dt>Emergency contact</dt><dd>{selected.emergencyContactName||'Not provided'} · {selected.emergencyContactPhone||'No phone'}</dd></div></dl><div className="modal-actions"><button className="secondary" onClick={()=>setSelected(null)}>Close</button><button className="primary" onClick={()=>{const user=selected;setSelected(null);editPerson(user)}}><Pencil size={16}/>Edit profile</button></div></section></div>}
  </>
}

function validCpf(value:string){const cpf=value.replace(/\D/g,'');if(cpf.length!==11||/^(\d)\1{10}$/.test(cpf))return false;const digit=(length:number)=>{let sum=0;for(let index=0;index<length;index++)sum+=Number(cpf[index])*(length+1-index);const remainder=(sum*10)%11;return remainder===10?0:remainder};return digit(9)===Number(cpf[9])&&digit(10)===Number(cpf[10])}
function formatCpf(value?:string){const cpf=(value||'').replace(/\D/g,'');return cpf.length===11?cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/,'$1.$2.$3-$4'):'Not provided'}

function Placeholder({ screen }: { screen: Exclude<Screen, 'Home' | 'People'> }) {
  const copy = { Calendar: ['Your shared rhythm', 'Plan services, gatherings, and every moment in between.'], Attendance: ['Gathering attendance', 'Record worship and Bible-study participation.'], Giving: ['Generosity at a glance', 'Track contributions and steward every gift with clarity.'], Ministries: ['Teams with purpose', 'Equip leaders, care for volunteers, and grow healthy ministries.'], Notifications: ['Stay connected', 'Share important updates with your church community.'], Files: ['Shared files', 'Keep important church documents organized.'], Reports: ['Leadership reports', 'See the health of your church in one place.'], Settings: ['Workspace settings', 'Manage your account and preferences.'], Help: ['Help center', 'Learn how to use FaithOS.'] }[screen]!
  return <><header className="page-heading"><div><span className="eyebrow">{screen}</span><h1>{copy[0]}</h1><p>{copy[1]}</p></div><button className="primary"><Plus size={18}/>Create new</button></header><section className="placeholder card"><div className="placeholder-orb">{screen==='Calendar'?<CalendarDays/>:screen==='Giving'?<CircleDollarSign/>:<HeartHandshake/>}</div><h2>{screen} is ready for the next step</h2><p>The navigation and visual system are in place. This workspace will connect to the next FaithOS API module.</p><button className="secondary">Explore the layout</button></section></>
}

function SettingsScreen({session,language,setLanguage}:{session:Session;language:Language;setLanguage:(language:Language)=>void}){
  const [currentPassword,setCurrentPassword]=useState('');const [newPassword,setNewPassword]=useState('');const [confirm,setConfirm]=useState('');const [saving,setSaving]=useState(false);const [error,setError]=useState('');const [success,setSuccess]=useState('')
  const [church,setChurch]=useState<Church|null>(null);const [churchError,setChurchError]=useState('');const [churchSaving,setChurchSaving]=useState(false)
  useEffect(()=>{if(!session.churchId)return;api.currentChurch().then(setChurch).catch(err=>setChurchError(err instanceof Error?err.message:'Unable to load church settings.'))},[session.churchId])
  async function changePassword(e:React.FormEvent){e.preventDefault();setError('');setSuccess('');if(newPassword!==confirm){setError('The new passwords do not match.');return}setSaving(true);try{await api.changePassword(currentPassword,newPassword);setCurrentPassword('');setNewPassword('');setConfirm('');setSuccess('Your password was updated successfully.')}catch(err){setError(err instanceof Error?err.message:'Unable to update password.')}finally{setSaving(false)}}
  async function saveChurch(e:React.FormEvent){e.preventDefault();if(!church)return;setChurchSaving(true);setChurchError('');try{setChurch(await api.updateCurrentChurch({name:church.name,email:church.email,phone:church.phone,address:church.address,cnpj:church.cnpj,principalPastor:church.principalPastor}));setSuccess('Church profile updated successfully.')}catch(err){setChurchError(err instanceof Error?err.message:'Unable to update church settings.')}finally{setChurchSaving(false)}}
  const canEditChurch=session.role==='CHURCH_ADMIN'
  return <><header className="page-heading"><div><span className="eyebrow">Settings</span><h1>Workspace settings</h1><p>Manage your church, account security, and display preferences.</p></div></header><section className="settings-grid"><article className="card settings-card"><div className="settings-icon"><Users/></div><div><span className="eyebrow">Account</span><h2>{session.fullName}</h2><p>{session.email}</p><dl><div><dt>Role</dt><dd>{session.role.replaceAll('_',' ')}</dd></div><div><dt>Church</dt><dd>{session.churchName||'Not assigned'}</dd></div></dl></div></article><article className="card settings-card"><div className="settings-icon"><BookOpen/></div><div><span className="eyebrow">Language</span><h2>Display language</h2><p>Choose the language used throughout FaithOS.</p><LanguageSelect language={language} onChange={setLanguage}/></div></article>{church&&<form className="card password-settings church-settings" onSubmit={saveChurch}><div className="settings-icon"><HeartHandshake/></div><div><span className="eyebrow">Church profile</span><h2>Workspace details</h2><p>{canEditChurch?'Keep your church contact and registration details current.':'Church profile details are managed by an administrator.'}</p><div className="form-grid"><label className="span-2">Church name<input disabled={!canEditChurch} required value={church.name} onChange={e=>setChurch({...church,name:e.target.value})}/></label><label>Email<input disabled={!canEditChurch} type="email" required value={church.email} onChange={e=>setChurch({...church,email:e.target.value})}/></label><label>Phone<input disabled={!canEditChurch} required value={church.phone} onChange={e=>setChurch({...church,phone:e.target.value})}/></label><label className="span-2">Address<input disabled={!canEditChurch} required value={church.address} onChange={e=>setChurch({...church,address:e.target.value})}/></label><label>CNPJ<input disabled={!canEditChurch} required value={church.cnpj} onChange={e=>setChurch({...church,cnpj:e.target.value})}/></label><label>Principal pastor<input disabled={!canEditChurch} required value={church.principalPastor} onChange={e=>setChurch({...church,principalPastor:e.target.value})}/></label></div>{churchError&&<div className="form-error">{churchError}</div>}{canEditChurch&&<button className="primary" disabled={churchSaving}>{churchSaving?'Saving…':'Save church profile'}</button>}</div></form>}{!church&&churchError&&<div className="data-message error-state"><b>Church settings unavailable</b><span>{churchError}</span></div>}<form className="card password-settings" onSubmit={changePassword}><div className="settings-icon"><KeyRound/></div><div><span className="eyebrow">Security</span><h2>Change password</h2><p>Use at least 12 characters for your new password.</p><label>Current password<input type="password" required value={currentPassword} onChange={e=>setCurrentPassword(e.target.value)}/></label><label>New password<input type="password" minLength={12} required value={newPassword} onChange={e=>setNewPassword(e.target.value)}/></label><label>Confirm new password<input type="password" minLength={12} required value={confirm} onChange={e=>setConfirm(e.target.value)}/></label>{error&&<div className="form-error">{error}</div>}{success&&<div className="form-success">{success}</div>}<button className="primary" disabled={saving}>{saving?'Updating…':'Update password'}</button></div></form></section></>
}

function HelpScreen({navigate,role}:{navigate:(screen:Screen)=>void;role:string}){
  const admin=['SUPER_ADMIN','CHURCH_ADMIN'].includes(role)
  const [supportOpen,setSupportOpen]=useState(false);const [support,setSupport]=useState({page:'',expected:'',error:''});const [copied,setCopied]=useState(false);const [supportSending,setSupportSending]=useState(false)
  const guides=[['People','Add accounts, assign roles, and activate or suspend access.',Users,admin?'People':'Home'],['Calendar','Schedule worship services, meetings, and church events.',CalendarDays,'Calendar'],['Attendance','Record worship and Bible-study attendance and review trends.',ClipboardCheck,'Attendance'],['Ministries','Assign leaders and organize ministry members.',HeartHandshake,'Ministries'],['Files','Upload and share important documents securely.',FolderOpen,'Files'],['Security','Temporary passwords must be replaced on first sign-in.',ShieldCheck,'Settings']] as const
  async function sendSupport(e:React.FormEvent){e.preventDefault();setSupportSending(true);try{await api.submitSupport(support);setSupportOpen(false);setSupport({page:'',expected:'',error:''});window.alert('Your support request was sent successfully.')}catch(error){window.alert(error instanceof Error?error.message:'Unable to send the support request.')}finally{setSupportSending(false)}}
  async function copyAddress(){await navigator.clipboard.writeText('obensonjoffre3@gmail.com');setCopied(true);window.setTimeout(()=>setCopied(false),2000)}
  return <><header className="page-heading"><div><span className="eyebrow">Help center</span><h1>How can we help?</h1><p>Quick guidance for the main FaithOS workflows.</p></div></header><section className="help-grid">{guides.map(([title,description,Icon,target])=><article className="card help-guide" key={title}><span><Icon/></span><h2>{title}</h2><p>{description}</p><button className="text-button" onClick={()=>navigate(target as Screen)}>Open {title} →</button></article>)}</section><section className="card support-panel"><div><HandHeart/><div><span className="eyebrow">Support</span><h2>Still need help?</h2><p>Include the page name, what you expected, and the exact error message when requesting support.</p><small>obensonjoffre3@gmail.com</small></div></div><div className="page-actions"><button className="secondary" onClick={copyAddress}>{copied?'Copied!':'Copy email'}</button><button className="primary" onClick={()=>setSupportOpen(true)}>Email support</button></div></section>{supportOpen&&<div className="modal-backdrop" onMouseDown={()=>setSupportOpen(false)}><form className="person-modal" onSubmit={sendSupport} onMouseDown={e=>e.stopPropagation()}><div className="modal-heading"><div><span className="eyebrow">Support</span><h2>Tell us what happened</h2></div><button type="button" onClick={()=>setSupportOpen(false)} aria-label="Close"><X/></button></div><div className="form-grid"><label className="span-2">Page or module<input required placeholder="For example: People" value={support.page} onChange={e=>setSupport({...support,page:e.target.value})}/></label><label className="span-2">What did you expect?<textarea required rows={3} value={support.expected} onChange={e=>setSupport({...support,expected:e.target.value})}/></label><label className="span-2">What happened or which error appeared?<textarea required rows={4} value={support.error} onChange={e=>setSupport({...support,error:e.target.value})}/></label></div><div className="modal-actions"><button type="button" className="secondary" onClick={()=>setSupportOpen(false)}>Cancel</button><button className="primary">Open email draft</button></div></form></div>}</>
}

function App() {
  const [session, setSession] = useState<Session | null>(null)
  const [sessionLoading,setSessionLoading]=useState(true)
  const [users,setUsers]=useState<User[]>([]);const [usersLoading,setUsersLoading]=useState(false);const [usersError,setUsersError]=useState('')
  const [usersPage,setUsersPage]=useState(0);const [usersSearch,setUsersSearch]=useState('');const [usersTotal,setUsersTotal]=useState(0);const [usersTotalPages,setUsersTotalPages]=useState(0)
  const [screen,setScreen]=useState<Screen>('Home'); const [menu,setMenu]=useState(false)
  const [language,setLanguageState]=useState<Language>(()=>(localStorage.getItem('faithos_language') as Language)||'pt-BR')
  const setLanguage=(next:Language)=>{localStorage.setItem('faithos_language',next);setLanguageState(next)}
  useEffect(()=>{
    const expire=()=>{localStorage.removeItem('faithos_session');setSession(null)}
    window.addEventListener('faithos:session-expired',expire)
    api.session().then(setSession).catch(()=>setSession(null)).finally(()=>setSessionLoading(false))
    return()=>window.removeEventListener('faithos:session-expired',expire)
  },[])
  async function refreshUsers(){if(!session||!['SUPER_ADMIN','CHURCH_ADMIN'].includes(session.role))return;setUsersLoading(true);setUsersError('');try{const result=await api.users(usersPage,10,usersSearch);setUsers(result.content);setUsersTotal(result.totalElements);setUsersTotalPages(result.totalPages)}catch(err){setUsersError(err instanceof Error?err.message:'Unable to load people.')}finally{setUsersLoading(false)}}
  useEffect(()=>{if(!session)return;const timer=window.setTimeout(()=>void refreshUsers(),usersSearch?300:0);return()=>window.clearTimeout(timer)},[session?.email,usersPage,usersSearch])
  function signedIn(next:Session){localStorage.removeItem('faithos_session');setSession(next)}
  usePageTranslation(language,screen)
  if(sessionLoading)return <main className="login-shell"><section className="login-panel"><div className="login-card">Loading FaithOS…</div></section></main>
  if (!session) return <Login onLogin={signedIn} language={language} setLanguage={setLanguage}/>
  if(session.mustChangePassword)return <ChangePassword session={session} onComplete={setSession} language={language} setLanguage={setLanguage}/>
  const isAdmin=['SUPER_ADMIN','CHURCH_ADMIN'].includes(session.role)
  return <div className="app-shell"><Sidebar screen={screen} setScreen={setScreen} open={menu} close={()=>setMenu(false)} churchName={session.churchName} role={session.role}/><main className="main">
    <div className="topbar"><button className="mobile-menu" onClick={()=>setMenu(true)}><Menu/></button><div/><div className="top-actions"><LanguageSelect language={language} onChange={setLanguage}/><button className="icon-button" title="Notifications" onClick={()=>setScreen('Notifications')}><Bell size={19}/><i/></button><button className="profile"><span>{session.fullName.split(' ').slice(0,2).map(name=>name[0]).join('').toUpperCase()}</span><div><b>{session.fullName}</b><small>{session.role.replaceAll('_',' ').toLowerCase()}</small></div><ChevronDown size={16}/></button><button className="logout" title="Sign out" onClick={async()=>{await api.logout().catch(()=>undefined);setSession(null)}}><LogOut size={18}/></button></div></div>
    <div className="content">{screen === 'Home' ? <Dashboard peopleCount={usersError?null:usersTotal} firstName={session.fullName.split(' ')[0]} navigate={setScreen} isAdmin={isAdmin} language={language} /> : screen === 'People' && isAdmin ? <People users={users} loading={usersLoading} error={usersError} refresh={refreshUsers} session={session} query={usersSearch} setQuery={setUsersSearch} page={usersPage} totalPages={usersTotalPages} totalElements={usersTotal} setPage={setUsersPage} /> : screen === 'Calendar' ? <CalendarScreen role={session.role} /> : screen === 'Attendance' ? <AttendanceScreen role={session.role} /> : screen === 'Notifications' ? <NotificationsScreen role={session.role} /> : screen === 'Files' ? <FilesScreen role={session.role} /> : screen === 'Settings' ? <><SettingsScreen session={session} language={language} setLanguage={setLanguage} /><LegalLinks/>{session.role==='SUPER_ADMIN'&&<AdminChurches/>}</> : screen === 'Help' ? <HelpScreen navigate={setScreen} role={session.role} /> : screen === 'Reports' && isAdmin ? <ReportsScreen /> : screen === 'Giving' && isAdmin ? <GivingScreen /> : <MinistriesScreen role={session.role} userId={session.id} />}</div>
  </main></div>
}

export default App
