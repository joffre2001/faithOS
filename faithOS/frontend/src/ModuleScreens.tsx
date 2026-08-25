import { useEffect, useState } from "react";
import {
  Bell,
  CalendarDays,
  Check,
  CircleDollarSign,
  Download,
  FileText,
  HeartHandshake,
  MapPin,
  MessageSquare,
  Paperclip,
  Pencil,
  Plus,
  Trash2,
  Upload,
  Users,
  X,
} from "lucide-react";
import {
  api,
  type Absence,
  type AttendanceReport,
  type AttendanceSession,
  type ChurchEvent,
  type ChurchFile,
  type Contribution,
  type Expense,
  type FinancialReport,
  type MemberMessage,
  type MessageContact,
  type Ministry,
  type MinistryMessage,
  type Notification,
} from "./api";

function StateMessage({
  loading,
  error,
  empty,
  retry,
}: {
  loading: boolean;
  error: string;
  empty: string;
  retry: () => void;
}) {
  if (loading) return <div className="data-message">Loading…</div>;
  if (error)
    return (
      <div className="data-message error-state">
        <b>Something went wrong</b>
        <span>{error}</span>
        <button onClick={retry}>Try again</button>
      </div>
    );
  return (
    <div className="data-message">
      <b>{empty}</b>
      <span>Create the first record to get started.</span>
    </div>
  );
}

