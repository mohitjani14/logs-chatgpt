async function searchAudit(){
  const q = document.getElementById('q').value;
  const r = await fetch('/api/admin/audit?q=' + encodeURIComponent(q));
  const data = await r.json();
  document.getElementById('results').textContent = data.join('\n');
}

async function refreshJobs(){
  const r = await fetch('/api/admin/jobs');
  if(!r.ok) return;
  const jobs = await r.json();
  document.getElementById('jobs').innerHTML = jobs.map(j =>
    `<div class="job"><div><strong>${j.id}</strong> ${j.user} ${j.project}/${j.server}</div><div>${j.status} (${j.progress}%)</div></div>`).join('');
}

refreshJobs();
setInterval(refreshJobs, 4000);
