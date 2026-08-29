import { useEffect, useMemo, useState } from "react";
import {
  Activity,
  Building2,
  CheckCircle2,
  ChevronRight,
  Church,
  ClipboardList,
  LogOut,
  Menu,
  Search,
  ShieldCheck,
  UserCog,
  Users,
  X,
} from "lucide-react";
import {
  api,
  type ChurchPayload,
  type Session,
  type SuperAdminAudit,
  type SuperAdminChurch,
  type SuperAdminOverview,
  type SuperAdminUser,
} from "./api";
import "./super-admin.css";

type View = "overview" | "churches" | "audit";
type Action =
  | { kind: "status"; church: SuperAdminChurch }
  | { kind: "administrator"; church: SuperAdminChurch }
  | { kind: "create" }
  | { kind: "members"; church: SuperAdminChurch }
  | null;

const emptyChurch: ChurchPayload = {
  name: "",
  email: "",
  phone: "",
  address: "",
  cnpj: "",
  principalPastor: "",
};

export function SuperAdminApp({ session, onLogout }: { session: Session; onLogout: () => void }) {
  const [view, setView] = useState<View>("overview");
  const [overview, setOverview] = useState<SuperAdminOverview | null>(null);
  const [churches, setChurches] = useState<SuperAdminChurch[]>([]);
  const [audits, setAudits] = useState<SuperAdminAudit[]>([]);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [menu, setMenu] = useState(false);
  const [action, setAction] = useState<Action>(null);
  const [users, setUsers] = useState<SuperAdminUser[]>([]);
  const [selectedUser, setSelectedUser] = useState("");
  const [reason, setReason] = useState("");
  const [churchForm, setChurchForm] = useState<ChurchPayload>(emptyChurch);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState<string | null>(null);
  const [notice, setNotice] = useState("");

  async function load() {
    setLoading(true);
    setError("");
    try {
      const [summary, churchList, auditList] = await Promise.all([
        api.superAdminOverview(),
        api.superAdminChurches(),
        api.superAdminAuditLog(),
      ]);
      setOverview(summary);
      setChurches(churchList);
      setAudits(auditList);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Unable to load the platform console.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { void load(); }, []);

  const visibleChurches = useMemo(() => {
    const needle = query.trim().toLowerCase();
    if (!needle) return churches;
    return churches.filter((church) =>
      [church.name, church.email, church.cnpj, church.administratorName]
        .filter(Boolean).some((value) => value!.toLowerCase().includes(needle))
    );
  }, [churches, query]);

  async function openAdministrator(church: SuperAdminChurch) {
    setError("");
    try {
      const candidates = await api.superAdminChurchUsers(church.id);
      setUsers(candidates);
      setSelectedUser(church.administratorId?.toString() ?? "");
      setReason("");
      setAction({ kind: "administrator", church });
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Unable to load church users.");
    }
  }

  async function openMembers(church: SuperAdminChurch) {
    setError("");
    setNotice("");
    try {
      setUsers(await api.superAdminChurchUsers(church.id));
      setAction({ kind: "members", church });
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Unable to load church members.");
    }
  }

  async function uploadLogo(church: SuperAdminChurch, file: File | undefined) {
    if (!file) return;
    setUploading(`church-${church.id}`);
    setError("");
    try {
      await api.uploadChurchLogoFor(church.id, file);
      setNotice(`${church.name} logo updated successfully.`);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Unable to upload church logo.");
    } finally { setUploading(null); }
  }

  async function uploadMemberPicture(user: SuperAdminUser, file: File | undefined) {
    if (!file) return;
    setUploading(`user-${user.id}`);
    setError("");
    try {
      await api.uploadProfilePicture(user.id, file);
      setNotice(`${user.fullName}'s profile picture was updated.`);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Unable to upload profile picture.");
    } finally { setUploading(null); }
  }

  async function submitAction(event: React.FormEvent) {
    event.preventDefault();
    if (!action) return;
    setSaving(true);
    setError("");
    try {
      if (action.kind === "status") {
        await api.setSuperAdminChurchStatus(action.church.id, !action.church.active, reason);
      } else if (action.kind === "administrator") {
        await api.assignSuperAdminChurchAdministrator(
          action.church.id, Number(selectedUser), reason
        );
      } else if (action.kind === "create") {
        await api.createChurch(churchForm);
      } else {
        return;
      }
      setAction(null);
      setReason("");
      setChurchForm(emptyChurch);
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "The action could not be completed.");
    } finally {
      setSaving(false);
    }
  }

  function navigate(next: View) { setView(next); setMenu(false); }

  return (
    <div className="sa-shell">
      {menu && <button className="sa-scrim" aria-label="Close menu" onClick={() => setMenu(false)} />}
      <aside className={`sa-sidebar ${menu ? "open" : ""}`}>
        <div className="sa-brand"><span><ShieldCheck /></span><div><b>faithOS</b><small>Platform console</small></div></div>
        <div className="sa-scope"><Activity size={16}/><span>System-wide access</span></div>
        <nav aria-label="Platform navigation">
          <button className={view === "overview" ? "active" : ""} onClick={() => navigate("overview")}><Activity/>Overview</button>
          <button className={view === "churches" ? "active" : ""} onClick={() => navigate("churches")}><Church/>Churches</button>
          <button className={view === "audit" ? "active" : ""} onClick={() => navigate("audit")}><ClipboardList/>Audit log</button>
        </nav>
        <div className="sa-privacy"><ShieldCheck/><div><b>Privacy boundary</b><p>Private messages, CPF, emergency contacts and donation details are not exposed here.</p></div></div>
        <button className="sa-signout" onClick={onLogout}><LogOut/>Sign out</button>
      </aside>

      <main className="sa-main">
        <header className="sa-topbar">
          <button className="sa-menu" onClick={() => setMenu(true)} aria-label="Open menu"><Menu/></button>
          <div><span>PLATFORM ADMINISTRATION</span><b>{view === "overview" ? "Command center" : view === "churches" ? "Church management" : "Security audit"}</b></div>
          <div className="sa-profile"><span>{session.fullName.split(" ").map((part) => part[0]).slice(0, 2).join("")}</span><div><b>{session.fullName}</b><small>Super administrator</small></div></div>
        </header>

        <div className="sa-content">
          {error && <div className="sa-alert">{error}<button onClick={() => setError("")}><X/></button></div>}
          {notice && <div className="sa-alert sa-success">{notice}<button onClick={() => setNotice("")}><X/></button></div>}
          {loading && !overview ? <div className="sa-loading">Loading the platform console…</div> : null}

          {view === "overview" && overview && (
            <>
              <div className="sa-heading"><div><span>LIVE PLATFORM</span><h1>Everything in one clear view.</h1><p>Operational totals across FaithOS without opening private church records.</p></div><button onClick={() => navigate("churches")}>Manage churches <ChevronRight/></button></div>
              <section className="sa-stat-grid">
                <article className="featured"><div><span>Church workspaces</span><strong>{overview.totalChurches}</strong><small>{overview.activeChurches} active</small></div><Building2/></article>
                <article><Users/><span>Platform users</span><strong>{overview.totalUsers}</strong><small>{overview.activeUsers} active accounts</small></article>
                <article><Church/><span>Ministries</span><strong>{overview.totalMinistries}</strong><small>Across all churches</small></article>
                <article><ClipboardList/><span>Audited actions</span><strong>{overview.auditEvents}</strong><small>Protected administrative events</small></article>
              </section>
              <section className="sa-panel sa-recent">
                <div className="sa-panel-title"><div><span>RECENT ACTIVITY</span><h2>Administrative trail</h2></div><button onClick={() => navigate("audit")}>View all</button></div>
                <AuditRows audits={audits.slice(0, 5)} />
              </section>
            </>
          )}

          {view === "churches" && (
            <>
              <div className="sa-heading"><div><span>WORKSPACES</span><h1>Church management</h1><p>Suspend access, appoint administrators, and review operational account totals.</p></div><button onClick={() => { setChurchForm(emptyChurch); setAction({ kind: "create" }); }}>+ Add church</button></div>
              <div className="sa-toolbar"><Search/><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search churches, email, CNPJ or administrator"/><span>{visibleChurches.length} results</span></div>
              <section className="sa-church-grid">
                {visibleChurches.map((church) => (
                  <article key={church.id} className={!church.active ? "suspended" : ""}>
                    <div className="sa-church-head"><span>{church.name.slice(0, 2).toUpperCase()}</span><div><b>{church.name}</b><small>{church.email}</small></div><i className={church.active ? "active" : ""}>{church.active ? "Active" : "Suspended"}</i></div>
                    <dl><div><dt>Members</dt><dd>{church.userCount}</dd></div><div><dt>CNPJ</dt><dd>{church.cnpj}</dd></div><div className="wide"><dt>Church administrator</dt><dd>{church.administratorName || "Not assigned"}</dd><small>{church.administratorEmail}</small></div></dl>
                    <div className="sa-card-actions">
                      <button onClick={() => void openAdministrator(church)}><UserCog/>Assign administrator</button>
                      <button onClick={() => void openMembers(church)}><Users/>Member pictures</button>
                      <label className={uploading === `church-${church.id}` ? "disabled" : ""}>
                        {uploading === `church-${church.id}` ? "Uploading…" : "Upload logo"}
                        <input type="file" accept="image/png,image/jpeg" disabled={uploading !== null} onChange={(event) => void uploadLogo(church, event.target.files?.[0])}/>
                      </label>
                      <button className={church.active ? "warning" : "success"} onClick={() => { setReason(""); setAction({ kind: "status", church }); }}>{church.active ? "Suspend" : "Reactivate"}</button>
                    </div>
                  </article>
                ))}
              </section>
            </>
          )}

          {view === "audit" && (
            <>
              <div className="sa-heading"><div><span>ACCOUNTABILITY</span><h1>Security audit log</h1><p>Every high-impact platform action includes an administrator and a required reason.</p></div></div>
              <section className="sa-panel"><AuditRows audits={audits}/></section>
            </>
          )}
        </div>
      </main>

      {action && (
        <div className="sa-modal-backdrop" onMouseDown={() => setAction(null)}>
          <form className="sa-modal" onSubmit={submitAction} onMouseDown={(event) => event.stopPropagation()}>
            <div className="sa-modal-title"><div><span>PROTECTED ACTION</span><h2>{action.kind === "status" ? `${action.church.active ? "Suspend" : "Reactivate"} church` : action.kind === "administrator" ? "Assign church administrator" : action.kind === "members" ? `${action.church.name} member pictures` : "Create church workspace"}</h2></div><button type="button" onClick={() => setAction(null)}><X/></button></div>
            {action.kind === "status" && <p>This will {action.church.active ? "block all church accounts while preserving their data" : "restore access to active church accounts"} for <b>{action.church.name}</b>.</p>}
            {action.kind === "administrator" && <label>New administrator<select required value={selectedUser} onChange={(event) => setSelectedUser(event.target.value)}><option value="">Select an active church member</option>{users.map((user) => <option key={user.id} value={user.id} disabled={!user.active}>{user.fullName} · {user.role.replaceAll("_", " ")}{!user.active ? " · inactive" : ""}</option>)}</select></label>}
            {action.kind === "members" && <div className="sa-member-pictures">{users.map((user) => <div key={user.id}><span>{user.fullName.split(" ").map((part) => part[0]).slice(0,2).join("")}</span><div><b>{user.fullName}</b><small>{user.email} · {user.role.replaceAll("_", " ")}</small></div><label className={uploading === `user-${user.id}` ? "disabled" : ""}>{uploading === `user-${user.id}` ? "Uploading…" : "Choose picture"}<input type="file" accept="image/png,image/jpeg" disabled={uploading !== null} onChange={(event) => void uploadMemberPicture(user, event.target.files?.[0])}/></label></div>)}</div>}
            {action.kind === "create" ? <ChurchFields value={churchForm} change={setChurchForm}/> : action.kind !== "members" && <label>Reason<textarea required maxLength={500} value={reason} onChange={(event) => setReason(event.target.value)} placeholder="Explain why this administrative action is necessary"/></label>}
            <div className="sa-modal-actions"><button type="button" onClick={() => setAction(null)}>{action.kind === "members" ? "Close" : "Cancel"}</button>{action.kind !== "members" && <button className="primary-action" disabled={saving}>{saving ? "Saving…" : "Confirm action"}</button>}</div>
          </form>
        </div>
      )}
    </div>
  );
}