export function MinistriesScreen({
  role,
  userId,
}: {
  role: string;
  userId: number;
}) {
  const canAdmin = ["SUPER_ADMIN", "CHURCH_ADMIN", "PASTOR"].includes(role);
  const canManage = canAdmin || role === "LEADER";
  const [items, setItems] = useState<Ministry[]>([]);
  const [leaders, setLeaders] = useState<
    { id: number; firstName: string; lastName: string; email: string }[]
  >([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [managing, setManaging] = useState<Ministry | null>(null);
  const [memberIds, setMemberIds] = useState<number[]>([]);
  const [form, setForm] = useState({
    name: "",
    description: "",
    leaderId: undefined as number | undefined,
    memberIds: [] as number[],
    active: true,
  });
  const [chat, setChat] = useState<Ministry | null>(null);
  const [messages, setMessages] = useState<MinistryMessage[]>([]);
  const [chatText, setChatText] = useState("");
  const [chatFile, setChatFile] = useState<File | undefined>();
  const [chatLoading, setChatLoading] = useState(false);
  async function load() {
    setLoading(true);
    setError("");
    try {
      setItems(await api.ministries());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load ministries.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    void load();
    if (canManage)
      api
        .users(0, 100)
        .then((page) => setLeaders(page.content.filter((user) => user.active)))
        .catch(() => setLeaders([]));
  }, []);
  function newMinistry() {
    setForm({
      name: "",
      description: "",
      leaderId: undefined,
      memberIds: [],
      active: true,
    });
    setError("");
    setOpen(true);
  }
  async function create(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await api.createMinistry(form);
      setOpen(false);
      setForm({
        name: "",
        description: "",
        leaderId: undefined,
        memberIds: [],
        active: true,
      });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to save ministry.");
    } finally {
      setSaving(false);
    }
  }
  function manage(item: Ministry) {
    setManaging(item);
    setForm({
      name: item.name,
      description: item.description ?? "",
      leaderId: item.leaderId,
      memberIds: item.members.map((member) => member.id),
      active: item.active,
    });
    setMemberIds(item.members.map((member) => member.id));
    setError("");
  }
  function closeManager() {
    setManaging(null);
    setError("");
  }
  async function assign(e: React.FormEvent) {
    e.preventDefault();
    if (!managing) return;
    setSaving(true);
    setError("");
    try {
      if (canAdmin)
        await api.updateMinistry(managing.id, { ...form, memberIds });
      else await api.updateMinistryMembers(managing.id, memberIds);
      closeManager();
      await load();
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Unable to update ministry."
      );
    } finally {
      setSaving(false);
    }
  }
  async function openChat(item: Ministry) {
    setChat(item);
    setChatLoading(true);
    setError("");
    try {
      setMessages(await api.ministryMessages(item.id));
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to load ministry conversation."
      );
    } finally {
      setChatLoading(false);
    }
  }
  async function sendMessage(e: React.FormEvent) {
    e.preventDefault();
    if (!chat) return;
    setSaving(true);
    setError("");
    try {
      await api.sendMinistryMessage(chat.id, chatText, chatFile);
      setChatText("");
      setChatFile(undefined);
      setMessages(await api.ministryMessages(chat.id));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to send message.");
    } finally {
      setSaving(false);
    }
  }
  async function remove(id: number) {
    if (!window.confirm("Delete this ministry? This action cannot be undone."))
      return;
    try {
      await api.deleteMinistry(id);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to delete ministry.");
    }
  }
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Ministries</span>
          <h1>Teams with purpose</h1>
          <p>
            Equip leaders, care for volunteers, and grow healthy ministries.
          </p>
        </div>
        {canAdmin && (
          <button className="primary" onClick={newMinistry}>
            <Plus size={18} />
            New ministry
          </button>
        )}
      </header>
      {!loading && !error && items.length > 0 ? (
        <section className="module-grid">
          {items.map((item) => {
            const leads = item.leaderId === userId;
            const belongs = item.members.some((member) => member.id === userId);
            return (
              <article className="card module-card" key={item.id}>
                <div className="module-icon">
                  <HeartHandshake />
                </div>
                <div className="module-copy">
                  <div className="module-title">
                    <h3>{item.name}</h3>
                    <span className={`pill ${item.active ? "" : "muted"}`}>
                      {item.active ? "Active" : "Inactive"}
                    </span>
                  </div>
                  <p>{item.description || "No description yet."}</p>
                  <div className="module-meta">
                    <Users size={15} />
                    {item.leaderName || "Leader not assigned"} ·{" "}
                    {item.members.length} members
                  </div>
                  <div className="ministry-actions">
                    {(canAdmin || leads) && (
                      <button
                        className="text-button"
                        onClick={() => manage(item)}
                      >
                        <Pencil size={14} />{" "}
                        {canAdmin ? "Edit ministry" : "Manage members"}
                      </button>
                    )}
                    {(canAdmin || leads || belongs) && (
                      <button
                        className="text-button"
                        onClick={() => openChat(item)}
                      >
                        <MessageSquare size={14} /> Conversation
                      </button>
                    )}
                  </div>
                </div>
                {canAdmin && (
                  <button
                    className="danger-icon"
                    onClick={() => remove(item.id)}
                    aria-label="Delete ministry"
                  >
                    <Trash2 />
                  </button>
                )}
              </article>
            );
          })}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No ministries yet"
          retry={load}
        />
      )}
      {open && (
        <div className="modal-backdrop" onMouseDown={() => setOpen(false)}>
          <form
            className="person-modal"
            onSubmit={create}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Ministry</span>
                <h2>Create a ministry</h2>
              </div>
              <button type="button" onClick={() => setOpen(false)}>
                <X />
              </button>
            </div>
            <div className="form-grid">
              <label className="span-2">
                Name
                <input
                  required
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                />
              </label>
              <label className="span-2">
                Leader
                <select
                  value={form.leaderId ?? ""}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      leaderId: e.target.value
                        ? Number(e.target.value)
                        : undefined,
                    })
                  }
                >
                  <option value="">No leader assigned</option>
                  {leaders.map((leader) => (
                    <option key={leader.id} value={leader.id}>
                      {leader.firstName} {leader.lastName}
                    </option>
                  ))}
                </select>
              </label>
              <label className="span-2">
                Description
                <textarea
                  rows={4}
                  value={form.description}
                  onChange={(e) =>
                    setForm({ ...form, description: e.target.value })
                  }
                />
              </label>
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button
                type="button"
                className="secondary"
                onClick={() => setOpen(false)}
              >
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving ? "Saving…" : "Create ministry"}
              </button>
            </div>
          </form>
        </div>
      )}
      {managing && (
        <div className="modal-backdrop" onMouseDown={closeManager}>
          <form
            className="person-modal"
            onSubmit={assign}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Ministry</span>
                <h2>
                  {canAdmin
                    ? `Edit ${managing.name}`
                    : `Manage ${managing.name} members`}
                </h2>
              </div>
              <button type="button" onClick={closeManager}>
                <X />
              </button>
            </div>
            {canAdmin && (
              <div className="form-grid">
                <label className="span-2">
                  Name
                  <input
                    required
                    value={form.name}
                    onChange={(e) => setForm({ ...form, name: e.target.value })}
                  />
                </label>
                <label className="span-2">
                  Leader
                  <select
                    value={form.leaderId ?? ""}
                    onChange={(e) =>
                      setForm({
                        ...form,
                        leaderId: e.target.value
                          ? Number(e.target.value)
                          : undefined,
                      })
                    }
                  >
                    <option value="">No leader assigned</option>
                    {leaders.map((leader) => (
                      <option key={leader.id} value={leader.id}>
                        {leader.firstName} {leader.lastName}
                      </option>
                    ))}
                  </select>
                </label>
                <label className="span-2">
                  Description
                  <textarea
                    rows={3}
                    value={form.description}
                    onChange={(e) =>
                      setForm({ ...form, description: e.target.value })
                    }
                  />
                </label>
                <label className="span-2 inline-check">
                  <input
                    type="checkbox"
                    checked={form.active}
                    onChange={(e) =>
                      setForm({ ...form, active: e.target.checked })
                    }
                  />
                  <span>Active ministry</span>
                </label>
              </div>
            )}
            <h3 className="picker-heading">Ministry members</h3>
            <div className="member-picker">
              {leaders.map((person) => (
                <label key={person.id}>
                  <input
                    type="checkbox"
                    checked={memberIds.includes(person.id)}
                    onChange={(e) =>
                      setMemberIds(
                        e.target.checked
                          ? [...memberIds, person.id]
                          : memberIds.filter((id) => id !== person.id)
                      )
                    }
                  />
                  <span>
                    <b>
                      {person.firstName} {person.lastName}
                    </b>
                    <small>{person.email}</small>
                  </span>
                </label>
              ))}
            </div>
            {leaders.length === 0 && (
              <div className="data-message">
                No active people are available.
              </div>
            )}
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button
                type="button"
                className="secondary"
                onClick={closeManager}
              >
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving
                  ? "Saving…"
                  : canAdmin
                  ? "Save ministry"
                  : "Save members"}
              </button>
            </div>
          </form>
        </div>
      )}
      {chat && (
        <div className="modal-backdrop" onMouseDown={() => setChat(null)}>
          <section
            className="person-modal ministry-chat"
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">{chat.name}</span>
                <h2>Ministry conversation</h2>
              </div>
              <button type="button" onClick={() => setChat(null)}>
                <X />
              </button>
            </div>
            <div className="chat-messages">
              {chatLoading ? (
                <div className="data-message">Loading conversation…</div>
              ) : messages.length === 0 ? (
                <div className="data-message">
                  <b>No messages yet</b>
                  <span>Start the ministry conversation.</span>
                </div>
              ) : (
                messages.map((value) => (
                  <article
                    className={`chat-message ${
                      value.senderId === userId ? "mine" : ""
                    }`}
                    key={value.id}
                  >
                    <div>
                      <b>{value.senderName}</b>
                      <time>{new Date(value.createdAt).toLocaleString()}</time>
                    </div>
                    {value.message && <p>{value.message}</p>}
                    {value.attachmentName && (
                      <a
                        href={api.ministryAttachment(chat.id, value.id)}
                        download
                      >
                        <Paperclip />
                        {value.attachmentName}
                        <small>
                          {value.attachmentSize
                            ? `${(value.attachmentSize / 1024).toFixed(1)} KB`
                            : ""}
                        </small>
                      </a>
                    )}
                  </article>
                ))
              )}
            </div>
            <form className="chat-composer" onSubmit={sendMessage}>
              <textarea
                rows={2}
                placeholder="Write a message…"
                value={chatText}
                onChange={(e) => setChatText(e.target.value)}
              />
              <div>
                <label className="secondary upload-button">
                  <Paperclip />
                  {chatFile ? chatFile.name : "Attach file"}
                  <input
                    type="file"
                    onChange={(e) => setChatFile(e.target.files?.[0])}
                  />
                </label>
                <button
                  className="primary"
                  disabled={saving || (!chatText.trim() && !chatFile)}
                >
                  {saving ? "Sending…" : "Send"}
                </button>
              </div>
              {error && <div className="form-error">{error}</div>}
            </form>
          </section>
        </div>
      )}
    </>
  );
}

