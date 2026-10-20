const CACHE='five-kalme-v8';
const ASSETS=['./app.webmanifest'];
self.addEventListener('install',e=>{
  self.skipWaiting();
  e.waitUntil(caches.open(CACHE).then(c=>c.addAll(ASSETS)));
});
self.addEventListener('activate',e=>{
  e.waitUntil((async()=>{
    const keys=await caches.keys();
    await Promise.all(keys.filter(k=>k!==CACHE).map(k=>caches.delete(k)));
    await self.clients.claim();
  })());
});
self.addEventListener('fetch',e=>{
  const req=e.request;
  const url=new URL(req.url);
  if(req.mode==='navigate' || url.pathname.endsWith('/index.html')){
    e.respondWith(fetch(req).catch(()=>caches.match(req)));
    return;
  }
  if(url.hostname.includes('youtube.com')||url.hostname.includes('googlevideo.com')) return;
  e.respondWith(caches.match(req).then(r=>r||fetch(req)));
});