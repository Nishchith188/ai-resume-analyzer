import { useState, useCallback } from "react";
import { resumeApi } from "./services/api";

// ─── Color & font tokens ────────────────────────────────────────────────────
const CSS = `
  @import url('https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Mono:wght@400;500&display=swap');

  *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

  :root {
    --bg: #0a0a0f;
    --surface: #13131a;
    --surface2: #1c1c27;
    --border: #2a2a3d;
    --accent: #6c63ff;
    --accent2: #ff6584;
    --accent3: #43e97b;
    --text: #e8e8f0;
    --muted: #7a7a9a;
    --font: 'Syne', sans-serif;
    --mono: 'DM Mono', monospace;
  }

  body { background: var(--bg); color: var(--text); font-family: var(--font); min-height: 100vh; }

  .app { max-width: 1100px; margin: 0 auto; padding: 40px 24px 80px; }

  /* NAV */
  .nav { display: flex; align-items: center; justify-content: space-between; margin-bottom: 56px; }
  .logo { font-size: 1.25rem; font-weight: 800; letter-spacing: -0.5px; }
  .logo span { color: var(--accent); }
  .steps { display: flex; gap: 8px; }
  .step { padding: 6px 14px; border-radius: 20px; font-size: 0.75rem; font-weight: 600;
    border: 1px solid var(--border); color: var(--muted); cursor: default; transition: all .3s; }
  .step.active { background: var(--accent); border-color: var(--accent); color: #fff; }
  .step.done { border-color: var(--accent3); color: var(--accent3); }

  /* UPLOAD */
  .upload-page { display: flex; flex-direction: column; align-items: center; text-align: center; padding: 40px 0; }
  .upload-headline { font-size: clamp(2rem, 5vw, 3.5rem); font-weight: 800; line-height: 1.1;
    letter-spacing: -1.5px; margin-bottom: 16px; }
  .upload-headline em { color: var(--accent); font-style: normal; }
  .upload-sub { color: var(--muted); font-size: 1rem; margin-bottom: 48px; max-width: 480px; }

  .drop-zone { width: 100%; max-width: 560px; border: 2px dashed var(--border);
    border-radius: 20px; padding: 64px 32px; cursor: pointer; transition: all .3s;
    background: var(--surface); position: relative; overflow: hidden; }
  .drop-zone:hover, .drop-zone.over { border-color: var(--accent); background: #1a1a2e; }
  .drop-zone.over { transform: scale(1.01); }
  .drop-icon { font-size: 3rem; margin-bottom: 16px; }
  .drop-label { font-size: 1.1rem; font-weight: 600; margin-bottom: 8px; }
  .drop-hint { color: var(--muted); font-size: 0.85rem; }
  .file-input { position: absolute; inset: 0; opacity: 0; cursor: pointer; }

  .selected-file { margin-top: 20px; background: var(--surface2); border: 1px solid var(--border);
    border-radius: 12px; padding: 16px 20px; display: flex; align-items: center; gap: 12px;
    width: 100%; max-width: 560px; }
  .file-icon { font-size: 1.5rem; }
  .file-name { font-family: var(--mono); font-size: 0.85rem; color: var(--accent); }
  .file-size { font-size: 0.75rem; color: var(--muted); }

  /* BTN */
  .btn { padding: 14px 32px; border-radius: 12px; border: none; cursor: pointer;
    font-family: var(--font); font-size: 0.95rem; font-weight: 700; transition: all .2s;
    display: inline-flex; align-items: center; gap: 8px; }
  .btn-primary { background: var(--accent); color: #fff; }
  .btn-primary:hover { background: #7c75ff; transform: translateY(-1px); box-shadow: 0 8px 24px rgba(108,99,255,.35); }
  .btn-primary:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }
  .btn-outline { background: transparent; color: var(--text); border: 1px solid var(--border); }
  .btn-outline:hover { border-color: var(--accent); color: var(--accent); }
  .btn-sm { padding: 8px 18px; font-size: 0.82rem; border-radius: 8px; }
  .upload-btn { margin-top: 32px; }

  /* LOADER */
  .loader { display: flex; flex-direction: column; align-items: center; gap: 16px; padding: 60px; }
  .spinner { width: 48px; height: 48px; border: 3px solid var(--border);
    border-top-color: var(--accent); border-radius: 50%; animation: spin 0.8s linear infinite; }
  @keyframes spin { to { transform: rotate(360deg); } }
  .loader-text { color: var(--muted); font-size: 0.9rem; }

  /* DASHBOARD */
  .dashboard { animation: fadeUp .4s ease; }
  @keyframes fadeUp { from { opacity:0; transform: translateY(16px); } to { opacity:1; transform:none; } }

  .page-header { display: flex; align-items: flex-start; justify-content: space-between;
    margin-bottom: 36px; gap: 16px; flex-wrap: wrap; }
  .page-title { font-size: 1.8rem; font-weight: 800; letter-spacing: -0.5px; }
  .page-title small { display: block; font-size: 0.9rem; font-weight: 400; color: var(--muted); margin-top: 4px; }

  .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 20px; }
  @media (max-width: 680px) { .grid-2 { grid-template-columns: 1fr; } }

  .card { background: var(--surface); border: 1px solid var(--border); border-radius: 16px; padding: 24px; }
  .card-title { font-size: 0.75rem; font-weight: 700; text-transform: uppercase;
    letter-spacing: 1.5px; color: var(--muted); margin-bottom: 16px; }

  .info-row { display: flex; flex-direction: column; gap: 2px; margin-bottom: 12px; }
  .info-label { font-size: 0.72rem; color: var(--muted); text-transform: uppercase; letter-spacing: 1px; }
  .info-value { font-size: 0.95rem; font-weight: 600; }
  .info-value a { color: var(--accent); text-decoration: none; }

  .skill-tags { display: flex; flex-wrap: wrap; gap: 8px; }
  .tag { padding: 5px 12px; border-radius: 20px; font-size: 0.78rem; font-weight: 600;
    font-family: var(--mono); }
  .tag-skill { background: #1e1e3a; color: var(--accent); border: 1px solid #3a3a6a; }
  .tag-matched { background: #0f2a1a; color: var(--accent3); border: 1px solid #1a4a2a; }
  .tag-missing { background: #2a0f1a; color: var(--accent2); border: 1px solid #4a1a2a; }

  /* ATS SCORE */
  .score-card { text-align: center; }
  .score-ring { position: relative; width: 120px; height: 120px; margin: 0 auto 16px; }
  .score-num { position: absolute; inset: 0; display: flex; flex-direction: column;
    align-items: center; justify-content: center; }
  .score-big { font-size: 2rem; font-weight: 800; line-height: 1; }
  .score-pct { font-size: 0.7rem; color: var(--muted); }
  .score-label { font-size: 0.85rem; color: var(--muted); }
  .score-label strong { display: block; font-size: 1rem; color: var(--text); }

  /* ANALYSIS */
  .jd-form { display: flex; flex-direction: column; gap: 16px; }
  .form-group label { display: block; font-size: 0.78rem; text-transform: uppercase;
    letter-spacing: 1px; color: var(--muted); margin-bottom: 8px; }
  textarea, input[type=text] { width: 100%; background: var(--surface2); border: 1px solid var(--border);
    border-radius: 10px; color: var(--text); font-family: var(--font); font-size: 0.9rem;
    padding: 12px 16px; resize: vertical; transition: border-color .2s; }
  textarea:focus, input[type=text]:focus { outline: none; border-color: var(--accent); }
  textarea { min-height: 120px; }
  .chip-input { display: flex; flex-wrap: wrap; gap: 8px; padding: 10px 12px;
    background: var(--surface2); border: 1px solid var(--border); border-radius: 10px; }
  .chip-input input { flex: 1; min-width: 120px; background: transparent; border: none;
    color: var(--text); font-family: var(--font); font-size: 0.9rem; outline: none; }
  .chip { background: #1e1e3a; color: var(--accent); border-radius: 4px; padding: 3px 10px;
    font-size: 0.78rem; font-family: var(--mono); cursor: pointer; }
  .chip:hover { background: var(--accent2); color: #fff; }

  /* SUGGESTIONS */
  .tip-list { display: flex; flex-direction: column; gap: 12px; }
  .tip { display: flex; gap: 12px; align-items: flex-start; }
  .tip-num { width: 28px; height: 28px; border-radius: 8px; background: var(--surface2);
    display: flex; align-items: center; justify-content: center; font-size: 0.78rem;
    font-weight: 700; color: var(--accent); flex-shrink: 0; font-family: var(--mono); }
  .tip-text { font-size: 0.9rem; line-height: 1.6; color: var(--muted); }
  .tip-text strong { color: var(--text); }

  .roles-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 12px; }
  .role-card { background: var(--surface2); border: 1px solid var(--border); border-radius: 12px;
    padding: 16px; cursor: default; transition: border-color .2s; }
  .role-card:hover { border-color: var(--accent); }
  .role-title { font-weight: 700; font-size: 0.9rem; margin-bottom: 4px; }
  .role-reason { font-size: 0.78rem; color: var(--muted); line-height: 1.5; }

  .section-block { background: var(--surface2); border-radius: 10px; padding: 16px;
    font-size: 0.85rem; line-height: 1.7; color: var(--muted); white-space: pre-wrap; }

  .gap-meta { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
  .meta-pill { padding: 6px 14px; border-radius: 20px; font-size: 0.78rem; font-weight: 600; }
  .pill-green { background: #0f2a1a; color: var(--accent3); }
  .pill-red { background: #2a0f1a; color: var(--accent2); }
  .pill-blue { background: #1a1a3a; color: var(--accent); }

  .tab-row { display: flex; gap: 4px; margin-bottom: 28px; }
  .tab { padding: 8px 20px; border-radius: 8px; border: none; cursor: pointer;
    font-family: var(--font); font-size: 0.85rem; font-weight: 600; transition: all .2s;
    background: transparent; color: var(--muted); }
  .tab.active { background: var(--surface2); color: var(--text); }
  .tab:hover:not(.active) { color: var(--text); }

  .error-box { background: #2a0f1a; border: 1px solid #4a1a2a; border-radius: 12px;
    padding: 16px 20px; color: var(--accent2); font-size: 0.9rem; margin-bottom: 20px; }
`;