export function CalendarScreen({ role }: { role: string }) {
  const canManage = [
    "SUPER_ADMIN",
    "CHURCH_ADMIN",
    "PASTOR",
    "LEADER",
  ].includes(role);
  const [items, setItems] = useState<ChurchEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<ChurchEvent | null>(null);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    title: "",
    description: "",
    startsAt: "",
    endsAt: "",
    location: "",
    category: "Worship",
  });
  const emptyForm = {
    title: "",
    description: "",
    startsAt: "",
    endsAt: "",
    location: "",
    category: "Worship",
  };
  async function load() {
    setLoading(true);
    setError("");
    try {
      setItems(await api.events());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load events.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    void load();
  }, []);
  function newEvent() {
    setEditing(null);
    setForm(emptyForm);
    setError("");
    setOpen(true);
  }
  function editEvent(item: ChurchEvent) {
    setEditing(item);
    setForm({
      title: item.title,
      description: item.description ?? "",
      startsAt: item.startsAt.slice(0, 16),
      endsAt: item.endsAt?.slice(0, 16) ?? "",
      location: item.location ?? "",
      category: item.category ?? "Worship",
    });
    setError("");
    setOpen(true);
  }
  function close() {
    setOpen(false);
    setEditing(null);
    setError("");
  }
  async function save(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    try {
      const payload = { ...form, endsAt: form.endsAt || undefined };
      if (editing) await api.updateEvent(editing.id, payload);
      else await api.createEvent(payload);
      close();
      setForm(emptyForm);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to save event.");
    } finally {
      setSaving(false);
    }
  }
  async function remove(id: number) {
    if (!window.confirm("Delete this event? This action cannot be undone."))
      return;
    try {
      await api.deleteEvent(id);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to delete event.");
    }
  }
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Calendar</span>
          <h1>Your shared rhythm</h1>
          <p>Plan services, gatherings, and every moment in between.</p>
        </div>
        {canManage && (
          <button className="primary" onClick={newEvent}>
            <Plus size={18} />
            New event
          </button>
        )}
      </header>
      {!loading && !error && items.length > 0 ? (
        <section className="card timeline">
          {items.map((item) => {
            const date = new Date(item.startsAt);
            return (
              <article className="timeline-event" key={item.id}>
                <div className="timeline-date">
                  <b>{date.getDate()}</b>
                  <span>
                    {date
                      .toLocaleString(undefined, { month: "short" })
                      .toUpperCase()}
                  </span>
                </div>
                <div className="timeline-copy">
                  <span className="eyebrow">{item.category || "Event"}</span>
                  <h3>{item.title}</h3>
                  <p>
                    {date.toLocaleString(undefined, {
                      weekday: "long",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                    {item.location ? ` · ${item.location}` : ""}
                  </p>
                  {item.description && <small>{item.description}</small>}
                </div>
                {canManage && (
                  <div className="timeline-actions">
                    <button
                      className="file-action"
                      onClick={() => editEvent(item)}
                      aria-label="Edit event"
                    >
                      <Pencil />
                    </button>
                    <button
                      className="danger-icon"
                      onClick={() => remove(item.id)}
                      aria-label="Delete event"
                    >
                      <Trash2 />
                    </button>
                  </div>
                )}
              </article>
            );
          })}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No events scheduled"
          retry={load}
        />
      )}
      {open && (
        <div className="modal-backdrop" onMouseDown={close}>
          <form
            className="person-modal"
            onSubmit={save}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Calendar</span>
                <h2>{editing ? "Edit event" : "Create an event"}</h2>
              </div>
              <button type="button" onClick={close}>
                <X />
              </button>
            </div>
            <div className="form-grid">
              <label className="span-2">
                Event title
                <input
                  required
                  value={form.title}
                  onChange={(e) => setForm({ ...form, title: e.target.value })}
                />
              </label>
              <label>
                Starts
                <input
                  type="datetime-local"
                  required
                  value={form.startsAt}
                  onChange={(e) =>
                    setForm({ ...form, startsAt: e.target.value })
                  }
                />
              </label>
              <label>
                Ends
                <input
                  type="datetime-local"
                  min={form.startsAt || undefined}
                  value={form.endsAt}
                  onChange={(e) => setForm({ ...form, endsAt: e.target.value })}
                />
              </label>
              <label>
                Location
                <input
                  value={form.location}
                  onChange={(e) =>
                    setForm({ ...form, location: e.target.value })
                  }
                />
              </label>
              <label>
                Category
                <select
                  value={form.category}
                  onChange={(e) =>
                    setForm({ ...form, category: e.target.value })
                  }
                >
                  <option>Worship</option>
                  <option>Community</option>
                  <option>Youth</option>
                  <option>Leadership</option>
                </select>
              </label>
              <label className="span-2">
                Description
                <textarea
                  rows={3}
                  value={form.description}
                  onChange={(e) =>
                    setForm({ ...form, description: e.target.value })
                  }
                />
              </label>
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button type="button" className="secondary" onClick={close}>
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving ? "Saving…" : editing ? "Save changes" : "Create event"}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}

export function CareScreen({ role, userId }: { role: string; userId: number }) {
  const staff = ["SUPER_ADMIN", "CHURCH_ADMIN", "PASTOR", "LEADER"].includes(
    role
  );
  const today = new Date().toISOString().slice(0, 10);
  const [absences, setAbsences] = useState<Absence[]>([]),
    [contacts, setContacts] = useState<MessageContact[]>([]),
    [messages, setMessages] = useState<MemberMessage[]>([]);
  const [contact, setContact] = useState<MessageContact | null>(null);
  const [date, setDate] = useState(today),
    [reason, setReason] = useState(""),
    [text, setText] = useState(""),
    [file, setFile] = useState<File | undefined>(),
    [error, setError] = useState(""),
    [saving, setSaving] = useState(false);
  async function load() {
    setError("");
    try {
      const [a, c] = await Promise.all([
        staff ? api.absences() : api.absencesMine(),
        api.messageContacts(),
      ]);
      setAbsences(a);
      setContacts(c);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load member care.");
    }
  }
  useEffect(() => {
    void load();
  }, []);
  async function select(value: MessageContact) {
    setContact(value);
    setMessages(await api.memberConversation(value.id));
  }
  async function submitAbsence(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    try {
      await api.submitAbsence(date, reason);
      setReason("");
      await load();
    } catch (x) {
      setError(x instanceof Error ? x.message : "Unable to send reason.");
    } finally {
      setSaving(false);
    }
  }
  async function send(e: React.FormEvent) {
    e.preventDefault();
    if (!contact) return;
    setSaving(true);
    try {
      await api.sendMemberMessage(contact.id, text, file);
      setText("");
      setFile(undefined);
      setMessages(await api.memberConversation(contact.id));
    } catch (x) {
      setError(x instanceof Error ? x.message : "Unable to send message.");
    } finally {
      setSaving(false);
    }
  }
  async function acknowledge(id: number) {
    await api.acknowledgeAbsence(id);
    await load();
  }
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Member care</span>
          <h1>Stay connected</h1>
          <p>Talk privately with church members and communicate an absence.</p>
        </div>
      </header>
      {error && <div className="form-error">{error}</div>}
      <section className="module-grid">
        <article className="card module-card">
          <div className="module-copy">
            <h3>{staff ? "Absence motivations" : "Cannot attend?"}</h3>
            {!staff && (
              <form onSubmit={submitAbsence}>
                <label>
                  Date
                  <input
                    type="date"
                    required
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                  />
                </label>
                <label>
                  Reason
                  <textarea
                    required
                    maxLength={2000}
                    rows={4}
                    value={reason}
                    onChange={(e) => setReason(e.target.value)}
                    placeholder="Tell your church why you cannot attend…"
                  />
                </label>
                <button className="primary" disabled={saving}>
                  Send reason
                </button>
              </form>
            )}
            <div className="chat-messages">
              {absences.map((a) => (
                <article className="chat-message" key={a.id}>
                  <b>
                    {staff && `${a.memberName} · `}
                    {new Date(`${a.absenceDate}T12:00:00`).toLocaleDateString()}
                  </b>
                  <p>{a.reason}</p>
                  <small>{a.status}</small>
                  {staff && a.status === "SUBMITTED" && (
                    <button
                      className="text-button"
                      onClick={() => acknowledge(a.id)}
                    >
                      Acknowledge
                    </button>
                  )}
                </article>
              ))}
            </div>
          </div>
        </article>
        <article className="card module-card">
          <div className="module-copy">
            <h3>Private messages</h3>
            <div className="member-picker">
              {contacts.map((c) => (
                <button
                  className="text-button"
                  key={c.id}
                  onClick={() => select(c)}
                >
                  <MessageSquare size={15} />
                  {c.name} · {c.role.replaceAll("_", " ")}
                </button>
              ))}
            </div>
            {contact && (
              <>
                <h3>{contact.name}</h3>
                <div className="chat-messages">
                  {messages.map((m) => (
                    <article
                      className={`chat-message ${
                        m.senderId === userId ? "mine" : ""
                      }`}
                      key={m.id}
                    >
                      <b>{m.senderName}</b>
                      <p>{m.message}</p>
                      {m.attachmentName && (
                        <a href={api.memberMessageAttachment(m.id)} download>
                          <Paperclip />
                          {m.attachmentName}
                        </a>
                      )}
                    </article>
                  ))}
                </div>
                <form className="chat-composer" onSubmit={send}>
                  <textarea
                    rows={2}
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    placeholder="Write a message…"
                  />
                  <label className="secondary upload-button">
                    <Paperclip />
                    {file?.name || "Attach file"}
                    <input
                      type="file"
                      onChange={(e) => setFile(e.target.files?.[0])}
                    />
                  </label>
                  <button
                    className="primary"
                    disabled={saving || (!text.trim() && !file)}
                  >
                    Send
                  </button>
                </form>
              </>
            )}
          </div>
        </article>
      </section>
    </>
  );
}

export function AttendanceScreen({ role }: { role: string }) {
  const canManage = [
    "SUPER_ADMIN",
    "CHURCH_ADMIN",
    "PASTOR",
    "LEADER",
  ].includes(role);
  const today = new Date().toISOString().slice(0, 10);
  const yearStart = `${new Date().getFullYear()}-01-01`;
  const [items, setItems] = useState<AttendanceSession[]>([]);
  const [report, setReport] = useState<AttendanceReport | null>(null);
  const [from, setFrom] = useState(yearStart);
  const [to, setTo] = useState(today);
  const [people, setPeople] = useState<
    { id: number; firstName: string; lastName: string; email: string }[]
  >([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<AttendanceSession | null>(null);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    title: "",
    type: "WORSHIP" as "WORSHIP" | "BIBLE_STUDY",
    sessionDate: new Date().toISOString().slice(0, 10),
    opensAt: "07:00",
    onTimeUntil: "08:40",
    closesAt: "",
    attendeeIds: [] as number[],
  });
  const emptyForm = {
    title: "",
    type: "WORSHIP" as "WORSHIP" | "BIBLE_STUDY",
    sessionDate: today,
    opensAt: "07:00",
    onTimeUntil: "08:40",
    closesAt: "",
    attendeeIds: [] as number[],
  };
  async function load() {
    setLoading(true);
    setError("");
    try {
      setItems(await api.attendance());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load attendance.");
    } finally {
      setLoading(false);
    }
  }
  async function loadReport() {
    try {
      setReport(await api.attendanceReport(from, to));
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to load attendance report."
      );
    }
  }
  useEffect(() => {
    void load();
    if (canManage)
      api
        .users(0, 100)
        .then((page) => setPeople(page.content.filter((user) => user.active)))
        .catch(() => setPeople([]));
  }, []);
  useEffect(() => {
    void loadReport();
  }, [from, to]);
  function newAttendance() {
    setEditing(null);
    setForm(emptyForm);
    setError("");
    setOpen(true);
  }
  function editAttendance(item: AttendanceSession) {
    setEditing(item);
    setForm({
      title: item.title,
      type: item.type,
      sessionDate: item.sessionDate,
      opensAt: item.opensAt || "07:00",
      onTimeUntil: item.onTimeUntil || "08:40",
      closesAt: item.closesAt || "",
      attendeeIds: item.attendees.map((person) => person.id),
    });
    setError("");
    setOpen(true);
  }
  function close() {
    setOpen(false);
    setEditing(null);
    setError("");
  }
  async function save(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      if (editing) await api.updateAttendance(editing.id, form);
      else await api.createAttendance(form);
      close();
      setForm(emptyForm);
      await Promise.all([load(), loadReport()]);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Unable to save attendance."
      );
    } finally {
      setSaving(false);
    }
  }
  async function remove(id: number) {
    if (
      !window.confirm(
        "Delete this attendance record? This action cannot be undone."
      )
    )
      return;
    try {
      await api.deleteAttendance(id);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to delete attendance.");
    }
  }
  async function clockIn() {
    setSaving(true);
    setError("");
    try {
      const result = await api.attendanceCheckIn();
      alert(
        `Check-in recorded: ${result.status === "ON_TIME" ? "On time" : "Late"}`
      );
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to check in.");
    } finally {
      setSaving(false);
    }
  }
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Attendance</span>
          <h1>Gathering attendance</h1>
          <p>Record worship and Bible-study participation.</p>
        </div>
        <div className="modal-actions">
          {!canManage && (
            <button className="primary" onClick={clockIn} disabled={saving}>
              <Check size={18} />
              Clock in now
            </button>
          )}
          {canManage && (
            <button className="primary" onClick={newAttendance}>
              <Plus size={18} />
              Record attendance
            </button>
          )}
        </div>
      </header>
      <div className="report-toolbar">
        <label>
          From
          <input
            type="date"
            value={from}
            max={to}
            onChange={(e) => setFrom(e.target.value)}
          />
        </label>
        <label>
          To
          <input
            type="date"
            value={to}
            min={from}
            onChange={(e) => setTo(e.target.value)}
          />
        </label>
      </div>
      {report && (
        <section className="attendance-stats">
          <article className="stat">
            <span>Gatherings</span>
            <strong>{report.totalSessions}</strong>
            <small>
              {report.sessionsByType.WORSHIP} worship ·{" "}
              {report.sessionsByType.BIBLE_STUDY} Bible study
            </small>
          </article>
          <article className="stat">
            <span>Total check-ins</span>
            <strong>{report.totalCheckIns}</strong>
            <small>Across the selected period</small>
          </article>
          <article className="stat">
            <span>Unique people</span>
            <strong>{report.uniqueAttendees}</strong>
            <small>Distinct attendees</small>
          </article>
          <article className="stat">
            <span>Average attendance</span>
            <strong>{report.averageAttendance}</strong>
            <small>People per gathering</small>
          </article>
        </section>
      )}
      {!loading && !error && items.length > 0 ? (
        <section className="card attendance-list">
          {items.map((item) => (
            <article className="attendance-row" key={item.id}>
              <div className="timeline-date">
                <b>{new Date(`${item.sessionDate}T12:00:00`).getDate()}</b>
                <span>
                  {new Date(`${item.sessionDate}T12:00:00`)
                    .toLocaleString(undefined, { month: "short" })
                    .toUpperCase()}
                </span>
              </div>
              <div>
                <span className="eyebrow">
                  {item.type === "BIBLE_STUDY" ? "Bible study" : "Worship"}
                </span>
                <h3>{item.title}</h3>
                <small>{item.attendees.length} people attended</small>
              </div>
              <div className="attendance-names">
                {item.attendees.slice(0, 3).map((person) => (
                  <span key={person.id}>
                    {person.firstName} {person.lastName}
                  </span>
                ))}
                {item.attendees.length > 3 && (
                  <span>+{item.attendees.length - 3} more</span>
                )}
              </div>
              {canManage && (
                <div className="timeline-actions">
                  <button
                    className="file-action"
                    onClick={() => editAttendance(item)}
                    aria-label="Edit attendance session"
                  >
                    <Pencil />
                  </button>
                  <button
                    className="danger-icon"
                    onClick={() => remove(item.id)}
                    aria-label="Delete attendance session"
                  >
                    <Trash2 />
                  </button>
                </div>
              )}
            </article>
          ))}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No attendance recorded"
          retry={load}
        />
      )}
      {open && (
        <div className="modal-backdrop" onMouseDown={close}>
          <form
            className="person-modal"
            onSubmit={save}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Attendance</span>
                <h2>{editing ? "Edit gathering" : "Record a gathering"}</h2>
              </div>
              <button type="button" onClick={close}>
                <X />
              </button>
            </div>
            <div className="form-grid">
              <label className="span-2">
                Title
                <input
                  required
                  value={form.title}
                  onChange={(e) => setForm({ ...form, title: e.target.value })}
                />
              </label>
              <label>
                Gathering type
                <select
                  value={form.type}
                  onChange={(e) =>
                    setForm({
                      ...form,
                      type: e.target.value as "WORSHIP" | "BIBLE_STUDY",
                    })
                  }
                >
                  <option value="WORSHIP">Worship</option>
                  <option value="BIBLE_STUDY">Bible study</option>
                </select>
              </label>
              <label>
                Date
                <input
                  type="date"
                  required
                  value={form.sessionDate}
                  onChange={(e) =>
                    setForm({ ...form, sessionDate: e.target.value })
                  }
                />
              </label>
              <label>
                Check-in opens
                <input type="time" required value={form.opensAt} onChange={(e) => setForm({ ...form, opensAt: e.target.value })} />
              </label>
              <label>
                On-time until
                <input type="time" required value={form.onTimeUntil} onChange={(e) => setForm({ ...form, onTimeUntil: e.target.value })} />
              </label>
              <label>
                Check-in closes (optional)
                <input type="time" value={form.closesAt} onChange={(e) => setForm({ ...form, closesAt: e.target.value })} />
              </label>
            </div>
            <h3 className="picker-heading">People present</h3>
            <div className="member-picker">
              {people.map((person) => (
                <label key={person.id}>
                  <input
                    type="checkbox"
                    checked={form.attendeeIds.includes(person.id)}
                    onChange={(e) =>
                      setForm({
                        ...form,
                        attendeeIds: e.target.checked
                          ? [...form.attendeeIds, person.id]
                          : form.attendeeIds.filter((id) => id !== person.id),
                      })
                    }
                  />
                  <span>
                    <b>
                      {person.firstName} {person.lastName}
                    </b>
                    <small>{person.email}</small>
                  </span>
                </label>
              ))}
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button type="button" className="secondary" onClick={close}>
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving
                  ? "Saving…"
                  : editing
                  ? "Save changes"
                  : "Save attendance"}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}

