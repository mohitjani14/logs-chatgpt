(function(){
  const el = document.getElementById('jobId');
  if(!el) return;
  const jobId = el.textContent;
  const statusEl = document.getElementById('status');
  const timer = setInterval(async () => {
    const r = await fetch('/api/jobs/' + jobId);
    if(!r.ok) return;
    const data = await r.json();
    statusEl.textContent = 'Status: ' + data.status;
    if(data.status === 'COMPLETED'){
      statusEl.innerHTML = 'Status: COMPLETED <a href="/api/download/' + jobId + '">Download ZIP</a>';
      clearInterval(timer);
    }
    if(data.status === 'FAILED') clearInterval(timer);
  }, 2000);
})();
