import { useState, useEffect } from "react";
import { useAuth } from "../../context/AuthContext";
import { FaEye, FaTrash, FaCheck } from "react-icons/fa";

function AdminJobs() {
  const [jobs, setJobs] = useState([]);
  const [newJob, setNewJob] = useState({ title: "", description: "", location: "", salary: "" });
  const { token } = useAuth();
  const [msg,     setMsg]     = useState("");
  const [error,   setError]   = useState("");

  useEffect(() => { fetchJobs(); }, []);

  const fetchJobs = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/jobs", {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      if (!res.ok) throw new Error()
      const data = await res.json();
      setJobs(data);
    } catch {  
      setError("Could not load jobs!")
    }  
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setError("")

    // Frontend validation
    if (!newJob.title.trim()) {
      setError("Title is required!"); return
    }
    if (!newJob.description.trim()) {
      setError("Description is required!"); return
    }
    try{
        const res = await  fetch("/api/jobs" , {
        method: "POST",  
        headers: { 
        "Content-Type": "application/json", 
        "Authorization": `Bearer ${token}` // <--- Εδώ είναι το κλειδί
        },
            
        body: JSON.stringify({
          title:       newJob.title,
          description: newJob.description,
          location:    newJob.location,
          salary:      newJob.salary ? parseFloat(newJob.salary) : null
        })
      })

      if (!res.ok) {
        const data = await res.json().catch(() => null)
        setError(data?.message || `Error: ${res.status}`)
        return
      }

      // Reset form and reload jobs
      setNewJob({ title: "", description: "", location: "", salary: "" })
      setMsg("Job created successfully!")
      setTimeout(() => setMsg(""), 3000)
      fetchJobs()

    } catch {
      setError("Could not connect to server!")
    }
  }

  const handleDelete = async (id, title) => {
    if (!window.confirm(`Delete "${title}"?`)) return

    try {
       const res = await fetch(`/api/jobs/${id}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      if (!res.ok) { setError("Could not delete job!"); return }

      setJobs(jobs.filter(j => j.id !== id))
      setMsg("Job deleted!")
      setTimeout(() => setMsg(""), 3000)

    } catch {
      setError("Could not connect to server!")
    }
  }

  return (
    <main className="main-content">
      <h2>Διαχείριση Αγγελιών</h2>
      
      {/* Φόρμα Προσθήκης */}
      <form className="admin-form" onSubmit={handleCreate}>
        <input type="text" placeholder="Τίτλος" value={newJob.title} onChange={e => setNewJob({...newJob, title: e.target.value})} required />
        <input type="text" placeholder="Τοποθεσία" value={newJob.location} onChange={e => setNewJob({...newJob, location: e.target.value})} />
        <input type="number" placeholder="Μισθός" value={newJob.salary} onChange={e => setNewJob({...newJob, salary: e.target.value})} />
        <textarea placeholder="Περιγραφή" value={newJob.description} onChange={e => setNewJob({...newJob, description: e.target.value})} required />
        <button type="submit" className="create-btn"><FaTrash /> Προσθήκη</button>
      </form>

      {/* Λίστα για Διαγραφή */}
      <div className="admin-list">
        {jobs.map(job => (
          <div key={job.id} className="admin-item">
            <span>{job.title}</span>
            <button onClick={() => handleDelete(job.id)} className="btn-danger"><FaTrash /></button>
          </div>
        ))}
      </div>
    </main>
  );
}
export default AdminJobs;