export function NotificationsScreen({ role }: { role: string }) {
  const canPublish = ["SUPER_ADMIN", "CHURCH_ADMIN", "PASTOR"].includes(role);
  const [items, setItems] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Notification | null>(null);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    title: "",
    message: "",
    type: "ANNOUNCEMENT",
  });
  async function load() {
    setLoading(true);
    setError("");
    try {
      setItems(await api.notifications());
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to load notifications."
      );
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    void load();
  }, []);
  function newNotification() {
    setEditing(null);
    setForm({ title: "", message: "", type: "ANNOUNCEMENT" });
    setError("");
    setOpen(true);
  }
  function editNotification(item: Notification) {
    setEditing(item);
    setForm({ title: item.title, message: item.message, type: item.type });
    setError("");
    setOpen(true);
  }
  function close() {
    setOpen(false);
    setEditing(null);
    setError("");
  }
  async function save(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      if (editing) await api.updateNotification(editing.id, form);
      else await api.createNotification(form);
      close();
      setForm({ title: "", message: "", type: "ANNOUNCEMENT" });
      await load();
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Unable to save notification."
      );
    } finally {
      setSaving(false);
    }
  }
  async function markRead(item: Notification) {
    if (item.read) return;
    try {
      await api.markNotificationRead(item.id);
      setItems((current) =>
        current.map((value) =>
          value.id === item.id ? { ...value, read: true } : value
        )
      );
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to mark notification as read."
      );
    }
  }
  async function remove(id: number) {
    if (
      !window.confirm("Delete this notification? This action cannot be undone.")
    )
      return;
    try {
      await api.deleteNotification(id);
      await load();
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to delete notification."
      );
    }
  }
  const unread = items.filter((item) => !item.read).length;
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Notifications</span>
          <h1>Stay connected</h1>
          <p>
            {unread} unread {unread === 1 ? "update" : "updates"} for your
            church.
          </p>
        </div>
        {canPublish && (
          <button className="primary" onClick={newNotification}>
            <Plus size={18} />
            Publish update
          </button>
        )}
      </header>
      {!loading && !error && items.length > 0 ? (
        <section className="notification-list">
          {items.map((item) => (
            <article
              className={`card notification-card ${item.read ? "" : "unread"}`}
              key={item.id}
              onClick={() => markRead(item)}
            >
              <div className="notification-icon">
                {item.read ? <Check /> : <Bell />}
              </div>
              <div>
                <div className="notification-title">
                  <span className="eyebrow">
                    {item.type.replaceAll("_", " ")}
                  </span>
                  <time>{new Date(item.createdAt).toLocaleString()}</time>
                </div>
                <h3>{item.title}</h3>
                <p>{item.message}</p>
              </div>
              {canPublish && (
                <div className="timeline-actions">
                  <button
                    className="file-action"
                    onClick={(e) => {
                      e.stopPropagation();
                      editNotification(item);
                    }}
                    aria-label="Edit notification"
                  >
                    <Pencil />
                  </button>
                  <button
                    className="danger-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      void remove(item.id);
                    }}
                    aria-label="Delete notification"
                  >
                    <Trash2 />
                  </button>
                </div>
              )}
            </article>
          ))}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No notifications yet"
          retry={load}
        />
      )}
      {open && (
        <div className="modal-backdrop" onMouseDown={close}>
          <form
            className="person-modal"
            onSubmit={save}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Notification</span>
                <h2>{editing ? "Edit update" : "Publish an update"}</h2>
              </div>
              <button type="button" onClick={close}>
                <X />
              </button>
            </div>
            <div className="form-grid">
              <label className="span-2">
                Title
                <input
                  required
                  maxLength={255}
                  value={form.title}
                  onChange={(e) => setForm({ ...form, title: e.target.value })}
                />
              </label>
              <label className="span-2">
                Type
                <select
                  value={form.type}
                  onChange={(e) => setForm({ ...form, type: e.target.value })}
                >
                  <option value="ANNOUNCEMENT">Announcement</option>
                  <option value="REMINDER">Reminder</option>
                  <option value="URGENT">Urgent</option>
                  <option value="CELEBRATION">Celebration</option>
                </select>
              </label>
              <label className="span-2">
                Message
                <textarea
                  required
                  maxLength={2000}
                  rows={5}
                  value={form.message}
                  onChange={(e) =>
                    setForm({ ...form, message: e.target.value })
                  }
                />
              </label>
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button type="button" className="secondary" onClick={close}>
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving ? "Saving…" : editing ? "Save changes" : "Publish"}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}