function AuditRows({ audits }: { audits: SuperAdminAudit[] }) {
  if (!audits.length) return <div className="sa-empty">No protected administrative actions have been recorded yet.</div>;
  return <div className="sa-audit-list">{audits.map((audit) => <article key={audit.id}><span><CheckCircle2/></span><div><b>{audit.action.replaceAll("_", " ").toLowerCase()}</b><p>{audit.reason}</p><small>{audit.actorEmail} · {new Date(audit.createdAt).toLocaleString()}</small></div><i>{audit.targetType} #{audit.targetId}</i></article>)}</div>;
}

function ChurchFields({ value, change }: { value: ChurchPayload; change: (next: ChurchPayload) => void }) {
  const field = (key: keyof ChurchPayload) => (event: React.ChangeEvent<HTMLInputElement>) => change({ ...value, [key]: event.target.value });
  return <div className="sa-form-grid"><label className="wide">Church name<input required value={value.name} onChange={field("name")}/></label><label>Email<input required type="email" value={value.email} onChange={field("email")}/></label><label>Phone<input required value={value.phone} onChange={field("phone")}/></label><label className="wide">Address<input required value={value.address} onChange={field("address")}/></label><label>CNPJ<input required value={value.cnpj} onChange={field("cnpj")}/></label><label>Principal pastor<input required value={value.principalPastor} onChange={field("principalPastor")}/></label></div>;
}
