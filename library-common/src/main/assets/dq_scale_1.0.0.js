; (function (win) {
  var doc = win.document;
  var docEl = doc.documentElement;
  var metaEl = doc.querySelector('meta[name="viewport"]');
  var dpr = 1;
  var scale = 1;
  var tid;

  var isIPhone = navigator.userAgent.match(/iphone|ipod|ipad|android/gi);

  dpr = isIPhone ? Math.min(win.top.devicePixelRatio, 3) : 1
  scale = 1 / dpr;
  docEl.setAttribute('data-dpr', dpr);
  if (!metaEl) {
    metaEl = doc.createElement('meta');
    metaEl.setAttribute('name', 'viewport');
    metaEl.setAttribute('content', 'initial-scale=' + scale + ', maximum-scale=' + scale + ', minimum-scale=' + scale + ', user-scalable=no');
    if (docEl.firstElementChild) {
      docEl.firstElementChild.appendChild(metaEl);
    } else {
      var wrap = doc.createElement('div');
      wrap.appendChild(metaEl);
      doc.write(wrap.innerHTML);
    }
  }

  function isPc() {
    var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone",
      "SymbianOS", "Windows Phone",
      "iPad", "iPod"];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
      if (userAgentInfo.indexOf(Agents[v]) > 0) {
        flag = false;
        break;
      }
    }
    return flag;
  }
  function refreshRem() {
    if (isPc()) {
      docEl.style.fontSize = '50px';
    } else {
      var width = win.top.document.documentElement.getBoundingClientRect().width;
      if (width / dpr > 750) {
        width = 750 * dpr;
      }
      var rem = 100 * (width / 750);
      docEl.style.fontSize = rem + 'px';
    }
  }

  win.addEventListener('resize', function () {
    clearTimeout(tid);
    tid = setTimeout(refreshRem, 300);
  }, false);
  win.addEventListener('pageshow', function (e) {
    if (e.persisted) {
      clearTimeout(tid);
      tid = setTimeout(refreshRem, 300);
    }
  }, false);
  refreshRem();
})(window);