export function FilesScreen({ role }: { role: string }) {
  const canManage = [
    "SUPER_ADMIN",
    "CHURCH_ADMIN",
    "PASTOR",
    "LEADER",
  ].includes(role);
  const [items, setItems] = useState<ChurchFile[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");
  async function load() {
    setLoading(true);
    setError("");
    try {
      setItems(await api.files());
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load files.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    void load();
  }, []);
  async function upload(file: File | undefined) {
    if (!file) return;
    setUploading(true);
    setError("");
    try {
      await api.uploadFile(file);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to upload file.");
    } finally {
      setUploading(false);
    }
  }
  async function remove(id: number) {
    if (!window.confirm("Delete this file? This action cannot be undone."))
      return;
    try {
      await api.deleteFile(id);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to delete file.");
    }
  }
  const size = (bytes: number) =>
    bytes < 1024
      ? `${bytes} B`
      : bytes < 1048576
      ? `${(bytes / 1024).toFixed(1)} KB`
      : `${(bytes / 1048576).toFixed(1)} MB`;
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Files</span>
          <h1>Shared documents</h1>
          <p>Keep important church files available to your team.</p>
        </div>
        {canManage && (
          <label
            className={`primary upload-button ${uploading ? "disabled" : ""}`}
          >
            <Upload size={18} />
            {uploading ? "Uploading…" : "Upload file"}
            <input
              type="file"
              disabled={uploading}
              onChange={(e) => {
                void upload(e.target.files?.[0]);
                e.target.value = "";
              }}
            />
          </label>
        )}
      </header>
      {!loading && !error && items.length > 0 ? (
        <section className="file-grid">
          {items.map((item) => (
            <article className="card file-card" key={item.id}>
              <div className="file-icon">
                <FileText />
              </div>
              <div className="file-copy">
                <h3>{item.name}</h3>
                <span>
                  {size(item.size)} ·{" "}
                  {new Date(item.createdAt).toLocaleString()}
                </span>
                <small>
                  {item.uploadedBy
                    ? `Uploaded by ${item.uploadedBy}`
                    : "Uploader unavailable"}
                </small>
              </div>
              <a
                className="file-action"
                href={api.fileContent(item.id)}
                download
                title="Download"
              >
                <Download />
              </a>
              {canManage && (
                <button
                  className="danger-icon"
                  onClick={() => remove(item.id)}
                  aria-label="Delete file"
                >
                  <Trash2 />
                </button>
              )}
            </article>
          ))}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No files uploaded"
          retry={load}
        />
      )}
    </>
  );
}

