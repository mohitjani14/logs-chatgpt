async function cancelJob(jobId){
  await fetch('/api/jobs/' + jobId + '/cancel', {method:'POST'});
}

async function refreshUserJobs(){
  const body = document.getElementById('jobsBody');
  if(!body) return;
  const r = await fetch('/api/jobs');
  if(!r.ok) return;
  const jobs = await r.json();
  body.innerHTML = jobs.map(j => `
  <tr>
    <td>${j.id}</td>
    <td>${j.project}/${j.environment}/${j.module}/${j.server}</td>
    <td>${j.status}</td>
    <td><div class="progress"><span style="width:${j.progress}%">${j.progress}%</span></div></td>
    <td>
      ${(j.status==='COMPLETED'||j.status==='DOWNLOADED') ? `<a class="btn tiny" href="/api/download/${j.id}">Download</a>` : ''}
      ${(j.status==='QUEUED'||j.status==='PROCESSING') ? `<button class="btn tiny warn" type="button" onclick="cancelJob('${j.id}')">Cancel</button>` : ''}
    </td>
  </tr>`).join('');
}

setInterval(refreshUserJobs, 2500);
refreshUserJobs();
