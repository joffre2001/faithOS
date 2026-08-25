import { useEffect, useState } from "react";
import { CalendarX2, Inbox, MessageSquare, Paperclip, Send } from "lucide-react";
import { api, type Absence, type MemberMessage, type MessageContact } from "./api";
import type { Language } from "./i18n";

const staffRoles = ["SUPER_ADMIN", "CHURCH_ADMIN", "PASTOR", "LEADER"];
const roleLabel: Record<string, string> = { SUPER_ADMIN:"Super admin", CHURCH_ADMIN:"Church admin", PASTOR:"Pastor", LEADER:"Leader", MEMBER:"Member" };

export function CareScreen({ role, userId, language }: { role:string; userId:number; language:Language }) {
  const staff = staffRoles.includes(role);
  const today = new Date().toISOString().slice(0,10);
  const [absences,setAbsences] = useState<Absence[]>([]);
  const [contacts,setContacts] = useState<MessageContact[]>([]);
  const [messages,setMessages] = useState<MemberMessage[]>([]);
  const [contact,setContact] = useState<MessageContact|null>(null);
  const [date,setDate] = useState(today);
  const [reason,setReason] = useState("");
  const [text,setText] = useState("");
  const [file,setFile] = useState<File>();
  const [error,setError] = useState("");
  const [saving,setSaving] = useState(false);

  async function load() {
    setError("");
    try {
      const [absenceData,contactData] = await Promise.all([staff ? api.absences() : api.absencesMine(),api.messageContacts()]);
      setAbsences(absenceData); setContacts(contactData);
    } catch (value) { setError(value instanceof Error ? value.message : "Unable to load member care."); }
  }
  useEffect(()=>{ void load(); },[role]);

  async function chooseContact(value:MessageContact) {
    setContact(value); setError("");
    try { setMessages(await api.memberConversation(value.id)); }
    catch (error) { setError(error instanceof Error ? error.message : "Unable to load conversation."); }
  }
  async function submitAbsence(event:React.FormEvent) {
    event.preventDefault(); setSaving(true); setError("");
    try { await api.submitAbsence(date,reason); setReason(""); await load(); }
    catch (error) { setError(error instanceof Error ? error.message : "Unable to send reason."); }
    finally { setSaving(false); }
  }
  async function sendMessage(event:React.FormEvent) {
    event.preventDefault(); if (!contact) return; setSaving(true); setError("");
    try { await api.sendMemberMessage(contact.id,text,file); setText(""); setFile(undefined); setMessages(await api.memberConversation(contact.id)); }
    catch (error) { setError(error instanceof Error ? error.message : "Unable to send message."); }
    finally { setSaving(false); }
  }
  async function acknowledge(id:number) {
    try { await api.acknowledgeAbsence(id); await load(); }
    catch (error) { setError(error instanceof Error ? error.message : "Unable to acknowledge reason."); }
  }
  const initials=(name:string)=>name.split(" ").slice(0,2).map(part=>part[0]).join("").toUpperCase();
  const formatDate=(value:string)=>new Date(`${value}T12:00:00`).toLocaleDateString(language,{day:"2-digit",month:"short",year:"numeric"});

  return <>
    <header className="page-heading"><div><span className="eyebrow">Member care</span><h1>Stay connected</h1><p>Talk privately with church members and communicate an absence.</p></div></header>
    {error&&<div className="form-error" role="alert">{error}</div>}
    <section className="care-layout">
      <article className="card care-panel">
        <header className="care-panel-header"><span className="care-panel-icon"><CalendarX2/></span><div><h2>{staff?"Absence motivations":"Cannot attend?"}</h2><p>{staff?"Review reasons shared by church members.":"Share a brief reason so your leaders can care for you and follow up when needed."}</p></div></header>
        {!staff&&<form className="absence-form" onSubmit={submitAbsence}><label>Date<input type="date" required value={date} onChange={event=>setDate(event.target.value)}/></label><label>Reason<textarea required maxLength={2000} rows={4} value={reason} onChange={event=>setReason(event.target.value)} placeholder="Tell your church why you cannot attend…"/></label><button className="primary" disabled={saving}><Send size={16}/>{saving?"Sending…":"Send reason"}</button></form>}
        <section className="absence-history"><h3>{staff?"Absence motivations":"Your submitted reasons"}</h3>{absences.length===0?<div className="care-empty"><Inbox/><span>No absence reasons yet</span></div>:<div className="absence-list">{absences.map(item=><article className="absence-item" key={item.id}><div className="absence-item-header"><b>{staff?item.memberName:formatDate(item.absenceDate)}</b>{staff&&<time>{formatDate(item.absenceDate)}</time>}</div><p>{item.reason}</p><footer className="absence-item-footer"><span className={`care-status ${item.status.toLowerCase()}`}>{item.status==="ACKNOWLEDGED"?"Acknowledged":"Submitted"}</span>{staff&&item.status==="SUBMITTED"&&<button className="text-button" onClick={()=>void acknowledge(item.id)}>Acknowledge</button>}</footer></article>)}</div>}</section>
      </article>
      <article className="card care-panel">
        <header className="care-panel-header"><span className="care-panel-icon green"><MessageSquare/></span><div><h2>Private messages</h2><p>Select a church leader to start a private conversation.</p></div></header>
        <div className="care-messaging"><aside className="care-contacts"><h3>Church contacts</h3>{contacts.length===0?<div className="care-empty"><span>No contacts available</span></div>:contacts.map(item=><button className={`care-contact ${contact?.id===item.id?"active":""}`} key={item.id} onClick={()=>void chooseContact(item)}><span className="contact-avatar">{initials(item.name)}</span><span><b>{item.name}</b><small>{roleLabel[item.role]||item.role}</small></span></button>)}</aside>
          <section className="care-conversation">{!contact?<div className="conversation-placeholder"><div><span><MessageSquare/></span><h3>Choose a conversation</h3><p>Select a church leader to start a private conversation.</p></div></div>:<><header className="conversation-header"><h3>{contact.name}</h3><small>{roleLabel[contact.role]||contact.role}</small></header><div className="chat-messages">{messages.length===0?<div className="care-empty"><Inbox/><b>No messages yet</b><span>Start the conversation with a message or file.</span></div>:messages.map(message=><article className={`chat-message ${message.senderId===userId?"mine":""}`} key={message.id}><b>{message.senderName}</b><p>{message.message}</p>{message.attachmentName&&<a href={api.memberMessageAttachment(message.id)} download><Paperclip/>{message.attachmentName}</a>}</article>)}</div><form className="care-composer" onSubmit={sendMessage}><textarea rows={3} value={text} onChange={event=>setText(event.target.value)} placeholder="Write a message…"/><div className="care-composer-actions"><label className="secondary"><Paperclip/><span>{file?.name||"Attach file"}</span><input type="file" onChange={event=>setFile(event.target.files?.[0])}/></label><button className="primary" disabled={saving||(!text.trim()&&!file)}><Send size={16}/>Send</button></div></form></>}</section>
        </div>
      </article>
    </section>
  </>;
}