export function ReportsScreen() {
  const today = new Date().toISOString().slice(0, 10);
  const yearStart = `${new Date().getFullYear()}-01-01`;
  const [from, setFrom] = useState(yearStart);
  const [to, setTo] = useState(today);
  const [attendance, setAttendance] = useState<AttendanceReport | null>(null);
  const [finance, setFinance] = useState<FinancialReport | null>(null);
  const [people, setPeople] = useState(0);
  const [activeMinistries, setActiveMinistries] = useState(0);
  const [upcomingEvents, setUpcomingEvents] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  async function load() {
    setLoading(true);
    setError("");
    try {
      const [attendanceData, financeData, userData, ministries, events] =
        await Promise.all([
          api.attendanceReport(from, to),
          api.financialReport(from, to),
          api.users(0, 1),
          api.ministries(),
          api.events(),
        ]);
      setAttendance(attendanceData);
      setFinance(financeData);
      setPeople(userData.totalElements);
      setActiveMinistries(ministries.filter((item) => item.active).length);
      setUpcomingEvents(
        events.filter((item) => new Date(item.startsAt) >= new Date()).length
      );
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load reports.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    void load();
  }, [from, to]);
  const money = (value: number) =>
    Number(value).toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    });
  const categories = finance ? Object.entries(finance.expensesByCategory) : [];
  const maxCategory = Math.max(
    1,
    ...categories.map(([, value]) => Number(value))
  );
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Reports</span>
          <h1>Leadership overview</h1>
          <p>Membership, ministry, attendance, and finances in one place.</p>
        </div>
      </header>
      <div className="report-toolbar">
        <label>
          From
          <input
            type="date"
            value={from}
            max={to}
            onChange={(e) => setFrom(e.target.value)}
          />
        </label>
        <label>
          To
          <input
            type="date"
            value={to}
            min={from}
            onChange={(e) => setTo(e.target.value)}
          />
        </label>
      </div>
      {error && (
        <StateMessage loading={false} error={error} empty="" retry={load} />
      )}{" "}
      {loading && <StateMessage loading empty="" error="" retry={load} />}{" "}
      {!loading && !error && attendance && finance && (
        <>
          <section className="report-kpis">
            <article className="stat feature">
              <div>
                <span>Church family</span>
                <strong>{people}</strong>
                <small>Registered people</small>
              </div>
              <Users />
            </article>
            <article className="stat">
              <span>Attendance</span>
              <strong>{attendance.totalCheckIns}</strong>
              <small>
                {attendance.averageAttendance} average per gathering
              </small>
            </article>
            <article className="stat">
              <span>Active ministries</span>
              <strong>{activeMinistries}</strong>
              <small>Serving teams</small>
            </article>
            <article className="stat">
              <span>Upcoming events</span>
              <strong>{upcomingEvents}</strong>
              <small>On the calendar</small>
            </article>
          </section>
          <section className="report-panels">
            <article className="card">
              <div className="card-title">
                <div>
                  <span className="eyebrow">Attendance</span>
                  <h3>Gathering health</h3>
                </div>
              </div>
              <div className="report-detail">
                <div>
                  <b>{attendance.sessionsByType.WORSHIP}</b>
                  <span>Worship services</span>
                </div>
                <div>
                  <b>{attendance.sessionsByType.BIBLE_STUDY}</b>
                  <span>Bible studies</span>
                </div>
                <div>
                  <b>{attendance.uniqueAttendees}</b>
                  <span>Unique people</span>
                </div>
              </div>
            </article>
            <article className="card">
              <div className="card-title">
                <div>
                  <span className="eyebrow">Finance</span>
                  <h3>Stewardship</h3>
                </div>
              </div>
              <div className="report-detail">
                <div>
                  <b>{money(finance.income)}</b>
                  <span>Income</span>
                </div>
                <div>
                  <b className="negative">{money(finance.expenses)}</b>
                  <span>Expenses</span>
                </div>
                <div>
                  <b>{money(finance.netBalance)}</b>
                  <span>Net balance</span>
                </div>
              </div>
            </article>
          </section>
          <section className="card category-report">
            <div className="card-title">
              <div>
                <span className="eyebrow">Expense categories</span>
                <h3>Where resources went</h3>
              </div>
            </div>
            {categories.length === 0 ? (
              <div className="dashboard-empty">
                <CircleDollarSign />
                <b>No expenses in this period</b>
              </div>
            ) : (
              categories.map(([name, value]) => (
                <div className="category-row" key={name}>
                  <span>{name}</span>
                  <div>
                    <i
                      style={{
                        width: `${(Number(value) / maxCategory) * 100}%`,
                      }}
                    />
                  </div>
                  <b>{money(value)}</b>
                </div>
              ))
            )}
          </section>
        </>
      )}
    </>
  );
}