// ─── Score ring SVG ─────────────────────────────────────────────────────────
function ScoreRing({ score }) {
  const r = 52;
  const circ = 2 * Math.PI * r;
  const offset = circ - (score / 100) * circ;
  const color = score >= 70 ? "#43e97b" : score >= 40 ? "#6c63ff" : "#ff6584";

  return (
    <div className="score-ring">
      <svg viewBox="0 0 120 120" width="120" height="120">
        <circle cx="60" cy="60" r={r} fill="none" stroke="#2a2a3d" strokeWidth="10" />
        <circle cx="60" cy="60" r={r} fill="none" stroke={color} strokeWidth="10"
          strokeDasharray={circ} strokeDashoffset={offset}
          strokeLinecap="round" transform="rotate(-90 60 60)"
          style={{ transition: "stroke-dashoffset 1s ease" }} />
      </svg>
      <div className="score-num">
        <span className="score-big" style={{ color }}>{score}</span>
        <span className="score-pct">/ 100</span>
      </div>
    </div>
  );
}

// ─── Upload page ─────────────────────────────────────────────────────────────
function UploadPage({ onUpload }) {
  const [file, setFile] = useState(null);
  const [over, setOver] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleFile = (f) => {
    if (f?.type === "application/pdf") { setFile(f); setError(""); }
    else setError("Please upload a PDF file.");
  };

  const handleDrop = useCallback((e) => {
    e.preventDefault(); setOver(false);
    handleFile(e.dataTransfer.files[0]);
  }, []);

  const handleSubmit = async () => {
    if (!file) return;
    setLoading(true); setError("");
    try {
      const data = await resumeApi.upload(file);
      onUpload(data);
    } catch (err) {
      setError("Upload failed. Make sure the backend is running on port 8080.");
    } finally { setLoading(false); }
  };

  if (loading) return (
    <div className="upload-page">
      <div className="loader">
        <div className="spinner" />
        <div className="loader-text">Parsing your resume with PDFBox…</div>
      </div>
    </div>
  );

  return (
    <div className="upload-page">
      <h1 className="upload-headline">Your resume,<br /><em>analyzed by AI.</em></h1>
      <p className="upload-sub">Upload your PDF resume and get ATS scores, skill gap analysis, and AI-powered improvement tips.</p>

      {error && <div className="error-box">⚠️ {error}</div>}

      <div className={`drop-zone ${over ? "over" : ""}`}
        onDragOver={(e) => { e.preventDefault(); setOver(true); }}
        onDragLeave={() => setOver(false)}
        onDrop={handleDrop}>
        <input className="file-input" type="file" accept=".pdf"
          onChange={(e) => handleFile(e.target.files[0])} />
        <div className="drop-icon">📄</div>
        <div className="drop-label">{file ? file.name : "Drop your resume here"}</div>
        <div className="drop-hint">{file ? "Click to change file" : "or click to browse · PDF only · max 10MB"}</div>
      </div>

      {file && (
        <div className="selected-file">
          <span className="file-icon">📋</span>
          <div>
            <div className="file-name">{file.name}</div>
            <div className="file-size">{(file.size / 1024).toFixed(1)} KB</div>
          </div>
        </div>
      )}

      <button className="btn btn-primary upload-btn" onClick={handleSubmit} disabled={!file}>
        Analyze Resume →
      </button>
    </div>
  );
}

