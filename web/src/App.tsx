import React, { useEffect, useState } from 'react'

type Job = { id: number; title: string; price: number }
type User = { id: number; email: string; firstName: string; lastName: string }

export default function App() {
  const [jobs, setJobs] = useState<Job[] | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [name, setName] = useState('')
  const [date, setDate] = useState('')
  const [selectedJob, setSelectedJob] = useState<number | null>(null)
  const [bookingResp, setBookingResp] = useState<string | null>(null)
  
  // Auth states
  const [user, setUser] = useState<User | null>(null)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [firstName, setFirstName] = useState('')
  const [lastName, setLastName] = useState('')
  const [phone, setPhone] = useState('')
  const [isRegistering, setIsRegistering] = useState(false)

  useEffect(() => {
    fetch('http://localhost:8080/jobs')
      .then((r) => r.json())
      .then(setJobs)
      .catch((e) => setError(String(e)))
  }, [])

  const submitBooking = async (e: React.FormEvent) => {
    e.preventDefault()
    setBookingResp(null)
    if (!selectedJob) {
      setBookingResp('Please select a job')
      return
    }

    try {
      const resp = await fetch('http://localhost:8080/book', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, date, jobId: selectedJob }),
      })
      const data = await resp.json()
      setBookingResp(JSON.stringify(data))
    } catch (err) {
      setBookingResp(String(err))
    }
  }

  return (
    <div style={{fontFamily: 'system-ui, sans-serif', padding: 20}}>
      <h1>Dustbusters of Destin</h1>
      <p>Cleaning jobs (from Java backend)</p>
      {error && <div style={{color: 'crimson'}}>Error: {error}</div>}
      {!jobs && !error && <div>Loading...</div>}
      {jobs && (
        <div>
          <ul>
            {jobs.map((j) => (
              <li key={j.id}>
                <label>
                  <input type="radio" name="job" value={j.id} onChange={() => setSelectedJob(j.id)} />{' '}
                  {j.title} — ${j.price}
                </label>
              </li>
            ))}
          </ul>

          <form onSubmit={submitBooking} style={{marginTop: 16}}>
            <div style={{marginBottom:8}}>
              <label>
                Your name:{' '}
                <input value={name} onChange={(e) => setName(e.target.value)} />
              </label>
            </div>
            <div style={{marginBottom:8}}>
              <label>
                Date:{' '}
                <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
              </label>
            </div>
            <button type="submit">Book selected job</button>
          </form>

          {bookingResp && (
            <div style={{marginTop:12}}>
              <strong>Booking response:</strong>
              <pre>{bookingResp}</pre>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