export function GivingScreen() {
  const today = new Date().toISOString().slice(0, 10);
  const monthStart = today.slice(0, 8) + "01";
  const [items, setItems] = useState<Contribution[]>([]);
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [report, setReport] = useState<FinancialReport | null>(null);
  const [from, setFrom] = useState(monthStart);
  const [to, setTo] = useState(today);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Contribution | null>(null);
  const [expenseOpen, setExpenseOpen] = useState(false);
  const [editingExpense, setEditingExpense] = useState<Expense | null>(null);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    donorName: "",
    amount: 0,
    contributionDate: today,
    type: "Tithe",
    method: "PIX",
    notes: "",
  });
  const emptyContribution = {
    donorName: "",
    amount: 0,
    contributionDate: today,
    type: "Tithe",
    method: "PIX",
    notes: "",
  };
  const [expenseForm, setExpenseForm] = useState({
    description: "",
    amount: 0,
    expenseDate: today,
    category: "Operations",
    payee: "",
    notes: "",
  });
  const emptyExpense = {
    description: "",
    amount: 0,
    expenseDate: today,
    category: "Operations",
    payee: "",
    notes: "",
  };
  async function load() {
    setLoading(true);
    setError("");
    try {
      const [income, outgoing] = await Promise.all([
        api.contributions(),
        api.expenses(),
      ]);
      setItems(income);
      setExpenses(outgoing);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load finances.");
    } finally {
      setLoading(false);
    }
  }
  async function loadReport() {
    try {
      setReport(await api.financialReport(from, to));
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to load financial report."
      );
    }
  }
  useEffect(() => {
    void load();
  }, []);
  useEffect(() => {
    void loadReport();
  }, [from, to]);
  function newContribution() {
    setEditing(null);
    setForm(emptyContribution);
    setError("");
    setOpen(true);
  }
  function editContribution(item: Contribution) {
    setEditing(item);
    setForm({
      donorName: item.donorName ?? "",
      amount: Number(item.amount),
      contributionDate: item.contributionDate,
      type: item.type,
      method: item.method ?? "PIX",
      notes: item.notes ?? "",
    });
    setError("");
    setOpen(true);
  }
  function closeContribution() {
    setOpen(false);
    setEditing(null);
    setError("");
  }
  async function saveContribution(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    try {
      if (editing) await api.updateContribution(editing.id, form);
      else await api.createContribution(form);
      closeContribution();
      setForm(emptyContribution);
      await Promise.all([load(), loadReport()]);
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Unable to save contribution."
      );
    } finally {
      setSaving(false);
    }
  }
  function newExpense() {
    setEditingExpense(null);
    setExpenseForm(emptyExpense);
    setError("");
    setExpenseOpen(true);
  }
  function editExpense(item: Expense) {
    setEditingExpense(item);
    setExpenseForm({
      description: item.description,
      amount: Number(item.amount),
      expenseDate: item.expenseDate,
      category: item.category,
      payee: item.payee ?? "",
      notes: item.notes ?? "",
    });
    setError("");
    setExpenseOpen(true);
  }
  function closeExpense() {
    setExpenseOpen(false);
    setEditingExpense(null);
    setError("");
  }
  async function saveExpense(e: React.FormEvent) {
    e.preventDefault();
    setSaving(true);
    try {
      if (editingExpense)
        await api.updateExpense(editingExpense.id, expenseForm);
      else await api.createExpense(expenseForm);
      closeExpense();
      setExpenseForm(emptyExpense);
      await Promise.all([load(), loadReport()]);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to save expense.");
    } finally {
      setSaving(false);
    }
  }
  async function remove(id: number) {
    if (
      !window.confirm("Delete this contribution? This action cannot be undone.")
    )
      return;
    try {
      await api.deleteContribution(id);
      await Promise.all([load(), loadReport()]);
    } catch (e) {
      setError(
        e instanceof Error ? e.message : "Unable to delete contribution."
      );
    }
  }
  async function removeExpense(id: number) {
    if (!window.confirm("Delete this expense? This action cannot be undone."))
      return;
    try {
      await api.deleteExpense(id);
      await Promise.all([load(), loadReport()]);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to delete expense.");
    }
  }
  const money = (value: number) =>
    Number(value).toLocaleString("pt-BR", {
      style: "currency",
      currency: "BRL",
    });
  return (
    <>
      <header className="page-heading">
        <div>
          <span className="eyebrow">Finance</span>
          <h1>Stewardship at a glance</h1>
          <p>Track generosity, expenses, and your church’s balance.</p>
        </div>
        <div className="page-actions">
          <button className="secondary" onClick={newExpense}>
            <Plus size={18} />
            Record expense
          </button>
          <button className="primary" onClick={newContribution}>
            <Plus size={18} />
            Record contribution
          </button>
        </div>
      </header>
      <div className="report-toolbar">
        <label>
          From
          <input
            type="date"
            value={from}
            max={to}
            onChange={(e) => setFrom(e.target.value)}
          />
        </label>
        <label>
          To
          <input
            type="date"
            value={to}
            min={from}
            onChange={(e) => setTo(e.target.value)}
          />
        </label>
      </div>
      {report && (
        <section className="finance-stats">
          <article className="stat feature">
            <div>
              <span>Income</span>
              <strong>{money(report.income)}</strong>
              <small>
                {Object.keys(report.incomeByType).length} contribution types
              </small>
            </div>
            <CircleDollarSign size={34} />
          </article>
          <article className="stat">
            <span>Expenses</span>
            <strong>{money(report.expenses)}</strong>
            <small>
              {Object.keys(report.expensesByCategory).length} categories
            </small>
          </article>
          <article className="stat">
            <span>Net balance</span>
            <strong className={report.netBalance < 0 ? "negative" : ""}>
              {money(report.netBalance)}
            </strong>
            <small>For the selected period</small>
          </article>
        </section>
      )}
      <h2 className="section-heading">Contributions</h2>
      {!loading && !error && items.length > 0 ? (
        <section className="card contribution-list">
          <div className="contribution-head">
            <span>Donor</span>
            <span>Date</span>
            <span>Type</span>
            <span>Method</span>
            <span>Amount</span>
            <span />
          </div>
          {items.map((item) => (
            <div className="contribution-row" key={item.id}>
              <b>{item.donorName || "Anonymous"}</b>
              <span>
                {new Date(
                  `${item.contributionDate}T12:00:00`
                ).toLocaleDateString()}
              </span>
              <span>{item.type}</span>
              <span>{item.method || "—"}</span>
              <strong>{money(item.amount)}</strong>
              <div className="timeline-actions">
                <button
                  className="file-action"
                  onClick={() => editContribution(item)}
                  aria-label="Edit contribution"
                >
                  <Pencil />
                </button>
                <button
                  className="danger-icon"
                  onClick={() => remove(item.id)}
                  aria-label="Delete contribution"
                >
                  <Trash2 />
                </button>
              </div>
            </div>
          ))}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No contributions recorded"
          retry={load}
        />
      )}
      <h2 className="section-heading">Expenses</h2>
      {!loading && !error && expenses.length > 0 ? (
        <section className="card contribution-list">
          <div className="contribution-head">
            <span>Description</span>
            <span>Date</span>
            <span>Category</span>
            <span>Payee</span>
            <span>Amount</span>
            <span />
          </div>
          {expenses.map((item) => (
            <div className="contribution-row" key={item.id}>
              <b>{item.description}</b>
              <span>
                {new Date(`${item.expenseDate}T12:00:00`).toLocaleDateString()}
              </span>
              <span>{item.category}</span>
              <span>{item.payee || "—"}</span>
              <strong className="negative">-{money(item.amount)}</strong>
              <div className="timeline-actions">
                <button
                  className="file-action"
                  onClick={() => editExpense(item)}
                  aria-label="Edit expense"
                >
                  <Pencil />
                </button>
                <button
                  className="danger-icon"
                  onClick={() => removeExpense(item.id)}
                  aria-label="Delete expense"
                >
                  <Trash2 />
                </button>
              </div>
            </div>
          ))}
        </section>
      ) : (
        <StateMessage
          loading={loading}
          error={error}
          empty="No expenses recorded"
          retry={load}
        />
      )}
      {open && (
        <div className="modal-backdrop" onMouseDown={closeContribution}>
          <form
            className="person-modal"
            onSubmit={saveContribution}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Giving</span>
                <h2>
                  {editing ? "Edit contribution" : "Record a contribution"}
                </h2>
              </div>
              <button type="button" onClick={closeContribution}>
                <X />
              </button>
            </div>
            <div className="form-grid">
              <label className="span-2">
                Donor name
                <input
                  value={form.donorName}
                  onChange={(e) =>
                    setForm({ ...form, donorName: e.target.value })
                  }
                />
              </label>
              <label>
                Amount
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  required
                  value={form.amount || ""}
                  onChange={(e) =>
                    setForm({ ...form, amount: Number(e.target.value) })
                  }
                />
              </label>
              <label>
                Date
                <input
                  type="date"
                  required
                  value={form.contributionDate}
                  onChange={(e) =>
                    setForm({ ...form, contributionDate: e.target.value })
                  }
                />
              </label>
              <label>
                Type
                <select
                  value={form.type}
                  onChange={(e) => setForm({ ...form, type: e.target.value })}
                >
                  <option>Tithe</option>
                  <option>Offering</option>
                  <option>Donation</option>
                  <option>Campaign</option>
                </select>
              </label>
              <label>
                Method
                <select
                  value={form.method}
                  onChange={(e) => setForm({ ...form, method: e.target.value })}
                >
                  <option>PIX</option>
                  <option>Cash</option>
                  <option>Card</option>
                  <option>Transfer</option>
                </select>
              </label>
              <label className="span-2">
                Notes
                <textarea
                  rows={3}
                  value={form.notes}
                  onChange={(e) => setForm({ ...form, notes: e.target.value })}
                />
              </label>
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button
                type="button"
                className="secondary"
                onClick={closeContribution}
              >
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving
                  ? "Saving…"
                  : editing
                  ? "Save changes"
                  : "Save contribution"}
              </button>
            </div>
          </form>
        </div>
      )}
      {expenseOpen && (
        <div className="modal-backdrop" onMouseDown={closeExpense}>
          <form
            className="person-modal"
            onSubmit={saveExpense}
            onMouseDown={(e) => e.stopPropagation()}
          >
            <div className="modal-heading">
              <div>
                <span className="eyebrow">Expense</span>
                <h2>{editingExpense ? "Edit expense" : "Record an expense"}</h2>
              </div>
              <button type="button" onClick={closeExpense}>
                <X />
              </button>
            </div>
            <div className="form-grid">
              <label className="span-2">
                Description
                <input
                  required
                  value={expenseForm.description}
                  onChange={(e) =>
                    setExpenseForm({
                      ...expenseForm,
                      description: e.target.value,
                    })
                  }
                />
              </label>
              <label>
                Amount
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  required
                  value={expenseForm.amount || ""}
                  onChange={(e) =>
                    setExpenseForm({
                      ...expenseForm,
                      amount: Number(e.target.value),
                    })
                  }
                />
              </label>
              <label>
                Date
                <input
                  type="date"
                  required
                  value={expenseForm.expenseDate}
                  onChange={(e) =>
                    setExpenseForm({
                      ...expenseForm,
                      expenseDate: e.target.value,
                    })
                  }
                />
              </label>
              <label>
                Category
                <select
                  value={expenseForm.category}
                  onChange={(e) =>
                    setExpenseForm({ ...expenseForm, category: e.target.value })
                  }
                >
                  <option>Operations</option>
                  <option>Ministry</option>
                  <option>Facilities</option>
                  <option>Missions</option>
                  <option>Payroll</option>
                  <option>Other</option>
                </select>
              </label>
              <label>
                Payee
                <input
                  value={expenseForm.payee}
                  onChange={(e) =>
                    setExpenseForm({ ...expenseForm, payee: e.target.value })
                  }
                />
              </label>
              <label className="span-2">
                Notes
                <textarea
                  rows={3}
                  value={expenseForm.notes}
                  onChange={(e) =>
                    setExpenseForm({ ...expenseForm, notes: e.target.value })
                  }
                />
              </label>
            </div>
            {error && <div className="form-error">{error}</div>}
            <div className="modal-actions">
              <button
                type="button"
                className="secondary"
                onClick={closeExpense}
              >
                Cancel
              </button>
              <button className="primary" disabled={saving}>
                {saving
                  ? "Saving…"
                  : editingExpense
                  ? "Save changes"
                  : "Save expense"}
              </button>
            </div>
          </form>
        </div>
      )}
    </>
  );
}