// ─── Dashboard page ───────────────────────────────────────────────────────────
function DashboardPage({ resume, onAnalyze, analysisResult, onReset }) {
  const [tab, setTab] = useState("overview");
  const [jd, setJd] = useState("");
  const [skillInput, setSkillInput] = useState("");
  const [skills, setSkills] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const addSkill = (e) => {
    if (e.key === "Enter" && skillInput.trim()) {
      setSkills([...skills, skillInput.trim()]);
      setSkillInput("");
    }
  };

  const removeSkill = (i) => setSkills(skills.filter((_, idx) => idx !== i));

  const runAnalysis = async () => {
    setLoading(true); setError("");
    try {
      const result = await resumeApi.analyze(resume.id, jd, skills);
      onAnalyze(result);
      setTab("analysis");
    } catch {
      setError("Analysis failed. Make sure your Gemini API key is configured.");
    } finally { setLoading(false); }
  };

  return (
    <div className="dashboard">
      <div className="page-header">
        <div>
          <h1 className="page-title">
            {resume.candidateName || "Resume Dashboard"}
            <small>{resume.email} {resume.phone && `· ${resume.phone}`}</small>
          </h1>
        </div>
        <button className="btn btn-outline btn-sm" onClick={onReset}>← New Resume</button>
      </div>

      <div className="tab-row">
        {["overview", "analysis", "suggestions"].map(t => (
          <button key={t} className={`tab ${tab === t ? "active" : ""}`} onClick={() => setTab(t)}>
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      {error && <div className="error-box">⚠️ {error}</div>}

      {/* ── OVERVIEW ── */}
      {tab === "overview" && (
        <>
          <div className="grid-2">
            <div className="card">
              <div className="card-title">Contact Info</div>
              <div className="info-row">
                <span className="info-label">Name</span>
                <span className="info-value">{resume.candidateName || "—"}</span>
              </div>
              <div className="info-row">
                <span className="info-label">Email</span>
                <span className="info-value"><a href={`mailto:${resume.email}`}>{resume.email || "—"}</a></span>
              </div>
              <div className="info-row">
                <span className="info-label">Phone</span>
                <span className="info-value">{resume.phone || "—"}</span>
              </div>
            </div>

            <div className="card">
              <div className="card-title">Extracted Skills ({resume.skills?.length || 0})</div>
              <div className="skill-tags">
                {resume.skills?.map(s => <span key={s} className="tag tag-skill">{s}</span>)}
                {(!resume.skills || resume.skills.length === 0) && <span style={{color:"var(--muted)"}}>No skills detected</span>}
              </div>
            </div>
          </div>

          <div className="card" style={{ marginBottom: 20 }}>
            <div className="card-title">Education</div>
            <div className="section-block">{resume.education || "No education section found"}</div>
          </div>

          <div className="card" style={{ marginBottom: 20 }}>
            <div className="card-title">Experience</div>
            <div className="section-block">{resume.experience || "No experience section found"}</div>
          </div>

          <div className="card">
            <div className="card-title">Projects</div>
            <div className="section-block">{resume.projects || "No projects section found"}</div>
          </div>
        </>
      )}

      {/* ── ANALYSIS ── */}
      {tab === "analysis" && (
        <>
          {!analysisResult ? (
            <div className="card">
              <div className="card-title">Run ATS Analysis</div>
              <div className="jd-form">
                <div className="form-group">
                  <label>Job Description</label>
                  <textarea placeholder="Paste the full job description here…"
                    value={jd} onChange={e => setJd(e.target.value)} />
                </div>
                <div className="form-group">
                  <label>Required Skills (press Enter to add)</label>
                  <div className="chip-input">
                    {skills.map((s, i) => (
                      <span key={i} className="chip" onClick={() => removeSkill(i)}>{s} ✕</span>
                    ))}
                    <input placeholder="e.g. React, Docker…" value={skillInput}
                      onChange={e => setSkillInput(e.target.value)} onKeyDown={addSkill} />
                  </div>
                </div>
                <button className="btn btn-primary" onClick={runAnalysis}
                  disabled={loading || !jd.trim()}>
                  {loading ? "Analyzing…" : "Run Analysis →"}
                </button>
              </div>
            </div>
          ) : (
            <>
              <div className="grid-2">
                <div className="card score-card">
                  <div className="card-title">ATS Score</div>
                  <ScoreRing score={analysisResult.atsScore} />
                  <div className="score-label" style={{ marginTop: 12 }}>
                    <strong>{analysisResult.atsScore >= 70 ? "Strong Match" : analysisResult.atsScore >= 40 ? "Moderate Match" : "Weak Match"}</strong>
                    vs. job requirements
                  </div>
                </div>

                <div className="card">
                  <div className="card-title">Skill Match Breakdown</div>
                  <div className="gap-meta">
                    <span className="meta-pill pill-green">✓ {analysisResult.matchedSkills?.length || 0} matched</span>
                    <span className="meta-pill pill-red">✗ {analysisResult.missingSkills?.length || 0} missing</span>
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <div className="card-title" style={{ marginBottom: 8 }}>Matched</div>
                    <div className="skill-tags">
                      {analysisResult.matchedSkills?.map(s => <span key={s} className="tag tag-matched">{s}</span>)}
                    </div>
                  </div>
                  <div>
                    <div className="card-title" style={{ marginBottom: 8 }}>Missing</div>
                    <div className="skill-tags">
                      {analysisResult.missingSkills?.map(s => <span key={s} className="tag tag-missing">{s}</span>)}
                    </div>
                  </div>
                </div>
              </div>

              <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-title">AI Skill Gap Analysis</div>
                <div className="section-block">{analysisResult.skillGapAnalysis || "Run analysis to see AI insights"}</div>
              </div>

              <button className="btn btn-outline btn-sm" onClick={() => onAnalyze(null)}>← Re-run Analysis</button>
            </>
          )}
        </>
      )}

      {/* ── SUGGESTIONS ── */}
      {tab === "suggestions" && (
        <>
          {!analysisResult ? (
            <div className="card">
              <div className="card-title" style={{ color: "var(--muted)" }}>Run the Analysis tab first to generate AI suggestions.</div>
            </div>
          ) : (
            <>
              <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-title">AI Improvement Tips</div>
                <div className="tip-list">
                  {(analysisResult.aiSuggestions || "").split("\n")
                    .filter(line => line.match(/^\d+\./))
                    .map((tip, i) => (
                      <div key={i} className="tip">
                        <div className="tip-num">{i + 1}</div>
                        <div className="tip-text">{tip.replace(/^\d+\.\s*/, "")}</div>
                      </div>
                    ))}
                  {!analysisResult.aiSuggestions && <div style={{color:"var(--muted)"}}>No suggestions available</div>}
                </div>
              </div>

              <div className="card">
                <div className="card-title">Recommended Job Roles</div>
                <div className="roles-grid">
                  {analysisResult.recommendedRoles?.filter(r => r.trim()).slice(0, 6).map((role, i) => (
                    <div key={i} className="role-card">
                      <div className="role-title">💼 {role.replace(/^\d+\.\s*/, "").split(":")[0]}</div>
                      <div className="role-reason">{role.split(":")[1] || ""}</div>
                    </div>
                  ))}
                </div>
              </div>
            </>
          )}
        </>
      )}
    </div>
  );
}

// ─── Root App ─────────────────────────────────────────────────────────────────
export default function App() {
  const [step, setStep] = useState("upload"); // upload | dashboard
  const [resume, setResume] = useState(null);
  const [analysis, setAnalysis] = useState(null);

  const handleUpload = (data) => { setResume(data); setStep("dashboard"); };
  const handleReset = () => { setResume(null); setAnalysis(null); setStep("upload"); };

  return (
    <>
      <style>{CSS}</style>
      <div className="app">
        <nav className="nav">
          <div className="logo">Resume<span>AI</span></div>
          <div className="steps">
            <div className={`step ${step === "upload" ? "active" : resume ? "done" : ""}`}>1 Upload</div>
            <div className={`step ${step === "dashboard" ? "active" : ""}`}>2 Analyze</div>
            <div className={`step ${analysis ? "done" : ""}`}>3 Improve</div>
          </div>
        </nav>

        {step === "upload" && <UploadPage onUpload={handleUpload} />}
        {step === "dashboard" && resume && (
          <DashboardPage
            resume={resume}
            onAnalyze={setAnalysis}
            analysisResult={analysis}
            onReset={handleReset}
          />
        )}
      </div>
    </>
  );
}
